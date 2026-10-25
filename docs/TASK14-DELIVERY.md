# Task 14 Delivery Report: Automated Provisioning and Reliability Validation

**Project:** Automated Reverse Logistics Management Portal  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Task ID:** TASK-14 (Sprint 3)  
**Date of Execution:** 22 October 2026  
**Repository Branch:** `main`  
**Configuration Management Tool:** Ansible Core 2.20.1 / Python 3.14 / Systemd 259  
**Target Node Platform:** Linux (Ubuntu 26.04 LTS x86_64)  
**Application Base:** OpenJDK 17 Headless / Spring Boot 3.x / Embedded Tomcat / H2 Persistent Database  
**Service Port:** `8084`  

---

## 1. Executive Summary

Task 14 delivers **Automated Provisioning and Reliability Validation** for the **Automated Reverse Logistics Management Portal**. Building upon the infrastructure-as-code foundation established in Task 13, this milestone operationalizes enterprise release versioning, proves **100% Ansible idempotency**, executes automated health check assertions, and validates **both operator-driven and autonomous self-healing rollback and recovery**.

### Key Outcomes:
1. **Clean Target Environment Provisioning:** Automated end-to-end node configuration deploying release `v1.0.0` with standard FHS layout, dedicated non-login system daemon account (`rlpuser:rlpgroup`), and atomic symlinks (`current`, `previous`, `bin/reverse-logistics-portal.war`).
2. **Idempotency Verification:** Re-executing the complete provisioning playbook against the configured node resulted in **`ok=33, changed=0, failed=0`**, confirming zero unintended modifications or service disruptions.
3. **Automated Health Probing:** Validated live endpoint responsiveness via Spring Boot Actuator (`http://localhost:8084/actuator/health` reporting status `UP`, DB `UP`, diskSpace `UP`) and the web portal login endpoint (HTTP 200 OK).
4. **Reliability & Recovery Demonstration:**
   - **Operator-Driven Rollback:** Executed smooth upgrade to `v1.1.0` followed by atomic rollback to `v1.0.0` with full health recovery.
   - **Autonomous Self-Healing Rollback:** Injected a corrupted release candidate (`v1.2.0-canary`); Ansible trapped the healthcheck failure via `block/rescue`, automatically reverted the symlink to previous stable `v1.0.0`, restarted the service, verified health recovery (`rescued=1, failed=0`), and generated a persistent audit log entry in `/var/log/reverse-logistics-portal/rollback.log`.

---

## 2. Release Management Architecture

To support zero-downtime deployments, atomic version switching, and instant rollback, the portal filesystem layout was evolved into an enterprise release structure:

```
/opt/reverse-logistics-portal/
├── bin/
│   └── reverse-logistics-portal.war -> /opt/reverse-logistics-portal/current/reverse-logistics-portal.war
├── current -> /opt/reverse-logistics-portal/releases/v1.0.0 (active release symlink)
├── previous -> /opt/reverse-logistics-portal/releases/v1.0.0 (rollback reference pointer)
├── releases/
│   ├── v1.0.0/
│   │   ├── reverse-logistics-portal.war (executable production WAR, 61.8 MB)
│   │   └── release-manifest.json
│   ├── v1.1.0/
│   │   ├── reverse-logistics-portal.war
│   │   └── release-manifest.json
│   └── v1.2.0-canary/
│       └── reverse-logistics-portal.war (fault-injection test artifact)
├── shared/
│   ├── data/ (H2 database files persist across releases)
│   └── logs/
└── backups/
```

### Systemd Integration
The systemd unit file (`/etc/systemd/system/reverse-logistics-portal.service`) executes `/opt/reverse-logistics-portal/bin/reverse-logistics-portal.war`, which dereferences through the `current` symlink. Switching versions is an atomic filesystem operation (`ln -sfn`), followed by a clean service restart.

---

## 3. Deliverable 1: Provisioned Node Evidence

