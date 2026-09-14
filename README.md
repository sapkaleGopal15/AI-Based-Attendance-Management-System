# 🤖 AI-Based Attendance Management System

A comprehensive **AI-Based Attendance Management System** designed to automate and simplify attendance management using **Face Recognition and modern web technologies**.

This project was developed during an internship at **Mauli Kavyanshi Foundation, Shirpur**. The system provides a multi-level administration structure for managing colleges, departments, teachers, students, and attendance.

---

## 🚀 Project Overview

Traditional attendance systems can be time-consuming and vulnerable to manual errors and proxy attendance.

This system helps automate the attendance process by integrating **AI-based face recognition** with a secure web-based management platform.

The application provides separate administration modules for different levels of management and uses role-based authentication and authorization to secure the system.

---

## 👨‍💻 My Role

I worked as a **Team Lead & Backend Developer** in a team of **5 members**.

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

---

## 🏢 Internship Details

**Organization:** Mauli Kavyanshi Foundation, Shirpur

**Project Duration:** 1 February – 5 April

**Team Size:** 5 Members

**Role:** Team Lead & Backend Developer

---

# 🏗️ System Modules

The system consists of four major administration modules.

## 1️⃣ System Administration

Responsible for managing the overall system.

### Features

- College Verification
- College Activation
- System-Level Management
- Monitoring College Registrations

---

## 2️⃣ College Administration

Responsible for managing college-level operations.

### Features

- College Profile Management
- Department Management
- HOD Management
- College Dashboard
- Password Management

---

## 3️⃣ Department Administration

Managed by the HOD.

### Features

- Department Management
- Teacher Management
- Student Management
- Department-Level Administration

---

## 4️⃣ Teacher Administration

Responsible for managing students and attendance.

### Features

- Student Registration
- Student Management
- Attendance Management
- Lecture Management
- Face Recognition Attendance

---

# ✨ Key Features

- 🔐 Secure Authentication & Authorization
- 👥 Role-Based Access Control
- 🏢 Multi-Level Administration
- 🏫 College Management
- 🏛️ Department Management
- 👨‍🏫 Teacher Management
- 👨‍🎓 Student Management
- 📋 Attendance Management
- 🎓 Lecture Management
- 🤖 AI-Based Face Recognition
- 🔑 Secure Password Encryption
- 📊 Dashboard Management
- 🔌 REST API Integration

---

# 🛠️ Technology Stack

## Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- REST APIs

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

- Maven
- Git
- GitHub
- Eclipse / IntelliJ IDEA
- MySQL Workbench

---

# 🏗️ Project Architecture

`
                    ┌──────────────────────┐
                    │       Users          │
                    │                      │
                    │ Admin / College      │
                    │ HOD / Teacher        │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │   Spring Boot App    │
                    │                      │
                    │ Controllers          │
                    │ Services             │
                    │ Security             │
                    │ Repositories         │
                    └──────────┬───────────┘
                               │
                 ┌─────────────┴─────────────┐
                 ▼                           ▼
      ┌─────────────────────┐      ┌─────────────────────┐
      │       MySQL         │      │ Python AI Service   │
      │                     │      │                     │
      │ Colleges            │      │ Face Recognition    │
      │ Departments         │      │ OpenCV              │
      │ Teachers            │      │ MTCNN               │
      │ Students            │      │                     │
      │ Attendance          │      │                     │
      └─────────────────────┘      └─────────────────────┘
