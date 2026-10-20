# Task 9 Delivery Report: Selenium Test Design and Local Execution

**Project:** Automated Reverse Logistics Management Portal (`ReturnHub`)  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Deliverable:** Task 9 — Test Plan, Selenium Scripts, Local Test Report & Failure Screenshot Mechanism  
**Date:** October 2026  
**Status:** **COMPLETED** (100% Pass — 9/9 Tests, 0 Failures, 0 Errors)

---

## 1. Executive Summary

In Task 9, we developed, integrated, and executed an automated **Selenium WebDriver** browser test suite for the *Automated Reverse Logistics Management Portal*. 

### Key Deliverables Completed:
1. **Comprehensive Test Plan (`docs/TASK9-TEST-PLAN.md`):** Complete test strategy covering 5 end-to-end critical user journeys, persona-based access flows, realistic test data models, deterministic UI assertions, and execution instructions.
2. **Robust Selenium WebDriver Test Scripts (`src/test/java/com/soham/reverselogistics/selenium/`):** 5 modular journey test classes utilizing JUnit 5 Jupiter, Page Object / Helper pattern, explicit WebDriverWait conditions, and Headless Chrome 155 via Selenium 4.25.0.
3. **Automated Failure Screenshot Mechanism (`ScreenshotExtension.java`):** A custom JUnit 5 `AfterTestExecutionCallback` extension that dynamically captures high-resolution screenshots to `target/screenshots/FAILURE_*.png` upon any test assertion or timeout failure, plus milestone evidence captures (`JOURNEY_*.png`).
4. **Local Maven Build & Surefire Execution:** Successfully executed locally via `mvnw.cmd test` with 9 passing tests (7 Selenium user journey tests + 2 Spring Boot integration/workflow unit tests) achieving `BUILD SUCCESS`.

---

## 2. Test Execution Summary

The test suite was executed against the active portal instance running on `http://localhost:8081` using headless Google Chrome 155.

| # | Test Class | User Journey / Scenario | Tests Run | Failures | Errors | Duration | Status |
|:---|:---|:---|:---:|:---:|:---:|:---:|:---:|
| 1 | `CustomerReturnRequestJourneyTest` | Journey 1: Customer creates return request, verifies detail page & 6-step progress stepper | 1 | 0 | 0 | 7.14 s | **PASSED** |
| 2 | `DashboardNavigationJourneyTest` | Journey 4: Return Manager views Executive Dashboard KPI cards & drills down into Action Queues | 1 | 0 | 0 | 7.10 s | **PASSED** |
| 3 | `ManagerWorkflowJourneyTest` | Journey 2: Return Manager inspects return records, audits status & validates workflow stepper | 1 | 0 | 0 | 5.48 s | **PASSED** |
| 4 | `ReturnsSearchAndFilterJourneyTest` | Journey 3: Keyword search (`q=ORD`), status filtering (`REQUESTED`) & CSV export verification | 1 | 0 | 0 | 5.67 s | **PASSED** |
| 5 | `SecurityAndValidationJourneyTest` | Journey 5A: Unauthenticated access redirects to login | 1 | 0 | 0 | 4.68 s | **PASSED** |
| 6 | `SecurityAndValidationJourneyTest` | Journey 5B: Bad credentials show alert banner | 1 | 0 | 0 | 3.59 s | **PASSED** |
| 7 | `SecurityAndValidationJourneyTest` | Journey 5C: Form validation blocks empty submission | 1 | 0 | 0 | 7.87 s | **PASSED** |
| 8 | `ReturnRequestWorkflowTests` | Backend State Machine & transition unit validation | 1 | 0 | 0 | 17.05 s | **PASSED** |
| 9 | `ReverseLogisticsPortalApplicationTests` | Spring context boot & bean wiring smoke test | 1 | 0 | 0 | 0.04 s | **PASSED** |
| **Total** | **All 7 Test Suites** | **Complete Portal Verification Suite** | **9** | **0** | **0** | **1m 20s** | **BUILD SUCCESS** |

---

## 3. Five Critical User Journeys Detailed

### Journey 1: Customer Return Request Lifecycle
- **Script:** [`CustomerReturnRequestJourneyTest.java`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/src/test/java/com/soham/reverselogistics/selenium/CustomerReturnRequestJourneyTest.java)
- **Role:** Customer (`customer1`)
- **Execution Flow:** Authenticates, navigates to `/returns/new`, dynamically submits an order with product `"Sony WH-1000XM5 Wireless Headphones"` and reason `"DEFECTIVE"`.
- **Assertions:** Redirects to `/returns/{id}`, verifies order number, product name, and initial status `Requested`.
- **Evidence:** `target/screenshots/JOURNEY_Journey1_ReturnCreated_1792527992293.png`.

### Journey 2: Return Manager Workflow Review & Stepper
- **Script:** [`ManagerWorkflowJourneyTest.java`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/src/test/java/com/soham/reverselogistics/selenium/ManagerWorkflowJourneyTest.java)
- **Role:** Return Manager (`manager1`)
- **Execution Flow:** Authenticates, loads `/returns` workspace, selects an active return record, opens inspection view.
- **Assertions:** Verifies URL pattern matches `/returns/\d+`, return code begins with `RR-`, status badge is displayed, and the 6-step lifecycle progress stepper is active.
- **Evidence:** `target/screenshots/JOURNEY_Journey2_ManagerApproval_1792528004937.png`.

