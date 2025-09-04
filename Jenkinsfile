pipeline {
    agent any
    stages{
        stage('Clone repo') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'tokentoken', url: 'https://github.com/MucuriAmalia/amucuribancassurance.git']])
            }
        }

        stage('Intergrate for absa main application') {
            steps {
                script {
                    sh 'cp /var/lib/jenkins/ci-files/absa/main/pom.xml pom.xml'
                    sh 'cp /var/lib/jenkins/ci-files/absa/main/config.properties src/main/resources/config.properties'
                    sh 'cp /var/lib/jenkins/ci-files/absa/main/AicareagencyUAT.pfx AicareagencyUAT.pfx'
                    sh 'cp /var/lib/jenkins/ci-files/absa/main/Dockerfile Dockerfile'
		    sh 'cp /var/lib/jenkins/ci-files/absa/main/id_rsa id_rsa'
		    sh 'ls default-calculators'
                    sh 'ls cert'
		    sh 'cp -r /var/lib/jenkins/ci-files/absa/main/images images'
		    sh 'cp /var/lib/jenkins/ci-files/absa/main/startup.sh startup.sh'
		    sh 'cp /var/lib/jenkins/ci-files/absa/main/config config'
                }
            }
        }

        stage('Clean project main') {
            steps {
                sh "mvn clean"
            }

        }

        stage('Build Docker image main'){
            steps {
                script {

                def appName = "absabanca"

                sh "docker build -t ${appName}:3.0.0 ."
            }
            }

        }

        stage('Push to Docker Registry main'){
            steps{

                 withCredentials([string(credentialsId: 'dockeruser', variable: 'dockeruser'),string(credentialsId: 'dockerpasswd', variable: 'dockerpasswd')]){

                        script {
                            def appName = "absabanca"
                            sh """
                            aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/y4m1g9m0
                            docker tag ${appName}:3.0.0 public.ecr.aws/y4m1g9m0/absabanca-public:3.0.1
                            docker push public.ecr.aws/y4m1g9m0/absabanca-public:3.0.1
                            """

                    }
                }

            }

        }

        stage('Intergrate for absa finance application') {
            steps {
                script {
                    sh 'cp /var/lib/jenkins/ci-files/absa/finance/pom.xml pom.xml'
                    sh 'cp /var/lib/jenkins/ci-files/absa/finance/config.properties src/main/resources/config.properties'
                    sh 'cp /var/lib/jenkins/ci-files/absa/finance/AicareagencyUAT.pfx AicareagencyUAT.pfx'
                    sh 'cp /var/lib/jenkins/ci-files/absa/finance/Dockerfile Dockerfile'
		            sh 'cp /var/lib/jenkins/ci-files/absa/finance/absabanca.pem absabanca.pem'
		            sh 'ls default-calculators'
                    sh 'ls cert'
		            sh 'cp -r /var/lib/jenkins/ci-files/absa/finance/images images'
		            sh 'cp /var/lib/jenkins/ci-files/absa/finance/startup.sh startup.sh'
                }
            }
        }

        stage('Clean project finance') {
            steps {
                sh "mvn clean"
            }

        }

        stage('Build Docker image finance'){
            steps {
                script {

                def appName = "absabancafinance"

                sh "docker build -t ${appName}:6.0.0 ."
            }
            }

        }

        stage('Push to Docker Registry finance'){
            steps{

                 withCredentials([string(credentialsId: 'dockeruser', variable: 'dockeruser'),string(credentialsId: 'dockerpasswd', variable: 'dockerpasswd')]){

                        script {
                            def appName = "absabancafinance"
                            sh """
                            aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/y4m1g9m0
                            docker tag ${appName}:6.0.0 public.ecr.aws/y4m1g9m0/absabanca-public:6.0.0
                            docker push public.ecr.aws/y4m1g9m0/absabanca-public:6.0.0
                            """

                    }
                }

            }

        }
    }
}