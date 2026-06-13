package com.Attendance.Services;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.Attendance.Entities.Teacher;
import com.Attendance.Entities.College;
import com.Attendance.Repositories.TeacherRepository;
import com.Attendance.Repositories.CollegeRepository;

@Service
public class SecurityCustomUserDetailsService implements UserDetailsService {

    private final TeacherRepository teacherRepo;
    private final CollegeRepository collegeRepo;

    public SecurityCustomUserDetailsService(TeacherRepository teacherRepo,
                                            CollegeRepository collegeRepo) {
        this.teacherRepo = teacherRepo;
        this.collegeRepo = collegeRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 🔍 1. Teacher / HOD check (email based)
        Teacher teacher = teacherRepo.findByEmail(username);

        if (teacher != null) {

            // ❌ inactive
            if (!teacher.isActive()) {
                throw new RuntimeException("not active");
            }

            // ❌ not verified
            if (!teacher.isVerified()) {
                throw new RuntimeException("not verified");
            }

            return User.builder()
                    .username(teacher.getEmail())
                    .password(teacher.getPassword()) // 🔐 encoded password expected
                    .roles(teacher.getRole()) // TEACHER / HOD
                    .build();
        }

        // 🔍 2. College check (collegeId based)
        College college = collegeRepo.findByCollegeId(username);

        if (college != null) {

            if (!college.isActive()) {
                throw new RuntimeException("not active");
            }

            if (!college.isVerified()) {
                throw new RuntimeException("not verified");
            }

            return User.builder()
                    .username(college.getCollegeId())
                    .password(college.getPassword()) // 🔐 encoded password
                    .roles(college.getRole())
                    .build();
        }

        // ❌ Not found
        throw new UsernameNotFoundException("User_not_found");
    }
}