pipeline {
    agent any

    environment {
        SONARQUBE = 'SonarQube'                     // Name of your SonarQube server in Jenkins config
        MAVEN_HOME = tool name: 'maven', type: 'maven'
        DOCKER_IMAGE = "demo-app:${env.BUILD_NUMBER}"
        K8S_NAMESPACE = "default"
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-amd64'  // Must match your java -version
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        SONAR_HOST_URL = 'http://56.228.7.5:9000'       // Your SonarQube URL
        SONAR_AUTH_TOKEN = credentials('sonarqube')  // Jenkins credential ID for SonarQube token
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/pavi426/Sonarqube-deploy.git'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') { // Must match Jenkins SonarQube server name
                    sh """
                        ${MAVEN_HOME}/bin/mvn clean verify sonar:sonar \
                        -Dsonar.projectKey=demo \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_AUTH_TOKEN}
                    """
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
