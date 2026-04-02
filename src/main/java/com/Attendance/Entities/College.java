package com.Attendance.Entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String collegeId;

    private String email;

    private String principle;

    private LocalDate date;

    private String university;

    private String address;

    private String password;

    @OneToMany(mappedBy = "college", cascade = CascadeType.ALL)
    private List<Teacher> teachers = new ArrayList<>();

    @OneToMany(mappedBy = "college", cascade = CascadeType.ALL)
    private List<Student> students = new ArrayList<>();
}
