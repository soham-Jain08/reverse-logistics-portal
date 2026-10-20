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
                echo "=== STAGE 1: CHECKOUT ==="
                echo "Target Environment: ${params.DEPLOY_ENV}"
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                echo "=== STAGE 2: BUILD & TEST ==="
                bat 'call mvnw.cmd clean test'
            }
        }

        stage('Package') {
            steps {
                echo "=== STAGE 3: PACKAGE ==="
                bat 'call mvnw.cmd package -DskipTests=true'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Deploy to Server') {
            steps {
                echo "=== STAGE 4: SERVER DEPLOYMENT ==="
                echo "Deploying to Tomcat on port ${params.TOMCAT_PORT} (${params.DEPLOY_ENV} environment)..."
                bat """
                    copy /Y "target\\reverse-logistics-portal-0.0.1-SNAPSHOT.war" "${params.TOMCAT_WEBAPPS}\\${params.CONTEXT_PATH}.war"
                """
                echo "WAR deployed to Tomcat webapps. Verifying HTTP health..."
                sleep time: 5, unit: 'SECONDS'
                bat "curl.exe -s -I http://localhost:${params.TOMCAT_PORT}/${params.CONTEXT_PATH}/login"
            }
        }
    }

    post {
        success {
            echo "=========================================================="
            echo "CI/CD PIPELINE SUCCESSFUL!"
            echo "Deployed Application URL: http://localhost:${params.TOMCAT_PORT}/${params.CONTEXT_PATH}/"
            echo "Environment: ${params.DEPLOY_ENV}"
            echo "=========================================================="
        }
        failure {
            echo "CI/CD Pipeline execution failed. Please check build logs."
        }
    }
}
