package com.Attendance.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {

    private Integer id;

    private String embedding;

    @NotBlank(message = "student name is required")
    @Size(min = 5, max = 50)
    private String name;

    @NotBlank(message = "class is required")
    @Size(min = 1, max = 20)
    private String classroom;

    @NotNull(message = "roll no is required")
    @Positive(message = "Value must be positive")
    private Long rollNo;

    @NotBlank(message = "student mobile is required")
    @Size(min = 10, max = 10)
    private String contact;

    @NotBlank(message = "student course is required")
    @Size(min = 2, max = 50)
    private String course;

    @NotBlank(message = "student department is required")
    @Size(min = 2, max = 50)
    private String department;
}
