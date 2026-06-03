\# Hospital Management System (HMS)

A desktop-based Hospital Management System built in Java with JavaFX and PostgreSQL. Designed to automate core hospital operations including patient registration, appointment scheduling, laboratory management, pharmacy, and billing.

Developed as a group project for CEF446 — Object Oriented Programming, Department of Computer Engineering, Faculty of Engineering and Technology, University of Buea.

---

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Usage](#usage)
- [Contributors](#contributors)
- [Academic Context](#academic-context)

---

## Features
- Patient registration and profile management
- Appointment scheduling and doctor assignment
- Electronic medical records management
- Laboratory test ordering and result tracking
- Prescription creation and medication dispensing
- Invoice generation and payment processing
- Role-based access control for all staff types
- Bed and theatre allocation
- External referral management
- Financial report generation

---

## Tech Stack
- **Language:** Java 26
- **GUI Framework:** JavaFX 26
- **Database:** PostgreSQL (local)
- **Build Tool:** Maven
- **IDE:** Visual Studio Code
- **Version Control:** Git & GitHub

## Project Structure
hospital-management-system/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hms/
│   │   │       ├── Main.java                  # JavaFX entry point
│   │   │       ├── controllers/               # JavaFX screen controllers
│   │   │       ├── database/                  # PostgreSQL connection
│   │   │       ├── models/                    # Java classes (Patient, Doctor, etc.)
│   │   │       ├── services/                  # Business logic layer
│   │   │       ├── test/                      # System test program
│   │   │       └── utils/                     # IDGenerator, PasswordHasher
│   │   └── resources/
│   │       └── com/hms/
│   │           ├── fxml/                      # JavaFX UI layout files
│   │           ├── images/                    # Logo and icons
│   │           └── styles/                    # CSS stylesheets
│
├── database.sql                               # Full PostgreSQL schema
├── pom.xml                                    # Maven configuration
└── README.md

---

## Prerequisites

Make sure the following are installed before running the project:

- **JDK 26** — [Download from Oracle](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.9+** — [Download from Apache](https://maven.apache.org/download.cgi)
- **JavaFX 26 SDK** — [Download from Gluon](https://gluonhq.com/products/javafx/)
- **PostgreSQL** — [Download here](https://www.postgresql.org/download/)
- **Visual Studio Code** — [Download here](https://code.visualstudio.com/)
- **Git** — [Download here](https://git-scm.com/)

---

## VS Code Setup

1. Install the **Extension Pack for Java** by Microsoft from the VS Code Extensions marketplace — this installs Maven support, IntelliSense, and the Java debugger in one step.

2. Add Maven to your system PATH:
   - Extract Maven to a permanent folder e.g. `C:\apache-maven-3.9.15`
   - Add `C:\apache-maven-3.9.15\bin` to your system environment variables under `Path`
   - Verify by running `mvn -version` in the terminal

3. Add JavaFX to your system:
   - Extract the JavaFX SDK to a permanent folder e.g. `C:\javafx-sdk-26`
   - In `.vscode/launch.json`, set the `vmArgs` to point to your JavaFX lib folder:
```json
     "--module-path C:\\javafx-sdk-26\\lib --add-modules javafx.controls,javafx.fxml"
```

4. Verify Java is working:
```bash
   java -version
   mvn -version
```

---

## Installation

1. Clone the repository:
```bash
   git clone https://github.com/YOUR_USERNAME/hospital-management-system.git
```

2. Open the project in VS Code:
```bash
   cd hospital-management-system
   code .
```

3. Let Maven download all dependencies automatically. If prompted, click **Load Maven Project** in VS Code.

---

## Database Setup

1. Install PostgreSQL and ensure it is running locally.

2. Create the database:
```bash
   psql -U postgres -c "CREATE DATABASE hms;"
```

3. Run the schema file to create all tables:
```bash
   psql -U postgres -d hms -f dbschema.sql
```

4. Update the database credentials in `src/main/java/com/hms/database/DatabaseConnection.java`:
```java
   private static final String URL      = "jdbc:postgresql://localhost:5432/hms";
   private static final String USER     = "postgres";
   private static final String PASSWORD = "your_password_here";
```

---

## Running the Application

To launch the HMS application:
```bash
mvn clean javafx:run
```

To run the backend system tests:
```bash
mvn exec:java -Dexec.mainClass="com.hms.test.Main"
```

---

## Usage

On launch you will be prompted to log in with a Staff ID and password. Each role sees only the modules relevant to their function.

| Role | Staff ID Format | Example |
|------|----------------|---------|
| Admin | HMSA + 3 digits | HMSA001 |
| Doctor | HMSD + 3 digits | HMSD001 |
| Nurse | HMSN + 3 digits | HMSN001 |
| Receptionist | HMSR + 3 digits | HMSR001 |
| Pharmacist | HMSP + 3 digits | HMSP001 |
| Lab Technician | HMSL + 3 digits | HMSL001 |

> Staff accounts are created by the Admin. Patients do not log into the system — their information is managed by hospital staff on their behalf.

### Role Capabilities

| Module | Admin | Doctor | Receptionist | Nurse | Pharmacist | Lab Tech |
|--------|-------|--------|--------------|-------|------------|---------|
| User Management | ✅ | | | | | |
| Patient Registration | | | ✅ | | | |
| Appointments | | ✅ | ✅ | | | |
| Medical Records | | ✅ | | ✅ | | |
| Lab Orders | | ✅ | | | | ✅ |
| Prescriptions | | ✅ | | | ✅ | |
| Billing & Payments | | | ✅ | | | |
| Beds & Theatres | ✅ | | | | | |
| Schedules | ✅ | | | | | |
| Reports | ✅ | | | | | |
| Referrals | | ✅ | | | | |

---

## Contributors

| Name | Matricule |
|------|-----------|
| Anyangwe Akwi Okawa | FE24A216 |
| Righteousness Ntonwi Ajuyo | FE24A361 |
| Tamokoue Fogue Landry Berthold | FE24A625 |
| Atemkeng Prince Lovet | FE24A230 |
| Hamasali Alim | FE24A289 |
| Eyong Ngoe Willy Kertis | FE24A274 |
| Nkemnkeng Blessing Ntsafac | FE24A346 |
| Wemnje Caleb Mbanji Wepnyu | FE24A388 |
| Cho Success Mbo | FE24A245 |
| Ndzo Ngum Joachim | FE24A331 |

---

## Academic Context

- **Course:** CEF446 — Object Oriented Programming (Java/C++)
- **Instructor:** Dr. Djouela Ines
- **Institution:** University of Buea
- **Faculty:** Faculty of Engineering and Technology
- **Department:** Department of Computer Engineering
---

## Project Structure
