package com.Attendance.Controllers;

import com.Attendance.Entities.Attendance;
import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Student;
import com.Attendance.Entities.Teacher;
import com.Attendance.Repositories.AttendanceRepository;
import com.Attendance.Repositories.LectureRepository;
import com.Attendance.Services.LectureService;
import com.Attendance.Services.StudentService;
import com.Attendance.Services.TeacherService;

import jakarta.servlet.http.HttpSession;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private LectureService lectureService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Value("${face.service.url}")
    private String faceServiceUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/live")
    public ResponseEntity<?> live(@RequestParam("file") MultipartFile file, @RequestParam Integer lectureId, HttpSession session) {

        try {
            Integer logedTeacherId = (Integer) session.getAttribute("logedTeacher");
            Teacher logedTeacher = teacherService.findById(logedTeacherId);
            Lecture lecture = lectureService.findById(lectureId);
            List<Student> student = studentService.findByCollegeAndDepartmentAndClassroom(logedTeacher.getCollege() ,logedTeacher.getDepartment(), lecture.getClassroom());
            List<Map<String, Object>> candidates = new ArrayList<>();
            List<Attendance> attendances = new ArrayList<>();

            for (Student s : student) {
                if (s.getEmbedding() != null && !s.getEmbedding().isBlank()) {
                    List<Double> emb = objectMapper.readValue(s.getEmbedding(), new TypeReference<List<Double>>() {});
                    Map<String, Object> cand = new HashMap<>();
                    cand.put("id", s.getId());
                    cand.put("name", s.getName());
                    cand.put("embedding", emb);
                    candidates.add(cand);
                }
            }

            RestTemplate rt = new RestTemplate();
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            body.add("candidates", objectMapper.writeValueAsString(candidates));
            body.add("threshold", "0.80");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> resp = rt.postForEntity(faceServiceUrl + "/recognize", request, Map.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "face service error"));
            }

            Map<String, Object> respBody = resp.getBody();
            List<Map<String, Object>> faces = (List<Map<String, Object>>) respBody.get("faces");
            if (faces == null) faces = Collections.emptyList();

            List<Map<String,Object>> outFaces = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (Map<String, Object> f : faces) {
                Map<String, Object> out = new HashMap<>();

                Object sidObj = f.get("student_id");
                Integer studentId = sidObj == null ? null : Integer.valueOf(sidObj.toString());
                String name = f.get("name") == null ? "Unknown" : f.get("name").toString();
                Double score = f.get("score") == null ? null : Double.valueOf(f.get("score").toString());

                out.put("x", f.get("x"));
                out.put("y", f.get("y"));
                out.put("w", f.get("w"));
                out.put("h", f.get("h"));
                out.put("score", score);

                boolean hasEyes = Boolean.TRUE.equals(f.get("eyes_open"));

                if (!hasEyes) {
                    out.put("studentId", null);
                    out.put("name", "No eyes detected");
                    out.put("status", "NO_EYES");
                } else if (studentId != null) {
                    out.put("studentId", studentId);
                    out.put("name", name);

                    Attendance att = attendanceRepository
                        .findByStudentIdAndLectureId(studentId, lectureId);

                    if(att != null){

                        if(att.getStatus().equals("PRESENT")){
                            out.put("status","ALREADY_MARKED");
                        } else {

                            Student student2 = studentService.findById(studentId);
                            
                            att.setTeacher(logedTeacher);
                            att.setStudent(student2);
                            att.setDate(today);
                            att.setStatus("PRESENT");
                            att.setTime(LocalTime.now());
                            attendances.add(attendanceRepository.save(att));
                            out.put("status","PRESENT");
                        }

                    }

                } else {
                    out.put("studentId", null);
                    out.put("name", "Not registered");
                    out.put("status", "NOT_REGISTERED");
                }

                outFaces.add(out);
            }

            lecture.setAttendances(attendances);

            return ResponseEntity.ok(Map.of("faces", outFaces));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "server error", "details", e.getMessage()));
        }
    }


    @PostMapping("/start")
    public ResponseEntity<?> startAttendance(
            @RequestParam String subject,
            @RequestParam String classroom,
            @RequestParam String mode,
            HttpSession session) {

        Integer teacherId = (Integer) session.getAttribute("logedTeacher");
        Teacher teacher = teacherService.findById(teacherId);

        LocalDate today = LocalDate.now();

        Lecture lecture;

        if(mode.equals("CONTINUE")) {

            lecture = lectureRepository
                    .findTopByTeacherIdAndDateOrderByStartTimeDesc(teacherId, today);

            List<Attendance> attendanceList =
                    attendanceRepository.findByLecture(lecture);

            if(lecture == null) {
                return ResponseEntity.badRequest().body("No previous lecture found");
            }

            return ResponseEntity.ok(Map.of(
                    "message","Continuing previous attendance",
                    "lectureId", lecture.getId(),
                    "students", lecture.getStudents(),
                    "attendance", attendanceList
            ));

        } else {

            List<Student> students = studentService.findByCollegeAndDepartmentAndClassroom(teacher.getCollege(), teacher.getDepartment(), classroom);
            
            lecture = new Lecture();
            lecture.setSubject(subject);
            lecture.setClassroom(classroom);
            lecture.setDate(today);
            lecture.setStartTime(LocalTime.now());
            lecture.setTeacher(teacher);
            lecture.setStudents(students);
            lectureRepository.save(lecture);
            
            for(Student s : students){
                Attendance att = new Attendance();
                att.setSubject(subject);
                att.setStudent(s);
                att.setTeacher(teacher);
                att.setLecture(lecture);
                att.setDate(today);
                att.setStatus("NOT_PRESENT");
                attendanceRepository.save(att);
            }

            return ResponseEntity.ok(Map.of(
                    "message","New attendance started",
                    "lectureId", lecture.getId()
            ));
        }
    }
    
}