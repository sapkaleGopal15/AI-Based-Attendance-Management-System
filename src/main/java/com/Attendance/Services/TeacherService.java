package com.Attendance.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.Attendance.DTO.HODDto;
import com.Attendance.DTO.TeacherDto;
import com.Attendance.Entities.College;
import com.Attendance.Entities.Teacher;
import com.Attendance.Repositories.TeacherRepository;
import com.Attendance.ServiceImplement.TeacherServiceImpl;

@Service
public class TeacherService implements TeacherServiceImpl {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Boolean saveTeacher(TeacherDto teacherDto, Integer id) {

        Teacher existTeacher = findById(id);

        Teacher teacher = Teacher.builder()
            .name(teacherDto.getName())
            .email(teacherDto.getEmail())
            .department(existTeacher.getDepartment())
            .college(existTeacher.getCollege())
            .password(passwordEncoder.encode(teacherDto.getPassword()))
            .contact(teacherDto.getContact())
            .role("TEACHER")
            .active(true)
            .verified(true)
            .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        if (ObjectUtils.isEmpty(savedTeacher)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean saveHOD(HODDto hodDto, Integer id) {

        College existCollege = collegeService.findById(id);

        Teacher teacher = Teacher.builder()
            .name(hodDto.getName())
            .email(hodDto.getEmail())
            .department(hodDto.getDepartment())
            .password(passwordEncoder.encode(hodDto.getPassword()))
            .contact(hodDto.getContact())
            .role(hodDto.getRole())
            .college(existCollege)
            .active(true)
            .verified(true)
            .build();

        Teacher savedHOD = teacherRepository.save(teacher);

        if (ObjectUtils.isEmpty(savedHOD)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean updateTeacher(Teacher teacher) {
        Teacher teacher2 = teacherRepository.save(teacher);
        if (!ObjectUtils.isEmpty(teacher2)) {
            return true;
        } else {
            return false;
        }   
    }

    @Override
    public Teacher findById(Integer id) {
        return teacherRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean findByEmailExist(String email) {
        Teacher existTeacher = teacherRepository.findByEmail(email);
        if (ObjectUtils.isEmpty(existTeacher)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Teacher findByEmail(String email) {
        Teacher existTeacher = teacherRepository.findByEmail(email);
        return existTeacher;
    }

    @Override
    public List<Teacher> findByDepartment(Integer id) {
        Teacher teacher = findById(id);
        return teacherRepository.findByCollegeAndDepartmentAndRole(teacher.getCollege() ,teacher.getDepartment(), "teacher");
    }

    @Override
    public List<Teacher> findByCollegeAndRole(College college, String role) {
        return teacherRepository.findByCollegeAndRole(college, role, Sort.by(Sort.Direction.ASC,"name"));
    }

    @Override
    public List<Teacher> findByCollege(College college) {
        return teacherRepository.findByCollege(college);
    }

    @Override
    public void deleteById(Integer id) {
        teacherRepository.deleteById(id);
    }
    

}
