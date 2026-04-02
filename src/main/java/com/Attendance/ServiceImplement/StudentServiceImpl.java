package com.Attendance.ServiceImplement;

import java.util.List;

import com.Attendance.DTO.StudentDto;
import com.Attendance.Entities.Attendance;
import com.Attendance.Entities.College;
import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Student;
import com.Attendance.Entities.Teacher;

public interface StudentServiceImpl {


    Boolean saveStudent(Student student, Teacher logedTeacher);
    Student findById(Integer id);
    List<Student> findAll();
    List<Student> findByCollegeAndDepartment(Integer id);
    void deleteById(Integer id);
    List<Student> findByCollegeAndDepartmentAndClassroom(College college ,String department, String classroom);
    List<Student> findByLecture(Lecture lecture);
    Student findByAttendances(Attendance attendances);
    Boolean saveStudentById(StudentDto studentDto, Teacher teacher, String embedding);
    List<Student> findByDepartment(Integer id);
    List<Student> findByTeacher(Teacher teacher);
}
