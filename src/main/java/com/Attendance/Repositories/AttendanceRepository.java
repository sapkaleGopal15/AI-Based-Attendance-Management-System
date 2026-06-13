package com.Attendance.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Attendance.Entities.Attendance;
import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Student;

public interface AttendanceRepository extends JpaRepository<Attendance,Long>{
    boolean existsByStudentIdAndDate(Integer id,LocalDate date);
    List<Attendance> findByDate(LocalDate date);

    Attendance findByStudentIdAndLectureId(Integer studentId, Integer lectureId);

    List<Attendance> findByStudent(Student student, Sort sort);

    List<Attendance> findByLecture(Lecture lecture);

    List<Attendance> findByStudentAndDate(Student student, LocalDate date);

    List<Attendance> findByStudentAndDateAndStatus(Student student, LocalDate date, String status);

    List<Attendance> findByStudentAndDateBetween(Student student, LocalDate startDate, LocalDate endDate);
 
}

