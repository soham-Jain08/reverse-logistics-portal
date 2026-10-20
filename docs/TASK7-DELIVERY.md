# Task 7 Deliverable Report: Jenkins Installation & Continuous Integration Job

**Project:** Automated Reverse Logistics Management Portal  
**Student:** Soham Kholapure (Roll No. 23102A0064, BE CMPN A)  
**Date:** October 21, 2026  
**Status:** **COMPLETED & VERIFIED**

---

## 1. Executive Summary & Deliverables Matrix

Task 7 requires installing and configuring Jenkins, connecting the **Reverse Logistics Management Portal** GitHub repository, setting up an automated Continuous Integration build job triggered by commit or scheduled polling, and archiving the built WAR artifact.

| Requirement | Deliverable | Status | Evidence Reference |
|:---|:---|:---:|:---|
| **1. Jenkins Installation & Job Setup** | Configured Jenkins CI job (`reverse-logistics-portal-ci`) | **DONE** | Section 2 & Section 3 |
| **2. Successful Build Log** | Build #3 console log (compilation, testing, packaging) | **DONE** | Section 4 |
| **3. Trigger Evidence** | SCM polling log (`H/5 * * * *`) querying GitHub | **DONE** | Section 5 |
| **4. Archived Artifact** | `reverse-logistics-portal-0.0.1-SNAPSHOT.war` (61.79 MB) | **DONE** | Section 6 |

---

## 2. Jenkins Server Environment

The Jenkins server runs in a dedicated directory with automated initialization scripts, isolated from the development server on port 8081:

* **Jenkins URL:** `http://localhost:8080/`
* **Jenkins Version:** `2.580.1 LTS`
* **Home Directory (`JENKINS_HOME`):** `C:\jenkins\home`
* **Java Runtime:** JDK 26 (`C:\Program Files\Java\jdk-26`) with `--enable-future-java`
* **Git Executable:** `C:\Program Files\Git\cmd\git.exe` (`git version 2.47.1.windows.2`)
* **Installed CI Plugins:**
  - `git` & `git-client` (Git SCM support)
  - `credentials` & `ssh-credentials` (Authentication management)
  - `scm-api` (Source Code Management abstraction)
  - `timestamper` (Console output timestamping)
  - `ws-cleanup` (Workspace management)
* **Port Allocation Map:**
  - `8080`: Jenkins CI Server
  - `8081`: Reverse Logistics Management Portal (Active Spring Boot dev instance)
  - `8082`: Apache Tomcat Deployment Target
  - `8083`: Docker Application Container

---

## 3. Configured Jenkins CI Job (`reverse-logistics-portal-ci`)

* **Job Type:** FreeStyle Project
* **Job URL:** `http://localhost:8080/job/reverse-logistics-portal-ci/`
* **Source Code Management:** Git
  - **Repository URL:** `https://github.com/soham-Jain08/reverse-logistics-portal.git`
  - **Branch Specifier:** `*/main`
* **Build Trigger:** Scheduled SCM Polling (`H/5 * * * *` — every 5 minutes)
* **Build Step:** Windows Batch Command:
  ```cmd
  set JAVA_HOME=C:\Program Files\Java\jdk-26
  set PATH=%JAVA_HOME%\bin;%PATH%
  call mvnw.cmd clean package -DskipTests=false
  ```
* **Post-Build Action:** Archive Artifacts:
  - **Path:** `target/*.war`
  - **Policy:** Archive only if build is successful

### Jenkins Job Configuration (`config.xml`)

```xml
<?xml version='1.1' encoding='UTF-8'?>
<project>
  <actions/>
  <description>Automated Reverse Logistics Management Portal - Continuous Integration Build Job</description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.plugins.git.GitSCM" plugin="git">
    <configVersion>2</configVersion>
    <userRemoteConfigs>
      <hudson.plugins.git.UserRemoteConfig>
        <url>https://github.com/soham-Jain08/reverse-logistics-portal.git</url>
      </hudson.plugins.git.UserRemoteConfig>
    </userRemoteConfigs>
    <branches>
      <hudson.plugins.git.BranchSpec>
        <name>*/main</name>
      </hudson.plugins.git.BranchSpec>
    </branches>
    <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
    <submoduleCfg class="empty-list"/>
    <extensions/>
  </scm>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers>
    <hudson.triggers.SCMTrigger>
      <spec>H/5 * * * *</spec>
      <ignorePostCommitHooks>false</ignorePostCommitHooks>
    </hudson.triggers.SCMTrigger>
  </triggers>
  <concurrentBuild>false</concurrentBuild>
  <builders>
    <hudson.tasks.BatchFile>
      <command>set JAVA_HOME=C:\Program Files\Java\jdk-26
set PATH=%JAVA_HOME%\bin;%PATH%
call mvnw.cmd clean package -DskipTests=false
</command>
      <configuredLocalRules/>
    </hudson.tasks.BatchFile>
  </builders>
  <publishers>
    <hudson.tasks.ArtifactArchiver>
      <artifacts>target/*.war</artifacts>
      <allowEmptyArchive>false</allowEmptyArchive>
      <onlyIfSuccessful>true</onlyIfSuccessful>
      <fingerprint>false</fingerprint>
      <defaultExcludes>true</defaultExcludes>
      <caseSensitive>true</caseSensitive>
      <followSymlinks>false</followSymlinks>
    </hudson.tasks.ArtifactArchiver>
  </publishers>
  <buildWrappers/>
</project>
```

