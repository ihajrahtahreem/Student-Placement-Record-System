# Student Placement Record System

A desktop-based **Student Placement Record System** developed using **Java Swing**, **MySQL**, and **JDBC** to automate and manage placement activities within an institution.

---

## Features

### Student Module
- Student Login Authentication
- View Eligible Jobs based on GPA
- Apply for Jobs
- View Application Status
- Restrict job applications for placed students

### Admin Module
- Admin Login Authentication
- View All Students
- View Job Postings
- View Applications
- Update Application Status
- View Placement Statistics
- Export Placement Report

### Additional Features
- Placement Statistics Graph
- Resume Path Management
- Placement Report Generation
- GUI-based Desktop Application

---

## Technology Stack

| Component | Technology |
|---|---|
| Frontend | Java Swing |
| Backend | Java |
| Database | MySQL |
| Connectivity | JDBC |
| IDE | VS Code / IntelliJ |
| Database Tool | MySQL Workbench |

---

## System Architecture

```text
Java Swing GUI
       ↓
Java Application Logic
       ↓
JDBC Connectivity
       ↓
MySQL Database
```

---

## Database Tables

### 1. Students
Stores student information:
- student_id
- name
- department
- gpa
- skills
- email
- resume_path
- status
- password

### 2. Job_Postings
Stores company job details:
- job_id
- company_name
- job_title
- min_cgpa
- salary_package
- required_skills

### 3. Applications
Stores application records:
- application_id
- student_id
- job_id
- status
- applied_date

---

## How to Run the Project

### Step 1: Clone Repository

```bash
git clone <your-github-repo-link>
cd Student-Placement-Record-System
```

### Step 2: Configure MySQL Database

Create a database named:

```sql
CREATE DATABASE college;
```

Import/create the required tables:
- students
- job_postings
- applications

---

### Step 3: Add MySQL JDBC Connector

Download MySQL Connector JAR and place it inside the project folder.

Example:

```text
mysql-connector-j-9.6.0.jar
```

---

### Step 4: Compile the Project

```bash
javac *.java
```

---

### Step 5: Run the Application

```bash
java -cp ".:mysql-connector-j-9.6.0.jar" PlacementGUI
```

---


### Login Interface
- Student Login
- Admin Login

### Student Dashboard
- View Jobs
- Apply for Jobs
- View Applications

### Admin Dashboard
- Manage Students and Applications
- Placement Statistics Graph

---

## Future Enhancements

- Web-based deployment
- Resume Upload Feature
- Email Notifications
- Recruiter Portal
- AI-based Job Recommendations
- Secure Password Encryption

---

## Learning Outcomes

Through this project, concepts learned include:
- Java Swing GUI Development
- Database Connectivity using JDBC
- CRUD Operations
- MySQL Database Design
- Data Visualization
- Real-world Placement Workflow Automation

---

## Developed By

**Hajrah Tahreem**  
Muffakham Jah College of Engineering & Technology

---

## License

This project is developed for educational purposes.
