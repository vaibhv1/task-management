# Task Management API

A Spring Boot REST API for managing tasks with pagination, filtering, and validation.

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Maven
- Jakarta Validation

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/tasks` | Create a task |
| `GET` | `/tasks/{id}` | Get task by ID |
| `PUT` | `/tasks/{id}` | Update a task |
| `DELETE` | `/tasks/{id}` | Delete a task |
| `GET` | `/tasks` | List all tasks (paginated) |

## Query Parameters (List endpoint)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `status` | PENDING, IN_PROGRESS, DONE | all | Filter by status |
| `page` | int | 0 | Page number |
| `size` | int | 10 | Page size |
| `direction` | asc, desc | asc | Sort order by due date |

## Request/Response Examples

### Create Task

```bash
POST /tasks
Content-Type: application/json

{
  "title": "Complete report",
  "description": "Q4 financial report",
  "status": "PENDING",
  "dueDate": "2026-05-30"
}
```

### List Tasks

```bash
GET /tasks?status=PENDING&page=0&size=10&direction=desc
```

## Running

```bash
mvn spring-boot:run
```

## Testing

```bash
mvn test
```