### Master Provisioning Playbook: `playbooks/provision_clean.yml`
```bash
ansible-playbook -i inventory/hosts.ini playbooks/provision_clean.yml
```

### Execution Log (Initial Provisioning & Release v1.0.0 Deployment)
```
PLAY [Provision Clean Target Node Environment] *********************************

TASK [Gathering Facts] *********************************************************
ok: [localhost]

TASK [Display provisioning target information] *********************************
ok: [localhost] => {
    "msg": [
        "==========================================================",
        "TARGET ENVIRONMENT PROVISIONING: localhost",
        "Operating System: Ubuntu 26.04",
        "Architecture: x86_64",
        "Initial Release Target: v1.0.0",
        "Force Clean Mode: False",
        "=========================================================="
    ]
}

TASK [reverse_logistics_portal : Update apt cache] *****************************
ok: [localhost]

TASK [reverse_logistics_portal : Install required server packages (Java 17, utilities, system diagnostics)] ***
ok: [localhost]

TASK [reverse_logistics_portal : Verify OpenJDK 17 installation] ***************
ok: [localhost]

TASK [reverse_logistics_portal : Create application system group] **************
ok: [localhost]

TASK [reverse_logistics_portal : Create dedicated application system user (Non-Login)] ***
ok: [localhost]

TASK [reverse_logistics_portal : Create base application directories] **********
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0755'})
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal/bin', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/etc/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/var/lib/reverse-logistics-portal/data', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/var/log/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0755'})
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal/releases', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal/shared', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal/backups', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})

TASK [reverse_logistics_portal : Deploy application.properties from template] ***
ok: [localhost]

TASK [reverse_logistics_portal : Deploy service environment variables file] ****
ok: [localhost]

TASK [reverse_logistics_portal : Verify artifact presence before deployment] ***
ok: [localhost] => {
    "changed": false,
    "msg": "Source artifact /mnt/c/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/target/reverse-logistics-portal-0.0.1-SNAPSHOT.war validated (Size: 61793101 bytes)."
}

TASK [reverse_logistics_portal : Ensure target versioned release directory exists] ***
changed: [localhost]

TASK [reverse_logistics_portal : Deploy application executable WAR to versioned release directory] ***
changed: [localhost]

TASK [reverse_logistics_portal : Generate release metadata manifest] ***********
changed: [localhost]

TASK [reverse_logistics_portal : Atomically update current symlink to target release] ***
changed: [localhost]

TASK [reverse_logistics_portal : Symlink target executable WAR in bin to active release WAR] ***
changed: [localhost]

TASK [reverse_logistics_portal : Deploy systemd service unit file] *************
changed: [localhost]

RUNNING HANDLER [reverse_logistics_portal : Reload systemd daemon] *************
ok: [localhost]

RUNNING HANDLER [reverse_logistics_portal : Restart reverse-logistics-portal] ***
changed: [localhost]

TASK [reverse_logistics_portal : Wait for application port to become active and listening] ***
ok: [localhost]

TASK [reverse_logistics_portal : Query Spring Boot Actuator health endpoint] ***
ok: [localhost]

TASK [reverse_logistics_portal : Assert health endpoint status is UP] **********
ok: [localhost] => {
    "changed": false,
    "msg": "Application health check verified: {\"components\":{\"db\":{\"details\":{\"database\":\"H2\",\"validationQuery\":\"isValid()\"},\"status\":\"UP\"},\"diskSpace\":{\"details\":{\"total\":1081101176832,\"free\":1022158921728,\"threshold\":10485760,\"path\":\"/opt/reverse-logistics-portal/.\",\"exists\":true},\"status\":\"UP\"},\"livenessState\":{\"status\":\"UP\"},\"ping\":{\"status\":\"UP\"},\"readinessState\":{\"status\":\"UP\"},\"ssl\":{\"details\":{\"expiringChains\":[],\"invalidChains\":[],\"validChains\":[]},\"status\":\"UP\"}},\"groups\":[\"liveness\",\"readiness\"],\"status\":\"UP\"}"
}

TASK [reverse_logistics_portal : Assert login page is serving HTTP 200] ********
ok: [localhost] => {
    "changed": false,
    "msg": "Application login page is serving live HTTP 200 traffic on port 8084."
}

TASK [reverse_logistics_portal : Display provisioning and healthcheck summary] ***
ok: [localhost] => {
    "msg": [
        "==========================================================",
        "PROVISIONING & HEALTHCHECK SUCCESSFUL",
        "Application: reverse-logistics-portal",
        "Active Release Path: /opt/reverse-logistics-portal/releases/v1.0.0",
        "Service State: active",
        "Main PID: 1361",
        "Listening Port: 8084",
        "Healthcheck: http://127.0.0.1:8084/actuator/health (Status: UP)",
        "Login URL: http://127.0.0.1:8084/login (HTTP 200)",
        "Data Directory: /var/lib/reverse-logistics-portal/data",
        "Log Directory: /var/log/reverse-logistics-portal",
        "User/Group: rlpuser:rlpgroup",
        "=========================================================="
    ]
}

PLAY RECAP *********************************************************************
localhost                  : ok=35   changed=8    unreachable=0    failed=0    skipped=4    rescued=0    ignored=0   
```

