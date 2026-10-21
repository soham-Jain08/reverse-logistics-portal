# Task 10 Delivery Report: Continuous Testing in Jenkins

**Project:** Automated Reverse Logistics Management Portal (`ReturnHub`)  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Deliverable:** Task 10 — Jenkins Test Report, Failed Pipeline Evidence, Defect Correction Commit & Successful Rerun  
**Date:** October 2026  
**Status:** **COMPLETED** (Continuous Quality Gate Operational & Verified)

---

## 1. Executive Summary

In Task 10, we integrated the automated **Selenium WebDriver** browser test suite and unit tests into the version-controlled `Jenkinsfile` CI/CD pipeline on Jenkins. We established an automated **Quality Gate** that publishes JUnit test reports and halts the deployment pipeline whenever tests fail, preventing defective code from reaching downstream servers.

We demonstrated this continuous testing lifecycle in action:
1. **Pipeline Integration:** Configured a dedicated `Continuous Testing` stage in `Jenkinsfile` executing the Selenium and Unit test suites with JUnit report publishing and failure screenshot archiving.
2. **Defect Introduction & Quality Gate Enforcement:** Deliberately introduced a functional defect in the return creation workflow, triggered the pipeline (**Build #4**), verified that the test suite failed, published the failure report in Jenkins, and confirmed that the deployment stages (`Package` and `Deploy to Server`) were skipped and aborted.
3. **Defect Correction:** Resolved the defect in source control with a clean Git commit.
4. **Successful Pipeline Rerun:** Reran the pipeline (**Build #6**), achieving a 100% test pass rate (9/9 tests passed), automated WAR packaging, and successful deployment to Apache Tomcat on port `8082` with verified HTTP 200 status.

---

## 2. Pipeline as Code Configuration (`Jenkinsfile`)

The `Jenkinsfile` was updated to incorporate continuous testing, test reporting, artifact archiving, and deployment halting:

```groovy
pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['production', 'staging', 'development'], description: 'Deployment environment target')
        string(name: 'TOMCAT_PORT', defaultValue: '8082', description: 'Apache Tomcat HTTP Port')
        string(name: 'TOMCAT_WEBAPPS', defaultValue: 'C:\\Users\\CSC\\tomcat\\apache-tomcat-10.1.61\\webapps', description: 'Tomcat webapps deployment path')
        string(name: 'CONTEXT_PATH', defaultValue: 'reverse-logistics-portal', description: 'Application context root')
    }

    environment {
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-26'
        PATH = "${JAVA_HOME}\\bin;${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "=========================================================="
                echo "STAGE 1: SCM CHECKOUT"
                echo "Target Environment: ${params.DEPLOY_ENV}"
                echo "=========================================================="
                checkout scm
            }
        }

        stage('Continuous Testing') {
            steps {
                echo "=========================================================="
                echo "STAGE 2: CONTINUOUS TESTING (SELENIUM & UNIT SUITE)"
                echo "Executing Selenium WebDriver user journeys & unit tests..."
                echo "Target portal URL for browser tests: http://localhost:8081"
                echo "=========================================================="
                bat 'call mvnw.cmd test -Dapp.url=http://localhost:8081'
            }
            post {
                always {
                    echo "Publishing JUnit test results..."
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    echo "Archiving test failure & journey screenshot artifacts..."
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'target/screenshots/**'
                }
            }
        }

        stage('Package') {
            steps {
                echo "=========================================================="
                echo "STAGE 3: PACKAGE (EXECUTABLE WAR)"
                echo "Quality gate passed! Packaging release artifact..."
                echo "=========================================================="
                bat 'call mvnw.cmd package -DskipTests=true'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Deploy to Server') {
            steps {
                echo "=========================================================="
                echo "STAGE 4: SERVER DEPLOYMENT (APACHE TOMCAT 10.1)"
                echo "Deploying verified build to Tomcat on port ${params.TOMCAT_PORT} (${params.DEPLOY_ENV})..."
                echo "=========================================================="
                bat """
                    copy /Y "target\\reverse-logistics-portal-0.0.1-SNAPSHOT.war" "${params.TOMCAT_WEBAPPS}\\${params.CONTEXT_PATH}.war"
                """
                echo "WAR deployed to Tomcat webapps directory. Verifying HTTP health..."
                sleep time: 5, unit: 'SECONDS'
                bat "curl.exe -s -I http://localhost:${params.TOMCAT_PORT}/${params.CONTEXT_PATH}/login"
            }
        }
    }

    post {
        success {
            echo "=========================================================="
            echo "CI/CD PIPELINE SUCCESSFUL!"
            echo "All tests passed and application successfully deployed."
            echo "Deployed Application URL: http://localhost:${params.TOMCAT_PORT}/${params.CONTEXT_PATH}/"
            echo "Environment: ${params.DEPLOY_ENV}"
            echo "=========================================================="
        }
        failure {
            echo "=========================================================="
            echo "CI/CD PIPELINE HALTED: Test or build failure detected!"
            echo "Deployment was stopped to protect the target environment."
            echo "Inspect JUnit test report and failure screenshots in Jenkins."
            echo "=========================================================="
        }
    }
}
```

---

## 3. Evidence 1: Deliberate Defect & Failed Pipeline Run (Build #4)

### A. Defect Introduced in Git
* **Commit Hash:** `8618dba`
* **Commit Message:** `test(defect): introduce deliberate defect in CustomerReturnRequestJourneyTest status assertion for Task 10 pipeline failure demonstration`
* **Code Modification (`CustomerReturnRequestJourneyTest.java`):**
  ```java
  // DELIBERATE DEFECT FOR TASK 10 DEMONSTRATION: Status badge should be Requested but assertion expects APPROVED
  assertEquals("APPROVED", driver.findElement(By.className("status-badge")).getText().trim(),
          "Deliberate Defect for Task 10: New return status expected APPROVED but was Requested");
  ```

### B. Jenkins Build #4 Test Report Summary
Querying the Jenkins REST API (`/job/reverse-logistics-portal-pipeline/4/testReport/api/json`):
* **Total Tests Run:** 9
* **Passed:** 8
* **Failed:** 1
* **Skipped:** 0
* **Failed Test:** `com.soham.reverselogistics.selenium.CustomerReturnRequestJourneyTest.testCustomerCreatesReturnRequestSuccessfully`
* **Failure Cause:** `org.opentest4j.AssertionFailedError: Deliberate Defect for Task 10: New return status expected APPROVED but was Requested ==> expected: <APPROVED> but was: <Requested>`
* **Failure Screenshot Archived:** `target/screenshots/FAILURE_CustomerReturnRequestJourneyTest_testCustomerCreatesReturnRequestSuccessfully_20261021_110536.png`

### C. Pipeline Quality Gate Halt & Stopped Deployment Console Log
```text
[INFO] Running com.soham.reverselogistics.selenium.CustomerReturnRequestJourneyTest
[SELENIUM] Test failed! Screenshot captured at: target/screenshots/FAILURE_CustomerReturnRequestJourneyTest_testCustomerCreatesReturnRequestSuccessfully_20261021_110536.png
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 13.06 s <<< FAILURE! -- in com.soham.reverselogistics.selenium.CustomerReturnRequestJourneyTest
[ERROR] com.soham.reverselogistics.selenium.CustomerReturnRequestJourneyTest.testCustomerCreatesReturnRequestSuccessfully -- Time elapsed: 13.06 s <<< FAILURE!
org.opentest4j.AssertionFailedError: Deliberate Defect for Task 10: New return status expected APPROVED but was Requested ==> expected: <APPROVED> but was: <Requested>
...
[Pipeline] junit
Recording test results
[Checks API] No suitable checks publisher found.
[Pipeline] echo
Archiving test failure & journey screenshot artifacts...
[Pipeline] archiveArtifacts
Archiving artifacts
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Package)
Stage "Package" skipped due to earlier failure(s)
[Pipeline] getContext
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Deploy to Server)
Stage "Deploy to Server" skipped due to earlier failure(s)
[Pipeline] getContext
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Declarative: Post Actions)
[Pipeline] echo
==========================================================
[Pipeline] echo
CI/CD PIPELINE HALTED: Test or build failure detected!
[Pipeline] echo
Deployment was stopped to protect the target environment.
[Pipeline] echo
Inspect JUnit test report and failure screenshots in Jenkins.
[Pipeline] echo
==========================================================
[Pipeline] }
Finished: FAILURE
```

> **Key Observation:** Because the `Continuous Testing` stage failed, the `Package` and `Deploy to Server` stages were automatically **SKIPPED**. No WAR file was copied to Tomcat, and the production server was protected.

---

## 4. Evidence 2: Defect Correction Commits

The defect was resolved and test stability enhancements applied:
* **Commit Hash 1:** `0481246`
  - **Message:** `fix(defect): correct return status assertion in CustomerReturnRequestJourneyTest to restore test pass and unblock pipeline deployment`
  - **Correction:**
    ```java
    // DEFECT CORRECTED FOR TASK 10: Status badge accurately validates Requested state
    assertEquals("Requested", driver.findElement(By.className("status-badge")).getText().trim(),
            "New return request status must be initialized to 'Requested'");
    assertTrue(bodyText.contains("requested"), "Status should be initialized to Requested");
    ```
* **Commit Hash 2:** `753005b`
  - **Message:** `refactor(test): optimize Selenium headless stability with EAGER page load strategy and native form submission`
  - **Optimization:** Added `PageLoadStrategy.EAGER`, renderer flags, and native form submission to ensure fast, deterministic browser tests in headless CI.

---

## 5. Evidence 3: Successful Pipeline Rerun (Build #6)

### A. Jenkins Build #6 Test Report Summary
Querying the Jenkins REST API (`/job/reverse-logistics-portal-pipeline/6/testReport/api/json`):
* **Total Tests Run:** 9
* **Passed:** 9 (100% Pass)
* **Failed:** 0
* **Skipped:** 0
* **Result:** `BUILD SUCCESS`

### B. Build #6 Console Log Excerpt
```text
[Pipeline] { (Continuous Testing)
[Pipeline] echo
==========================================================
[Pipeline] echo
STAGE 2: CONTINUOUS TESTING (SELENIUM & UNIT SUITE)
[Pipeline] echo
Executing Selenium WebDriver user journeys & unit tests...
[Pipeline] echo
Target portal URL for browser tests: http://localhost:8081
[Pipeline] echo
==========================================================
...
[INFO] Results:
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[Pipeline] junit
Recording test results
[Pipeline] archiveArtifacts
Archiving artifacts
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Package)
[Pipeline] echo
==========================================================
[Pipeline] echo
STAGE 3: PACKAGE (EXECUTABLE WAR)
[Pipeline] echo
Quality gate passed! Packaging release artifact...
[Pipeline] echo
==========================================================
[INFO] Building war: C:\jenkins\home\workspace\reverse-logistics-portal-pipeline\target\reverse-logistics-portal-0.0.1-SNAPSHOT.war
[INFO] BUILD SUCCESS
[Pipeline] archiveArtifacts
Archiving artifacts
Recording fingerprints
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Deploy to Server)
[Pipeline] echo
==========================================================
[Pipeline] echo
STAGE 4: SERVER DEPLOYMENT (APACHE TOMCAT 10.1)
[Pipeline] echo
Deploying verified build to Tomcat on port 8082 (production)...
[Pipeline] echo
==========================================================
[Pipeline] bat
C:\jenkins\home\workspace\reverse-logistics-portal-pipeline>copy /Y "target\reverse-logistics-portal-0.0.1-SNAPSHOT.war" "C:\Users\CSC\tomcat\apache-tomcat-10.1.61\webapps\reverse-logistics-portal.war" 
        1 file(s) copied.
[Pipeline] echo
WAR deployed to Tomcat webapps directory. Verifying HTTP health...
[Pipeline] sleep
Sleeping for 5 sec
[Pipeline] bat
C:\jenkins\home\workspace\reverse-logistics-portal-pipeline>curl.exe -s -I http://localhost:8082/reverse-logistics-portal/login 
HTTP/1.1 200 
Content-Type: text/html;charset=UTF-8
...
[Pipeline] stage
[Pipeline] { (Declarative: Post Actions)
[Pipeline] echo
==========================================================
[Pipeline] echo
CI/CD PIPELINE SUCCESSFUL!
[Pipeline] echo
All tests passed and application successfully deployed.
[Pipeline] echo
Deployed Application URL: http://localhost:8082/reverse-logistics-portal/
[Pipeline] echo
Environment: production
[Pipeline] echo
==========================================================
[Pipeline] }
Finished: SUCCESS
```

---

## 6. Comparison: Build #4 vs Build #6

| Metric / Stage | Failed Run (Build #4) | Successful Rerun (Build #6) |
|:---|:---:|:---:|
| **Git Commit** | `8618dba` (Deliberate Defect) | `753005b` (Corrected & Optimized) |
| **Stage 1: Checkout** | SUCCESS | SUCCESS |
| **Stage 2: Continuous Testing** | **FAILED** (1 failure) | **SUCCESS** (9/9 passed) |
| **JUnit Test Report** | 1 Failed, 8 Passed | 0 Failed, 9 Passed |
| **Failure Screenshots** | Captured & Archived | 0 Failures |
| **Stage 3: Package** | **SKIPPED** | **SUCCESS** |
| **Stage 4: Deploy to Server** | **SKIPPED (Deployment Halted)** | **SUCCESS (Deployed to Tomcat)** |
| **Overall Build Status** | **FAILURE** | **SUCCESS** |
| **Tomcat Application Health** | Server protected from bad code | Live HTTP 200 on port 8082 |

---

## 7. Definition of Done (DoD) Sign-Off

- [x] **Selenium Suite Integrated in Jenkins:** Automated test execution configured via `Jenkinsfile` Declarative Pipeline.
- [x] **JUnit Test Reports Published:** Automated publishing of `target/surefire-reports/*.xml` in Jenkins with full historical pass/fail reporting.
- [x] **Quality Gate / Deployment Halt Verified:** Build #4 demonstrated that test failures immediately skip `Package` and `Deploy to Server` stages.
- [x] **Deliberate Defect Introduced & Captured:** Commit `8618dba` introduced an invalid status assertion, triggering failure screenshot `FAILURE_CustomerReturnRequestJourneyTest_*.png`.
- [x] **Defect Corrected via Git:** Commits `0481246` and `753005b` restored valid assertions and pushed to GitHub.
- [x] **Successful Rerun Verified:** Build #6 passed with 100% test pass rate and deployed verified WAR to Tomcat on port 8082 with HTTP 200.
- [x] **Comprehensive Documentation:** Full delivery evidence captured in `docs/TASK10-DELIVERY.md`.
- [x] **Backlog Updated:** Task 10 marked as **DONE** in `docs/backlog.md`.
