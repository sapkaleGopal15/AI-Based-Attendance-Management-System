package com.Attendance.Utils;

// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeachersDTOValidate {

    // @NotBlank(message = "Teacher name is required")
    // @Size(min = 5, max = 50)
    private String name;

    // @NotBlank(message = "Deparment is required")
    // @Size(min = 3, max = 50)
    private String department;

    // @NotBlank(message = "Email is required")
    // @Email
    private String email;

    // @NotBlank(message = "Password is required")
    // @Size(min = 8, max = 14)
    private String password;

    //private String confirmPassword;

    // @NotNull(message = "Mobile number is required")
    // @Size(min = 10, max = 10)
    private String contact;

    // @NotBlank(message = "Select teacher role")
    private String role;
}
