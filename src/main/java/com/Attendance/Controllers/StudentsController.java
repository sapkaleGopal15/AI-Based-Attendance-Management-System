package com.Attendance.Controllers;


import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Attendance.DTO.LectureAttendanceDto;
import com.Attendance.DTO.LecturesReportsByTeacher;
import com.Attendance.DTO.StudentDto;
import com.Attendance.Entities.Attendance;
import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Student;
import com.Attendance.Entities.Teacher;
import com.Attendance.Services.AttendanceService;
import com.Attendance.Services.LectureService;
import com.Attendance.Services.StudentService;
import com.Attendance.Services.TeacherService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
// @PreAuthorize("hasRole('teacher')")
public class StudentsController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LectureService lectureService;

    public String embedding;

    @GetMapping("/")
    public String dashboard(Model model, HttpSession session) {

        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // String email = auth.getName();

        // Teacher teacher = teacherService.findByEmail(email);

        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }

        Integer teacherId = (Integer) session.getAttribute("logedTeacher");

        List<Lecture> lectures = lectureService.findTop10ByTeacher(teacherId);
        List<LecturesReportsByTeacher> lecturesReportsByTeachers = new ArrayList<>();
        for (Lecture lecture : lectures) {
            List<Attendance> attendances = attendanceService.findByLecture(lecture);
            LecturesReportsByTeacher lecturesReportsByTeacher = new LecturesReportsByTeacher();
            long absent = 0;
            long present = 0;
            for (Attendance attendance : attendances) {
                if (attendance.getStatus().equals("PRESENT")) {
                    present++;
                } else {
                    absent++;
                }
            }
            long totalStudents = present + absent;
            long todayPresent = present;

            double percent = totalStudents == 0 ? 0 :
                    ((double) todayPresent / totalStudents) * 100;
            
            lecturesReportsByTeacher.setTodayAbsent(absent);
            lecturesReportsByTeacher.setTotalStudent(totalStudents);
            lecturesReportsByTeacher.setTodayPresent(todayPresent);
            lecturesReportsByTeacher.setPercent(String.format("%.0f%%", percent));
            lecturesReportsByTeacher.setClassroom(lecture.getClassroom());
            lecturesReportsByTeacher.setSubject(lecture.getSubject());

            lecturesReportsByTeachers.add(lecturesReportsByTeacher);
        }
        model.addAttribute("lectures", lecturesReportsByTeachers);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "dashboard");
        return "Teachers/dashboard";
    }

    @GetMapping("/lectures/attendance/{id}")
    public String lecturesAttendance(@PathVariable Integer id, HttpSession session, Model model) {

        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        
        Lecture lecture = lectureService.findById(id);

        List<Attendance> attendances = attendanceService.findByLecture(lecture);

        List<LectureAttendanceDto> lectureAttendanceDtos = new ArrayList<>();
        long absent = 0;
        long present = 0;
        String subject = null;
        for (Attendance attendance : attendances) {
            Student student = attendance.getStudent();

            LectureAttendanceDto lectureAttendanceDto = new LectureAttendanceDto();

            lectureAttendanceDto.setName(student.getName());
            lectureAttendanceDto.setClassroom(student.getClassroom());
            lectureAttendanceDto.setRollNo(student.getRollNo());
            lectureAttendanceDto.setStatus(attendance.getStatus());
            subject = attendance.getSubject();
            if (attendance.getStatus().equals("PRESENT")) {
                present++;
            } else {
                absent++;
            }
            lectureAttendanceDtos.add(lectureAttendanceDto);
        }

        long totalStudents = present + absent;
        long todayPresent = present;

        double percent = totalStudents == 0 ? 0 :
                ((double) todayPresent / totalStudents) * 100;

        model.addAttribute("subject", subject);
        model.addAttribute("todayPresent", todayPresent);
        model.addAttribute("attendancePercent", String.format("%.0f%%", percent));
        model.addAttribute("students", lectureAttendanceDtos);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "lectures");
        return "Teachers/lectures_attendance";
    }

    @GetMapping("/all-students")
    public String allStudents(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        Integer teacherId = (Integer) session.getAttribute("logedTeacher");
        List<Student> students = studentService.findByCollegeAndDepartment(teacherId);
        model.addAttribute("students", students);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "students");
        return "Teachers/students";
    }

    @GetMapping("/add-student")
    public String addStudent(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "addStudent");
        return "Teachers/add_student";
    }

    @GetMapping("/start-attendance")
    public String attendancePage(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        model.addAttribute("showSidebar", false);
        model.addAttribute("paramValue", "startAttendance");
        return "Teachers/attendance";
    }

    @GetMapping("/student-delete/{id}")
    public String deleteStudentById(@PathVariable Integer id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        studentService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMsg", "Deleted successfull...");
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "students");
        return "redirect:/teacher/all-students";
    }

    @GetMapping("/student-view-attendance/{id}")
    public String studentViewAttendance(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        List<Attendance> attendances = attendanceService.findByStudentId(id);
        List<Attendance> emptyAttendances = new ArrayList<>();
        for (Attendance attendance : attendances) {
            if (attendance.getStudent().getClassroom().equals(student.getClassroom())) {
                emptyAttendances.add(attendance);
            }
        }
        model.addAttribute("attendances", emptyAttendances);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "studentAttendance");
        return "Teachers/view_attendance";
    }

    @GetMapping("/students/attendance")
    public String studentAttendance(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        Integer teacherId = (Integer) session.getAttribute("logedTeacher");
        List<Student> students = studentService.findByCollegeAndDepartment(teacherId);
        model.addAttribute("students", students);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "studentAttendance");
        return "Teachers/student-attendance";
    }

    @GetMapping("/lectures")
    public String lecturesPage(HttpSession session, Model model) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        Integer teacherId = (Integer) session.getAttribute("logedTeacher");
        List<Lecture> lectures = lectureService.findByTeacherId(teacherId);
        model.addAttribute("lectures",lectures);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "lectures");
        return "Teachers/lectures";
    }

    @GetMapping("/profile")
    public String teacherProfile(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        Integer id = (Integer) session.getAttribute("logedTeacher");
        Teacher teacher = teacherService.findById(id);
        List<Lecture> lectures = lectureService.findByTeacher(teacher);

        List<Student> students = studentService.findByCollegeAndDepartment(id);
        model.addAttribute("students", students.size());
        model.addAttribute("lectures", lectures.size());
        model.addAttribute("showSidebar", true);
        return "Teachers/profile";
    }

    @GetMapping("/student-update/{id}")
    public String studentUpdate(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        Student student = studentService.findById(id);
        embedding = student.getEmbedding();
        StudentDto studentDto = StudentDto.builder()
            .id(student.getId())
            .name(student.getName())
            .classroom(student.getClassroom())
            .contact(student.getContact())
            .course(student.getCourse())
            .department(student.getDepartment())
            .rollNo(student.getRollNo())
            .build();
        model.addAttribute("studentDto", studentDto);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "students");
        return "Teachers/student_update";
    }

    @GetMapping("/update-password/{id}")
    public String updatePassword(@PathVariable("id") Integer id, HttpSession session, Model model) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wrong !");
            return "redirect:/login";
        }
        Teacher teacher = teacherService.findById(id);
        model.addAttribute("showSidebar", true);
        session.setAttribute("updatePasswordTeacher", teacher);
        return "Teachers/update_password";
    }


    @GetMapping("/student-face-update/{id}")
    public String studentFaceUpdate(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "students");
        return "Teachers/student_face_update";
    }



    // ================================ Proccessing ==================================================================================
    @PostMapping("/student-update")
    public String studentDoUpdate(@Valid @ModelAttribute StudentDto studentDto, BindingResult result, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
        
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("showSidebar", true);
            return "Teachers/student_update";            
        }

        Teacher teacher = teacherService.findById((Integer) session.getAttribute("logedTeacher"));
        Boolean student = studentService.saveStudentById(studentDto, teacher, embedding);

        if (student) {
            redirectAttributes.addFlashAttribute("successMsg","Student update succesful...");
            model.addAttribute("showSidebar", true);
            return "redirect:/teacher/all-students";
        } else {
            model.addAttribute("errorMsg","Something went wrong");
            model.addAttribute("showSidebar", true);
            return "Teachers/student_update";
        }

    }

    @PostMapping("/update-face")
    public String updateFace(
            @RequestParam Integer studentId,
            @RequestParam MultipartFile file,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {

            RestTemplate rt = new RestTemplate();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    rt.postForEntity(faceServiceUrl + "/generate-embedding", request, Map.class);

            Map<String, Object> bodyMap = response.getBody();
            
            // VALIDATION
            if (bodyMap == null || !(Boolean) bodyMap.getOrDefault("face_detected", false)) {
                redirectAttributes.addFlashAttribute("errorMsg", "⚠ Face not detected");
                return "redirect:/teacher/student-face-update/" + studentId;
            }
            
            if ((Boolean) bodyMap.getOrDefault("multiple_faces", false)) {
                redirectAttributes.addFlashAttribute("errorMsg", "⚠ Multiple faces detected");
                return "redirect:/teacher/student-face-update/" + studentId;            
            }
            // if (!(Boolean) bodyMap.getOrDefault("quality_ok", false)) {
            //     redirectAttributes.addFlashAttribute("errorMsg", "⚠ Face quality issue");
            //     return "redirect:/teacher/student-face-update/" + studentId;
            // }
            //  GET EMBEDDING
            Object embObj = bodyMap.get("embedding");

            ObjectMapper om = new ObjectMapper();

            double[] newEmbedding = om.convertValue(embObj, double[].class);

            if (newEmbedding == null || newEmbedding.length == 0) {
                redirectAttributes.addFlashAttribute("errorMsg", "⚠ Invalid embedding");
                return "redirect:/teacher/student-face-update/" + studentId;
            }

            newEmbedding = normalize(newEmbedding);

            String newEmbeddingJson = om.writeValueAsString(newEmbedding);

            // DUPLICATE CHECK
            List<Student> allStudents = studentService.findAll();

            for (Student existing : allStudents) {

                // skip same student
                if (existing.getId().equals(studentId)) continue;

                if (existing.getEmbedding() == null || existing.getEmbedding().isBlank()) {
                    continue;
                }

                double[] existingEmbedding =
                        om.readValue(existing.getEmbedding(), double[].class);

                existingEmbedding = normalize(existingEmbedding);

                double similarity = cosineSimilarity(newEmbedding, existingEmbedding);

                if (similarity >= 0.80) {
                    redirectAttributes.addFlashAttribute("errorMsg", "⚠ Face already registered as " + existing.getName());
                    return "redirect:/teacher/student-face-update/" + studentId;
                }
            }

            // UPDATE STUDENT FACE
            Student student = studentService.findById(studentId);

            if (student == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "Student not found");
                return "redirect:/teacher/student-face-update/" + studentId;
            }


            Boolean updated = studentService.updateFaceById(student, teacherService.findById((Integer) session.getAttribute("logedTeacher")), newEmbeddingJson);
            System.out.println("Updating face for student ID: " + studentId + ", Similarity with existing faces: " + newEmbeddingJson);
            if (updated) {
                redirectAttributes.addFlashAttribute("successMsg", "✅ Face updated successfully as "+student.getName());
                return "redirect:/teacher/all-students";
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "❌ Failed to update face");
                return "redirect:/teacher/student-face-update/" + studentId;
            }


        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "Server error while updating face");
            return "redirect:/teacher/student-face-update/" + studentId;
        }
    }

    @PostMapping("/do-update-password")
    public String doUpdatePassword(@RequestParam String oldPass, @RequestParam String newPass, @RequestParam String conPass, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login";
        }

        Teacher teacher = (Teacher) session.getAttribute("updatePasswordTeacher");

        if (!oldPass.equals(teacher.getPassword())) {
            model.addAttribute("showSidebar", true);
            model.addAttribute("errorMsg", "Old password doesn't exist !");
            return "Teachers/update_password";
        }

        int MIN = 8;
        int MAX = 15;
        if (newPass.length() > MIN && newPass.length() < MAX) {

            if (!newPass.equals(conPass)) {
                model.addAttribute("showSidebar", true);
                model.addAttribute("errorMsg", "Confirm password not match !");
                return "Teachers/update_password";
            } else {
                teacher.setPassword(newPass);
                if (teacherService.updateTeacher(teacher)) {
                    redirectAttributes.addFlashAttribute("successMsg", "Password update successfull...");
                    return "redirect:/teacher/profile";
                } else {
                    model.addAttribute("errorMsg", "Something went wrong !");
                    model.addAttribute("showSidebar", true);
                    return "Teachers/update_password";
                }
                
            }
        } else {
            model.addAttribute("showSidebar", true);
            model.addAttribute("errorMsg", "New password length between 7-15 !");
            return "Teachers/update_password";
        }
    }




    @Value("${face.service.url}")
    private String faceServiceUrl;

    public double cosineSimilarity(double[] a, double[] b) {
        if (a == null || b == null) {
            return -1.0;
        }

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += Math.pow(a[i], 2);
            normB += Math.pow(b[i], 2);
        }

        if (normA == 0 || normB == 0) return -1.0;

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public double[] normalize(double[] emb) {

        if (emb == null || emb.length == 0) {
            return null;
        }

        double norm = 0.0;
        for (double v : emb) norm += v * v;

        norm = Math.sqrt(norm);

        if (norm == 0) return emb;

        double[] result = new double[emb.length];
        for (int i = 0; i < emb.length; i++) {
            result[i] = emb[i] / norm;
        }

        return result;
    }



    @PostMapping("/check-face")
    @ResponseBody
    public Map<String, Object> checkFace(@RequestParam MultipartFile file) throws Exception {

        RestTemplate rt = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                rt.postForEntity(faceServiceUrl + "/generate-embedding", request, Map.class);

        Map<String, Object> bodyMap = response.getBody();

        // {
        //     "face_detected": true,
        //     "eyes": true,
        //     "multiple_faces": false,
        //     "quality_ok": true,
        //     "embedding": [0.12, 0.98, ...]
        // }

        if (bodyMap == null) {
            return Map.of(
                    "face_detected", false
            );
        }

        boolean faceDetected = (Boolean) bodyMap.getOrDefault("face_detected", false);
        boolean eyes = (Boolean) bodyMap.getOrDefault("eyes", false);
        boolean multipleFaces = (Boolean) bodyMap.getOrDefault("multiple_faces", false);
        boolean qualityOk = (Boolean) bodyMap.getOrDefault("quality_ok", false);
        String reason = (String) bodyMap.getOrDefault("reason", null);

        if (multipleFaces) {
            return Map.of(
                    "face_detected", true,
                    "multiple_faces", true,
                    "quality_ok", false,
                    "reason", "multiple_faces_detected"
            );
        }

        if (!faceDetected) {
            return Map.of(
                    "face_detected", false,
                    "quality_ok", false
            );
        }

        if (!qualityOk) {
            return Map.of(
                    "face_detected", true,
                    "eyes", eyes,
                    "quality_ok", false,
                    "reason", reason
            );
        }


        ObjectMapper om = new ObjectMapper();

        // Json array to double[]
        double[] newEmbedding =
                om.readValue(
                        om.writeValueAsString(bodyMap.get("embedding")),
                        double[].class);

        List<Student> allStudents = studentService.findAll();

        for (Student s : allStudents) {

            if (s.getEmbedding() == null || s.getEmbedding().isBlank()) {
                continue;
            }

            double[] existing =
                    om.readValue(s.getEmbedding(), double[].class);

            double[] normNew = normalize(newEmbedding);
            double[] normExisting = normalize(existing);

            if (normNew == null || normExisting == null || normNew.length != normExisting.length) {
                continue;
            }

            double sim = cosineSimilarity(normNew, normExisting);

            if (sim >= 0.80) {
                return Map.of(
                        "face_detected", true,
                        "quality_ok", true,
                        "duplicate", true,
                        "name", s.getName()
                );
            }
        }

        return Map.of(
                "face_detected", true,
                "quality_ok", true,
                "duplicate", false
        );
    }


    @PostMapping("/add-student")
    public String register(
            @RequestParam String name,
            @RequestParam Long rollNo,
            @RequestParam String contact,
            @RequestParam String course,
            @RequestParam String classroom,
            @RequestParam MultipartFile file,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            RestTemplate rt = new RestTemplate();

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    rt.postForEntity(faceServiceUrl + "/generate-embedding", request, Map.class);

            Map<String, Object> bodyMap = response.getBody();

            if (bodyMap == null || !(Boolean) bodyMap.getOrDefault("face_detected", false)) {
                model.addAttribute("showSidebar", true);
                model.addAttribute("errorMsg", "⚠ Face not detected properly");
                return "Teachers/add_student";
            }

            if ((Boolean) bodyMap.getOrDefault("multiple_faces", false)) {
                model.addAttribute("errorMsg", "⚠ Multiple faces detected. Only one face allowed");
                model.addAttribute("showSidebar", true);
                return "Teachers/add_student";
            }

            Object embObj = bodyMap.get("embedding");

            ObjectMapper om = new ObjectMapper();

            double[] newEmbedding = om.convertValue(embObj, double[].class);

            if (newEmbedding == null || newEmbedding.length == 0) {
                model.addAttribute("errorMsg", "⚠ Embedding conversion failed");
                model.addAttribute("showSidebar", true);
                return "Teachers/add_student";
            }

            newEmbedding = normalize(newEmbedding);

            List<?> embList = (List<?>) embObj;

            if (embList.isEmpty()) {
                model.addAttribute("errorMsg", "⚠ Invalid face embedding");
                model.addAttribute("showSidebar", true);
                return "Teachers/add_student";
            }
            String newEmbeddingJson = om.writeValueAsString(newEmbedding);

            List<Student> allStudents = studentService.findAll();
            
            for (Student existing : allStudents) {
                if (existing.getEmbedding() == null || existing.getEmbedding().isBlank()) {
                    continue;
                }

                double[] existingEmbedding = om.readValue(existing.getEmbedding(), double[].class);

                existingEmbedding = normalize(existingEmbedding);

                double similarity = cosineSimilarity(newEmbedding, existingEmbedding);

                if (similarity >= 0.80) {
                    model.addAttribute("errorMsg", "⚠ Face already registered as " + existing.getName());
                    model.addAttribute("showSidebar", true);
                    return "Teachers/add_student";
                }
            }

            Student s = new Student();
            s.setName(name);
            s.setRollNo(rollNo);
            s.setClassroom(classroom);
            s.setContact(contact);
            s.setCourse(course);
            s.setEmbedding(newEmbeddingJson);

            Integer logedTeacherId = (Integer) session.getAttribute("logedTeacher");
            Teacher logedTeacher = teacherService.findById(logedTeacherId);

            Boolean registered = studentService.saveStudent(s, logedTeacher);

            if (!registered) {
                model.addAttribute("errorMsg", "Something went wrong");
                model.addAttribute("showSidebar", true);
                return "Teachers/add_student";
            }

            redirectAttributes.addFlashAttribute("successMsg", "✅ Student Registered Successfully");
            redirectAttributes.addFlashAttribute("showSidebar", true);
            return "redirect:/teachers/add_student";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMsg", "Server error while registering");
            model.addAttribute("showSidebar", true);
            return "Teachers/add_student";
        }
    }

}