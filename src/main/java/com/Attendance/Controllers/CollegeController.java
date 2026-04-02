package com.Attendance.Controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Attendance.DTO.HODDto;
import com.Attendance.Entities.College;
import com.Attendance.Entities.Student;
import com.Attendance.Entities.Teacher;
import com.Attendance.Services.CollegeService;
import com.Attendance.Services.LectureService;
import com.Attendance.Services.StudentService;
import com.Attendance.Services.TeacherService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/college")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private LectureService lectureService;

    @GetMapping("/")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }

        Integer logedCollegeId = (Integer) session.getAttribute("logedCollege");
        College college = collegeService.findById(logedCollegeId);
        List<Teacher> teachers = teacherService.findByCollege(college);
        List<Student> students = new ArrayList<>();
        long todayLectures = 0;
        long totalDepartment = 0;
        for (Teacher teacher : teachers) {
            students.addAll(studentService.findByTeacher(teacher));
            todayLectures = todayLectures + lectureService.findByTeacherAndDate(teacher, LocalDate.now()).size();
            if (teacher.getRole().equals("HOD")) {
                totalDepartment++;
            }
        }

        model.addAttribute("totalDepartment", totalDepartment);
        model.addAttribute("totalStudent", students.size());
        model.addAttribute("totalTeacher", teachers.size());
        model.addAttribute("todayLectures", todayLectures);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "dashboard");
        return "College/dashboard";
    }

    @GetMapping("/hods")
    public String HOD(Model model, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Integer logedCollegeId = (Integer) session.getAttribute("logedCollege");
        College college = collegeService.findById(logedCollegeId);
        List<Teacher> teachers = teacherService.findByCollegeAndRole(college, "HOD");
        model.addAttribute("HOD", teachers);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "HOD");
        return "College/HOD";
    }

    @GetMapping("/teachers")
    public String teachers(Model model, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Integer logedCollegeId = (Integer) session.getAttribute("logedCollege");
        College college = collegeService.findById(logedCollegeId);
        List<Teacher> teachers = teacherService.findByCollegeAndRole(college, "teacher");
        model.addAttribute("teachers", teachers);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "teacher");
        return "College/teacher";
    }

    @GetMapping("/students")
    public String students(Model model, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Integer logedCollegeId = (Integer) session.getAttribute("logedCollege");
        College college = collegeService.findById(logedCollegeId);
        List<Teacher> teachers = teacherService.findByCollege(college);
        List<Student> students = new ArrayList<>();

        for (Teacher teacher : teachers) {
            students.addAll(studentService.findByTeacher(teacher));
        }

        model.addAttribute("students", students);
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "student");
        return "College/students";
    }
    
    @GetMapping("/add-HOD")
    public String register(Model model, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        model.addAttribute("hodDto", new HODDto());
        model.addAttribute("showSidebar", true);
        model.addAttribute("paramValue", "addHOD");
        return "College/register_HOD";
    }

    @GetMapping("/update-profile/{id}")
    public String updateProfile(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        College college = collegeService.findById(id);
        session.setAttribute("password", college.getPassword());
        model.addAttribute("college", college);
        model.addAttribute("showSidebar", true);
        return "College/update_profile";
    }

    @GetMapping("/update-teacher/{id}")
    public String updateTeacher(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Teacher teacher = teacherService.findById(id);
        session.setAttribute("teacherPassword", teacher.getPassword());
        model.addAttribute("hod", teacher);
        model.addAttribute("showSidebar", true);
        return "College/update_HOD";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Integer existCollegeId = (Integer) session.getAttribute("logedCollege");
        College college = collegeService.findById(existCollegeId);
        List<Teacher> teachers = teacherService.findByCollege(college);
        List<Student> students = new ArrayList<>();
        long totalDepartment = 0;
        for (Teacher teacher : teachers) {
            students.addAll(studentService.findByTeacher(teacher));
            if (teacher.getRole().equals("HOD")) {
                totalDepartment++;
            }
        }

        model.addAttribute("totalDepartments", totalDepartment);
        model.addAttribute("totalStudents", students.size());
        model.addAttribute("totalTeachers", teachers.size());
        model.addAttribute("college", college);
        model.addAttribute("showSidebar", true);

        return "College/profile";
    }


    // ====================== Processing =================================
    @PostMapping("/do-register")
    public String doRegister(
        @Valid 
        @ModelAttribute("hodDto") HODDto hodDto, 
        BindingResult result, 
        Model model, 
        RedirectAttributes redirectAttributes,
        HttpSession session
    ) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }

        Integer existCollegeID = (Integer) session.getAttribute("logedCollege");
        if (result.hasErrors()) {
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addHOD");
            return "College/register_HOD";
        }

        if (!hodDto.getPassword().equals(hodDto.getConfirmPassword())) {
            model.addAttribute("errorMsg", "Please enter same confirm password");
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addHOD");
            return "College/register_HOD";
        }

        if (!teacherService.findByEmailExist(hodDto.getEmail())) {
            model.addAttribute("errorMsg", "Email already existed !");
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addHOD");
            return "College/register_HOD";
        }

        if (!teacherService.saveHOD(hodDto, existCollegeID)) {
            model.addAttribute("errorMsg", "Something went wrong !");
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addHOD");
            return "College/register_HOD";
        } else {
            redirectAttributes.addFlashAttribute("successMsg","HOD register successful..");
            model.addAttribute("showSidebar", true);
            model.addAttribute("paramValue", "addHOD");
            return "redirect:/college/add-HOD";
        }
    }

    @PostMapping("/do-update-profile")
    public String doUpdateProfile(@ModelAttribute("college") College college, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        String password = (String) session.getAttribute("password");
        college.setPassword(password);
        if (collegeService.updateCollege(college)) {
            redirectAttributes.addFlashAttribute("successMsg","Profile updated successfull...");
            return "redirect:/college/profile";
        } else {
            model.addAttribute("showSidebar", true);
            model.addAttribute("errorMsg", "Something went wrong !");
            return "College/update_profile";
        }
    }

    @PostMapping("/do-update-teacher") 
    public String doUpdateHOD(@ModelAttribute("hod") Teacher teacher, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }

        String password = (String) session.getAttribute("teacherPassword");
        Integer id = (Integer) session.getAttribute("logedCollege");
        College college = collegeService.findById(id);
        teacher.setCollege(college);
        teacher.setPassword(password);
        if (teacherService.updateTeacher(teacher)) {
            redirectAttributes.addFlashAttribute("successMsg","Teacher updated successfull...");
            return "redirect:/college/hods";
        } else {
            model.addAttribute("showSidebar", true);
            model.addAttribute("errorMsg", "Something went wrong !");
            return "College/update_HOD";
        }
    }

    @PostMapping("/do-update-password")
    public String doUpdatePassword(@RequestParam String oldPass, @RequestParam String newPass, @RequestParam String conPass, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("logedCollege")==(null)) {
            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "redirect:/login/depa";
        }
        Integer logedCollegeId = (Integer) session.getAttribute("logedCollege");
        College college = collegeService.findById(logedCollegeId);

        if (!oldPass.equals(college.getPassword())) {
            model.addAttribute("showSidebar", true);
            model.addAttribute("college", college);
            model.addAttribute("errorMsg", "Old password doesn't exist !");
            return "College/update_profile";
        }

        int MIN = 8;
        int MAX = 15;
        if (newPass.length() > MIN && newPass.length() < MAX) {

            if (!newPass.equals(conPass)) {
                model.addAttribute("showSidebar", true);
                model.addAttribute("college", college);
                model.addAttribute("errorMsg", "Confirm password not match !");
                return "College/update_profile";
            } else {
                if (collegeService.updateCollegePassword(college, newPass)) {
                    redirectAttributes.addFlashAttribute("successMsg", "Password update successfull...");
                    return "redirect:/college/profile";
                } else {
                    model.addAttribute("errorMsg", "Something went wrong !");
                    model.addAttribute("showSidebar", true);
                    model.addAttribute("college", college);
                    return "College/update_profile";
                }
                
            }
        } else {
            model.addAttribute("showSidebar", true);
            model.addAttribute("college", college);
            model.addAttribute("errorMsg", "New password length between 7-15 !");
            return "College/update_profile";
        }
    }
}
