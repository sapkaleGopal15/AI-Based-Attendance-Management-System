package com.Attendance.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Attendance.DTO.CollegeDto;
import com.Attendance.Services.CollegeService;
import com.Attendance.Services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/email-otp-verification")
public class EmailOTPVerificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private CollegeService collegeService;

    
    @GetMapping("/send-otp")
    public String sendOTP(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("verificationEmail");
        CollegeDto registerCollegeDto = (CollegeDto) session.getAttribute("registeredCollegeDto");

        if (email == null || email.trim().isEmpty() || registerCollegeDto == null) {
            model.addAttribute("errorMsg", "Invalid input data.");
            return "register";
        }

        try {

            emailService.sendOTPEmail(email);

            model.addAttribute("email",email);
            model.addAttribute("successMsg", "OTP has been sent successfully to your email.");

            return "OTP_verification";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMsg", "Unable to send OTP. Please try again.");

            return "register";
        }
    }

    @GetMapping("/re-send-otp")
    public String reSendOTP(HttpSession session, Model model) {

        String email = (String) session.getAttribute("verificationEmail");

        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("errorMsg", "Invalid input data.");
            return "register";
        }

        try {

            emailService.sendOTPEmail(email);

            model.addAttribute("email",email);
            model.addAttribute("successMsg", "OTP has been re-sent successfully to your email.");

            return "OTP_verification";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMsg", "Unable to send OTP. Please try again.");

            return "register";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(
            @RequestParam("otp") String otp,
            HttpSession session,
            Model model) {

        String email =(String) session.getAttribute("verificationEmail");
        CollegeDto registerCollegeDto = (CollegeDto) session.getAttribute("registeredCollegeDto");

        if (otp.isEmpty() && otp.equals("")) {
            model.addAttribute("errorMsg", "Please enter the OTP.");
            model.addAttribute("email", email);
            return "OTP_verification";
        }

        // Email not found
        if (email == null) {

            registerCollegeDto.setVerify(false);
            model.addAttribute("errorMsg", "Verification session expired. Please register again.");

            return "register";
        }

        // Verify OTP
        String verified = emailService.verifyOTP(email, otp);


        if (!verified.equals("OTP verified successfully.")) {

            registerCollegeDto.setVerify(false);
            model.addAttribute("email", email);

            model.addAttribute("errorMsg", verified);
            
            return "OTP_verification";
        }

        registerCollegeDto.setVerify(true);

        if (collegeService.saveCollege(registerCollegeDto)) {
            model.addAttribute("verification", "verification");
            
            session.removeAttribute("registeredCollegeDto");
            session.removeAttribute("verificationEmail");

            model.addAttribute("successMsg1", "✅ Email verification and 🎓 college registration completed successfully.");
            model.addAttribute("successMsg2", "🔐 Your college account will be verified by our backend team.");
            model.addAttribute("successMsg3", "📧 Login details will be sent to your registered email after verification and ✅ activated college.");
            return "OTP_verification";
        } else {
            model.addAttribute("errorMsg", "something went wrong !\n\n Please try again later.");
            return "register";
        }

    }

}
