package com.Attendance.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/system")
public class SystemController {

    @GetMapping("/")
    public String getMethodName() {
        return "System/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "System/index";
    }
    
}
