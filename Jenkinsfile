pipeline {
  agent any

  tools {
    maven 'maven'
  }

  stages {

    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/pavi426/Sonarqube-deploy.git'

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
        timeout(time: 5, unit: 'MINUTES') {
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
            --upload-file target/demo-1.0.jar \
            http://16.171.200.176:8081/repository/maven-releases123/com/example/demo/1.0/demo-1.0.jar

          """
        }
      }
    }

    stage('Docker Build & Push') {
  steps {
    withCredentials([usernamePassword(
      credentialsId: 'dockerhub-creds',
      usernameVariable: 'DOCKER_USER',
      passwordVariable: 'DOCKER_PASS'
    )]) {
      sh '''
        docker login -u $DOCKER_USER -p $DOCKER_PASS
        docker build -t $DOCKER_USER/demo:1.0 .
        docker push $DOCKER_USER/demo:1.0
      '''
    }
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
