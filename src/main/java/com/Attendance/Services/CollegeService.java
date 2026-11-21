package com.Attendance.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.Attendance.DTO.CollegeDto;
import com.Attendance.Entities.College;
import com.Attendance.Entities.Teacher;
import com.Attendance.Repositories.CollegeRepository;
import com.Attendance.Repositories.TeacherRepository;
import com.Attendance.ServiceImplement.CollegeServiceImpl;

@Service
public class CollegeService implements CollegeServiceImpl{

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public College findByCollegeId(String collegeId) {
        return collegeRepository.findByCollegeId(collegeId);
    }

    @Override
    public College findByEmail(String email) {
        College college = collegeRepository.findByEmail(email);
        if (ObjectUtils.isEmpty(college)) {
            Teacher teacher = teacherRepository.findByEmail(email);
            if (!ObjectUtils.isEmpty(teacher)) {
                return teacher.getCollege();
            } else {
                return null;
            }
        } else {
            return college;
        }
    }

    @Override
    public Boolean saveCollege(CollegeDto collegeDto) {
        College college = College.builder()
            .name(collegeDto.getName())
            .address(collegeDto.getAddress())
            .university(collegeDto.getUniversity())
            .collegeId(collegeDto.getCollegeId())
            .password(passwordEncoder.encode(collegeDto.getPassword()))
            .principle(collegeDto.getPrinciple())
            .role("COLLEGE")
            .email(collegeDto.getEmail())
            .date(collegeDto.getDate())
            .active(true)
            .verified(collegeDto.getVerify())
            .build();

        College college2 = collegeRepository.save(college);

        if (ObjectUtils.isEmpty(college2)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public College findById(Integer id) {
        return collegeRepository.findById(id).orElseThrow();
    }

    @Override
    public Boolean updateCollege(College college) {
        College updatedCollege = collegeRepository.save(college);

        if (ObjectUtils.isEmpty(updatedCollege)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean updateCollegePassword(College college, String newPass) {
        
        College college2 = new College();

        college2.setId(college.getId());
        college2.setName(college.getName());
        college2.setAddress(college.getAddress());
        college2.setCollegeId(college.getCollegeId());
        college2.setDate(college.getDate());
        college2.setEmail(college.getEmail());
        college2.setPassword(passwordEncoder.encode(newPass));
        college2.setPrinciple(college.getPrinciple());
        college2.setTeachers(college.getTeachers());
        college2.setUniversity(college.getUniversity());
        college2.setRole(college.getRole());
        college2.setActive(college.isActive());
        college2.setVerified(college.isVerified());

        College savedColleged = collegeRepository.save(college2);

        if (ObjectUtils.isEmpty(savedColleged)) {
            return false;
        } else {
            return true;
        }
    }
}
