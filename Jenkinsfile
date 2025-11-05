pipeline {
  agent any

  options {
    disableConcurrentBuilds()
    timestamps()
  }

  environment {
    GIT_URL = &apos;https://github.com/beyner62838/AutoYa.git&apos;
    GIT_BRANCH = &apos;dev&apos;
    COMPOSE_FILE = &apos;docker-compose.dev.yml&apos;
    ENV_FILE = &apos;.env.dev&apos;
  }

  stages {
    stage(&apos;Checkout repo&apos;) {
      steps {
        git branch: &quot;${env.GIT_BRANCH}&quot;, url: &quot;${env.GIT_URL}&quot;
      }
    }

    stage(&apos;Build images&apos;) {
      steps {
        sh &quot;&quot;&quot;
          docker compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} build --pull --parallel
        &quot;&quot;&quot;
      }
    }

    stage(&apos;Deploy&apos;) {
      steps {
        sh &quot;&quot;&quot;
          docker compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} down -v || true
          docker compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} up -d --build
        &quot;&quot;&quot;
      }
    }
  }

  post {
    success {
      echo &quot;✅ Deploy successful!&quot;
    }
    failure {
      echo &quot;❌ Deploy failed!&quot;
    }
  }
}
