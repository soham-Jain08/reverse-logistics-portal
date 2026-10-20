# Automated Reverse Logistics Management Portal — Product Backlog

**Project:** Automated Reverse Logistics Management Portal  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Methodology:** Agile Scrum (3 Sprints, 15 Tasks)  
**Last Updated:** Sprint 2 (Task 6 MVP Completion)

---

## 1. 15-Task Roadmap Overview

| Sprint | Task ID | Task Title | Type | Status | Deliverable |
|:---|:---|:---|:---|:---|:---|
| **Sprint 1** | **TASK-01** | Problem Definition and Scope | Planning | **DONE** | Scope, Stakeholder List, Objectives |
| | **TASK-02** | Agile Planning & DevOps Lifecycle | Agile/Scrum | **DONE** | Backlog, DoD, DevOps Diagram |
| | **TASK-03** | Requirements, Architecture & Tech Setup | Architecture | **DONE** | SRS, Use-case & Architecture Diagrams, Skeleton |
| | **TASK-04** | Git & GitHub Repository Initialization | SCM | **DONE** | GitHub Repo, README, Templates, Policies |
| | **TASK-05** | Feature Development with Branching | Feature | **DONE** | Feature branch 1, PR, Merge into `develop` |
| **Sprint 2** | **TASK-06** | MVP Completion & Git Collaboration | Integration | **DONE** | Functional MVP, Conflict Resolution, Tagging |
| | **TASK-07** | Unit Testing and Test Automation | Quality | Backlog | JUnit test suite & coverage report |
| | **TASK-08** | Static Code Analysis & Linting | Quality | Backlog | Checkstyle / SpotBugs / SonarQube setup |
| | **TASK-09** | Continuous Integration with Jenkins | CI/CD | Backlog | Jenkinsfile, Automated build pipeline |
| | **TASK-10** | Automated Test Integration in CI | CI/CD | Backlog | Automated test stage in Jenkins |
| **Sprint 3** | **TASK-11** | Containerization with Docker | Containers | Backlog | Dockerfile, Containerized app & test |
| | **TASK-12** | Multi-Container Orchestration (Docker Compose) | Ops | Backlog | docker-compose.yml, App + DB service |
| | **TASK-13** | Configuration Management with Ansible | IaC | Backlog | Ansible playbook, automated provisioning |
| | **TASK-14** | Automated End-to-End Testing (Selenium) | Testing | Backlog | Selenium test automation suite |
| | **TASK-15** | Continuous Deployment and Monitoring | Operations | Backlog | CD deployment, Health monitoring & final demo |

---

## 2. Sprint 1 & 2 Detailed User Stories & Done Criteria

### Sprint 1: Foundation & Initial Feature (Tasks 1 to 5) — COMPLETED
* **TASK-01: Problem Statement & Scope**
  - *User Story:* As a business stakeholder, I want to clearly define the problem of manual reverse logistics so that development focuses on reducing return cycle times and tracking returned merchandise.
  - *Acceptance Criteria:* Documented pain points, measurable success criteria, 15-task scope frozen.
  - *Status:* **COMPLETED**.
* **TASK-02: Agile Planning & DevOps Workflow**
  - *User Story:* As a Scrum Master / DevOps Engineer, I want an agile task board and a DevOps pipeline blueprint so that our team follows an automated continuous delivery lifecycle.
  - *Acceptance Criteria:* User stories with acceptance criteria, Definition of Done, Scrum plan, DevOps diagram.
  - *Status:* **COMPLETED**.
* **TASK-03: Requirements, Architecture and Tech Setup**
  - *User Story:* As a system architect, I want an SRS summary and architectural models for Java 17 / Spring Boot / Maven / H2.
  - *Acceptance Criteria:* Architecture diagram, data model, use cases, working starter app.
  - *Status:* **COMPLETED**.
* **TASK-04: Git and GitHub Repository Initialization**
  - *User Story:* As a developer, I want a Git repository with branching policies and issue templates to collaborate systematically.
  - *Acceptance Criteria:* GitHub repository initialized, branch naming conventions, PR templates, .gitignore.
  - *Status:* **COMPLETED**.
