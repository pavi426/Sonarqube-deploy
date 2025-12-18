pipeline {
    agent any

    environment {
        SONARQUBE = 'SonarQube' // SonarQube server name in Jenkins config
        MAVEN_HOME = tool name: 'maven', type: 'maven'
        DOCKER_IMAGE = "demo-app:${env.BUILD_NUMBER}"
        K8S_NAMESPACE = "default"
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/pavi426/Sonarqube-deploy.git'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(SONARQUBE) {
                    sh "${MAVEN_HOME}/bin/mvn clean verify sonar:sonar"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build & Deploy to Nexus') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean deploy -s /var/lib/jenkins/.m2/settings.xml"
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    docker.build(DOCKER_IMAGE)
                    docker.withRegistry('http://localhost:5000', 'docker-credentials') {
                        docker.image(DOCKER_IMAGE).push()
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                kubectl apply -f k8s/deployment.yaml -n ${K8S_NAMESPACE}
                kubectl apply -f k8s/service.yaml -n ${K8S_NAMESPACE}
                """
            }
        }
    }
}
