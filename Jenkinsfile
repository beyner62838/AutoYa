pipeline {
  agent any

  options {
    disableConcurrentBuilds()
    timestamps()
  }

  environment {
    // Archivos específicos del entorno QA
    COMPOSE_FILE = 'docker-compose.qa.yml'
    ENV_FILE = '.env.qa'
  }

  stages {
    stage('Checkout') {
      steps {
        // Garantiza que Jenkins tenga el código más reciente
        checkout scm
        echo "🌀 Branch actual: ${env.BRANCH_NAME}"
      }
    }

    stage('Build images') {
      steps {
        sh """
          echo "🚧 Construyendo imágenes Docker para QA..."
          docker compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} build --pull --parallel
        """
      }
    }

    stage('Clean existing containers') {
      steps {
        sh """
          echo "🧹 Deteniendo y eliminando contenedores previos si existen..."
          docker stop redis minio || true
          docker rm redis minio || true
        """
      }
    }

    stage('Deploy') {
      steps {
        sh """
          echo "🚀 Desplegando entorno QA..."
          docker compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} down -v || true
          docker compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} up -d --build
        """
      }
    }
  }

  post {
    success {
      echo "✅ Deploy QA exitoso en la rama: ${env.BRANCH_NAME}"
    }
    failure {
      echo "❌ Deploy QA falló en la rama: ${env.BRANCH_NAME}"
    }
  }
}
