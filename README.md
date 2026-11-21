<h2 align="center"> 🤖 AI-Based Automatic Attendance Management System </h2>

<p align="center">
  <b>An AI-powered attendance management system built with Java, Spring Boot, and Face Recognition.</b>
</p>

<p align="center">
  Automating attendance management using secure role-based administration and AI-powered facial recognition.
</p>

---

## 📌 Table of Contents

- [About the Project](#-about-the-project)
- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [System Modules](#-system-modules)
- [My Role](#-my-role)
- [Internship Details](#-internship-details)
- [Technology Stack](#-technology-stack)
- [System Architecture](#-system-architecture)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [How to Run the Project](#-how-to-run-the-project)
- [Database Setup](#-database-setup)
- [AI Service Setup](#-ai-service-setup)
- [Run Spring Boot Application](#-run-spring-boot-application)
- [Project Workflow](#-project-workflow)
- [Security](#-security)
- [Screenshots](#-screenshots)
- [Troubleshooting](#-troubleshooting)
- [Future Enhancements](#-future-enhancements)
- [Developer](#-developer)
- [Contact](#-contact)

---

# 📖 About the Project

The **AI-Based Automatic Attendance Management System** is a web-based application designed to automate and simplify the attendance process using **AI-powered face recognition**.

Traditional attendance systems require manual work and can be affected by human errors and proxy attendance. This project helps automate attendance management by combining a secure Spring Boot web application with a Python-based AI service for facial recognition.

The system provides multiple levels of administration for managing colleges, departments, teachers, students, lectures, and attendance.

---

# 🚀 Project Overview

The application follows a multi-level administration structure.

Different users have different responsibilities and permissions.

The main administration levels are:

1. 🛡️ System Administration
2. 🏫 College Administration
3. 🏢 Department Administration
4. 👨‍🏫 Teacher Administration

The system uses **Spring Security** to provide authentication and role-based authorization.

The AI-based attendance functionality uses a separate Python service to process face recognition requests.

---

# ✨ Key Features

## 🔐 Authentication & Security

- Secure Login System
- Role-Based Authentication
- Role-Based Authorization
- Protected URLs
- Password Encryption using BCrypt
- Secure Access Control

## 🏢 Administration

- System Administration
- College Administration
- Department Administration
- Teacher Administration

## 🏫 College Management

- College Registration
- OTP Verification
- College Verification
- College Activation
- College Profile Management
- College Dashboard

## 🏢 Department Management

- Department Management
- HOD Management
- Teacher Management
- Student Management

## 👨‍🏫 Teacher Management

- Teacher Login
- Student Registration
- Student Management
- Lecture Management
- Attendance Management

## 🤖 AI Attendance

- Face Recognition
- Student Identification
- Automated Attendance Marking
- Duplicate Attendance Prevention
- AI Service Integration

---

# 🏗️ System Modules

## 1️⃣ System Administration

The System Administration module manages the overall platform.

### Responsibilities

- Verify College Registrations
- Activate Colleges
- Manage System-Level Operations
- Monitor Registered Colleges

---

## 2️⃣ College Administration

The College Administration module manages college-level operations.

### Responsibilities

- Manage College Profile
- Manage Departments
- Add HOD
- Manage College Information
- Update Password
- Access College Dashboard

---

## 3️⃣ Department Administration

The Department Administration module is managed at the department level.

### Responsibilities

- Manage Department
- Manage Teachers
- Manage Students
- Manage Department Operations

---

## 4️⃣ Teacher Administration

The Teacher Administration module handles students and attendance.

### Responsibilities

- Register Students
- Manage Students
- Create Lectures
- Start Attendance
- Manage Attendance Records
- Use AI-Based Face Recognition

---

# 👨‍💻 My Role

I worked on this project as a:

## ⭐ Team Lead & Backend Developer

### My Responsibilities

- 👨‍💼 Team Leadership
- 🏗️ System Design
- 💻 Backend Development
- 🔐 Spring Security Implementation
- 🔌 REST API Development
- 🗄️ Database Design
- 🤝 Client Communication
- 🎨 Major Frontend Contribution
- 🔗 Project Integration
- 🧠 Business Logic Development

---

# 🏢 Internship Details

| Information | Details |
|---|---|
| Organization | Mauli Kavyanshi Foundation, Shirpur |
| Project Type | Internship Project |
| Role | Team Lead & Backend Developer |
| Team Size | 5 Members |
| Duration | 1 February – 5 April |

---

# 🛠️ Technology Stack

## Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- REST APIs
- Maven

## Database

- MySQL

## Frontend

- Thymeleaf
- HTML
- CSS
- JavaScript

## AI / Face Recognition

- Python
- Flask
- OpenCV
- MTCNN
- Face Recognition

## Tools

- Git
- GitHub
- Maven
- Eclipse
- IntelliJ IDEA
- VS Code
- MySQL Workbench

---

# 🏗️ System Architecture

`
                         ┌──────────────────────┐
                         │       USERS          │
                         │                      │
                         │ System Admin         │
                         │ College Admin        │
                         │ HOD                  │
                         │ Teacher              │
                         └───────────┬──────────┘
                                     │
                                     ▼
                         ┌──────────────────────┐
                         │   SPRING BOOT APP    │
                         │                      │
                         │ Controllers          │
                         │ Services             │
                         │ Spring Security      │
                         │ Repositories         │
                         │ Business Logic       │
                         └───────────┬──────────┘
                                     │
                   ┌─────────────────┴─────────────────┐
                   │                                   │
                   ▼                                   ▼
       ┌────────────────────────┐         ┌────────────────────────┐
       │       MYSQL DB         │         │    PYTHON AI SERVICE   │
       │                        │         │                        │
       │ attendance_system      │         │ src/main/AI/           │
       │                        │         │                        │
       │ Colleges               │         │ Flask                  │
       │ Departments            │         │ OpenCV                 │
       │ Teachers               │         │ MTCNN                  │
       │ Students               │         │ Face Recognition       │
       │ Lectures               │         │                        │
       │ Attendance             │         │                        │
       └────────────────────────┘         └────────────────────────┘


  AI-Based-Attendance-Management-System
│
├── src
│   └── main
│       │
│       ├── java
│       │   └── com
│       │       └── Attendance
│       │           │
│       │           ├── controller
│       │           ├── service
│       │           ├── repository
│       │           ├── entity
│       │           ├── config
│       │           └── security
│       │
│       ├── resources
│       │   │
│       │   ├── templates
│       │   ├── static
│       │   └── application.properties
│       │
│       └── AI
│           │
│           ├── app.py
│           └── requirements.txt
│
├── pom.xml
│
└── README.md     