---

## 4. Successful Build Log (Build #3 Console Output)

```text
Started by user admin
Running as SYSTEM
Building in workspace C:\jenkins\home\workspace\reverse-logistics-portal-ci
The recommended git tool is: NONE
No credentials specified
Cloning the remote Git repository
Cloning repository https://github.com/soham-Jain08/reverse-logistics-portal.git
 > git.exe init C:\jenkins\home\workspace\reverse-logistics-portal-ci # timeout=10
Fetching upstream changes from https://github.com/soham-Jain08/reverse-logistics-portal.git
 > git.exe --version # timeout=10
 > git --version # 'git version 2.47.1.windows.2'
 > git.exe fetch --tags --force --progress -- https://github.com/soham-Jain08/reverse-logistics-portal.git +refs/heads/*:refs/remotes/origin/* # timeout=10
 > git.exe config remote.origin.url https://github.com/soham-Jain08/reverse-logistics-portal.git # timeout=10
 > git.exe config --add remote.origin.fetch +refs/heads/*:refs/remotes/origin/* # timeout=10
Avoid second fetch
 > git.exe rev-parse "refs/remotes/origin/main^{commit}" # timeout=10
Checking out Revision e8d6e616b8c10205314c02158a4445dead8d3de5 (refs/remotes/origin/main)
 > git.exe config core.sparsecheckout # timeout=10
 > git.exe checkout -f e8d6e616b8c10205314c02158a4445dead8d3de5 # timeout=10
Commit message: "Merge fix for create return form and search filters"
First time build. Skipping changelog.
[reverse-logistics-portal-ci] $ cmd /c call C:\Users\CSC\AppData\Local\Temp\jenkins14301380379279458209.bat

C:\jenkins\home\workspace\reverse-logistics-portal-ci>set JAVA_HOME=C:\Program Files\Java\jdk-26 

C:\jenkins\home\workspace\reverse-logistics-portal-ci>set PATH=C:\Program Files\Java\jdk-26\bin;...

C:\jenkins\home\workspace\reverse-logistics-portal-ci>call mvnw.cmd clean package -DskipTests=false 
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------------< com.soham:reverse-logistics-portal >-----------------
[INFO] Building reverse-logistics-portal 0.0.1-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ war ]---------------------------------
[INFO] 
[INFO] --- clean:3.5.0:clean (default-clean) @ reverse-logistics-portal ---
[INFO] 
[INFO] --- resources:3.5.0:resources (default-resources) @ reverse-logistics-portal ---
[INFO] Copying 1 resource from src\main\resources to target\classes
[INFO] Copying 14 resources from src\main\resources to target\classes
[INFO] 
[INFO] --- compiler:3.15.0:compile (default-compile) @ reverse-logistics-portal ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 24 source files with javac [debug parameters release 17] to target\classes
[INFO] 
[INFO] --- resources:3.5.0:testResources (default-testResources) @ reverse-logistics-portal ---
[INFO] Copying 1 resource from src\test\resources to target\test-classes
[INFO] 
[INFO] --- compiler:3.15.0:testCompile (default-testCompile) @ reverse-logistics-portal ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 2 source files with javac [debug parameters release 17] to target\test-classes
[INFO] 
[INFO] --- surefire:3.5.6:test (default-test) @ reverse-logistics-portal ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.soham.reverselogistics.ReturnRequestWorkflowTests

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.1.1)

2026-10-21T00:36:14.484+05:30  INFO 35132 --- [reverse-logistics-portal] [           main] c.s.r.ReturnRequestWorkflowTests         : Starting ReturnRequestWorkflowTests using Java 26 with PID 35132 (started by CSC in C:\jenkins\home\workspace\reverse-logistics-portal-ci)
2026-10-21T00:36:14.488+05:30  INFO 35132 --- [reverse-logistics-portal] [           main] c.s.r.ReturnRequestWorkflowTests         : No active profile set, falling back to 1 default profile: "default"
2026-10-21T00:36:17.252+05:30  INFO 35132 --- [reverse-logistics-portal] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 130 ms. Found 2 JPA repository interfaces.
2026-10-21T00:36:21.575+05:30  INFO 35132 --- [reverse-logistics-portal] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-10-21T00:36:23.994+05:30  INFO 35132 --- [reverse-logistics-portal] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2026-10-21T00:36:30.469+05:30  INFO 35132 --- [reverse-logistics-portal] [           main] c.s.r.ReturnRequestWorkflowTests         : Started ReturnRequestWorkflowTests in 16.626 seconds
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 19.56 s -- in com.soham.reverselogistics.ReturnRequestWorkflowTests
[INFO] Running com.soham.reverselogistics.ReverseLogisticsPortalApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.036 s -- in com.soham.reverselogistics.ReverseLogisticsPortalApplicationTests
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] 
[INFO] --- war:3.5.1:war (default-war) @ reverse-logistics-portal ---
[INFO] Packaging webapp
[INFO] Assembling webapp [reverse-logistics-portal] in [C:\jenkins\home\workspace\reverse-logistics-portal-ci\target\reverse-logistics-portal-0.0.1-SNAPSHOT]
[INFO] Processing war project
[INFO] Building war: C:\jenkins\home\workspace\reverse-logistics-portal-ci\target\reverse-logistics-portal-0.0.1-SNAPSHOT.war
[INFO] 
[INFO] --- spring-boot:4.1.1:repackage (repackage) @ reverse-logistics-portal ---
[INFO] Replacing main artifact C:\jenkins\home\workspace\reverse-logistics-portal-ci\target\reverse-logistics-portal-0.0.1-SNAPSHOT.war with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] The original artifact has been renamed to C:\jenkins\home\workspace\reverse-logistics-portal-ci\target\reverse-logistics-portal-0.0.1-SNAPSHOT.war.original
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  39.920 s
[INFO] Finished at: 2026-10-21T00:36:39+05:30
[INFO] ------------------------------------------------------------------------
Archiving artifacts
Finished: SUCCESS
```