---

## 4. Deliverable 2: Idempotency Evidence

### Idempotency Re-Execution Command:
```bash
ansible-playbook -i inventory/hosts.ini playbooks/provision_clean.yml
```

### Execution Log (Pure Idempotency Validation):
```
PLAY [Provision Clean Target Node Environment] *********************************

TASK [Gathering Facts] *********************************************************
ok: [localhost]

TASK [Display provisioning target information] *********************************
ok: [localhost]

TASK [reverse_logistics_portal : Update apt cache] *****************************
ok: [localhost]

TASK [reverse_logistics_portal : Install required server packages (Java 17, utilities, system diagnostics)] ***
ok: [localhost]

TASK [reverse_logistics_portal : Verify OpenJDK 17 installation] ***************
ok: [localhost]

TASK [reverse_logistics_portal : Create application system group] **************
ok: [localhost]

TASK [reverse_logistics_portal : Create dedicated application system user (Non-Login)] ***
ok: [localhost]

TASK [reverse_logistics_portal : Create base application directories] **********
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0755'})
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal/bin', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/etc/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/var/lib/reverse-logistics-portal/data', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/var/log/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0755'})
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal/releases', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal/shared', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
ok: [localhost] => (item={'path': '/opt/reverse-logistics-portal/backups', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})

TASK [reverse_logistics_portal : Deploy application.properties from template] ***
ok: [localhost]

TASK [reverse_logistics_portal : Deploy service environment variables file] ****
ok: [localhost]

TASK [reverse_logistics_portal : Verify artifact presence before deployment] ***
ok: [localhost]

TASK [reverse_logistics_portal : Ensure target versioned release directory exists] ***
ok: [localhost]

TASK [reverse_logistics_portal : Deploy application executable WAR to versioned release directory] ***
ok: [localhost]

TASK [reverse_logistics_portal : Generate release metadata manifest] ***********
ok: [localhost]

TASK [reverse_logistics_portal : Atomically update current symlink to target release] ***
ok: [localhost]

TASK [reverse_logistics_portal : Symlink target executable WAR in bin to active release WAR] ***
ok: [localhost]

TASK [reverse_logistics_portal : Deploy systemd service unit file] *************
ok: [localhost]

TASK [reverse_logistics_portal : Enable and start reverse-logistics-portal systemd service] ***
ok: [localhost]

TASK [reverse_logistics_portal : Deploy logrotate configuration for portal logs] ***
ok: [localhost]

TASK [reverse_logistics_portal : Allow application incoming traffic on target port via UFW] ***
ok: [localhost]

TASK [reverse_logistics_portal : Wait for application port to become active and listening] ***
ok: [localhost]

TASK [reverse_logistics_portal : Query Spring Boot Actuator health endpoint] ***
ok: [localhost]

TASK [reverse_logistics_portal : Assert health endpoint status is UP] **********
ok: [localhost]

TASK [reverse_logistics_portal : Query application login endpoint] *************
ok: [localhost]

TASK [reverse_logistics_portal : Assert login page is serving HTTP 200] ********
ok: [localhost]

TASK [reverse_logistics_portal : Check systemd service status] *****************
ok: [localhost]

TASK [reverse_logistics_portal : Display provisioning and healthcheck summary] ***
ok: [localhost]

PLAY RECAP *********************************************************************
localhost                  : ok=33   changed=0    unreachable=0    failed=0    skipped=4    rescued=0    ignored=0   
```

