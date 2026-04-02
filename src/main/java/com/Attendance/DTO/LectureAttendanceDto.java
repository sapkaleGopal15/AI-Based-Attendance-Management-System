package com.Attendance.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureAttendanceDto {

    private String name;

    private Long rollNo;

    private String classroom;

    private String status;

    private long present;

    private long absent;
}
