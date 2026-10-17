# Automated Reverse Logistics Management Portal

A web portal where customers raise product return requests and managers and warehouse staff process them through a role-based status workflow. Built as a DevOps mini project (Git, Jenkins, Selenium, Docker, Ansible).

**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)

## Tech stack
Java 17, Spring Boot 4, Spring MVC, Thymeleaf, Spring Data JPA, H2, Maven, Tomcat 11.

## MVP features
- Create, view, update and search return requests
- Role-based status workflow (Customer, Return Manager, Warehouse Staff)
- Summary dashboard
- Health endpoint (`/actuator/health`)

## How to run
Requirements: Java 17 and Git.

```bash
git clone git@github.com:YOUR-USERNAME/reverse-logistics-portal.git
cd reverse-logistics-portal
./mvnw spring-boot:run
```
Open http://localhost:8081

## How to build
```bash
./mvnw clean package
```
The WAR file is created in `target/`.

## Branching policy
| Branch | Purpose |
|---|---|
| `main` | Stable, release-ready code. Receives merges only from `develop`. Releases are tagged (v1.0.0). |
| `develop` | Integration branch. All finished features are merged here. |
| `feature/<short-name>` | One feature per branch, created from `develop`, merged back with a pull request. |
| `bugfix/<short-name>` | Fix for a defect found in `develop`. |

Rules:
1. No direct commits to `main` or `develop` after the initial setup.
2. Every change goes through a pull request with at least one review comment.
3. Keep commits small and focused.

## Commit message convention
`type: short summary`, where type is one of: `feat`, `fix`, `docs`, `chore`, `test`, `refactor`, `style`.
Example: `feat: add create return request form`

## Project plan
The project follows 15 tasks in 3 sprints. Each task is a GitHub issue on the project board.

## Ports
| Component | Port |
|---|---|
| Jenkins | 8080 |
| Application (development) | 8081 |
| Tomcat | 8082 |
| Docker container | 8083 |
