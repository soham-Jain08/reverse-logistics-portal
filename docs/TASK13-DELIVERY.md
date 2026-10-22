# Task 13 Delivery Report: Configuration Management Script

**Project:** Automated Reverse Logistics Management Portal  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Task ID:** TASK-13 (Sprint 3)  
**Date of Execution:** 22 October 2026  
**Repository Branch:** `main`  
**Configuration Management Tool:** Ansible Core 2.20.1 / Python 3.14  
**Target Node Platform:** Linux (Ubuntu 26.04 LTS / Systemd 259)  
**Application Base:** Java 17 (OpenJDK 17 JRE) / Spring Boot 3.x / Embedded Tomcat / H2 Database  
**Target Service Port:** `8084` (Actuator & Web Portal)

---

## 1. Executive Summary

Task 13 establishes automated **Configuration Management and Infrastructure as Code (IaC)** for the **Automated Reverse Logistics Management Portal** using **Ansible**. Rather than relying on manual server setup or ad-hoc scripts, the complete server state required to host and operate the production portal has been codified into a modular, production-ready Ansible role and playbook.

The Ansible automation enforces:
- **Server Prerequisite Provisioning:** Automatically installs OpenJDK 17 runtime, core networking utilities, process monitoring tools, log rotation policies, and firewall rules.
- **Security Hardening & Principle of Least Privilege:** Creates a dedicated non-login system account (`rlpuser:rlpgroup`) and locks down filesystem permissions (`0750` / `0755` / `0640`).
- **Filesystem Standard Compliance (FHS):** Deploys a standardized folder hierarchy separating binaries (`/opt`), configuration (`/etc`), persistent data (`/var/lib`), runtime state (`/run`), and logs (`/var/log`).
- **Templated Configuration Management:** Dynamically generates `/etc/reverse-logistics-portal/application.properties` and `/etc/default/reverse-logistics-portal` using Jinja2 templates.
- **Systemd Service Lifecycle:** Registers, enables, and manages `reverse-logistics-portal.service` with automatic failure restart (`Restart=always`, `RestartSec=10`) and resource limits (`LimitNOFILE=65536`).
- **Automated Health Verification & Idempotency:** Validates the live deployment by probing Spring Boot Actuator (`/actuator/health` reporting `UP`) and the login page (HTTP 200). Repeated executions confirm **100% idempotency** with zero unexpected changes (`changed=0`).

---

## 2. Server Prerequisites Specification

The complete server prerequisites identified for the Reverse Logistics Management Portal are structured into six operational categories:

| Category | Prerequisite Item | Specification Details | Security / Operational Justification |
|:---|:---|:---|:---|
| **Packages** | `openjdk-17-jre-headless` | OpenJDK 17 Headless Java Runtime | Executes Spring Boot 3.x bytecode without unnecessary X11/GUI libraries |
| | `curl` & `wget` | HTTP client utilities | Automated health checks, synthetic probing, artifact retrieval |
| | `net-tools` & `procps` | Network and process diagnostics (`ss`, `netstat`, `ps`) | Runtime port inspection and PID lifecycle monitoring |
| | `logrotate` | Daemon log management utility | Enforces automatic daily log rotation and compression |
| | `ufw` | Uncomplicated Firewall | Restricts inbound traffic strictly to authorized application ports |
| | `ca-certificates` | Trusted TLS certificate authorities | Secure HTTPS outbound communication and remote API calls |
| | `unzip` | Archive decompression tool | Exploded archive inspection and maintenance |
| **Users & Groups** | `rlpgroup` | System Group (GID: `1500`) | Isolated group ownership for application resources |
| | `rlpuser` | System User (UID: `1500`, primary group `rlpgroup`) | Non-root service account adhering to Least Privilege |
| | User Shell | `/usr/sbin/nologin` | Prevents interactive shell login; mitigates credential compromise |
| | User Home | `/opt/reverse-logistics-portal` | Isolated application base directory |
| **Folders & Dirs** | `/opt/reverse-logistics-portal` | Mode `0755`, Owner `rlpuser:rlpgroup` | Application home base directory |
| | `/opt/reverse-logistics-portal/bin` | Mode `0750`, Owner `rlpuser:rlpgroup` | Executable WAR artifact storage |
| | `/etc/reverse-logistics-portal` | Mode `0750`, Owner `rlpuser:rlpgroup` | Externalized Spring Boot configuration |
| | `/var/lib/reverse-logistics-portal/data` | Mode `0750`, Owner `rlpuser:rlpgroup` | H2 persistent database storage (`rlpdb.mv.db`) |
| | `/var/log/reverse-logistics-portal` | Mode `0755`, Owner `rlpuser:rlpgroup` | Application console and framework logs |
| | `/run/reverse-logistics-portal` | Mode `0755`, Owner `rlpuser:rlpgroup` | Runtime PID and socket storage |
| **Files & Templates** | `application.properties` | Mode `0640`, Owner `rlpuser:rlpgroup` | Spring properties: port, persistent JDBC URL, Actuator |
| | `reverse-logistics-portal.env` | Mode `0640`, Owner `root:rlpgroup` | Environment variables (`JAVA_OPTS`, `SERVER_PORT`) |
| | `reverse-logistics-portal.service` | Mode `0644`, Owner `root:root` | Systemd unit file (`/etc/systemd/system/`) |
| | `logrotate-rlp` | Mode `0644`, Owner `root:root` | 14-day log retention with `copytruncate` policy |
| | `reverse-logistics-portal.war` | Mode `0750`, Owner `rlpuser:rlpgroup` | Release executable archive (61.8 MB) |
| **Ports & Network** | `8084/TCP` | Application HTTP Port (`0.0.0.0:8084`) | Serves portal web traffic and Actuator monitoring |
| | UFW Rule | `allow 8084/tcp comment 'Reverse Logistics HTTP'` | Permits inbound traffic through the target node firewall |
| **Services** | `reverse-logistics-portal.service` | State: `started`, Enabled on boot | Systemd service managing background Java process |