> **Idempotency Verdict:** `changed=0, failed=0` across 33 tasks. The target environment was already in the desired state, resulting in zero unnecessary restarts or file modifications.

---

## 5. Deliverable 3: Health-Check Results

### 1. Spring Boot Actuator Health Probe (`GET http://localhost:8084/actuator/health`):
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 1081101176832,
        "free": 1022150389760,
        "threshold": 10485760,
        "path": "/opt/reverse-logistics-portal/.",
        "exists": true
      }
    },
    "livenessState": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    },
    "readinessState": {
      "status": "UP"
    },
    "ssl": {
      "status": "UP",
      "details": {
        "expiringChains": [],
        "invalidChains": [],
        "validChains": []
      }
    }
  },
  "groups": [
    "liveness",
    "readiness"
  ]
}
```

### 2. HTTP Login Endpoint Probe (`GET http://localhost:8084/login`):
```http
HTTP/1.1 200 
Set-Cookie: JSESSIONID=B50957E666DEB4DD1467686DD233C15D; Path=/; HttpOnly
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/html;charset=UTF-8
Content-Language: en
```

### 3. Systemd Service Runtime Status:
```ini
● reverse-logistics-portal.service - Automated Reverse Logistics Management Portal - Enterprise Edition
     Loaded: loaded (/etc/systemd/system/reverse-logistics-portal.service; enabled; preset: enabled)
     Active: active (running)
   Main PID: 1361 (java)
      Tasks: 27 (limit: 4555)
     Memory: 214.1M
     CGroup: /system.slice/reverse-logistics-portal.service
             └─1361 /usr/bin/java -Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom -jar /opt/reverse-logistics-portal/bin/reverse-logistics-portal.war --spring.config.additional-location=file:/etc/reverse-logistics-portal/
```

---

## 6. Deliverable 4: Rollback & Recovery Demonstration

The reliability test suite was automated in [`playbooks/test_reliability_recovery.yml`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/ansible/playbooks/test_reliability_recovery.yml) spanning four distinct validation phases:

```
+-------------------------------------------------------------------------------+
|                      RELIABILITY & RECOVERY TEST FLOW                        |
+-------------------------------------------------------------------------------+
|  Stage 1: Baseline Health Check (v1.0.0 UP)                                  |
|     │                                                                         |
|     ▼                                                                         |
|  Stage 2: Version Upgrade (Deploy v1.1.0 -> Set previous -> Restart -> UP)   |
|     │                                                                         |
|     ▼                                                                         |
|  Stage 3: Operator Rollback (Revert symlink to v1.0.0 -> Restart -> UP)      |
|     │                                                                         |
|     ▼                                                                         |
|  Stage 4: Autonomous Recovery / Self-Healing Demonstration                    |
|           [Block] Deploy faulty candidate (v1.2.0-canary)                     |
|                   Healthcheck fails (Connection refused)                     |
|           [Rescue] Automatically revert symlink to stable v1.0.0              |
|                    Restart service -> Verify Actuator UP                     |
|                    Record audit log                                           |
+-------------------------------------------------------------------------------+
```

