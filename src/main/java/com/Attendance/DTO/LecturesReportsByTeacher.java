package com.Attendance.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LecturesReportsByTeacher {

    private String subject;

    private String classroom;

    private long totalStudent;

    private long todayPresent;

    private long todayAbsent;

    private String percent;
}
