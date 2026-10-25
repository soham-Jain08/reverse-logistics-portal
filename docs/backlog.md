# Automated Reverse Logistics Management Portal — Product Backlog

**Project:** Automated Reverse Logistics Management Portal  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Methodology:** Agile Scrum (3 Sprints, 15 Tasks)  
**Last Updated:** Sprint 3 (Task 14 Automated Provisioning & Reliability Validation Completion)

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
| | **TASK-08** | Pipeline as Code & Server Deployment | CI/CD | **DONE** | Jenkinsfile, Pipeline Run Log, Tomcat Deployment on Port 8082 |
| | **TASK-09** | Selenium Test Design and Local Execution | Testing | **DONE** | Test Plan, Selenium Scripts, Local Test Report, Failure Screenshot Mechanism |
| | **TASK-10** | Continuous Testing in Jenkins | CI/CD | **DONE** | Jenkins Test Report, Failed Pipeline Evidence, Defect Correction Commit, Successful Rerun |
| **Sprint 3** | **TASK-11** | Containerization with Docker | Containers | **DONE** | Dockerfile, image details, Docker log, running container |
| | **TASK-12** | Jenkins-Docker Continuous Deployment | CI/CD & Containers | **DONE** | Versioned Image, Registry Evidence, End-to-End Commit-to-Container Pipeline |
| | **TASK-13** | Configuration Management with Ansible | IaC | **DONE** | Configuration Specification, Ansible Playbook, First Execution Log |
| | **TASK-14** | Automated Provisioning & Reliability Validation | IaC & Reliability | **DONE** | Provisioned Node, Idempotency Evidence, Health-Check Result, Rollback/Recovery Demonstration |
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

### Sprint 2: MVP Completion, Git Collaboration & CI/CD Pipelines

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

#### TASK-08: Pipeline as Code and Server Deployment — COMPLETED
* **User Story:** As a DevOps engineer, I want to define our entire CI/CD delivery lifecycle as code using a version-controlled `Jenkinsfile` with distinct stages (Checkout, Build & Test, Package, Deploy to Server), parameterize target environment configurations, and automatically deploy the packaged application to an Apache Tomcat server on port 8082 with automated post-deployment health verification.
* **Scope & Completed Capabilities:**
  1. **Pipeline as Code (`Jenkinsfile`):**
     - Declarative pipeline defined in repo root and version-controlled on GitHub.
     - 4 Core Stages: `Checkout`, `Build & Test`, `Package`, `Deploy to Server`.
  2. **Environment Parameterization:**
     - `DEPLOY_ENV`: Choice parameter (`production`, `staging`, `development`).
     - `TOMCAT_PORT`: Target server port (default `8082`).
     - `TOMCAT_WEBAPPS`: Filesystem target for WAR deployment (`C:\Users\CSC\tomcat\apache-tomcat-10.1.61\webapps`).
     - `CONTEXT_PATH`: Context path root (`reverse-logistics-portal`).
  3. **Server Deployment (Apache Tomcat on Port 8082):**
     - Apache Tomcat 10.1.61 installed and running on port `8082`.
     - `ReverseLogisticsPortalApplication` updated to extend `SpringBootServletInitializer` for container compatibility.
     - Automated hot deployment of `reverse-logistics-portal.war` to Tomcat webapps.
  4. **Verification & Pipeline Runs:**
     - Pipeline Build #1 (Production): **SUCCESS**.
     - Pipeline Build #2 (Parameterized Staging): **SUCCESS**.
     - Live application URL verified: `http://localhost:8082/reverse-logistics-portal/` (HTTP 200).
* **Definition of Done (DoD) Checklist:**
  - [x] `Jenkinsfile` created, committed, and pushed to GitHub repository root.
  - [x] Pipeline parameters configured and tested (`DEPLOY_ENV`, `TOMCAT_PORT`, `CONTEXT_PATH`).
  - [x] Apache Tomcat configured and verified running on port `8082`.
  - [x] Automated copy/deployment to Tomcat webapps executed within the pipeline.
  - [x] Post-deployment automated health check returns HTTP 200.
  - [x] Comprehensive evidence documented in `docs/TASK8-DELIVERY.md`.

