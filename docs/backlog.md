# Automated Reverse Logistics Management Portal — Product Backlog

**Project:** Automated Reverse Logistics Management Portal  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Methodology:** Agile Scrum (3 Sprints, 15 Tasks)  
**Last Updated:** Sprint 2 (Task 7 Jenkins CI Job Completion)

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
| | **TASK-07** | Jenkins Installation & Continuous Integration Job | CI/CD | **DONE** | Configured Jenkins Job, Build Log, Polling Trigger, Archived WAR |
| | **TASK-08** | Unit Testing & Automated Test Execution in CI | Quality | Backlog | JUnit test suite & automated test execution |
| | **TASK-09** | Static Code Analysis & Linting | Quality | Backlog | Checkstyle / SpotBugs / SonarQube setup |
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

### Sprint 2: MVP Completion, Git Collaboration & CI Pipeline

#### TASK-06: MVP Completion and Git Collaboration — COMPLETED
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

#### TASK-07: Jenkins Installation and Continuous Integration Job — COMPLETED
* **User Story:** As a DevOps engineer, I want an automated Continuous Integration build job in Jenkins triggered by SCM polling or commit so that every change committed to the GitHub repository is automatically checked out, compiled, tested, packaged into an executable WAR, and archived as a release artifact.
* **Scope & Completed Capabilities:**
  1. **Jenkins Installation & Environment Configuration:**
     - Jenkins LTS (v2.580.1) installed and operational on designated CI port `http://localhost:8080/`.
     - Configured with `JENKINS_HOME=C:\jenkins\home`.
     - Essential CI plugins installed: `git`, `git-client`, `credentials`, `scm-api`, `timestamper`, `ws-cleanup`.
     - Security realm and admin access configured.
  2. **GitHub SCM Integration:**
     - Connected remote repository: `https://github.com/soham-Jain08/reverse-logistics-portal.git`.
     - Monitored release branch: `*/main`.
  3. **Scheduled Polling / Commit Trigger:**
     - Scheduled cron trigger configured: `H/5 * * * *` (polling GitHub every 5 minutes for new commits).
     - Automated change detection and SCM polling log verified.
  4. **Automated Build & Test Execution:**
     - Automated batch execution: `call mvnw.cmd clean package -DskipTests=false`.
     - Executes full Spring Boot lifecycle: resource filtering, compilation, unit/integration testing (100% pass), and WAR packaging.
  5. **Artifact Archiving:**
     - Build artifact pattern: `target/*.war`.
     - Successfully archived: `reverse-logistics-portal-0.0.1-SNAPSHOT.war` (61.79 MB).
* **Definition of Done (DoD) Checklist:**
  - [x] Jenkins server running on port `8080` with zero conflicts against dev server (`8081`).
  - [x] Jenkins job `reverse-logistics-portal-ci` created and connected to GitHub.
  - [x] Build trigger (SCM Polling `H/5 * * * *`) active with verified polling log.
  - [x] Clean build execution with all tests passing (`BUILD SUCCESS`).
  - [x] Build artifact `reverse-logistics-portal-0.0.1-SNAPSHOT.war` archived and downloadable.
  - [x] Comprehensive evidence documented in `docs/TASK7-DELIVERY.md`.

---

## 3. Upcoming Sprint Backlog (Tasks 8 to 15)

* **TASK-08 (Sprint 2):** Unit Testing & Automated Test Execution in CI (JUnit 5 + Mockito).
* **TASK-09 (Sprint 2):** Static Code Analysis & Linting (Checkstyle / SpotBugs / SonarQube).
* **TASK-10 (Sprint 2):** Automated Test Integration in CI Pipeline.
* **TASK-11 (Sprint 3):** Docker containerization (multi-stage build, minimal Alpine/Eclipse-Temurin JRE).
* **TASK-12 (Sprint 3):** Docker Compose multi-service deployment.
* **TASK-13 (Sprint 3):** Ansible playbook for automated provisioning and deployment.
* **TASK-14 (Sprint 3):** Selenium automated browser testing.
* **TASK-15 (Sprint 3):** CD Pipeline & Production Monitoring (`/actuator/health`, metrics, live demo).
