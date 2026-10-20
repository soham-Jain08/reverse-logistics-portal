# Task 6 Delivery Evidence — MVP Completion and Git Collaboration

**Project:** Automated Reverse Logistics Management Portal (`ReturnHub`)  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Deliverable:** Functional MVP, Resolved Merge Conflict, Tagged Version and Updated Backlog  
**Release Tag:** `v1.0.0-mvp`  
**Base Branches:** `develop` (Integration), `main` (Release Baseline)

---

## 1. Overview of Task 6 Achievements

Task 6 achieves the completion of the Minimum Viable Product (MVP) and demonstrates professional DevOps Git collaboration workflows:

1. **Functional MVP Completed:**
   - **Create:** Customers can raise return requests with order number, product, quantity, reason category, detailed description, and pickup address.
   - **View:** Detailed return request view with 6-stage interactive stepper, audit trail/history timeline, status badges, and printable courier return labels.
   - **Update:** Pre-approval edits by customers, administrative notes/remarks by staff, and role-based status action transitions.
   - **Search:** Dynamic multi-field search (order #, customer name, product), status filters (9 statuses), reason category filters, and CSV export.
   - **Role-Based Status Workflow:** Strict permissions separating Customer (`ROLE_CUSTOMER`), Return Manager (`ROLE_MANAGER`), and Warehouse Staff (`ROLE_WAREHOUSE`).
   - **Summary Dashboard:** Dedicated `/dashboard` endpoint displaying live KPI counts, active action queues, lifecycle stage distribution, total refunds issued (`₹`), and recent activity.
   - **Health Monitoring:** Verified `/actuator/health` endpoint returning `{"status":"UP"}`.

2. **Git Collaboration & Merge Conflict Resolution:**
   - Demonstrated merging multiple parallel feature branches: `feature/create-return-request` (Feature 1), `feature/security-and-ui`, and `feature/returns-management` (Feature 2).
   - Real merge conflicts encountered and resolved in:
     - `src/main/resources/templates/fragments/layout.html`
     - `src/main/resources/templates/index.html`
     - `src/main/resources/application.properties`
     - Core models, controllers, and templates.
   - Conflict resolution verified by passing all JUnit test suites (`BUILD SUCCESS`).

3. **Release Baseline & Tagging:**
   - `develop` merged into `main`.
   - Release version tagged: `v1.0.0-mvp`.

---

## 2. Evidence of Merge Conflict Creation & Resolution

### Encountered Conflicts During Integration:
During the merge of parallel feature branches into `develop`, conflicting edits occurred on shared frontend templates and backend configurations:

```text
Auto-merging src/main/resources/templates/fragments/layout.html
CONFLICT (add/add): Merge conflict in src/main/resources/templates/fragments/layout.html
Auto-merging src/main/resources/templates/index.html
CONFLICT (content): Merge conflict in src/main/resources/templates/index.html
Auto-merging src/main/resources/application.properties
CONFLICT (content): Merge conflict in src/main/resources/application.properties
Automatic merge failed; fix conflicts and then commit the result.
```

### Resolution Strategy:
1. **`fragments/layout.html`**:
   - Replaced basic skeleton head with ReturnHub's responsive navigation, including links for Home, Dashboard, Returns, New Return, user role pills, and health checks.
2. **`index.html`**:
   - Integrated full modern landing page with reverse logistics stage explanation, live statistics strip, role highlights, and direct authenticated CTA buttons to the Dashboard.
3. **`application.properties`**:
   - Preserved file-based persistent H2 database (`jdbc:h2:file:./data/rlpdb`) and demo data auto-seeding flag (`app.seed-demo-data=true`).
4. **Conflict Resolution Commits:**
   - `Merge branch 'feature/security-and-ui' into develop (resolved conflicts in layout and index)`
   - `Merge branch 'feature/returns-management' into develop (resolved conflicts in models, controllers, templates, properties)`

---

## 3. Demo Credentials for Testing

| Username | Password | Role | Capabilities |
|:---|:---|:---|:---|
| `customer1` | `Customer@123` | `CUSTOMER` | Raise returns, edit pending returns, view status stepper & label |
| `customer2` | `Customer@123` | `CUSTOMER` | Customer 2 workspace |
| `manager1` | `Manager@123` | `MANAGER` | Approve/reject returns, schedule pickups, issue refunds, export CSV |
| `warehouse1` | `Warehouse@123` | `WAREHOUSE` | Receive packages, inspect goods, restock or dispose |

---

## 4. Verification and Test Results

Automated tests executed via Maven Wrapper (`.\mvnw.cmd test`):
```text
[INFO] Running com.soham.reverselogistics.ReturnRequestWorkflowTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 17.18 s
[INFO] Running com.soham.reverselogistics.ReverseLogisticsPortalApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.028 s
[INFO] Results:
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 5. How to Run Locally

```bash
# From reverse-logistics-portal directory
.\mvnw.cmd spring-boot:run
```

Access in browser:
- Application URL: http://localhost:8081
- Dashboard: http://localhost:8081/dashboard
- Returns Management: http://localhost:8081/returns
- Health Check: http://localhost:8081/actuator/health