### Full Test Execution Log:
```
PLAY [Reliability & Rollback/Recovery Demonstration] ***************************

TASK [Gathering Facts] *********************************************************
ok: [localhost]

TASK [Display test suite banner] ***********************************************
ok: [localhost] => {
    "msg": [
        "==========================================================",
        "STARTING RELIABILITY & RECOVERY VALIDATION SUITE",
        "Target: localhost",
        "Port: 8084",
        "=========================================================="
    ]
}

TASK [[Stage 1] Wait for initial baseline port to be active] *******************
ok: [localhost]

TASK [[Stage 1] Query healthcheck for initial baseline release] ****************
ok: [localhost]

TASK [[Stage 1] Assert initial baseline is healthy] ****************************
ok: [localhost] => {
    "changed": false,
    "msg": "Stage 1 Passed: Initial release is UP and healthy."
}

TASK [[Stage 2] Inspect active release before upgrade] *************************
ok: [localhost]

TASK [[Stage 2] Record previous release pointer] *******************************
changed: [localhost]

TASK [[Stage 2] Create release directory for v1.1.0] ***************************
changed: [localhost]

TASK [[Stage 2] Deploy v1.1.0 application artifact] ****************************
changed: [localhost]

TASK [[Stage 2] Deploy v1.1.0 release manifest] ********************************
changed: [localhost]

TASK [[Stage 2] Switch current symlink to v1.1.0] ******************************
changed: [localhost]

TASK [[Stage 2] Ensure bin WAR symlink points to new release] ******************
ok: [localhost]

TASK [[Stage 2] Restart service on v1.1.0] *************************************
changed: [localhost]

TASK [[Stage 2] Wait for v1.1.0 port] ******************************************
ok: [localhost]

TASK [[Stage 2] Verify v1.1.0 health endpoint] *********************************
ok: [localhost]

TASK [[Stage 2] Assert v1.1.0 is healthy] **************************************
ok: [localhost] => {
    "changed": false,
    "msg": "Stage 2 Passed: v1.1.0 deployed successfully and healthy."
}

TASK [[Stage 3] Read previous release pointer] *********************************
ok: [localhost]

TASK [[Stage 3] Revert current symlink back to previous stable release (v1.0.0)] ***
changed: [localhost]

TASK [[Stage 3] Point bin WAR link to restored release] ************************
ok: [localhost]

TASK [[Stage 3] Restart service with restored release] *************************
changed: [localhost]

TASK [[Stage 3] Wait for recovered application port] ***************************
ok: [localhost]

TASK [[Stage 3] Verify Actuator health post-rollback] **************************
ok: [localhost]

TASK [[Stage 3] Assert recovery of previous stable release] ********************
ok: [localhost] => {
    "changed": false,
    "msg": "Stage 3 Passed: Operator rollback restored stable release (v1.0.0)."
}

TASK [[Stage 3] Log operator rollback event] ***********************************
changed: [localhost]

TASK [[Stage 4 Block] Stage faulty candidate release v1.2.0-canary] ************
changed: [localhost]

TASK [[Stage 4 Block] Create corrupt/unbootable artifact in candidate directory] ***
changed: [localhost]

TASK [[Stage 4 Block] Point current symlink to faulty candidate] ***************
changed: [localhost]

TASK [[Stage 4 Block] Ensure bin points to faulty candidate] *******************
ok: [localhost]

TASK [[Stage 4 Block] Attempt to restart service on candidate release] *********
changed: [localhost]

TASK [[Stage 4 Block] Probe health check with short timeout (expected to fail)] ***
FAILED - RETRYING: [localhost]: [Stage 4 Block] Probe health check with short timeout (expected to fail) (2 retries left).
FAILED - RETRYING: [localhost]: [Stage 4 Block] Probe health check with short timeout (expected to fail) (1 retries left).
fatal: [localhost]: FAILED! => {"attempts": 2, "changed": false, "elapsed": 0, "msg": "Status code was -1 and not [200]: Request failed: <urlopen error [Errno 111] Connection refused>", "redirected": false, "status": -1, "url": "http://127.0.0.1:8084/actuator/health"}

TASK [[Stage 4 Rescue] Health check failure detected! Triggering Automated Recovery...] ***
ok: [localhost] => {
    "msg": "CRITICAL: Fault detected in candidate release v1.2.0-canary. Autonomous self-healing recovery initiated!"
}

TASK [[Stage 4 Rescue] Automatically revert current symlink to previous stable release (v1.0.0)] ***
changed: [localhost]

TASK [[Stage 4 Rescue] Restore bin WAR symlink to stable release] **************
ok: [localhost]

TASK [[Stage 4 Rescue] Restart service with stable release] ********************
changed: [localhost]

TASK [[Stage 4 Rescue] Wait for restored service port] *************************
ok: [localhost]

TASK [[Stage 4 Rescue] Verify Actuator health after automated recovery] ********
ok: [localhost]

TASK [[Stage 4 Rescue] Assert service successfully self-healed] ****************
ok: [localhost] => {
    "changed": false,
    "msg": "Stage 4 Passed: Autonomous self-healing recovery restored service to UP state!"
}

TASK [[Stage 4 Rescue] Log automated self-healing recovery event] **************
changed: [localhost]

TASK [Display complete reliability and recovery validation report] *************
ok: [localhost] => {
    "msg": [
        "==========================================================",
        "RELIABILITY & RECOVERY VALIDATION SUITE: 100% PASSED",
        "Stage 1 (Baseline Verification): PASSED (v1.0.0 UP)",
        "Stage 2 (Version Upgrade): PASSED (v1.1.0 UP)",
        "Stage 3 (Operator Rollback): PASSED (reverted to v1.0.0 UP)",
        "Stage 4 (Self-Healing Auto-Rollback): PASSED (restored v1.0.0 UP)",
        "Audit Log: /var/log/reverse-logistics-portal/rollback.log",
        "=========================================================="
    ]
}

PLAY RECAP *********************************************************************
localhost                  : ok=38   changed=16   unreachable=0    failed=0    skipped=0    rescued=1    ignored=0   
```

