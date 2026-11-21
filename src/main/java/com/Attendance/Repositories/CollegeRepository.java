package com.Attendance.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Attendance.Entities.College;


@Repository
public interface CollegeRepository extends JpaRepository<College, Integer>{

    College findByCollegeId(String collegeId);
    College findByEmail(String email);
}
