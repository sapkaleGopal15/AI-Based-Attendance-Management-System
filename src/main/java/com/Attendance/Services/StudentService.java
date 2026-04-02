package com.Attendance.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.Attendance.DTO.StudentDto;
import com.Attendance.Entities.Attendance;
import com.Attendance.Entities.College;
import com.Attendance.Entities.Lecture;
import com.Attendance.Entities.Student;
import com.Attendance.Entities.Teacher;
import com.Attendance.Repositories.StudentRepository;
import com.Attendance.ServiceImplement.StudentServiceImpl;

@Service
public class StudentService implements StudentServiceImpl{

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherService teacherService;

    @Override
    public Boolean saveStudent(Student student, Teacher logedTeacher) {
        student.setTeacher(logedTeacher);
        student.setDepartment(logedTeacher.getDepartment());
        student.setCollege(logedTeacher.getCollege());
        Student registredStudent = studentRepository.save(student);
        if (ObjectUtils.isEmpty(registredStudent)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean saveStudentById(StudentDto studentDto, Teacher teacher, String embedding) {

        Student student2 = Student.builder()
            .classroom(studentDto.getClassroom())
            .contact(studentDto.getContact())
            .course(studentDto.getCourse())
            .department(studentDto.getDepartment())
            .teacher(teacher)
            .id(studentDto.getId())
            .name(studentDto.getName())
            .rollNo(studentDto.getRollNo())
            .college(teacher.getCollege())
            .embedding(embedding)
            .build();

        Student student = studentRepository.save(student2);

        if (ObjectUtils.isEmpty(student)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Student findById(Integer id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> findByCollegeAndDepartmentAndClassroom(College college ,String department, String classroom) {
        List<Student> students = studentRepository.findByCollegeAndDepartmentAndClassroom(college ,department, classroom);
        return students;
    }

    @Override
    public List<Student> findByLecture(Lecture lecture) {
        return studentRepository.findByLecture(lecture);
    }

    @Override
    public Student findByAttendances(Attendance attendances) {
        return studentRepository.findByAttendances(attendances);
    }

    @Override
    public List<Student> findByDepartment(Integer id) {
        Teacher teacher = teacherService.findById(id);
        return studentRepository.findByCollegeAndDepartment(teacher.getCollege() ,teacher.getDepartment());
    }

    @Override
    public List<Student> findByTeacher(Teacher teacher) {
        return studentRepository.findByTeacher(teacher);
    }

    @Override
    public List<Student> findByCollegeAndDepartment(Integer id) {
        Teacher teacher = teacherService.findById(id);
        return studentRepository.findByCollegeAndDepartment(teacher.getCollege(), teacher.getDepartment());
    }

}
