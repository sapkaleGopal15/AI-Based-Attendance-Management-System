from flask import Flask, request, jsonify
from facenet_pytorch import MTCNN, InceptionResnetV1
import json
from PIL import Image, ImageOps
import torch
import numpy as np
import io
import cv2
import mediapipe as mp

app = Flask(__name__)

device = 'cuda' if torch.cuda.is_available() else 'cpu'

mtcnn = MTCNN(keep_all=True, device=device, min_face_size=80)
resnet = InceptionResnetV1(pretrained='vggface2').eval().to(device)

mp_face_mesh = mp.solutions.face_mesh
face_mesh = mp_face_mesh.FaceMesh(
    static_image_mode=True,
    max_num_faces=1,
    refine_landmarks=True,
    min_detection_confidence=0.5
)


MIN_FACE_WIDTH = 90
MIN_FACE_HEIGHT = 90
MIN_EAR_THRESHOLD = 0.16 


def normalize_embedding(emb):
    emb = emb.astype(np.float32)
    norm = np.linalg.norm(emb)
    if norm == 0:
        return emb
    return emb / norm


def eye_aspect_ratio(landmarks, idx):
    """
    EAR = (||p2-p6|| + ||p3-p5||) / (2 * ||p1-p4||)
    """
    p1 = np.array([landmarks[idx[0]].x, landmarks[idx[0]].y], dtype=np.float32)
    p2 = np.array([landmarks[idx[1]].x, landmarks[idx[1]].y], dtype=np.float32)
    p3 = np.array([landmarks[idx[2]].x, landmarks[idx[2]].y], dtype=np.float32)
    p4 = np.array([landmarks[idx[3]].x, landmarks[idx[3]].y], dtype=np.float32)
    p5 = np.array([landmarks[idx[4]].x, landmarks[idx[4]].y], dtype=np.float32)
    p6 = np.array([landmarks[idx[5]].x, landmarks[idx[5]].y], dtype=np.float32)

    vertical1 = np.linalg.norm(p2 - p6)
    vertical2 = np.linalg.norm(p3 - p5)
    horizontal = np.linalg.norm(p1 - p4)

    if horizontal == 0:
        return 0.0

    return float((vertical1 + vertical2) / (2.0 * horizontal))


def is_eyes_open(face_rgb):
    result = face_mesh.process(face_rgb)
    if not result.multi_face_landmarks:
        return True, 0.0  

    landmarks = result.multi_face_landmarks[0].landmark

    left_eye_idx = [33, 160, 158, 133, 153, 144]
    right_eye_idx = [362, 385, 387, 263, 373, 380]

    try:
        left_ear = eye_aspect_ratio(landmarks, left_eye_idx)
        right_ear = eye_aspect_ratio(landmarks, right_eye_idx)
        ear = (left_ear + right_ear) / 2.0

        return True, ear  

    except Exception:
        return True, 0.0


def get_embeddings_and_boxes(image_bytes):
    """
    Returns list of faces with:
    - embedding
    - x, y, w, h
    - blur, blur_score
    - eyes_open, eye_score
    - quality_ok
    - reason
    """
    img_pil = ImageOps.exif_transpose(Image.open(io.BytesIO(image_bytes))).convert('RGB')
    img_np = np.array(img_pil)  

    boxes, probs, _ = mtcnn.detect(img_pil, landmarks=True)

    if boxes is None or len(boxes) == 0:
        return []

    faces_tensor = mtcnn(img_pil)
    if faces_tensor is None:
        return []

    if isinstance(faces_tensor, torch.Tensor) and faces_tensor.dim() == 3:
        faces_tensor = faces_tensor.unsqueeze(0)

    results = []

    for i, box in enumerate(boxes):
        x1, y1, x2, y2 = [int(round(v)) for v in box]

        x1 = max(0, x1)
        y1 = max(0, y1)
        x2 = min(img_np.shape[1], x2)
        y2 = min(img_np.shape[0], y2)

        if x2 <= x1 or y2 <= y1:
            continue

        face_w = x2 - x1
        face_h = y2 - y1

        face_rgb = img_np[y1:y2, x1:x2]
        face_bgr = cv2.cvtColor(face_rgb, cv2.COLOR_RGB2BGR)

        face_rgb = cv2.resize(face_rgb, (320, 320))
        eyes_open, ear_score = is_eyes_open(face_rgb)

        reason = None
        quality_ok = True

        if face_w < MIN_FACE_WIDTH or face_h < MIN_FACE_HEIGHT:
            reason = "face_too_small"
            quality_ok = False
       
        elif ear_score > 0.18:
            reason = "eyes_not_open"
            quality_ok = False

        embedding = []
        
        if i < len(faces_tensor):
            face_tensor = faces_tensor[i].unsqueeze(0).to(device)
            with torch.no_grad():
                emb = resnet(face_tensor).squeeze().cpu().numpy()
                embedding = normalize_embedding(emb).tolist()

        results.append({
            "embedding": embedding if embedding else [],
            "x": x1,
            "y": y1,
            "w": face_w,
            "h": face_h,
            "eyes_open": bool(eyes_open),
            "eye_score": round(ear_score, 3),
            "quality_ok": bool(quality_ok),
            "reason": reason
        })

    return results


