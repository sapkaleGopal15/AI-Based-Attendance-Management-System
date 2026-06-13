package com.Attendance.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.Attendance.Entities.College;
import com.Attendance.Entities.Teacher;
import com.Attendance.Repositories.CollegeRepository;
import com.Attendance.Repositories.TeacherRepository;
import com.Attendance.Services.SecurityCustomUserDetailsService;

import jakarta.servlet.http.HttpSession;

@Configuration
public class SecurityConfig {

    @Autowired
    private CollegeRepository collegeRepo;

    @Autowired
    private TeacherRepository teacherRepo;

    private final SecurityCustomUserDetailsService userDetailsService;

    public SecurityConfig(SecurityCustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/register",
                    "/home",
                    "/about",
                    "/contact",
                    "/login",
                    "/login/depa",
                    "/college-login",
                    "/css/**",
                    "/forgot-password",
                    "/college-forgot-password",
                    "/hod-forgot-password",
                    "/do-register",
                    "/do-forgot-password",
                    "/do-college-forgot-password",
                    "/do-hod-forgot-password"
                ).permitAll()

                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/hod/**").hasRole("HOD")
                .requestMatchers("/college/**").hasRole("COLLEGE")

                .anyRequest().authenticated()
            )

            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String username = authentication.getName();
            String role = authentication.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();

                    HttpSession session = request.getSession();

            

            String requestedRole = request.getParameter("role");

            if (!role.equals("ROLE_" + requestedRole)) {
                request.getSession().setAttribute("errorMsg", "Invalid panel selected for this user!");
                String panel = request.getParameter("panel");

                if ("college".equals(panel)) {

                    response.sendRedirect("/college-login");

                } else if ("hod".equals(panel)) {

                    response.sendRedirect("/login/depa");

                } else {

                    response.sendRedirect("/login");
                }
                return;
            }

            if (role.equals("ROLE_TEACHER")) {
                response.sendRedirect("/teacher/");

            } else if (role.equals("ROLE_HOD")) {
                response.sendRedirect("/hod/");

            } else if (role.equals("ROLE_COLLEGE")) {
                response.sendRedirect("/college/");
            }

            Teacher teacher = teacherRepo.findByEmail(username);
            if (teacher != null) {

                session.setAttribute("authentication", teacher);
                session.setAttribute("logedTeacher", teacher.getId());

            } else {
                College college = collegeRepo.findByCollegeId(username);

                if (college != null) {
                    session.setAttribute("college", college);
                    session.setAttribute("logedCollege", college.getId());
                }
            }

        };
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {

            String errorMsg = "Invalid Username or Password";

            if (exception.getMessage().equals("User_not_found")) {
                errorMsg = "User not found !";

            } else if (exception.getMessage().contains("Bad credentials")) {
                errorMsg = "Invalid id or password !";

            } else if (exception.getMessage().contains("not active")) {
                errorMsg = "Account is not active !";

            } else if (exception.getMessage().contains("not verified")) {
                errorMsg = "Account is not verified !";
            } 

            request.getSession().setAttribute("errorMsg", errorMsg);

            String panel = request.getParameter("panel");

            if ("college".equals(panel)) {

                response.sendRedirect("/college-login");

            } else if ("hod".equals(panel)) {

                response.sendRedirect("/login/depa");

            } else {

                response.sendRedirect("/login");
            }

        };
    }

   @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {

        return (request, response, authentication) -> {

            String role = "";

            if (authentication != null) {

                role = authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority();
            }

            request.getSession()
                    .setAttribute("successMsg",
                            "Sign out successfully...");

            if (role.equals("ROLE_COLLEGE")) {

                response.sendRedirect("/college-login");

            } else if (role.equals("ROLE_HOD")) {

                response.sendRedirect("/login/depa");

            } else {

                response.sendRedirect("/login");
            }
        };
    }
}