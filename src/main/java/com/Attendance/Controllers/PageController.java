package com.Attendance.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CollegeService collegeService;

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

    @GetMapping("/college-forget-password")
    public String collegeForgetPassword() {
        return "college_forget_password";
    }

    @GetMapping("/hod-forget-password")
    public String hodForgetPassword() {
        return "hod_forget_password";
    }

    @GetMapping("/forget-password")
    public String forgetPassword() {
        return "forget_password";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMsg", "Sign out seccessfully...");
        return "redirect:/";
    }



    // =================== Proccessing ==========================
    @PostMapping("/do-login")
    public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session,
            RedirectAttributes redirectAttributes, Model model) {
        try {

            if (email.isEmpty() && password.isEmpty()) {
                session.setAttribute("errorMsg", "Email & Password is must !");
                return "login";
            }

            Teacher existTeacher = teacherService.findByEmail(email);
            if (ObjectUtils.isEmpty(existTeacher)) {
                session.setAttribute("errorMsg", "Email doesn't exist !");
                return "login";
            }

            if (!existTeacher.getPassword().equals(password)) {
                session.setAttribute("errorMsg", "Invalid password !");
                return "login";
            }

            if (existTeacher.getRole().equals("HOD")) {
                session.setAttribute("errorMsg", "Login with Department panel !");
                return "login";
            }

            if (existTeacher.getRole().equals("teacher")) {
                session.setAttribute("authentication", existTeacher);
                session.setAttribute("logedTeacher", existTeacher.getId());
                redirectAttributes.addFlashAttribute("successMsg", "Login successfull...");
                return "redirect:/teacher/";
            }

            session.setAttribute("errorMsg", "Somathing went wromg !");
            return "login";

        } catch (Exception exception) {
            model.addAttribute("errorMsg", "Something went wrong !");
            return "login";
        }
    }

    @PostMapping("/do-hod-login")
    public String doHODLogin(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (email.isEmpty() && password.isEmpty()) {
            session.setAttribute("errorMsg", "Email & Password is must !");
            return "hod_login";
        }

        Teacher existTeacher = teacherService.findByEmail(email);
        if (ObjectUtils.isEmpty(existTeacher)) {
            session.setAttribute("errorMsg", "Email doesn't exist !");
            return "hod_login";
        }

        if (!existTeacher.getPassword().equals(password)) {
            session.setAttribute("errorMsg", "Invalid password !");
            return "hod_login";
        }

        if (existTeacher.getRole().equals("teacher")) {
            session.setAttribute("errorMsg", "Login with faculty panel !");
            return "hod_login";
        }

        if (existTeacher.getRole().equals("HOD")) {
            session.setAttribute("authentication", existTeacher);
            session.setAttribute("logedTeacher", existTeacher.getId()); 
            redirectAttributes.addFlashAttribute("successMsg", "Login successfull...");
            return "redirect:/hod/";
        }
        
        session.setAttribute("errorMsg", "Somathing went wromg !");
        return "login";
    }

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


    @PostMapping("/do-college-login")
    public String collgeLogin(@RequestParam String collegeId, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (collegeId.isEmpty() && password.isEmpty()) {
            session.setAttribute("errorMsg", "College id & Password is must !");
            return "college_login";
        }

        College existCollege = collegeService.findByCollegeId(collegeId);
        if (ObjectUtils.isEmpty(existCollege)) {
            session.setAttribute("errorMsg", "College id doesn't exist !");
            return "college_login";
        }

        if (!existCollege.getPassword().equals(password)) {
            session.setAttribute("errorMsg", "Invalid password !");
            return "college_login";
        }
        session.setAttribute("college", existCollege);
        session.setAttribute("logedCollege", existCollege.getId()); 
        redirectAttributes.addFlashAttribute("successMsg", "Login successfull...");
        return "redirect:/college/";
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
            return "college_forget_password";
        } else {
            session.setAttribute("errorMsg", "Invalid details!");
            return "college_forget_password";
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
            return "hod_forget_password";
        } else {
            session.setAttribute("errorMsg", "Invalid details!");
            return "hod_forget_password";
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
            return "forget_password";
        } else {
            session.setAttribute("errorMsg", "Invalid details!");
            return "forget_password";
        }
    }
}
