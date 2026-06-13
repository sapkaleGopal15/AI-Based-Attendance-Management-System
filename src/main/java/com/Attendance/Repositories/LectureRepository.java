package com.Attendance.Repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Teacher;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    Lecture findTopByTeacherIdAndDateOrderByStartTimeDesc(Integer teacherId, LocalDate today);

    // @Query("SELECT l FROM Lecture l " +
    //     "WHERE l.teacher.id = :teacherId " +
    //     "AND l.date = :today " +
    //     "AND l.startTime BETWEEN :oneHourAgo AND :now " +
    //     "ORDER BY l.startTime DESC")
    // List<Lecture> findLastOneHourLatestLecture(
    //         Integer teacherId,
    //         LocalDate today,
    //         LocalTime oneHourAgo,
    //         LocalTime now
    // );

    List<Lecture> findByTeacher(Teacher teacher, Sort sort);
    List<Lecture> findTop10ByTeacher(Teacher teacher, Sort sort);
    List<Lecture> findByTeacherAndDate(Teacher teacher, LocalDate date, Sort sort);
}
