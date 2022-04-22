pipeline {
  agent {
    node {
      label 'maven'
    }

  }
  stages {
    stage('clone code') {
      agent none
      steps {
        container('maven') {
          git(url: 'https://github.com/hungwen0425/yygh-parent-master.git', credentialsId: 'github-id', branch: 'main', changelog: true, poll: false)
          sh 'ls -al'
        }
      }
    }

    stage('project compile') {
      agent none
      steps {
        container('maven') {
          sh 'ls'
          sh 'mvn clean package -Dmaven.test.skip=true'
          sh 'ls hospital-manage/target'
        }
      }
    }

    stage('default-2') {
      parallel {
        stage('build and push hospital-manage image') {
          agent none
          steps {
            container('maven') {
              sh 'ls hospital-manage/target'
              sh 'docker build -t hospital-manage:latest -f hospital-manage/Dockerfile  ./hospital-manage/'
              withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag hospital-manage:latest $REGISTRY/$DOCKERHUB_NAMESPACE/hospital-manage:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/hospital-manage:SNAPSHOT-$BUILD_NUMBER'
             }
            }
          }
        }

        stage('build and push server-gateway image') {
          agent none
          steps {
            container('maven') {
              sh 'ls server-gateway/target'
              sh 'docker build -t server-gateway:latest -f server-gateway/Dockerfile  ./server-gateway/'
              withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag server-gateway:latest $REGISTRY/$DOCKERHUB_NAMESPACE/server-gateway:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/server-gateway:SNAPSHOT-$BUILD_NUMBER'
              }
            }
          }
        }

        stage('build and push service-cmn image') {
          agent none
          steps {
            container('maven') {
              sh 'ls service/service-cmn/target'
              sh 'docker build -t service-cmn:latest -f service/service-cmn/Dockerfile  ./service/service-cmn/'
              withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag service-cmn:latest $REGISTRY/$DOCKERHUB_NAMESPACE/service-cmn:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/service-cmn:SNAPSHOT-$BUILD_NUMBER'
              }
            }
          }
        }

        stage('build and push service-hosp image') {
          agent none
          steps {
            container('maven') {
              sh 'ls service/service-order/target'
              sh 'docker build -t service-order:latest -f service/service-order/Dockerfile  ./service/service-hosp/'
              withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag service-hosp:latest $REGISTRY/$DOCKERHUB_NAMESPACE/service-hosp:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/service-hosp:SNAPSHOT-$BUILD_NUMBER'
              }
            }
          }
        }

        stage('build and push service-order image') {
          agent none
          steps {
            container('maven') {
              sh 'ls service/service-order/target'
              sh 'docker build -t service-order:latest -f service/service-order/Dockerfile  ./service/service-order/'
              withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag service-order:latest $REGISTRY/$DOCKERHUB_NAMESPACE/service-order:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/service-order:SNAPSHOT-$BUILD_NUMBER'
              }
            }
          }
        }

        stage('build and push service-oss image') {
          agent none
          steps {
            container('maven') {
              sh 'ls service/service-oss/target'
              sh 'docker build -t service-oss:latest -f service/service-oss/Dockerfile  ./service/service-oss/'
              withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag service-oss:latest $REGISTRY/$DOCKERHUB_NAMESPACE/service-oss:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/service-oss:SNAPSHOT-$BUILD_NUMBER'
              }
            }
          }
        }

        stage('build and push service-sms image') {
          agent none
          steps {
            container('maven') {
              sh 'ls service/service-sms/target'
              sh 'docker build -t service-sms:latest -f service/service-sms/Dockerfile  ./service/service-sms/'
               withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag service-sms:latest $REGISTRY/$DOCKERHUB_NAMESPACE/service-sms:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/service-sms:SNAPSHOT-$BUILD_NUMBER'
              }
            }
          }
        }

        stage('build and push service-statistics image') {
          agent none
          steps {
            container('maven') {
              sh 'ls service/service-statistics/target'
              sh 'docker build -t service-statistics:latest -f service/service-statistics/Dockerfile  ./service/service-statistics/'
              withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag service-statistics:latest $REGISTRY/$DOCKERHUB_NAMESPACE/service-statistics:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/service-statistics:SNAPSHOT-$BUILD_NUMBER'
              }
            }
          }
        }

        stage('build and push service-task image') {
          agent none
          steps {
            container('maven') {
              sh 'ls service/service-task/target'
              sh 'docker build -t service-task:latest -f service/service-task/Dockerfile  ./service/service-task/'
               withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag service-task:latest $REGISTRY/$DOCKERHUB_NAMESPACE/service-task:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/service-task:SNAPSHOT-$BUILD_NUMBER'
              }
            }
          }
        }

        stage('build and push service-user image') {
          agent none
          steps {
            container('maven') {
              sh 'ls service/service-user/target'
              sh 'docker build -t service-user:latest -f service/service-user/Dockerfile  ./service/service-user/'
              withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
                    sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
                   sh 'docker tag service-user:latest $REGISTRY/$DOCKERHUB_NAMESPACE/service-user:SNAPSHOT-$BUILD_NUMBER'
                sh 'docker push  $REGISTRY/$DOCKERHUB_NAMESPACE/service-user:SNAPSHOT-$BUILD_NUMBER'
              }
            }
          }
        }
      }
    }


    stage('default-3') {
      parallel {
        stage('deploy hospital-manage to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'hospital-manage/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }

        stage('deploy server-gateway to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'server-gateway/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }

        stage('deploy service-cmn to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'service/service-cmn/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }

        stage('deploy service-hosp to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'service/service-hosp/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }

        stage('deploy service-order to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'service/service-order/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }

        stage('deploy service-oss to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'service/service-oss/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }

        stage('deploy service-sms to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'service/service-sms/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }

        stage('deploy service-statistics to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'service/service-statistics/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }

        stage('deploy service-task to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'service/service-task/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }

        stage('deploy service-user to dev') {
          agent none
          steps {
            kubernetesDeploy(configs: 'service/service-user/deploy/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
          }
        }
      }
    }

    stage('finish') {
      agent none
      steps {
        sh 'echo "Pipline finish sucessful !! "'
      }
    }

  }
  environment {
    DOCKER_CREDENTIAL_ID = 'dockerhub-id'
    GITHUB_CREDENTIAL_ID = 'github-id'
    KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
    REGISTRY = 'docker.io'
    DOCKERHUB_NAMESPACE = 'hungwen0425'
    GITHUB_ACCOUNT = 'hungwen0425'
  }
  parameters {
    string(name: 'TAG_NAME', defaultValue: '', description: '')
  }
}