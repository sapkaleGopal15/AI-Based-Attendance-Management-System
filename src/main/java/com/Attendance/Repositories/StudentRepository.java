package com.Attendance.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Student;
import com.Attendance.Entities.Teacher;
import com.Attendance.Entities.Attendance;
import com.Attendance.Entities.College;


@Repository
public interface StudentRepository extends JpaRepository<Student,Integer>{
    List<Student> findByTeacher(Teacher teacher);
    List<Student> findByCollegeAndDepartmentAndClassroom(College college ,String department, String classroom);
    List<Student> findByLecture(Lecture lecture);

    Student findByAttendances(Attendance attendances);
    List<Student> findByCollegeAndDepartment(College college, String department);
}