pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')  // Check for changes every 5 minutes (triggers on merge/push)
    }

    options {
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build WAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy to Tomcat') {
            when {
                branch 'master'  // Only deploy on master branch (after merge)
            }
            steps {
                echo 'Deploying new version...'
                sh '''
                    # Clean old deployment
                    rm -rf /deploy-webapps/*

                    # Copy new WAR as ROOT.war (deploys at http://localhost:8080/)
                    cp target/*.war /deploy-webapps/ROOT.war
                '''
            }
        }
    }

    post {
        success {
            echo 'Deployment successful! Access at http://localhost:8080/'
        }
        failure {
            echo 'Build or deploy failed.'
        }
        always {
            // Optional: archive WAR
            archiveArtifacts artifacts: 'target/*.war', allowEmptyArchive: true
        }
    }
}