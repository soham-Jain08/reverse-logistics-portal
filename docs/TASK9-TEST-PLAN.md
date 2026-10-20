# Task 9 — Selenium WebDriver Test Plan: Critical User Journeys & Local Execution

**Project:** Automated Reverse Logistics Management Portal (`ReturnHub`)  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Deliverable:** Task 9 — Test Plan, Selenium Scripts, Local Test Report & Failure Screenshot Mechanism  
**Date:** October 2026  

---

## 1. Objectives and Scope

The objective of Task 9 is to implement an automated End-to-End (E2E) functional testing framework using **Selenium WebDriver** and **JUnit 5** to validate the core user journeys of the Reverse Logistics Management Portal.

### Primary Goals:
1. **Critical User Journey Coverage:** Automate 5 distinct, end-to-end user workflows spanning Customer, Return Manager, and Warehouse personas.
2. **Deterministic Assertions & Test Data:** Ensure each test executes with realistic test datasets and precise UI/URL assertions.
3. **Automated Failure Screenshot Mechanism:** Integrate a JUnit 5 lifecycle extension that automatically captures full-page browser screenshots whenever an assertion fails or an exception occurs.
4. **Local Maven Build Execution:** Enable one-touch headless test execution via `mvn test`, integrating seamlessly into the project's Continuous Integration lifecycle.

---

## 2. Test Environment & Architecture

| Component | Specification / Tool | Version |
|:---|:---|:---|
| **Programming Language** | Java (JDK) | OpenJDK 26 / 17 compatible |
| **Automation Framework** | Selenium WebDriver (`selenium-java`) | 4.25.0 |
| **Testing Framework** | JUnit Jupiter (JUnit 5) | 5.11.x |
| **Browser & Engine** | Google Chrome (Headless mode) / Selenium Manager | Chrome 155 |
| **Target Application Host** | Spring Boot Portal (`ReturnHub`) | `http://localhost:8081` |
| **Build & Execution Tool** | Apache Maven | Maven Wrapper (`./mvnw`) |
| **Reporting Engine** | Maven Surefire Plugin | 3.5.6 |

### System Under Test Architecture:
```
+-------------------------------------------------------------+
|                      Maven Surefire                         |
|                       `mvn test`                            |
+------------------------------+------------------------------+
                               |
                               v
            +-------------------------------------+
            |        JUnit 5 Test Engine          |
            |   (@ExtendWith(ScreenshotExtension)) |
            +------------------+------------------+
                               |
                               v
            +-------------------------------------+
            |     BaseSeleniumTest & DriverFactory|
            |     (Headless ChromeDriver 155)     |
            +------------------+------------------+
                               |
                               v
            +-------------------------------------+
            | Reverse Logistics Portal (Port 8081)|
            |  Spring Security + Thymeleaf + H2   |
            +-------------------------------------+
                               |
               [On Assertion Error / Exception]
                               v
            +-------------------------------------+
            | target/screenshots/FAILURE_*.png    |
            +-------------------------------------+
```

---

## 3. Five Critical User Journeys Identified

### Journey 1: Customer Return Request Lifecycle
* **Test Class:** `CustomerReturnRequestJourneyTest.java`
* **Persona:** Customer (`customer1` / `Customer@123`)
* **Objective:** Verify that a registered customer can log in, navigate to the return initiation form (`/returns/new`), submit a complete return request with valid details, and verify that the request is created in `REQUESTED` status with a generated tracking ID.
* **Test Steps:**
  1. Authenticate as Customer via `/login`.
  2. Navigate to Return Request Form (`/returns/new`).
  3. Enter order number (`ORD-SEL-XXXXX`), customer name, product, quantity, category (`DEFECTIVE`), description, and pickup address.
  4. Submit form via POST.
  5. Verify redirect to Return Details page (`/returns/{id}`).
  6. Assert order number, product name, and status badge `Requested` are displayed.
* **Evidence:** Saved as `target/screenshots/JOURNEY_Journey1_ReturnCreated_*.png`.

---

### Journey 2: Return Manager Inspection & Workflow Stepper
* **Test Class:** `ManagerWorkflowJourneyTest.java`
* **Persona:** Return Manager (`manager1` / `Manager@123`)
* **Objective:** Validate that an operations manager can log in, access the complete return repository (`/returns`), view active customer requests, and inspect the interactive 6-step progress stepper and status badges.
* **Test Steps:**
  1. Authenticate as Return Manager via `/login`.
  2. Navigate to Returns List (`/returns`).
  3. Ensure return records table is visible and locate return links (`RR-*`).
  4. Open return record detail view (`/returns/{id}`).
  5. Assert return code is displayed with prefix `RR-`.
  6. Assert status badge is present.
  7. Assert interactive workflow stepper (`#progressSteps`) is visible.
