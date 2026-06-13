package com.Attendance.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.Attendance.Entities.Attendance;
import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Student;
import com.Attendance.Repositories.AttendanceRepository;
import com.Attendance.ServiceImplement.AttendanceServiceImpl;

@Service
public class AttendanceService implements AttendanceServiceImpl{

    @Autowired
    private StudentService studentService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public List<Attendance> findByStudentId(Integer id) {
        Student student = studentService.findById(id);
        return attendanceRepository.findByStudent(student, Sort.by(Direction.DESC, "id"));
    }

    @Override
    public List<Attendance> findByLecture(Lecture lecture) {
        return attendanceRepository.findByLecture(lecture);
    }

    @Override
    public List<Attendance> findByStudentAndDate(Student student, LocalDate date) {
        List<Attendance> attendances = attendanceRepository.findByStudentAndDate(student, date);
        return attendances;
    }

    @Override
    public List<Attendance> findByStudentAndDateAndStatus(Student student, LocalDate date, String status) {
        List<Attendance> attendances = attendanceRepository.findByStudentAndDateAndStatus(student, date, status);
        return attendances;
    }

    @Override
    public Attendance findByStudentIdAndLectureId(Integer studentId, Integer lectureId) {
        return attendanceRepository.findByStudentIdAndLectureId(studentId, lectureId);
    }

    @Override
    public List<Attendance> findByStudentAndDateBetween(Student student, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByStudentAndDateBetween(student, startDate, endDate);
    }

}
