# DevOps Playground - Task Manager API

Repository for learning and practicing DevOps tools and workflows.

## Task Manager API

A simple REST API for managing tasks (to-do list) built with Spring Boot and PostgreSQL.

### Features

- **CRUD Operations**: Create, Read, Update, Delete tasks
- **Postgres Database**: Production-ready database with Docker setup  
- **API Documentation**: Swagger/OpenAPI documentation
- **Testing**: Comprehensive unit and integration tests
- **Docker Support**: Containerized deployment

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/tasks` | Get all tasks |
| GET    | `/tasks/{id}` | Get task by ID |
| POST   | `/tasks` | Create new task |
| PUT    | `/tasks/{id}` | Update task |
| DELETE | `/tasks/{id}` | Delete task |

### Quick Start

#### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven 3.6+

#### 1. Clone the repository
```bash
git clone https://github.com/qusar01/devops-playground.git
cd devops-playground
```

#### 2. Start PostgreSQL database
```bash
docker compose up -d postgres
```

#### 3. Build and run the application
```bash
mvn clean package
java -jar target/task-manager-api-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`

#### 4. View API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

### Testing the API

#### Create a task
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "My First Task"}'
```

#### Get all tasks
```bash
curl -X GET http://localhost:8080/tasks
```

#### Update a task
```bash
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Updated Task", "done": true}'
```

#### Delete a task
```bash
curl -X DELETE http://localhost:8080/tasks/1
```

### Running Tests
```bash
mvn test
```

### Docker Deployment

#### Build and run with Docker Compose
```bash
# Build the application
mvn clean package

# Start both database and application
docker compose up --build
```

### Tech Stack

- **Backend**: Spring Boot 3.1.5
- **Database**: PostgreSQL 15
- **ORM**: Hibernate/JPA
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

### Project Structure
```
src/
├── main/java/com/devopsplayground/taskmanager/
│   ├── TaskManagerApiApplication.java
│   ├── config/
│   │   └── OpenApiConfig.java
│   ├── controller/
│   │   └── TaskController.java
│   ├── dto/
│   │   ├── TaskCreateRequest.java
│   │   └── TaskUpdateRequest.java
│   ├── model/
│   │   └── Task.java
│   ├── repository/
│   │   └── TaskRepository.java
│   └── service/
│       └── TaskService.java
└── test/java/com/devopsplayground/taskmanager/
    ├── controller/
    │   └── TaskControllerWebTest.java
    └── service/
        └── TaskServiceTest.java
```

### Development Notes

- Application uses PostgreSQL in production and H2 in-memory database for tests
- JPA will automatically create the `tasks` table on startup
- All endpoints include proper HTTP status codes and error handling
- API includes comprehensive validation and Swagger documentation
