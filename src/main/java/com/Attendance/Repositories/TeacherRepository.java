package com.Attendance.Repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Attendance.Entities.College;
import com.Attendance.Entities.Teacher;


@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Integer>{
    Teacher findByEmail(String email);
    List<Teacher> findByCollegeAndDepartmentAndRole(College college ,String department, String role);
    List<Teacher> findByCollegeAndRole(College college, String role, Sort sort);
    List<Teacher> findByCollege(College college);
}