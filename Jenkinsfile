pipeline {
    agent {
        docker {
            image 'maven:3.9-eclipse-temurin-17'
        }
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Notify Pending') {
                    steps {
                        githubNotify(
                            status: 'PENDING',
                            description: 'Started',
                            context: 'CI/CD',
                            credentialsId: 'github-token'
                        )
                    }
                }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }

    post {
        success {
            echo '✅'
            githubNotify(
                            status: 'SUCCESS',
                            description: 'Passed',
                            context: 'CI/CD',
                            credentialsId: 'github-token'
                        )
        }
        failure {
            echo '❌'
            githubNotify(
                status: 'FAILURE',
                description: 'Failed',
                context: 'CI/CD',
                credentialsId: 'github-token'
            )
        }
    }
}