#### TASK-09: Selenium Test Design and Local Execution — COMPLETED
* **User Story:** As a QA / DevOps engineer, I want an automated browser testing framework using Selenium WebDriver and JUnit 5 to identify and automate 5 critical user journeys, assert UI elements and business flows, capture full-page screenshots upon any failure, and execute cleanly within the local Maven lifecycle.
* **Scope & Completed Capabilities:**
  1. **Test Plan Design:** Complete test strategy identifying 5 critical user journeys across Customer, Manager, and Warehouse personas documented in `docs/TASK9-TEST-PLAN.md`.
  2. **Selenium WebDriver Automation Suite (`src/test/java/com/soham/reverselogistics/selenium/`):**
     - `CustomerReturnRequestJourneyTest.java` (Journey 1): Return request creation, dynamic input entry, and detail view verification.
     - `ManagerWorkflowJourneyTest.java` (Journey 2): Manager reviews return records, validates status badges, and inspects the 6-step progress stepper.
     - `ReturnsSearchAndFilterJourneyTest.java` (Journey 3): Keyword search (`q=ORD`), status filtering (`REQUESTED`), and CSV export link verification.
     - `DashboardNavigationJourneyTest.java` (Journey 4): Executive dashboard KPI cards verification and action queue navigation.
     - `SecurityAndValidationJourneyTest.java` (Journey 5A, 5B, 5C): Unauthenticated access redirect, invalid credentials alert banner, and form validation blocking empty submissions.
  3. **Failure Screenshot Mechanism:**
     - Implemented `ScreenshotExtension.java` (JUnit 5 `AfterTestExecutionCallback`) capturing full-page PNGs to `target/screenshots/FAILURE_*.png` on any assertion error or timeout.
     - Captured milestone journey evidence to `target/screenshots/JOURNEY_*.png`.
  4. **Local Execution & Reporting:**
     - Executed via `./mvnw.cmd test` — 9/9 tests passed with 0 failures and 0 errors (`BUILD SUCCESS`).
     - Surefire test reports generated in `target/surefire-reports/`.
* **Definition of Done (DoD) Checklist:**
  - [x] Test plan created with 5 critical user journeys (`docs/TASK9-TEST-PLAN.md`).
  - [x] Selenium WebDriver scripts implemented with assertions and test data.
  - [x] Failure screenshot mechanism implemented and verified (`target/screenshots/`).
  - [x] Local test suite executed through Maven with 100% pass rate.
  - [x] Local test delivery report documented (`docs/TASK9-DELIVERY.md`).
  - [x] Product backlog updated.

#### TASK-10: Continuous Testing in Jenkins — COMPLETED
* **User Story:** As a DevOps engineer, I want our Jenkins Declarative Pipeline to automatically execute the Selenium browser journey and unit test suites on every build, publish interactive JUnit test reports and failure screenshot artifacts, and enforce an automated Quality Gate that halts downstream packaging and server deployment upon any test failure.
* **Scope & Completed Capabilities:**
  1. **Continuous Testing Pipeline Integration:**
     - Updated `Jenkinsfile` with a dedicated `Continuous Testing` stage running `call mvnw.cmd test -Dapp.url=http://localhost:8081`.
     - Configured `junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'`.
     - Configured `archiveArtifacts allowEmptyArchive: true, artifacts: 'target/screenshots/**'`.
  2. **Deliberate Defect Introduction & Quality Gate Halt:**
     - Introduced an invalid status assertion in `CustomerReturnRequestJourneyTest.java` (commit `8618dba`).
     - Executed pipeline Build #4: 1 failure recorded in JUnit test report.
     - Quality gate immediately skipped `Package` and `Deploy to Server` stages, halting the deployment to protect the target server.
     - Failure screenshot captured and archived in Jenkins.
  3. **Defect Correction Commit:**
     - Restored valid status assertion in `CustomerReturnRequestJourneyTest.java` (commit `0481246`).
     - Hardened browser test driver with `PageLoadStrategy.EAGER` and native form submission (commit `753005b`).
  4. **Successful Pipeline Rerun & Server Deployment:**
     - Executed pipeline Build #6: 100% test pass rate (9/9 tests passed).
     - Released and packaged `reverse-logistics-portal-0.0.1-SNAPSHOT.war`.
     - Deployed to Apache Tomcat 10.1 on port `8082` (`/reverse-logistics-portal/`) with HTTP 200 health check verification.
