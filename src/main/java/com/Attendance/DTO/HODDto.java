package com.Attendance.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HODDto {

    @NotBlank(message = "Name is required")
    @Size(min = 5, max = 50)
    private String name;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Department is required")
    @Size(min = 3, max = 50)
    private String department;

    @NotBlank(message = "select role")
    private String role;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 14)
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 10)
    private String contact;
}