@app.route('/generate-embedding', methods=['POST'])
def generate_embedding():
    file = request.files.get('file')

    if file is None:
        return jsonify({
            "face_detected": False,
            "multiple_faces": False,
            "eyes": False,
            "message": "no_file"
        }), 400

    image_bytes = file.read()
    faces = get_embeddings_and_boxes(image_bytes)

    if len(faces) == 0:
        return jsonify({
            "face_detected": False,
            "multiple_faces": False,
            "eyes": False,
            "message": "no_face_detected"
        })

    if len(faces) > 1:
        return jsonify({
            "face_detected": True,
            "multiple_faces": True,
            "eyes": False,
            "message": "multiple_faces_detected"
        })

    face = faces[0]

    if not face["quality_ok"]:
        return jsonify({
            "face_detected": True,
            "multiple_faces": False,
            "eyes": face["eyes_open"],
            "eye_score": face["eye_score"],
            "quality_ok": face["quality_ok"],
            "reason": face["reason"],
            "embedding": face["embedding"]
        })

    return jsonify({
        "face_detected": True,
        "multiple_faces": False,
        "eyes": True,
        "eye_score": face["eye_score"],
        "quality_ok": True,
        "reason": None,
        "embedding": face["embedding"]
    })


@app.route('/recognize', methods=['POST'])
def recognize():
    """
    expects:
      - file (image)
      - candidates (json string): list of {id, name, embedding}
      - threshold (float, optional)
    returns: {"faces": [{student_id,name,x,y,w,h,score,has_eyes,reason?}, ...]}
    """
    file = request.files.get('file')
    if file is None:
        return jsonify({"error": "no file provided"}), 400

    candidates = json.loads(request.form.get('candidates', '[]'))
    threshold = float(request.form.get('threshold', 0.85))

    faces = get_embeddings_and_boxes(file.read())

    output_faces = []
    for face in faces:
        if not face.get("eyes_open", False):
            output_faces.append({
                "student_id": None,
                "name": None,
                "x": face["x"],
                "y": face["y"],
                "w": face["w"],
                "h": face["h"],
                "score": None,
                "eyes_open": False,
                "reason": "no_eyes_detected"
            })
            continue

        emb_np = np.array(face["embedding"])
        best_score = -1.0
        best_student = None

        for c in candidates:
            cemb = np.array(c['embedding'])
            denom = (np.linalg.norm(emb_np) * np.linalg.norm(cemb))
            score = float(np.dot(emb_np, cemb) / denom) if denom > 0 else -1.0

            if score > best_score:
                best_score = score
                best_student = c

        if best_score >= threshold and best_student is not None:
            output_faces.append({
                "student_id": best_student.get("id"),
                "name": best_student.get("name"),
                "x": face["x"],
                "y": face["y"],
                "w": face["w"],
                "h": face["h"],
                "score": round(best_score, 4),
                "eyes_open": True
            })
        else:
            output_faces.append({
                "student_id": None,
                "name": None,
                "x": face["x"],
                "y": face["y"],
                "w": face["w"],
                "h": face["h"],
                "score": round(best_score, 4) if best_score >= 0 else None,
                "eyes_open": True,
                "reason": "not_matched"
            })

    return jsonify({"faces": output_faces})

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000)