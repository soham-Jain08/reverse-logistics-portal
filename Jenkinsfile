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
