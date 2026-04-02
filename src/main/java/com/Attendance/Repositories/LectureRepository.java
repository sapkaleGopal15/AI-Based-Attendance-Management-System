package com.Attendance.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Teacher;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    Lecture findTopByTeacherIdAndDateOrderByStartTimeDesc(Integer teacherId, LocalDate today);
    List<Lecture> findByTeacher(Teacher teacher, Sort sort);
    List<Lecture> findTop10ByTeacher(Teacher teacher, Sort sort);
    List<Lecture> findByTeacherAndDate(Teacher teacher, LocalDate date, Sort sort);
}
