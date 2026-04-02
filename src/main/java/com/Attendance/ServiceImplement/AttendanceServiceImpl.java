package com.Attendance.ServiceImplement;

import java.util.List;

import com.Attendance.Entities.Attendance;
import com.Attendance.Entities.Lecture;

public interface AttendanceServiceImpl {

    List<Attendance> findByStudentId(Integer id);
    List<Attendance> findByLecture(Lecture lecture);
}
