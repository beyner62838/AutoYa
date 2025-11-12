pipeline {
  agent any

  options {
    disableConcurrentBuilds()
    timestamps()
  }

  environment {
    // Se detecta automáticamente la rama desde el contexto del Multibranch Pipeline
    COMPOSE_FILE = 'docker-compose.dev.yml'
    ENV_FILE = '.env.dev'
  }

  stages {
    stage('Checkout') {
      steps {
        // Jenkins Multibranch ya hace el checkout automáticamente,
        // pero esto garantiza que tengamos la última versión
        checkout scm
        echo "🌀 Branch actual: ${env.BRANCH_NAME}"
      }
    }

    stage('Build images') {
      steps {
        sh """
          docker compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} build --pull --parallel
        """
      }
    }

    stage('Deploy') {
      steps {
        sh """
          docker compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} down -v || true
          docker compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} up -d --build
        """
      }
    }
  }

  post {
    success {
      echo "✅ Deploy successful on branch: ${env.BRANCH_NAME}"
    }
    failure {
      echo "❌ Deploy failed on branch: ${env.BRANCH_NAME}"
    }
  }
}
