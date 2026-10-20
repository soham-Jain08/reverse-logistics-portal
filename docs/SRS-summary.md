# Task 3 — SRS Summary

## 1. Purpose
The Automated Reverse Logistics Management Portal records and tracks goods returned by customers to a seller.

## 2. Users
- Customer: creates and views their own return requests.
- Return Manager: approves/rejects requests, schedules pickups and records refunds.
- Warehouse Staff: records receipt, inspection and restock/disposal outcomes.
- Finance Team: uses refund information for accounting (outside the first implementation).

## 3. Functional requirements
1. Create a return request with order number, customer name, product, quantity and reason.
2. Assign a unique ID, creation time and initial status `REQUESTED`.
3. View a list and the details of return requests.
4. Search and filter returns (planned in a later feature task).
5. Enforce the approved status workflow and role permissions (later feature task).
6. Display counts by status and total refund amount (later feature task).
7. Expose `/actuator/health` for deployment health checks.

## 4. Non-functional requirements
- Java 17 and Spring Boot 3.
- Maven build.
- H2 database for the MVP.
- Local development port 8081.
- Validate required fields and quantity.
- Keep the application structure suitable for later Jenkins, Selenium, Docker and Ansible tasks.

## 5. Scope and limitations
This Task 3 starter provides a working backend foundation: create/list/detail API, persistence and health endpoint. Login, role-based access, status transition actions, dashboard, UI, CI/CD and production-grade security are implemented in later tasks. Do not expose this development setup as a public production service.
