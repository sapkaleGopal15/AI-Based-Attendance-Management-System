package com.Attendance.ServiceImplement;

import com.Attendance.DTO.CollegeDto;
import com.Attendance.Entities.College;

public interface CollegeServiceImpl {

    College findByCollegeId(String collegeId);
    College findByEmail(String email);

    Boolean saveCollege(CollegeDto collegeDto);

    College findById(Integer id);

    Boolean updateCollege(College college);

    Boolean updateCollegePassword(College college, String newPass);
}
