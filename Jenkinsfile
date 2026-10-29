pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['production', 'staging', 'development'], description: 'Deployment environment target')
        string(name: 'DOCKER_REGISTRY', defaultValue: 'localhost:5000', description: 'Target Docker Registry endpoint')
        string(name: 'IMAGE_NAME', defaultValue: 'reverse-logistics-portal', description: 'Docker image repository name')
        string(name: 'CONTAINER_NAME', defaultValue: 'rlp-production-container', description: 'Target deployed container name')
        string(name: 'HOST_PORT', defaultValue: '8083', description: 'Host port mapped to container port 8080')
        string(name: 'APP_TEST_URL', defaultValue: 'http://localhost:8081', description: 'Portal URL for Selenium journeys')
    }

    environment {
        JAVA_HOME = '/opt/java/openjdk'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "=========================================================="
                echo "STAGE 1: SCM CHECKOUT"
                echo "Environment: ${params.DEPLOY_ENV}"
                echo "Build Number: ${env.BUILD_NUMBER}"
                echo "=========================================================="
                checkout scm
            }
        }

        stage('Continuous Testing') {
            steps {
                echo "=========================================================="
                echo "STAGE 2: CONTINUOUS TESTING (SELENIUM & UNIT SUITE)"
                echo "Executing Selenium WebDriver user journeys & unit tests..."
                echo "Target portal URL for browser tests: ${params.APP_TEST_URL}"
                echo "=========================================================="
                sh """
                    chmod +x mvnw
                    ./mvnw package -DskipTests=true -q
                    nohup java -jar target/*.war --server.port=8081 > /tmp/app.log 2>&1 &
                    echo "Waiting for app to start..."
                    sleep 30
                    curl --retry 15 --retry-delay 5 --retry-connrefused -s -f http://localhost:8081/actuator/health
                    echo "App is up, running tests..."
                    ./mvnw test -Dapp.url=http://localhost:8081 -Dsurefire.failIfNoSpecifiedTests=false
                """
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
                sh 'chmod +x mvnw && ./mvnw package -DskipTests=true'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "=========================================================="
                echo "STAGE 4: BUILD VERSIONED DOCKER IMAGE"
                echo "Registry Target: ${params.DOCKER_REGISTRY}"
                echo "Image Repository: ${params.IMAGE_NAME}"
                echo "Version Tag: build-${env.BUILD_NUMBER}"
                echo "=========================================================="
                sh """
                    docker build -t ${params.DOCKER_REGISTRY}/${params.IMAGE_NAME}:build-${env.BUILD_NUMBER} -t ${params.DOCKER_REGISTRY}/${params.IMAGE_NAME}:latest -t ${params.IMAGE_NAME}:build-${env.BUILD_NUMBER} -t ${params.IMAGE_NAME}:latest .
                """
                echo "Image build successful. Inspecting built image:"
                sh "docker images ${params.IMAGE_NAME}"
            }
        }

        stage('Publish to Registry') {
            steps {
                echo "=========================================================="
                echo "STAGE 5: PUBLISH IMAGE TO REGISTRY"
                echo "Pushing versioned image and latest tag to ${params.DOCKER_REGISTRY}..."
                echo "=========================================================="
                sh """
                    docker push ${params.DOCKER_REGISTRY}/${params.IMAGE_NAME}:build-${env.BUILD_NUMBER}
                    docker push ${params.DOCKER_REGISTRY}/${params.IMAGE_NAME}:latest
                """
                echo "Verifying image in Docker Registry catalog:"
                sh "curl -s http://${params.DOCKER_REGISTRY}/v2/${params.IMAGE_NAME}/tags/list"
            }
        }

        stage('Deploy Container') {
            steps {
                echo "=========================================================="
                echo "STAGE 6: CONTINUOUS DEPLOYMENT (CONTAINER DEPLOYMENT)"
                echo "Deploying fresh container '${params.CONTAINER_NAME}' on port ${params.HOST_PORT}..."
                echo "Deploying image: ${params.DOCKER_REGISTRY}/${params.IMAGE_NAME}:build-${env.BUILD_NUMBER}"
                echo "=========================================================="
                sh """
                    docker stop ${params.CONTAINER_NAME} || true
                    docker rm -f ${params.CONTAINER_NAME} || true
                    docker stop rlp-container || true
                    docker rm -f rlp-container || true
                    docker run -d --name ${params.CONTAINER_NAME} -p ${params.HOST_PORT}:8080 ${params.DOCKER_REGISTRY}/${params.IMAGE_NAME}:build-${env.BUILD_NUMBER}
                """
                echo "Verifying deployed container status:"
                sh "docker ps --filter \"name=${params.CONTAINER_NAME}\""
            }
        }

        stage('Verify Deployment') {
            steps {
                echo "=========================================================="
                echo "STAGE 7: POST-DEPLOYMENT HEALTH VERIFICATION"
                echo "Waiting for Spring Boot initialization inside container..."
                echo "=========================================================="
                sleep time: 25, unit: 'SECONDS'
                echo "Probing container health endpoint with automatic retry..."
                sh "curl --retry 15 --retry-delay 5 --retry-connrefused -s -f http://localhost:${params.HOST_PORT}/actuator/health"
                echo "Probing container login endpoint..."
                sh "curl --retry 10 --retry-delay 3 --retry-connrefused -s -o /dev/null -w \"HTTP STATUS: %{http_code}\" http://localhost:${params.HOST_PORT}/login"
                echo "Fresh container is healthy and serving HTTP traffic!"
            }
        }
    }

    post {
        success {
            echo "=========================================================="
            echo "JENKINS-DOCKER CD PIPELINE SUCCESSFUL!"
            echo "Image Version: ${params.DOCKER_REGISTRY}/${params.IMAGE_NAME}:build-${env.BUILD_NUMBER}"
            echo "Deployed Container: ${params.CONTAINER_NAME} (Port ${params.HOST_PORT})"
            echo "Live Application URL: http://localhost:${params.HOST_PORT}/"
            echo "Health Check: http://localhost:${params.HOST_PORT}/actuator/health"
            echo "Target Environment: ${params.DEPLOY_ENV}"
            echo "=========================================================="
        }
        failure {
            echo "=========================================================="
            echo "CI/CD PIPELINE HALTED: Test, build, or deployment failure!"
            echo "Deployment was halted to protect the target environment."
            echo "Inspect test reports, build logs, or container diagnostics in Jenkins."
            echo "=========================================================="
        }
    }
}
