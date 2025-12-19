pipeline {
  agent any

  tools {
    maven 'maven'
  }

  stages {

    stage('Checkout') {
      steps {
        git 'https://github.com/pavi426/springboot-app.git'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withCredentials([string(credentialsId: 'sonartoken-id', variable: 'SONAR_TOKEN')]) {
          sh '''
            mvn clean verify sonar:sonar \
            -Dsonar.login=$SONAR_TOKEN
          '''
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
        withCredentials([usernamePassword(credentialsId: 'nexus-creds', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
          sh """
            curl -v -u $NEXUS_USER:$NEXUS_PASS \
            --upload-file target/my-app.jar \
            http://<NEXUS_HOST>:8081/repository/maven-releases/my-app.jar
          """
        }
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

  } // closes stages
} // closes pipeline
