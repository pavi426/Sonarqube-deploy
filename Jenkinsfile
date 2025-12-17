pipeline {
  agent any

  tools {
    maven 'maven'
  }

  stages {

    stage('Checkout') {
      steps {
        git 'https://github.com/your-repo/springboot-app.git'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('SonarQube') {
          sh 'mvn clean verify sonar:sonar'
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 2, unit: 'MINUTES') {
          waitForQualityGate abortPipeline: true
        }
      }
    }

    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }

    stage('Upload to Nexus') {
      steps {
        sh '''
        mvn deploy \
        -DaltDeploymentRepository=nexus::default::http://localhost:8081/repository/maven-releases/
        '''
      }
    }

    stage('Docker Build & Push') {
      steps {
        sh '''
        docker build -t yourdockerhub/demo:1.0 .
        docker push yourdockerhub/demo:1.0
        '''
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        sh '''
        kubectl apply -f k8s/deployment.yaml
        kubectl apply -f k8s/service.yaml
        '''
      }
    }
  }
}

