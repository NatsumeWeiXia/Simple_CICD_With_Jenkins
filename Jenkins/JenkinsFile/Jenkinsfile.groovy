#!groovy

pipeline {

    agent none

    stages {

        stage('代码编译') {

            agent { label '172.16.41.13' }

            steps {
                script {

                    try {
                        sh 'pwd'

                        docker.image('172.16.41.2/maven-jdk7:3-jdk-7')
                                .inside(' -v /root/.m2:/root/.m2 ') {
                            
                            echo '*** maven version ***'

                            sh 'mvn --version'

                            echo '*** GIT PULL ***'

//            git credentialsId: 'XXXXXXXX-XXXXXXXX-XXXXXXXX', url: 'http://172.16.41.4/XXX.git'
                            git credentialsId: 'XXXXXXXX-XXXXXXXX-XXXXXXXX', url: 'http://172.16.41.4/XXX.git'

                            echo '*** BUILD WAR ***'

                            sh 'mvn -U -am clean package'
                        }

                        docker.withRegistry('http://172.16.41.2', 'XXXXXXXX-XXXXXXXX-XXXXXXXX') {

                            def mosImage
                            stage('镜像构建') {
                                echo '*** BUILD IMAGE ***'

                                mosImage = docker.build('172.16.41.2/XXX:xxxx')

                                echo '*** PUSH IMAGE ***'

                                mosImage.push()
                            }
                        }

                        node('172.16.41.15') {

                            stage('容器更新') {
                                echo '*** COMPOSE ***'

                                sh 'pwd'

                                sh 'docker-compose down --rmi all'

                                sh 'docker-compose up -d'
                            }
                        }

                        if (currentBuild.result == null) {
                            currentBuild.result = "SUCCESS"
                        }
                    } catch (err) {
                        if (currentBuild.result == null) {
                            currentBuild.result = "FAILURE"
                        }
                        throw err
                    } finally {
                        step([$class       : 'InfluxDbPublisher',
                              customData   : null,
                              customDataMap: null,
                              customPrefix : null,
                              traget       : 'http://172.16.41.16:8086,jenkins_data'])
                    }
                }
            }
        }
    }

    post() {
        success {

            emailext(
                    subject: '构建成功:${PROJECT_NAME} - Build # ${BUILD_NUMBER}!',
                    body: '${JELLY_SCRIPT,template="mail-success"}',
                    to: "songyang@XXXXXXX.com",
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }

        failure {

            emailext(
                    subject: '构建失败:${PROJECT_NAME} - Build # ${BUILD_NUMBER}!',
                    body: '${JELLY_SCRIPT,template="mail-failure"}',
                    to: "songyang@XXXXXXX.com",
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
    }

}




