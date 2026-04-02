package com.Attendance.Utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeachersDTO {

    private Integer id;

    private Integer college_id;

    private String name;

    private String department;

    private String contact;

    private String email;

    private String password;

    private String role;
}