### Journey 3: Keyword Search, Status Filters & CSV Data Export
- **Script:** [`ReturnsSearchAndFilterJourneyTest.java`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/src/test/java/com/soham/reverselogistics/selenium/ReturnsSearchAndFilterJourneyTest.java)
- **Role:** Return Manager (`manager1`)
- **Execution Flow:** Searches repository using query prefix `ORD`, validates filtered rows, navigates to status filter tab `REQUESTED`, and checks CSV export action availability.
- **Assertions:** Table rows matching search criteria exist, status-filtered rows display accurately, CSV export link points to `/returns/export`.
- **Evidence:** `target/screenshots/JOURNEY_Journey3_SearchFilterExport_1792528010542.png`.

### Journey 4: Executive Dashboard KPI Cards & Action Queue Navigation
- **Script:** [`DashboardNavigationJourneyTest.java`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/src/test/java/com/soham/reverselogistics/selenium/DashboardNavigationJourneyTest.java)
- **Role:** Return Manager (`manager1`)
- **Execution Flow:** Loads `/dashboard`, verifies role pill (`Return Manager`), counts summary KPI metric cards, and clicks the quick action queue.
- **Assertions:** Heading and title indicate Dashboard, >= 3 KPI metric cards rendered (`Total returns`, `Active / In Progress`, `Courier pickups`, `Refunds processed`), queue navigation routes directly to `/returns`.
- **Evidence:** `target/screenshots/JOURNEY_Journey4_DashboardKPIs_1792527999332.png`.

### Journey 5: Security Boundary & Input Validation
- **Script:** [`SecurityAndValidationJourneyTest.java`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/src/test/java/com/soham/reverselogistics/selenium/SecurityAndValidationJourneyTest.java)
- **Sub-Journey 5A:** Direct unauthenticated access to `/returns` is intercepted and redirected to `/login`. (Evidence: `JOURNEY_Journey5A_AuthRedirect_1792528022358.png`)
- **Sub-Journey 5B:** Submitting invalid credentials redirects to `/login?error` displaying the alert `"Wrong username or password"`. (Evidence: `JOURNEY_Journey5B_InvalidLogin_1792528015068.png`)
- **Sub-Journey 5C:** Submitting empty return request form triggers HTML5/Spring `.invalid-feedback` validation messages, blocking invalid record insertion. (Evidence: `JOURNEY_Journey5C_ValidationBlocked_1792528029010.png`)

---

## 4. Failure Screenshot Mechanism Verification

The failure screenshot mechanism in `ScreenshotExtension.java` was verified during test development. Whenever an assertion or timeout occurs:
1. `AfterTestExecutionCallback.afterTestExecution(ExtensionContext)` inspects `context.getExecutionException()`.
2. Resolves the active `WebDriver` instance from the test class implementing `WebDriverHolder`.
3. Calls `TakesScreenshot.getScreenshotAs(OutputType.FILE)` and writes the PNG to:
   `target/screenshots/FAILURE_<TestClass>_<TestMethod>_<timestamp>.png`.
4. Outputs the exact file path to standard error / test logs.

### Verified Failure Captures on Record:
- `target/screenshots/FAILURE_ManagerWorkflowJourneyTest_testManagerWorkflowApprovalJourney_20261021_015437.png`
- `target/screenshots/FAILURE_DashboardNavigationJourneyTest_testExecutiveDashboardMetricsAndNavigation_20261021_014744.png`
- `target/screenshots/FAILURE_ReturnsSearchAndFilterJourneyTest_testSearchFilterAndExportJourney_20261021_014826.png`

---

## 5. Maven Build & Surefire Log Excerpt

```
[INFO] --- surefire:3.5.6:test (default-test) @ reverse-logistics-portal ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.soham.reverselogistics.ReturnRequestWorkflowTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 20.35 s -- in com.soham.reverselogistics.ReturnRequestWorkflowTests
[INFO] Running com.soham.reverselogistics.ReverseLogisticsPortalApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.041 s -- in com.soham.reverselogistics.ReverseLogisticsPortalApplicationTests
[INFO] Running com.soham.reverselogistics.selenium.CustomerReturnRequestJourneyTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 7.140 s -- in com.soham.reverselogistics.selenium.CustomerReturnRequestJourneyTest
[INFO] Running com.soham.reverselogistics.selenium.DashboardNavigationJourneyTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 7.104 s -- in com.soham.reverselogistics.selenium.DashboardNavigationJourneyTest
[INFO] Running com.soham.reverselogistics.selenium.ManagerWorkflowJourneyTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.488 s -- in com.soham.reverselogistics.selenium.ManagerWorkflowJourneyTest
[INFO] Running com.soham.reverselogistics.selenium.ReturnsSearchAndFilterJourneyTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.676 s -- in com.soham.reverselogistics.selenium.ReturnsSearchAndFilterJourneyTest
[INFO] Running com.soham.reverselogistics.selenium.SecurityAndValidationJourneyTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 18.45 s -- in com.soham.reverselogistics.selenium.SecurityAndValidationJourneyTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:20 min
[INFO] Finished at: 2026-10-21T01:57:10+05:30
[INFO] ------------------------------------------------------------------------
```

---

## 6. Definition of Done (DoD) Sign-Off

- [x] **Critical User Journeys Identified & Automated:** 5 critical user journeys automated with Selenium WebDriver 4.25.0 and JUnit 5.
- [x] **Deterministic Test Data & Assertions:** Comprehensive element presence, URL regex, and status transition assertions.
- [x] **Failure Screenshot Mechanism:** Implemented via `ScreenshotExtension` writing full-page failure captures to `target/screenshots/`.
- [x] **Local Test Execution:** Executed locally via Maven wrapper (`./mvnw test`) with 100% test pass rate (`BUILD SUCCESS`).
- [x] **Documentation & Traceability:** Detailed Test Plan (`docs/TASK9-TEST-PLAN.md`) and Delivery Report (`docs/TASK9-DELIVERY.md`) produced.
- [x] **Product Backlog Updated:** Task 9 marked as **DONE** in `docs/backlog.md`.