### Persistent Rollback Audit Log (`/var/log/reverse-logistics-portal/rollback.log`)
```
[2026-10-22T18:58:58Z] OPERATOR ROLLBACK SUCCESSFUL: Reverted from v1.1.0 to /opt/reverse-logistics-portal/releases/v1.0.0 | Actuator UP
[2026-10-22T18:58:58Z] AUTONOMOUS RECOVERY SUCCESSFUL: Candidate v1.2.0-canary failed healthcheck; automatically restored v1.0.0 | Actuator: UP
```

---

## 7. Artifacts and Playbooks Directory

All automated provisioning and reliability assets are maintained in [`ansible/`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/ansible):

| File Path | Description |
|:---|:---|
| [`ansible/playbooks/provision_clean.yml`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/ansible/playbooks/provision_clean.yml) | Clean target environment provisioning & idempotency testing playbook |
| [`ansible/playbooks/deploy_release.yml`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/ansible/playbooks/deploy_release.yml) | Parameterized versioned release deployment playbook |
| [`ansible/playbooks/rollback.yml`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/ansible/playbooks/rollback.yml) | Operator rollback playbook restoring previous stable release |
| [`ansible/playbooks/test_reliability_recovery.yml`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/ansible/playbooks/test_reliability_recovery.yml) | Complete 4-stage reliability & recovery validation suite |
| [`ansible/roles/reverse_logistics_portal/tasks/05_artifact.yml`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/ansible/roles/reverse_logistics_portal/tasks/05_artifact.yml) | Versioned release deployment and atomic symlink management |
| [`ansible/roles/reverse_logistics_portal/tasks/09_healthcheck.yml`](file:///c:/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/ansible/roles/reverse_logistics_portal/tasks/09_healthcheck.yml) | Actuator probe, login verification, and service health assertions |
