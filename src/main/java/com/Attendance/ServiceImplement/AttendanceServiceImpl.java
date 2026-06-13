package com.Attendance.ServiceImplement;

import java.time.LocalDate;
import java.util.List;

import com.Attendance.Entities.Attendance;
import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Student;

public interface AttendanceServiceImpl {

    List<Attendance> findByStudentId(Integer id);
    List<Attendance> findByLecture(Lecture lecture);
    Attendance findByStudentIdAndLectureId(Integer studentId, Integer lectureId);
    List<Attendance> findByStudentAndDate(Student student, LocalDate date);
    List<Attendance> findByStudentAndDateAndStatus(Student student, LocalDate date, String status);
    List<Attendance> findByStudentAndDateBetween(Student student, LocalDate startDate, LocalDate endDate);
}
