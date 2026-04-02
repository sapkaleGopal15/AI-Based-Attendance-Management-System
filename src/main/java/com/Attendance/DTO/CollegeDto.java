package com.Attendance.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CollegeDto {

    @NotBlank(message = "college name is required")
    @Size(max = 100, min = 5)
    private String name;

    @NotBlank(message = "address is required")
    @Size(max = 100, min = 5)
    private String address;

    @NotBlank(message = "university name is required")
    @Size(max = 100, min = 5)
    private String university;

    @NotBlank(message = "principle name is required")
    @Size(max = 100, min = 5)
    private String principle;

    @NotNull(message = "select college established date")
    @PastOrPresent
    private LocalDate date;

    @NotBlank(message = "college id is required")
    @Size(max = 6, min = 6)
    private String collegeId;

    @NotBlank(message = "college email is required")
    @Email
    private String email;

    @NotBlank(message = "password is required")
    @Size(max = 15, min = 7)
    private String password;

    @NotBlank(message = "confirm password is required")
    private String confirmPass;
}
