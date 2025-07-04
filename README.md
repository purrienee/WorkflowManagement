# Workflow Management System API

**Live API Endpoint:** [https://workflowmanagement-production.up.railway.app/](https://workflowmanagement-production.up.railway.app/)

A simple and robust RESTful API for managing workflows, built with Spring Boot and deployed on Railway. This API provides user authentication, hierarchical roles, and CRUD operations for workflows.

---

## Tech Stack
-   **Java 17**
-   **Spring Boot 3**
-   **Spring Security** & **JWT**
-   **JPA / Hibernate** & **MySQL**
-   **Maven**
-   **Railway** (Cloud Deployment)

---

## API Usage

The application is live and can be tested directly via its public endpoints. On startup, the API creates three default users with different roles.

### Test Credentials

| Role      | Username  | Password      |
| :-------- | :-------- | :------------ |
| Admin     | `admin`   | `adminpass`   |
| Manager   | `manager` | `managerpass` |
| Employee  | `aru`     | `aruraina`    |

## 📚 API Endpoints

| Module                  | Method | Endpoint                         | Description                                         | Authorization                             |
|-------------------------|--------|----------------------------------|-----------------------------------------------------|-------------------------------------------|
| **HomeController**      | GET    | `/`                              | Redirects to the login page                         | Public                                    |
| **DashboardController** | GET    | `/api/dashboard`                 | Fetch dashboard data for the logged-in user         | Authenticated                             |
| **LeaveRequestController** | POST | `/api/leave/apply`               | Apply for leave                                     | Authenticated                             |
| **LeaveRequestController** | GET  | `/api/leave/my-requests`         | Get all leave requests of the current user          | Authenticated                             |
| **LeaveRequestController** | GET  | `/api/leave/pending`             | Get all pending leave requests                      | Manager or Admin                          |
| **LeaveRequestController** | POST | `/api/leave/{id}/approve`        | Approve a leave request by ID                       | Manager or Admin                          |
| **LeaveRequestController** | POST | `/api/leave/{id}/reject`         | Reject a leave request by ID                        | Manager or Admin                          |
| **TaskController**      | POST   | `/api/tasks`                     | Assign a new task                                   | Manager                                   |
| **TaskController**      | GET    | `/api/tasks/my-tasks`            | Get tasks assigned to the current user              | Authenticated                             |
| **TaskController**      | PUT    | `/api/tasks/{taskId}/status`     | Update status of a task                             | Authenticated                             |
| **TaskController**      | GET    | `/api/tasks/{taskId}`            | Get task by ID (if assigned to/by user)             | Authenticated, PostAuthorize checks      |
| **UserController**      | GET    | `/api/users`                     | Get all users                                       | Admin                                     |
| **UserController**      | GET    | `/api/users/{userId}`            | Get user by ID                                      | Admin                                     |
| **UserController**      | POST   | `/api/users`                     | Create a new user                                   | Admin                                     |
| **UserController**      | PUT    | `/api/users/{userId}`            | Update a user by ID                                 | Admin                                     |
| **UserController**      | DELETE | `/api/users/{userId}`            | Delete a user by ID                                 | Admin                                     |
| **UserController**      | GET    | `/api/users/employees`           | Get all employees                                   | Admin or Manager                          |
| **UserController**      | GET    | `/api/users/managers`            | Get all managers                                    | Admin or Manager                          |
