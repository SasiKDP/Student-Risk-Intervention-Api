# 🎓 Student Risk Assessment & Intervention Tracking System

A Spring Boot-powered REST API to assess student risk levels and manage intervention programs that support academic success.

---

## 📘 Project Description

This system identifies at-risk students based on academic, attendance, and behavioral data. It calculates a risk score and facilitates tracking of intervention programs to help improve outcomes for struggling students.

---

## 🎯 Risk Assessment Logic

Risk scores range from **0 to 100**, weighted as follows:

| Component     | Criteria                              | Points |
|---------------|----------------------------------------|--------|
| 🎓 Academic (40%) | Grades < 70% → +25<br>State Assessment < 500 → +15 | 40 |
| 📅 Attendance (30%) | Attendance rate < 90% → +20<br>Absent days > 10 → +10 | 30 |
| ⚠️ Behavior (20%) | Disciplinary actions > 2 → +15<br>Suspensions > 0 → +5 | 20 |
| ⏰ Tardiness (10%) | Tardy days > 5 → +10 | 10 |

### Risk Categories
- 🟥 **High Risk**: 70–100
- 🟧 **Medium Risk**: 40–69
- 🟩 **Low Risk**: 0–39

---

## 🛠️ Intervention Management

- ✅ Create and track interventions for at-risk students
- 📊 Progress tracked via: Start Score → Current Score → Goal Score
- 🟢 Students marked **On Track** if performance aligns with expectations
- 🔔 Notifications when intervention goals are met

---

## 🔐 Authentication & Access Control

- JWT-based token authentication
- Role-based access via Spring Security:

| Role     | Permissions                          |
|----------|--------------------------------------|
| ADMIN    | Full CRUD + assign interventions     |
| TEACHER  | Can create students & assign plans   |
| STUDENT  | View-only access to own profile      |

**Public Endpoints**:
- `/auth/login`
- `/student/register`

---


## 📚 API Modules

| Module                    | Purpose                                      |
|---------------------------|----------------------------------------------|
| `StudentController`       | Manage students (create, view, list)         |
| `AcademicPerformanceController` | Store grades & ELA/math assessments       |
| `AttendanceController`    | Track student attendance                     |
| `BehaviorController`      | Record behavioral incidents                  |
| `RiskAssessmentController`| Calculate and return risk scores             |
| `InterventionController`  | Manage interventions                         |
| `UserController`          | Register and list users                      |
| `LoginController`         | Login and JWT issuance                       |


---

## 🧪 Testing Strategy

### ✅ Unit Tests
- Isolated service logic using `Mockito`
- Example:
```java
@ExtendWith(MockitoExtension.class)
class RiskAssessmentServiceTest {
    @Test void calculateRiskScore_HighRiskStudent_ReturnsCorrectScore() { ... }
    @Test void identifyAtRiskStudents_WithFilters_ReturnsFilteredResults() { ... }
}
```

### 🔗 Integration Tests
- API tests using `MockMvc` and `@SpringBootTest`
- Secure endpoint tests with valid/invalid JWTs
- Response validation using `ApiResponse<T>`

---

## 📂 Project Structure

```
src/
├── controller/       # REST controllers
├── service/          # Business logic
├── repository/       # JPA repositories
├── model/            # Entity classes
├── dto/              # Request/response DTOs
├── config/           # Security & global configs
├── jwt/              # JWT utilities & filters
├── security/         # Access handlers
└── exception/        # Global exception handling
```

---

## 💻 Technologies Used

- Java 17+
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Docker
- Lombok
- JUnit 5, Mockito
- Swagger

---

## 🚀 Getting Started

```bash
# Clone the project
git clone https://github.com/SasiKDP/student-api.git
cd student-risk-api

# Set DB configuration in application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/studentdb
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

# Run the project
./mvnw spring-boot:run
```

---

🐳 Docker Deployment
```
# 1. Build Docker image
docker build -t student-risk-api .

# 2. Run application container
docker run --name student-app -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/studentdb \
  -e SPRING_DATASOURCE_USERNAME=your_db_user \
  -e SPRING_DATASOURCE_PASSWORD=your_db_password \
  student-risk-api
```

---


## API Documentation

### 🔍 Swagger UI
This project includes interactive API documentation powered by Swagger.

Once the application is running, you can access it at:

```
http://localhost:8080/swagger-ui/index.html
```

> Replace `localhost:8080` with your actual host and port if deployed elsewhere.

Swagger allows developers to explore endpoints, view request/response formats, and test APIs directly.

---

### 📬 Postman Collection

A fully tested Postman collection is available to interact with the Student Risk Assessment API.

👉 **[Open Postman Collection](https://dev-team-8647.postman.co/workspace/cd16ea6a-d7c9-47b4-89a2-436361078852/collection/39976997-dd1c0b51-b338-43a7-a120-a43c4043a7dd?action=share&source=copy-link&creator=39976997)**

To use:
1. Click the link above
2. Import the collection into your Postman workspace
3. Set the `authToken` (JWT) in the headers/cookies for protected endpoints

---

### 📎 Sample Endpoints

| Method | Endpoint                    | Description                                |
|--------|-----------------------------|--------------------------------------------|
| POST   | `/student/login`            | User login with JWT generation             |
| POST   | `/student/addStudent`       | Create a new student                       |
| GET    | `/student/risk/{studentId}` | Get risk assessment for a student          |
| GET    | `/student/at-risk`          | List of students identified as at-risk     |
| POST   | `/student/addPerformance`   | Add academic performance for a student     |
| POST   | `/student/createAttendance` | Record attendance                          |
| POST   | `/student/createBehavior`   | Record behavior metrics                    |
| POST   | `/student/interventions`    | Create new intervention                    |
| GET    | `/student/interventions`    | View interventions by student ID           |


> This API is built to empower educators with insights and tools to identify, support, and guide students toward better academic outcomes.