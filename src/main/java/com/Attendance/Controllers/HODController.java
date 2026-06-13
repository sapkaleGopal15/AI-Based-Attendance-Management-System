package com.Attendance.Controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Attendance.DTO.LectureAttendanceDto;
import com.Attendance.DTO.LecturesHODDto;
import com.Attendance.DTO.TeacherDto;
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


@Controller
@RequestMapping("/hod")
// @PreAuthorize("hasRole('HOD')")
public class HODController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LectureService lectureService;


    @GetMapping("/")
    public String dashboard(Model model, HttpSession session) {

        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }

        Integer id = (Integer) session.getAttribute("logedTeacher");
        List<Teacher> teachers = teacherService.findByDepartment(id);
        List<Student> students = studentService.findByDepartment(id);
        
        model.addAttribute("totalTeachers", teachers.size());
        model.addAttribute("totalStudents",students.size());
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "dashboard");
        return "HOD/dashboard";
    }

    @GetMapping("/teachers")
    public String teachers(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Integer id = (Integer) session.getAttribute("logedTeacher");
        List<Teacher> teachers = teacherService.findByDepartment(id);
        model.addAttribute("teachers", teachers);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "teachers");
        return "HOD/teachers";
    }

    @GetMapping("/lectures")
    public String lectures(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Integer id = (Integer) session.getAttribute("logedTeacher");
        List<Teacher> teachers = teacherService.findByDepartment(id);
        List<LecturesHODDto> lecturesHODDtos = new ArrayList<>();
        for (Teacher teacher : teachers) {
            List<Lecture> lectures = lectureService.findByTeacher(teacher);
            for (Lecture lecture : lectures) {
                LecturesHODDto lecturesHODDto = new LecturesHODDto();

                lecturesHODDto.setId(lecture.getId());
                lecturesHODDto.setClassroom(lecture.getClassroom());
                lecturesHODDto.setDate(lecture.getDate());
                lecturesHODDto.setStartTime(lecture.getStartTime());
                lecturesHODDto.setSubject(lecture.getSubject());
                lecturesHODDto.setTeacherName(teacher.getName());
                
                List<Attendance> attendances = lecture.getAttendances();

                long present = 0;
                long absent = 0;
                for (Attendance attendance : attendances) {
                    if (attendance.getStatus().equals("PRESENT")) {
                        present++;
                    } else {
                        absent++;
                    }
                }
                long totalStudents = present + absent;
                double percent = totalStudents == 0 ? 0 :
                    ((double) present / totalStudents) * 100;

                lecturesHODDto.setTotalStudents(totalStudents);
                lecturesHODDto.setPercent(String.format("%.0f%%", percent));

                lecturesHODDtos.add(lecturesHODDto);
            }
        }
        model.addAttribute("lectures", lecturesHODDtos);
        model.addAttribute("paramValue", "lectures");
        model.addAttribute("showSidebar", true);
        return "HOD/lectures";
    }

    @GetMapping("/students")
    public String students(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Integer id = (Integer) session.getAttribute("logedTeacher");
        List<Student> students = studentService.findByDepartment(id);
        model.addAttribute("students", students);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "students");
        return "HOD/students";
    }

    @GetMapping("/profile")
    public String teacherProfile(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        model.addAttribute("showSidebar", true);
        return "HOD/profile";
    }

    @GetMapping("/add-teacher")
    public String register(Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        TeacherDto teacherDto = new TeacherDto();
        model.addAttribute("teacherDto", teacherDto);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "addTeacher");
        return "HOD/register_teacher";
    }

    @GetMapping("/update-teacher/{id}")
    public String updateTeacher(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Teacher teacher = teacherService.findById(id);
        session.setAttribute("updateTeacher", teacher);
        model.addAttribute("teacher", teacher);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "teachers");
        return "HOD/update_teacher";
    }

    @GetMapping("/student-view-attendance/{id}")
    public String studentViewAttendance(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
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
        model.addAttribute("paramValue", "students");
        return "HOD/student_attendance";
    }

    @GetMapping("/lectures/attendance/{id}")
    public String lecturesAttendance(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
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
        return "HOD/lecture_attendance";
    }

    @GetMapping("/update-password/{id}")
    public String updatePassword(@PathVariable("id") Integer id, HttpSession session, Model model) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Teacher teacher = teacherService.findById(id);
        model.addAttribute("showSidebar", true);
        session.setAttribute("updatePasswordTeacher", teacher);
        return "HOD/update_password";
    }

    // ======================= Proccessing ===========================
    @PostMapping("/do-register")
    public String doRegister(
        @Valid 
        @ModelAttribute TeacherDto teacherDto, 
        BindingResult result, 
        Model model, 
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Integer id = (Integer) session.getAttribute("logedTeacher");
        if (result.hasErrors()) {
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addTeacher");
            return "HOD/register_teacher";
        }

        if (!teacherDto.getPassword().equals(teacherDto.getConfirmPassword())) {
            model.addAttribute("errorMsg", "Please enter same confirm password");
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addTeacher");
            return "HOD/register_teacher";
        }

        if (!teacherService.findByEmailExist(teacherDto.getEmail())) {
            model.addAttribute("errorMsg", "Email already existed !");
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addTeacher");
            return "HOD/register_teacher";
        }

        if (!teacherService.saveTeacher(teacherDto, id)) {
            model.addAttribute("errorMsg", "Something went wrong !");
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addTeacher");
            return "HOD/register_teacher";
        } else {
            redirectAttributes.addFlashAttribute("successMsg","Teacher register successful..");
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addTeacher");
            return "redirect:/hod/add-teacher";
        }
    }

    @PostMapping("/do-update-teacher") 
    public String doUpdateTeacher(@ModelAttribute("teacher") Teacher teacher, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Teacher updateTeacher = (Teacher) session.getAttribute("updateTeacher");

        updateTeacher.setName(teacher.getName());
        updateTeacher.setEmail(teacher.getEmail());
        updateTeacher.setContact(teacher.getContact());
        updateTeacher.setActive(teacher.isActive());
        updateTeacher.setVerified(teacher.isVerified());
        if (teacherService.updateTeacher(updateTeacher)) {
            redirectAttributes.addFlashAttribute("successMsg","Teacher updated successfull...");
            return "redirect:/hod/teachers";
        } else {
            model.addAttribute("showSidebar", true);
            model.addAttribute("errorMsg", "Something went wrong !");
            return "HOD/update_teacher";
        }
    }

    @PostMapping("/delete-teacher/{id}")
    public String deleteTeacher(@PathVariable("id") Integer id, RedirectAttributes ra) {
        teacherService.deleteById(id);
        ra.addFlashAttribute("successMsg", "Teacher deleted successfully!");
        return "redirect:/hod/teachers";
    }


    @PostMapping("/do-update-password")
    public String doUpdatePassword(@RequestParam String oldPass, @RequestParam String newPass, @RequestParam String conPass, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("logedTeacher")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Teacher teacher = (Teacher) session.getAttribute("updatePasswordTeacher");

        if (!oldPass.equals(teacher.getPassword())) {
            model.addAttribute("showSidebar", true);
            model.addAttribute("errorMsg", "Old password doesn't exist !");
            return "HOD/update_password";
        }

        int MIN = 8;
        int MAX = 15;
        if (newPass.length() > MIN && newPass.length() < MAX) {

            if (!newPass.equals(conPass)) {
                model.addAttribute("showSidebar", true);
                model.addAttribute("errorMsg", "Confirm password not match !");
                return "HOD/update_password";
            } else {
                teacher.setPassword(newPass);
                if (teacherService.updateTeacher(teacher)) {
                    redirectAttributes.addFlashAttribute("successMsg", "Password update successfull...");
                    return "redirect:/hod/teachers";
                } else {
                    model.addAttribute("errorMsg", "Something went wrong !");
                    model.addAttribute("showSidebar", true);
                    return "HOD/update_password";
                }
                
            }
        } else {
            model.addAttribute("showSidebar", true);
            model.addAttribute("errorMsg", "New password length between 7-15 !");
            return "HOD/update_password";
        }
    }

}