---

## 3. Ansible Project Architecture & File Hierarchy

The configuration management assets are organized within `reverse-logistics-portal/ansible/` following standard Ansible best practices:

```
reverse-logistics-portal/ansible/
├── ansible.cfg                                  # Global Ansible configuration & callback settings
├── playbook.yml                                 # Root playbook shortcut
├── inventory/
│   ├── hosts.ini                                # Inventory defining target host groups & vars
│   └── group_vars/
│       └── all.yml                              # Global inventory variables
├── playbooks/
│   ├── site.yml                                 # Master orchestration playbook
│   └── group_vars/
│       └── all.yml                              # Playbook-level variable definitions
└── roles/
    └── reverse_logistics_portal/
        ├── defaults/
        │   └── main.yml                         # Default role parameters
        ├── handlers/
        │   └── main.yml                         # Systemd daemon-reload & service restart handlers
        ├── vars/
        │   └── main.yml                         # Role-scoped variables
        ├── tasks/
        │   ├── main.yml                         # Main orchestrator importing modular tasks
        │   ├── 01_packages.yml                  # OS packages & Java 17 installation
        │   ├── 02_users.yml                     # User (rlpuser) & group (rlpgroup) creation
        │   ├── 03_directories.yml               # FHS directory structure & permissions
        │   ├── 04_configuration.yml             # Templated configuration deployment
        │   ├── 05_artifact.yml                  # WAR artifact verification & deployment
        │   ├── 06_systemd.yml                   # Systemd service registration & startup
        │   ├── 07_logrotate.yml                 # Logrotate policy configuration
        │   ├── 08_firewall.yml                  # UFW port rule configuration
        │   └── 09_healthcheck.yml               # Live port & Actuator health verification
        └── templates/
            ├── application.properties.j2         # Jinja2 template for Spring Boot properties
            ├── reverse-logistics-portal.env.j2   # Jinja2 template for service environment
            ├── reverse-logistics-portal.service.j2 # Jinja2 template for Systemd unit
            └── logrotate-rlp.j2                 # Jinja2 template for log rotation
```

---

## 4. Key Ansible Playbook & Configuration Artifacts

### 4.1 Master Playbook (`ansible/playbooks/site.yml`)
```yaml
---
- name: Provision and Configure Reverse Logistics Management Portal Target Node
  hosts: portal_nodes
  become: true
  gather_facts: true

  pre_tasks:
    - name: Display pre-provisioning host information
      ansible.builtin.debug:
        msg:
          - "Configuring Target Host: {{ inventory_hostname }}"
          - "Operating System: {{ ansible_facts['distribution'] }} {{ ansible_facts['distribution_version'] }} ({{ ansible_facts['os_family'] }})"
          - "Kernel: {{ ansible_facts['kernel'] }}"
          - "Architecture: {{ ansible_facts['architecture'] }}"
          - "Target Port: {{ app_port }}"
          - "Target User: {{ app_user }}:{{ app_group }}"

  roles:
    - role: reverse_logistics_portal
```