---

## 5. Trigger Evidence (SCM Polling Log & Automated Build #4)

The job is configured with an SCM trigger (`H/5 * * * *`) that polls the remote GitHub repository for changes.

### 5.1 SCM Polling Log
Below is the verified polling execution log from Jenkins detecting the new commit `6146eea`:

```text
Started on 21 Oct 2026, 12:40:49 am
Using strategy: Default
[poll] Last Built Revision: Revision e8d6e616b8c10205314c02158a4445dead8d3de5 (refs/remotes/origin/main)
The recommended git tool is: NONE
No credentials specified
 > git.exe --version # timeout=10
 > git --version # 'git version 2.47.1.windows.2'
 > git.exe ls-remote -h -- https://github.com/soham-Jain08/reverse-logistics-portal.git # timeout=10
Found 5 remote heads on https://github.com/soham-Jain08/reverse-logistics-portal.git
[poll] Latest remote head revision on refs/heads/main is: 6146eea7460741c4b45c645c8439d183653539a6
Done. Took 1.4 sec
Changes found
```

### 5.2 Automated Build Execution Trigger Evidence
Upon detecting `Changes found`, Jenkins immediately and automatically enqueued and executed **Build #4**:

* **Build Number:** `4`
* **Trigger Cause:** `Started by an SCM change` (`hudson.triggers.SCMTrigger$SCMTriggerCause`)
* **Checked-out Revision:** `6146eea7460741c4b45c645c8439d183653539a6`
* **Build Result:** `SUCCESS` (duration: ~51s)
* **Archived Artifact:** `target/reverse-logistics-portal-0.0.1-SNAPSHOT.war` (61.79 MB)

---

## 6. Archived Artifact Evidence

The build artifact produced by Maven packaging was automatically archived by the Jenkins `ArtifactArchiver` post-build task:

* **Artifact Name:** `reverse-logistics-portal-0.0.1-SNAPSHOT.war`
* **File Size:** `61,792,893 bytes` (`61.79 MB`)
* **File Path on CI Host:**
  `C:\jenkins\home\jobs\reverse-logistics-portal-ci\builds\3\archive\target\reverse-logistics-portal-0.0.1-SNAPSHOT.war`
* **Direct Jenkins Download Link:**
  `http://localhost:8080/job/reverse-logistics-portal-ci/3/artifact/target/reverse-logistics-portal-0.0.1-SNAPSHOT.war`
* **Verification Command:**
  ```powershell
  Get-Item "C:\jenkins\home\jobs\reverse-logistics-portal-ci\builds\3\archive\target\reverse-logistics-portal-0.0.1-SNAPSHOT.war"
  ```
* **Output:**
  ```text
  Name                                          Length LastWriteTime      
  ----                                          ------ -------------      
  reverse-logistics-portal-0.0.1-SNAPSHOT.war 61792893 01-02-1980 05:30:00
  ```

---

## 7. How to Access and Re-Run

1. **Open Jenkins UI:**
   Navigate to [http://localhost:8080/](http://localhost:8080/)
   Credentials:
   - **Username:** `admin`
   - **Password:** `admin`
2. **Access the CI Job:**
   Navigate to [http://localhost:8080/job/reverse-logistics-portal-ci/](http://localhost:8080/job/reverse-logistics-portal-ci/)
3. **Trigger a Manual Build:**
   Click **"Build Now"** in the left sidebar.
4. **Inspect Archived Artifacts:**
   Click on the latest build number (e.g. `#3`) -> Click **"Build Artifacts"** -> Download `reverse-logistics-portal-0.0.1-SNAPSHOT.war`.
