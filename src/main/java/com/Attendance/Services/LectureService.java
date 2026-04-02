package com.Attendance.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Teacher;
import com.Attendance.Repositories.LectureRepository;
import com.Attendance.ServiceImplement.LectureServiceImpl;

@Service
public class LectureService implements LectureServiceImpl{

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private TeacherService teacherService;

    @Override
    public List<Lecture> findByTeacherId(Integer teacherId) {
        Teacher teacher = teacherService.findById(teacherId);
        return lectureRepository.findByTeacher(teacher, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public Lecture findById(Integer id) {
        return lectureRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Lecture> findTop10ByTeacher(Integer teacherId) {
        Teacher teacher = teacherService.findById(teacherId);
        return lectureRepository.findTop10ByTeacher(teacher, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<Lecture> findByTeacher(Teacher teacher) {
        return lectureRepository.findByTeacher(teacher, Sort.by(Sort.Direction.DESC,"id"));
    }

    @Override
    public List<Lecture> findByTeacherAndDate(Teacher teacher, LocalDate date) {
        return lectureRepository.findByTeacherAndDate(teacher, date, Sort.by(Sort.Direction.DESC,"id"));
    }
}
