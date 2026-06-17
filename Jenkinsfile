pipeline {
    agent any

    triggers {
        pollSCM('*/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean compile'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }

        stage('Deploy') {
            when {
                expression { currentBuild.currentResult == 'SUCCESS' }
            }
            steps {
                sh 'ansible-playbook -i ansible/inventory.yml ansible/playbook.yml'
            }
        }
    }

    post {
        failure {
            script {
                def devEmail = sh(
                    script: 'git log -1 --format="%ae"',
                    returnStdout: true
                ).trim()
                mail to: "srengty@gmail.com, zenocoder101@gmail.com",
                     cc: devEmail,
                     subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                     body: """
                        BUILD FAILED

                        Project : ${env.JOB_NAME}
                        Build # : ${env.BUILD_NUMBER}
                        URL     : ${env.BUILD_URL}

                        Committed by : ${devEmail}

                         
                     """
            }
        }
        success {
            echo "Build, tests, and deploy completed successfully"
        }
    }
}