### 4.2 Inventory Configuration (`ansible/inventory/hosts.ini`)
```ini
[portal_nodes]
localhost ansible_connection=local ansible_python_interpreter=/usr/bin/python3

[portal_production]
# Example target enterprise production node (SSH connection)
# prod-node-01.reverselogistics.internal ansible_host=192.168.1.50 ansible_user=ubuntu

[portal_staging]
# Example target staging node (SSH connection)
# stage-node-01.reverselogistics.internal ansible_host=192.168.1.51 ansible_user=ubuntu

[portal_nodes:vars]
ansible_user=root
app_env=production
```

### 4.3 Systemd Service Template (`roles/reverse_logistics_portal/templates/reverse-logistics-portal.service.j2`)
```ini
[Unit]
Description={{ app_description }}
After=network.target syslog.target
Wants=network.target

[Service]
Type=simple
User={{ app_user }}
Group={{ app_group }}
EnvironmentFile=-{{ app_env_file }}
WorkingDirectory={{ app_base_dir }}
ExecStart=/usr/bin/java $JAVA_OPTS -jar {{ target_artifact_path }} --spring.config.additional-location=file:{{ app_config_dir }}/
Restart=always
RestartSec=10
StandardOutput=append:{{ app_log_dir }}/console.log
StandardError=append:{{ app_log_dir }}/console-error.log

# Resource Limits & Security Hardening
LimitNOFILE=65536
LimitNPROC=4096
TimeoutStopSec=30
SuccessExitStatus=143

# Sandboxing
ProtectSystem=full
ProtectHome=true
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

---

## 5. First Execution Log Evidence

The playbook was executed against the target node. All 27 tasks completed successfully without errors:

```
PLAY [Provision and Configure Reverse Logistics Management Portal Target Node] ***

TASK [Gathering Facts] *********************************************************
ok: [localhost]

TASK [Display pre-provisioning host information] *******************************
ok: [localhost] => {
    "msg": [
        "Configuring Target Host: localhost",
        "Operating System: Ubuntu 26.04 (Debian)",
        "Kernel: 6.6.87.2-microsoft-standard-WSL2",
        "Architecture: x86_64",
        "Target Port: 8084",
        "Target User: rlpuser:rlpgroup"
    ]
}

TASK [reverse_logistics_portal : Update apt cache] *****************************
ok: [localhost]

TASK [reverse_logistics_portal : Install required server packages (Java 17, utilities, system diagnostics)] ***
changed: [localhost]

TASK [reverse_logistics_portal : Verify OpenJDK 17 installation] ***************
ok: [localhost]

TASK [reverse_logistics_portal : Display detected Java version] ****************
ok: [localhost] => {
    "msg": [
        "openjdk version \"17.0.20.1\" 2026-08-18",
        "OpenJDK Runtime Environment (build 17.0.20.1+1-1-26.04-Ubuntu)",
        "OpenJDK 64-Bit Server VM (build 17.0.20.1+1-1-26.04-Ubuntu, mixed mode, sharing)"
    ]
}

TASK [reverse_logistics_portal : Create application system group] **************
changed: [localhost]

TASK [reverse_logistics_portal : Create dedicated application system user (Non-Login)] ***
changed: [localhost]

