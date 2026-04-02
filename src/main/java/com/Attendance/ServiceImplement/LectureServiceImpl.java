package com.Attendance.ServiceImplement;

import java.time.LocalDate;
import java.util.List;

import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Teacher;

public interface LectureServiceImpl {

    List<Lecture> findByTeacherId(Integer teacherId);
    Lecture findById(Integer id);
    List<Lecture> findTop10ByTeacher(Integer teacherId);
    List<Lecture> findByTeacher(Teacher teacher);
    List<Lecture> findByTeacherAndDate(Teacher teacher, LocalDate date);
}
