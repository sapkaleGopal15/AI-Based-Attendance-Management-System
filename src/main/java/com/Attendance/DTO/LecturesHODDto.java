package com.Attendance.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LecturesHODDto {

    private Integer id;

    private String subject;

    private String classroom;

    private String teacherName;

    private LocalDate date;

    private LocalTime startTime;

    private long totalStudents;

    private String percent;
}