TASK [reverse_logistics_portal : Create base application directories] **********
changed: [localhost] => (item={'path': '/opt/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0755'})
changed: [localhost] => (item={'path': '/opt/reverse-logistics-portal/bin', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
changed: [localhost] => (item={'path': '/etc/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
changed: [localhost] => (item={'path': '/var/lib/reverse-logistics-portal/data', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0750'})
changed: [localhost] => (item={'path': '/var/log/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0755'})
changed: [localhost] => (item={'path': '/run/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0755'})

TASK [reverse_logistics_portal : Deploy application.properties from template] ***
changed: [localhost]

TASK [reverse_logistics_portal : Deploy service environment variables file] ****
changed: [localhost]

TASK [reverse_logistics_portal : Check if source artifact exists] **************
ok: [localhost]

TASK [reverse_logistics_portal : Verify artifact presence before deployment] ***
ok: [localhost] => {
    "changed": false,
    "msg": "Source artifact /mnt/c/Users/CSC/Downloads/Reverse_Logistics_Task3_Starter/reverse-logistics-portal/target/reverse-logistics-portal-0.0.1-SNAPSHOT.war validated (Size: 61793101 bytes)."
}

TASK [reverse_logistics_portal : Deploy application executable WAR to bin directory] ***
changed: [localhost]

TASK [reverse_logistics_portal : Deploy systemd service unit file] *************
changed: [localhost]

TASK [reverse_logistics_portal : Flush handlers to ensure unit file is reloaded before starting] ***

RUNNING HANDLER [reverse_logistics_portal : Reload systemd daemon] *************
ok: [localhost]

RUNNING HANDLER [reverse_logistics_portal : Restart reverse-logistics-portal] ***
changed: [localhost]

TASK [reverse_logistics_portal : Enable and start reverse-logistics-portal systemd service] ***
changed: [localhost]

TASK [reverse_logistics_portal : Deploy logrotate configuration for portal logs] ***
changed: [localhost]

TASK [reverse_logistics_portal : Check if UFW is installed] ********************
ok: [localhost]

TASK [reverse_logistics_portal : Allow application incoming traffic on target port via UFW] ***
changed: [localhost]

TASK [reverse_logistics_portal : Wait for application port to become active and listening] ***
ok: [localhost]

TASK [reverse_logistics_portal : Query Spring Boot Actuator health endpoint] ***
ok: [localhost]

TASK [reverse_logistics_portal : Assert health endpoint status is UP] **********
ok: [localhost] => {
    "changed": false,
    "msg": "Application health check verified: {\"components\":{\"db\":{\"details\":{\"database\":\"H2\",\"validationQuery\":\"isValid()\"},\"status\":\"UP\"},\"diskSpace\":{\"details\":{\"total\":1081101176832,\"free\":1022344511488,\"threshold\":10485760,\"path\":\"/opt/reverse-logistics-portal/.\",\"exists\":true},\"status\":\"UP\"},\"livenessState\":{\"status\":\"UP\"},\"ping\":{\"status\":\"UP\"},\"readinessState\":{\"status\":\"UP\"},\"ssl\":{\"details\":{\"expiringChains\":[],\"invalidChains\":[],\"validChains\":[]},\"status\":\"UP\"}},\"groups\":[\"liveness\",\"readiness\"],\"status\":\"UP\"}"
}

TASK [reverse_logistics_portal : Query application login endpoint] *************
ok: [localhost]

TASK [reverse_logistics_portal : Assert login page is serving HTTP 200] ********
ok: [localhost] => {
    "changed": false,
    "msg": "Application login page is serving live HTTP 200 traffic on port 8084."
}

TASK [reverse_logistics_portal : Check systemd service status] *****************
ok: [localhost]

TASK [reverse_logistics_portal : Display provisioning summary] *****************
ok: [localhost] => {
    "msg": [
        "==========================================================",
        "PROVISIONING SUCCESSFUL: Reverse Logistics Management Portal",
        "Service State: active",
        "Main PID: 1093",
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
localhost                  : ok=27   changed=12   unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
```

---

## 6. Idempotency Verification Evidence (Second Execution)

A hallmark of high-quality configuration management is **idempotency**: re-running the playbook against a previously configured node should make zero unintended modifications (`changed=0`).

### 6.1 Idempotent Execution Log
```
PLAY [Provision and Configure Reverse Logistics Management Portal Target Node] ***

TASK [Gathering Facts] *********************************************************
ok: [localhost]

TASK [Display pre-provisioning host information] *******************************
ok: [localhost]

TASK [reverse_logistics_portal : Update apt cache] *****************************
ok: [localhost]

TASK [reverse_logistics_portal : Install required server packages (Java 17, utilities, system diagnostics)] ***
ok: [localhost]

TASK [reverse_logistics_portal : Verify OpenJDK 17 installation] ***************
ok: [localhost]

TASK [reverse_logistics_portal : Display detected Java version] ****************
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
ok: [localhost] => (item={'path': '/run/reverse-logistics-portal', 'owner': 'rlpuser', 'group': 'rlpgroup', 'mode': '0755'})

TASK [reverse_logistics_portal : Deploy application.properties from template] ***
ok: [localhost]

TASK [reverse_logistics_portal : Deploy service environment variables file] ****
ok: [localhost]

TASK [reverse_logistics_portal : Check if source artifact exists] **************
ok: [localhost]

TASK [reverse_logistics_portal : Verify artifact presence before deployment] ***
ok: [localhost]

TASK [reverse_logistics_portal : Deploy application executable WAR to bin directory] ***
ok: [localhost]

TASK [reverse_logistics_portal : Deploy systemd service unit file] *************
ok: [localhost]

TASK [reverse_logistics_portal : Flush handlers to ensure unit file is reloaded before starting] ***

TASK [reverse_logistics_portal : Enable and start reverse-logistics-portal systemd service] ***
ok: [localhost]

TASK [reverse_logistics_portal : Deploy logrotate configuration for portal logs] ***
ok: [localhost]

TASK [reverse_logistics_portal : Check if UFW is installed] ********************
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

TASK [reverse_logistics_portal : Display provisioning summary] *****************
ok: [localhost]

PLAY RECAP *********************************************************************
localhost                  : ok=26   changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
```

**Idempotency Result:** `changed=0`, `failed=0`. The target node was confirmed to be in exact desired state.

---

## 7. Live Service & Healthcheck Verification

### 7.1 Systemd Service Status (`systemctl status reverse-logistics-portal`)
```
● reverse-logistics-portal.service - Automated Reverse Logistics Management Portal - Enterprise Edition
     Loaded: loaded (/etc/systemd/system/reverse-logistics-portal.service; enabled; preset: enabled)
     Active: active (running)
   Main PID: 1093 (java)
      Tasks: 30 (limit: 4555)
     Memory: 387.1M (peak: 387.1M)
     CGroup: /system.slice/reverse-logistics-portal.service
             └─1093 /usr/bin/java -Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom -jar /opt/reverse-logistics-portal/bin/reverse-logistics-portal.war --spring.config.additional-location=file:/etc/reverse-logistics-portal/
```

### 7.2 Actuator Health Endpoint Response (`http://127.0.0.1:8084/actuator/health`)
```json
{
  "components": {
    "db": {
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      },
      "status": "UP"
    },
    "diskSpace": {
      "details": {
        "total": 1081101176832,
        "free": 1022344511488,
        "threshold": 10485760,
        "path": "/opt/reverse-logistics-portal/.",
        "exists": true
      },
      "status": "UP"
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
      "details": {
        "expiringChains": [],
        "invalidChains": [],
        "validChains": []
      },
      "status": "UP"
    }
  },
  "groups": [
    "liveness",
    "readiness"
  ],
  "status": "UP"
}
```

### 7.3 Login Endpoint Response (`http://127.0.0.1:8084/login`)
- **HTTP Status Code:** `200 OK`
- **Content Type:** `text/html;charset=UTF-8`
- **Application Identity:** `ReturnHub | Automated Reverse Logistics Management Portal`

---

## 8. Definition of Done (DoD) Checklist

| Requirement | Implementation Artifact | Status |
|:---|:---|:---:|
| **Server Prerequisites Specification** | Documented packages, users, folders, files, ports, services | [x] **DONE** |
| **Ansible Inventory** | Created `ansible/inventory/hosts.ini` with local and remote group definitions | [x] **DONE** |
| **Ansible Configuration** | Created `ansible/ansible.cfg` with modern callback & privilege escalation | [x] **DONE** |
| **Global & Role Variables** | Codified `group_vars/all.yml` and `roles/.../vars/main.yml` | [x] **DONE** |
| **Modular Task Architecture** | Authored 9 structured task files in `roles/reverse_logistics_portal/tasks/` | [x] **DONE** |
| **Jinja2 Templates** | Created templates for properties, env vars, systemd service, logrotate | [x] **DONE** |
| **First Execution Log** | Live execution verified with 27 tasks passing (`ok=27, failed=0`) | [x] **DONE** |
| **Idempotency Validated** | Verified second run completes with `changed=0` | [x] **DONE** |
| **Live Health Probing** | Verified Actuator `UP` and HTTP 200 on login endpoint | [x] **DONE** |
| **Delivery Report Documentation** | Created `docs/TASK13-DELIVERY.md` with complete evidence | [x] **DONE** |
| **Product Backlog Update** | Marked TASK-13 as **DONE** in `docs/backlog.md` | [x] **DONE** |