* **Definition of Done (DoD) Checklist:**
  - [x] Selenium and unit tests integrated into Jenkins Declarative Pipeline.
  - [x] JUnit test report publishing and failure screenshot archiving configured.
  - [x] Quality Gate halts pipeline deployment when any test fails (verified in Build #4).
  - [x] Deliberate defect committed and verified halting deployment.
  - [x] Defect corrected via Git commit (`0481246`).
  - [x] Successful pipeline rerun (Build #6) passes 100% and deploys to Tomcat on port 8082.
  - [x] Full delivery evidence documented in `docs/TASK10-DELIVERY.md`.
  - [x] Product backlog updated.

---

### Sprint 3: Containerization, Orchestration & Production Readiness

#### TASK-11: Docker Image and Container Lifecycle — COMPLETED
* **User Story:** As a DevOps engineer, I want to containerize the Reverse Logistics Management Portal with a secure, lightweight Dockerfile, build and tag the production container image, map dedicated host ports, inspect runtime logs, and validate the complete container lifecycle (run, stop, start, restart, rm) with active healthchecks and live endpoint responses.
* **Scope & Completed Capabilities:**
  1. **Production Dockerfile & Optimization:**
     - Base image: `eclipse-temurin:17-jre-alpine` (lightweight 181.6 MB footprint).
     - Non-root user security policy: Dedicated user `appuser:appgroup`.
     - Signal forwarding: `exec java $JAVA_OPTS -jar app.war` for graceful SIGTERM (exit code 143) handling.
     - Embedded H2 database persistence directory pre-created at `/app/data/rlpdb`.
     - Integrated Docker `HEALTHCHECK` against Spring Boot Actuator (`/actuator/health`).
     - Added `.dockerignore` eliminating repository build clutter.
  2. **Image Building & Tagging:**
     - Tagged versioned image `reverse-logistics-portal:v1.0.0` and `reverse-logistics-portal:latest`.
     - Total unpacked image size: 181.6 MB.
     - Extracted full image metadata using `docker image inspect`.
  3. **Port Mapping & Container Deployment:**
     - Container `rlp-container` deployed with host port mapping `8083:8080`.
     - Zero collision with Jenkins (`8080`), local dev (`8081`), and Tomcat (`8082`).
  4. **Live Verification & Health Monitoring:**
     - Container status: `Up (healthy)`.
     - Actuator health check response: `{"groups":["liveness","readiness"],"status":"UP"}`.
     - Application login page response: HTTP 200 OK (`Sign in | ReturnHub`).
  5. **Complete Lifecycle Transition Verification:**
     - `docker run`: Container booted and assigned PID 1.
     - `docker inspect`: Extracted runtime state and healthcheck records.
     - `docker logs`: Inspected Spring Boot 4.1.1 and HikariCP database connection logs.
     - `docker stop`: Verified graceful shutdown with exit code `143` (`SIGTERM`).
     - `docker start`: Brought container back to healthy state in 3 seconds.
     - `docker restart`: Verified clean service restart.
     - `docker rm`: Created and removed test container `rlp-temp-test` to validate decommissioning.
* **Definition of Done (DoD) Checklist:**
  - [x] Dockerfile authored with Alpine JRE 17, non-root user, and healthcheck.
  - [x] .dockerignore authored.
  - [x] Image built and tagged (`v1.0.0`, `latest`).
  - [x] Image metadata inspected and documented.
  - [x] Container deployed with port mapping `8083:8080`.
  - [x] Container logs inspected and verified.
  - [x] Live HTTP endpoints verified (HTTP 200, Actuator `UP`).
  - [x] Full container lifecycle validated (`run`, `stop`, `start`, `restart`, `rm`).
  - [x] Full delivery evidence documented in `docs/TASK11-DELIVERY.md`.
  - [x] Product backlog updated.

---

#### TASK-12: Jenkins-Docker Continuous Deployment — COMPLETED
* **User Story:** As a DevOps engineer, I want to extend the Jenkins CI/CD pipeline to automatically execute our automated testing suite, build a versioned Docker image, publish it to a local Docker registry, and deploy a fresh container with post-deployment health verification so that we achieve a zero-touch, commit-to-container continuous deployment lifecycle.
* **Scope & Completed Capabilities:**
  1. **Pipeline as Code Extension (`Jenkinsfile`):**
     - Parameterized settings: `DEPLOY_ENV`, `DOCKER_REGISTRY`, `IMAGE_NAME`, `CONTAINER_NAME`, `HOST_PORT`, `APP_TEST_URL`.
     - Structured 7-stage Declarative Pipeline with clean stage separation.
  2. **Continuous Testing Quality Gate:**
     - Executes all 9 Selenium WebDriver user journeys and Spring Boot unit tests before any packaging or deployment.
     - Automated halting on test failure to protect the target production environment.
  3. **Release Packaging & Fingerprinting:**
     - Packages release-ready executable WAR archive `reverse-logistics-portal-0.0.1-SNAPSHOT.war` (51.8 MB).
     - Fingerprints and archives build artifact in Jenkins.
  4. **Versioned Image Building & Tagging:**
     - Builds lightweight image tagged dynamically with immutable build number (`localhost:5000/reverse-logistics-portal:build-11`) and floating `:latest` tag.
     - Unpacked image size: 182 MB.
  5. **Docker Registry Publication (`localhost:5000`):**
     - Pushes all image layers to active Docker Registry container (`registry:2`).
     - Registry V2 API validation: `http://localhost:5000/v2/_catalog` and `/v2/reverse-logistics-portal/tags/list`.
  6. **Automated Container Deployment:**
     - Deploys fresh container `rlp-production-container` on host port `8083`.
     - Zero collision with Jenkins (`8080`), local dev (`8081`), and Tomcat (`8082`).
  7. **Post-Deployment Health Verification:**
     - Automatic polling of `/actuator/health` (returns `{"groups":["liveness","readiness"],"status":"UP"}`).
     - Live authentication endpoint verification (HTTP STATUS: 200).
* **Definition of Done (DoD) Checklist:**
  - [x] Jenkinsfile updated with versioned image build, registry publish, and automated container deployment.
  - [x] Parameterized environment settings configured.
  - [x] Continuous Testing Quality Gate enforces 100% test pass before deployment.
  - [x] Versioned Docker image built and tagged (`build-11`, `latest`).
  - [x] Versioned image pushed to local Docker registry on port 5000.
  - [x] Registry catalog and tags API evidence verified.
  - [x] Fresh container deployed (`rlp-production-container` on port 8083).
  - [x] Post-deployment health verification passes (`UP`, HTTP 200).
  - [x] End-to-end commit-to-container pipeline executed with 100% SUCCESS in Build #11.
  - [x] Full delivery report documented in `docs/TASK12-DELIVERY.md`.
  - [x] Product backlog updated.

---

#### TASK-13: Configuration Management Script — COMPLETED
* **User Story:** As a DevOps engineer, I want to identify all server prerequisites (packages, users, directories, files, ports, and services) and codify target node provisioning using Ansible playbooks, roles, and Jinja2 templates so that we achieve consistent, reproducible, secure, and idempotent automated deployment across environments.
* **Scope & Completed Capabilities:**
  1. **Server Prerequisites Specification:**
     - Packages: `openjdk-17-jre-headless`, `curl`, `wget`, `net-tools`, `procps`, `logrotate`, `ufw`, `ca-certificates`, `unzip`.
     - Users & Groups: Non-login system user `rlpuser` (UID 1500) and group `rlpgroup` (GID 1500) enforcing Principle of Least Privilege.
     - Directories: Standardized FHS layout (`/opt/reverse-logistics-portal/bin`, `/etc/reverse-logistics-portal`, `/var/lib/reverse-logistics-portal/data`, `/var/log/reverse-logistics-portal`, `/run/reverse-logistics-portal`).
     - Configuration Files: Externalized `application.properties` (0640) and `/etc/default/reverse-logistics-portal` (0640) templated with Jinja2.
     - Service Unit: Systemd unit `/etc/systemd/system/reverse-logistics-portal.service` (0644) with auto-restart (`Restart=always`, `RestartSec=10`) and `LimitNOFILE=65536`.
     - Log Retention: `/etc/logrotate.d/reverse-logistics-portal` with 14-day rotation and `copytruncate`.
     - Firewall: Dedicated TCP port `8084` allowed via UFW.
  2. **Modular Ansible Role Architecture:**
     - Created `reverse-logistics-portal/ansible/` structure with `ansible.cfg`, `inventory/hosts.ini`, `group_vars/all.yml`, and `playbooks/site.yml`.
     - Role `reverse_logistics_portal` organized into 9 modular task files (`01_packages.yml` through `09_healthcheck.yml`), handlers, vars, defaults, and templates.
  3. **First Execution & Health Verification:**
     - Executed against target node (`localhost` via WSL2 Linux Ubuntu 26.04 LTS).
     - First run result: 27 tasks executed with `ok=27, changed=12, failed=0`.
     - Verified Spring Boot Actuator endpoint (`/actuator/health`) reporting `UP` (`components: {db: {status: UP, database: H2}, diskSpace: {status: UP}}`).
     - Verified HTTP 200 OK on web login portal (`http://localhost:8084/login`).
  4. **Idempotency & Resilience Validation:**
     - Re-executed complete playbook against already configured node: verified `ok=26, changed=0, failed=0` (100% idempotent).
* **Definition of Done (DoD) Checklist:**
  - [x] Server prerequisites specification documented (packages, users, folders, files, ports, services).
  - [x] Ansible project structure created (`ansible.cfg`, `inventory/`, `roles/`, `playbooks/`, `templates/`).
  - [x] Automated role tasks configured for package installation, user/group management, directory creation, templating, systemd service setup, log rotation, firewall rules, and health checks.
  - [x] Jinja2 templates created for `application.properties`, environment defaults, systemd service, and logrotate.
  - [x] First execution completed successfully (`ok=27, changed=12, failed=0`).
  - [x] Service running on dedicated port 8084 and Spring Boot Actuator reporting `UP`.
  - [x] Playbook idempotency verified (`ok=26, changed=0, failed=0`).
  - [x] Comprehensive delivery report documented in `docs/TASK13-DELIVERY.md`.
  - [x] Product backlog updated.

---

#### TASK-14: Automated Provisioning and Reliability Validation — COMPLETED
* **User Story:** As a DevOps engineer, I want to provision a clean target environment using Ansible, deploy the release artifact through atomic symlinks, prove playbook idempotency on subsequent runs, perform automated healthcheck assertions against Actuator and web endpoints, and validate both operator-driven rollback and autonomous self-healing recovery so that the application achieves enterprise reliability and resilience.
* **Scope & Completed Capabilities:**
  1. **Release Architecture & Clean Provisioning:**
     - Provisioned target node (`localhost` on Ubuntu 26.04 LTS x86_64) using `playbooks/provision_clean.yml`.
     - Deployed release `v1.0.0` with standard FHS layout, versioned releases directory (`/opt/reverse-logistics-portal/releases/v1.0.0`), and atomic symlinks (`current`, `previous`, and `bin/reverse-logistics-portal.war`).
     - Systemd service `reverse-logistics-portal.service` bound to port `8084` with `RuntimeDirectory` isolation.
  2. **Pure Idempotency Validation:**
     - Executed a clean second run of `playbooks/provision_clean.yml` on the provisioned node.
     - Result: `ok=33, changed=0, failed=0, skipped=4, rescued=0`.
     - Zero unintended modifications, zero task failures, zero unnecessary service restarts.
  3. **Automated Health Probing:**
     - Spring Boot Actuator probe (`/actuator/health`): validated status `UP`, DB `UP` (H2), and diskSpace `UP`.
     - Web Portal probe (`/login`): confirmed HTTP 200 OK.
  4. **Rollback & Autonomous Recovery Demonstration:**
     - Executed 4-stage validation suite via `playbooks/test_reliability_recovery.yml`.
     - **Stage 1 (Baseline):** Verified stable release `v1.0.0` is UP.
     - **Stage 2 (Version Upgrade):** Deployed release `v1.1.0` and verified health.
     - **Stage 3 (Operator Rollback):** Atomically rolled back to previous stable release `v1.0.0`, restarted service, and verified health recovery.
     - **Stage 4 (Autonomous Self-Healing):** Staged corrupted non-bootable candidate `v1.2.0-canary`; trapped healthcheck failure via `block/rescue`, automatically reverted symlink to `v1.0.0`, restarted service, verified health recovery (`ok=38, changed=16, failed=0, rescued=1`), and recorded audit log in `/var/log/reverse-logistics-portal/rollback.log`.
* **Definition of Done (DoD) Checklist:**
  - [x] Clean target environment provisioned using Ansible.
  - [x] Application deployed using versioned release structure and atomic symlinks.
  - [x] Playbook idempotency proven with `changed=0, failed=0`.
  - [x] Live health check verified via Actuator (`UP`) and login endpoint (HTTP 200).
  - [x] Operator-driven rollback tested and verified.
  - [x] Autonomous recovery / self-healing tested and verified via `block/rescue`.
  - [x] Rollback audit log generated and verified (`rollback.log`).
  - [x] Full delivery report documented in `docs/TASK14-DELIVERY.md`.
  - [x] Product backlog updated.

---

## 3. Upcoming Sprint Backlog (Task 15)

* **TASK-15 (Sprint 3):** Continuous Deployment, Monitoring, Live Demo & Final Project Release.
