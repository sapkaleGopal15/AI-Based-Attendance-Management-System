package com.Attendance.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Attendance.DTO.CollegeDto;
import com.Attendance.Entities.College;
import com.Attendance.Entities.Teacher;
import com.Attendance.Services.CollegeService;
import com.Attendance.Services.TeacherService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CollegeService collegeService;

    PageController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("showSidebar", false);
        model.addAttribute("paramValue", "home");
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("showSidebar", false);
        model.addAttribute("paramValue", "home");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("showSidebar", false);
        model.addAttribute("paramValue", "about");
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("showSidebar", false);
        model.addAttribute("paramValue", "contact");
        return "contact";
    }

    @GetMapping("/register")
    public String register(Model model) {
        CollegeDto collegeDto = new CollegeDto();
        model.addAttribute("paramValue", "registerCollege");
        model.addAttribute("collegeDto", collegeDto);
        return "register";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login/depa")
    public String departmentLogin() {
        return "hod_login";
    }

    @GetMapping("/college-login")
    public String collegeLogin() {
        return "college_login";
    }

    @GetMapping("/college-forgot-password")
    public String collegeForgetPassword() {
        return "college_forgot_password";
    }

    @GetMapping("/hod-forgot-password")
    public String hodForgetPassword() {
        return "hod_forgot_password";
    }

    @GetMapping("/forgot-password")
    public String forgetPassword() {
        return "forgot_password";
    }

    // =================================== Proccessing ==========================================================

    @PostMapping("/do-register")
    public String doRegister(@Valid @ModelAttribute CollegeDto collegeDto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (!collegeDto.getPassword().equals(collegeDto.getConfirmPass())) {
            model.addAttribute("errorMsg", "Confirm password doesn't match !");
            return "register";
        }

        College college = collegeService.findByCollegeId(collegeDto.getCollegeId());


        if (!ObjectUtils.isEmpty(college)) {
            model.addAttribute("errorMsg", "College id already register !");
            return "register";
        }

        if (collegeService.saveCollege(collegeDto)) {
            redirectAttributes.addFlashAttribute("successMsg", "College Register successfull...");
            return "redirect:/college-login";
        } else {
            model.addAttribute("errorMsg", "something went wrong !");
            return "register";
        }
    }

    @PostMapping("/do-college-forgot-password")
    public String doCollegeForgotPassword(
            @RequestParam String collegeId,
            @RequestParam String collegeName,
            @RequestParam String collegeEmail,
            HttpSession session) {

        College college = collegeService.findByCollegeId(collegeId);

        if (college != null &&
            college.getName().equals(collegeName) &&
            college.getEmail().equals(collegeEmail)) {
    
            session.setAttribute("successMsg", "Verified! your password is ("+college.getPassword()+")");
            return "college_forgot_password";
        } else {
            session.setAttribute("errorMsg", "Invalid details!");
            return "college_forgot_password";
        }
    }

    @PostMapping("/do-hod-forgot-password")
    public String doHodForgotPassword(
            @RequestParam String departmentEmail,
            @RequestParam String teacherName,
            @RequestParam String departmentMobile,
            @RequestParam String department,
            HttpSession session) {

        Teacher teacher = teacherService.findByEmail(departmentEmail);
        if (teacher != null &&
            teacher.getName().equals(teacherName) &&
            teacher.getDepartment().equals(department) && 
            teacher.getContact().equals(departmentMobile) &&
            teacher.getRole().equals("HOD")) {

            session.setAttribute("successMsg", "Verified! your password is ("+teacher.getPassword()+")");
            return "hod_forgot_password";
        } else {
            session.setAttribute("errorMsg", "Invalid details!");
            return "hod_forgot_password";
        }
    }

    @PostMapping("/do-forgot-password")
    public String doForgotPassword(
            @RequestParam String teacherEmail,
            @RequestParam String teacherName,
            @RequestParam String teacherMobile,
            @RequestParam String department,
            HttpSession session) {

        Teacher teacher = teacherService.findByEmail(teacherEmail);

        if (teacher != null &&
            teacher.getName().equals(teacherName) &&
            teacher.getDepartment().equals(department) && 
            teacher.getContact().equals(teacherMobile) &&
            teacher.getRole().equals("teacher")) {

            session.setAttribute("successMsg", "Verified! your password is ("+teacher.getPassword()+")");
            return "forgot_password";
        } else {
            session.setAttribute("errorMsg", "Invalid details!");
            return "forgot_password";
        }
    }
}