* **Evidence:** Saved as `target/screenshots/JOURNEY_Journey2_ManagerApproval_*.png`.

---

### Journey 3: Search, Multi-Criteria Filtering & CSV Export
* **Test Class:** `ReturnsSearchAndFilterJourneyTest.java`
* **Persona:** Return Manager / Warehouse Staff (`manager1` / `Manager@123`)
* **Objective:** Ensure staff can search return requests by keywords, filter requests by lifecycle status tabs, and access the CSV data export functionality.
* **Test Steps:**
  1. Authenticate as Manager via `/login`.
  2. Navigate to `/returns`.
  3. Input search query keyword `ORD` into `#q` search field and submit.
  4. Verify matching table rows are displayed in the results table.
  5. Navigate to filtered status view (`/returns?status=REQUESTED`).
  6. Assert return records matching `REQUESTED` are rendered.
  7. Verify CSV Export link (`#exportBtn` pointing to `/returns/export`) is present and functional.
* **Evidence:** Saved as `target/screenshots/JOURNEY_Journey3_SearchFilterExport_*.png`.

---

### Journey 4: Executive Dashboard KPIs & Action Queue Navigation
* **Test Class:** `DashboardNavigationJourneyTest.java`
* **Persona:** Return Manager (`manager1` / `Manager@123`)
* **Objective:** Validate executive visibility into reverse logistics operations, verifying that KPI metric cards render accurate counters and action queue links provide direct drill-down navigation.
* **Test Steps:**
  1. Authenticate as Manager via `/login`.
  2. Navigate to Executive Dashboard (`/dashboard`).
  3. Assert dashboard heading and role pill (`Return Manager`) are displayed.
  4. Verify KPI metric cards (Total Returns, Active / In Progress, Courier Pickups, Refunds Processed) render properly.
  5. Verify Action Queue links (Awaiting Approval, Expected Deliveries, Inspection Queue) are present.
  6. Click action queue link and verify navigation to the filtered return workspace (`/returns`).
* **Evidence:** Saved as `target/screenshots/JOURNEY_Journey4_DashboardKPIs_*.png`.

---

### Journey 5: Security, Authentication & Input Validation
* **Test Class:** `SecurityAndValidationJourneyTest.java`
* **Persona:** Anonymous Guest & Customer
* **Objective:** Validate system security boundaries, unauthenticated redirects, invalid credential alerts, and client/server-side form validation.
* **Sub-Journeys:**
  - **Journey 5A (Unauthenticated Access):**
    1. Anonymous user attempts direct access to protected URL `/returns`.
    2. System intercepts and forces redirect to `/login`.
    3. Assert login form fields are rendered.
  - **Journey 5B (Invalid Credentials Handling):**
    1. User navigates to `/login` and inputs invalid username and password.
    2. System rejects authentication and redirects to `/login?error`.
    3. Assert error alert banner informs user of bad credentials.
  - **Journey 5C (Form Validation Enforcement):**
    1. Authenticated customer navigates to `/returns/new`.
    2. Submits empty form without required inputs (blank order number, empty reason, blank address).
    3. Assert form is rejected and HTML5/Spring validation errors (`.invalid-feedback`) are rendered.
* **Evidence:** Saved as `target/screenshots/JOURNEY_Journey5A_AuthRedirect_*.png`, `JOURNEY_Journey5B_InvalidLogin_*.png`, `JOURNEY_Journey5C_ValidationBlocked_*.png`.

---

## 4. Failure Screenshot Mechanism

A core requirement of Task 9 is automatic failure screenshot capture. We implemented this using a native **JUnit 5 Extension** rather than try-catch blocks in individual tests.

### Extension Implementation:
- **Class:** `com.soham.reverselogistics.selenium.support.ScreenshotExtension`
- **Interface:** `org.junit.jupiter.api.extension.AfterTestExecutionCallback`
- **Execution Hook:** Triggers immediately after each test method runs.
- **Condition:** Checks `context.getExecutionException().isPresent()`.
- **Output:** Saves full-viewport PNG files to:
  ```
  target/screenshots/FAILURE_<TestClassName>_<TestMethodName>_<yyyyMMdd_HHmmss>.png
  ```
- **Manual Capture Helper:** Provides `captureScreenshot(WebDriver, String)` for taking step-by-step milestone evidence during successful test runs.

---

## 5. Test Execution Instructions

### Local Maven Execution:
```bash
# Execute entire test suite (Unit + Selenium journeys)
./mvnw test

# Execute only Selenium UI test suite
./mvnw test -Dtest=*JourneyTest

# Execute single user journey
./mvnw test -Dtest=CustomerReturnRequestJourneyTest
```

### Surefire Test Reports Location:
- XML Reports: `target/surefire-reports/TEST-*.xml`
- Text Summary: `target/surefire-reports/*.txt`
- Screenshot Captures: `target/screenshots/`