* **TASK-05: Feature Development with Branching (Feature 1)**
  - *User Story:* As a customer, I want to submit a basic return request so that my return is recorded in the system.
  - *Acceptance Criteria:* Feature branch `feature/create-return-request` created, entity and service built, PR raised and reviewed.
  - *Status:* **COMPLETED**.

---

### Sprint 2: MVP Completion & Git Collaboration (Task 6) — COMPLETED

#### TASK-06: MVP Completion and Git Collaboration
* **User Story:** As an operations manager and customer, I want a fully functioning Reverse Logistics MVP with complete CRUD, role-based workflows, search filters, and an operational summary dashboard, developed using DevOps Git collaboration practices.
* **Scope & Completed Capabilities:**
  1. **Create Record:** Customer creates returns with order number, product, quantity, category, reason, pickup address (`/returns/new`).
  2. **View Record:** Detailed inspection view with interactive 6-step progress stepper, status badges, and complete audit trail (`/returns/{id}`).
  3. **Update Record:**
     - Customers can edit pending requests before approval (`/returns/{id}/edit`).
     - Staff can update internal remarks, inspection findings, and refund amounts (`/returns/{id}/notes`).
     - State machine transitions for Return Manager (`APPROVE`, `SCHEDULE`, `REFUND`, `REJECT`) and Warehouse Staff (`RECEIVE`, `INSPECT`, `RESTOCK`, `DISPOSE`).
  4. **Search & Filter Records:**
     - Search by order number, customer name, or product keyword.
     - Filter by 9 statuses (`REQUESTED`, `APPROVED`, `PICKUP_SCHEDULED`, `RECEIVED`, `INSPECTED`, `REFUNDED`, `RESTOCKED`, `DISPOSED`, `REJECTED`).
     - Filter by reason category.
     - One-click CSV Export with formula injection sanitization.
  5. **Summary Dashboard:**
     - KPI cards: Total returns, Active/Pending action counter, Courier pickups in transit, Total refunds issued (`₹`).
     - Role-based action queues (Awaiting Approval, Expected Deliveries, Inspection Queue).
     - Lifecycle stage distribution pipeline.
     - Recent return requests activity table.
  6. **DevOps Git Collaboration:**
     - Second feature branch integration (`feature/returns-management` and `feature/security-and-ui`).
     - Realistic merge conflict deliberately created and cleanly resolved in layouts, templates, and application properties.
     - Full integration on `develop`.
     - Fast-forward / merge into `main` as the stable release baseline.
     - Git version tagging: `v1.0.0-mvp`.
* **Definition of Done (DoD) Checklist:**
  - [x] Code implements all MVP user stories and acceptance criteria.
  - [x] Merge conflicts between feature branches resolved and committed to Git history.
  - [x] All unit and integration tests compile and pass (`BUILD SUCCESS`).
  - [x] Role-based access control tested for Customer, Manager, and Warehouse.
  - [x] Tagged release version created (`v1.0.0-mvp`).
  - [x] Product backlog updated.

---

## 3. Upcoming Sprint Backlog (Tasks 7 to 15)

* **TASK-07 (Sprint 2):** Unit Testing and Test Automation (JUnit 5 + Mockito).
* **TASK-08 (Sprint 2):** Static Code Analysis & Linting (Checkstyle / SpotBugs / SonarQube).
* **TASK-09 (Sprint 2):** Jenkins CI Pipeline setup with declarative `Jenkinsfile`.
* **TASK-10 (Sprint 2):** Automated test reporting in Jenkins.
* **TASK-11 (Sprint 3):** Docker containerization (multi-stage build, minimal Alpine/Eclipse-Temurin JRE).
* **TASK-12 (Sprint 3):** Docker Compose multi-service deployment.
* **TASK-13 (Sprint 3):** Ansible playbook for automated provisioning and deployment.
* **TASK-14 (Sprint 3):** Selenium automated browser testing.
* **TASK-15 (Sprint 3):** CD Pipeline & Production Monitoring (`/actuator/health`, metrics, live demo).
