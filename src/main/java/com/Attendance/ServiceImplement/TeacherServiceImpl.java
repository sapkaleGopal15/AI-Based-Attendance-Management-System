package com.Attendance.ServiceImplement;

import java.util.List;

import com.Attendance.DTO.HODDto;
import com.Attendance.DTO.TeacherDto;
import com.Attendance.Entities.College;
import com.Attendance.Entities.Teacher;

public interface TeacherServiceImpl {

    Boolean saveTeacher(TeacherDto teacherDto, Integer id);
    Boolean saveHOD(HODDto hodDto, Integer id);
    Boolean updateTeacher(Teacher teacher);
    Teacher findById(Integer id);
    Teacher findByEmail(String email);
    Boolean findByEmailExist(String email);
    List<Teacher> findByDepartment(Integer id);
    List<Teacher> findByCollegeAndRole(College college, String role);
    List<Teacher> findByCollege(College college);
    void deleteById(Integer id);
}