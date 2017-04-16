// Jenkins Job pipeline groovy script.
// Autor: Sergey Shimanskiy flyer8@yandex.ru
node ('docker-jenkins-slave') {
// Get Artifactory server instance, defined in the Artifactory Plugin administration page.
def server = Artifactory.server "Artifactory_localhost"
// Create an Artifactory Maven instance.
def rtMaven = Artifactory.newMavenBuild()

stage 'Clone sources'
git url: 'https://github.com/mzagar/messaging-example.git'

stage 'Artifactory configuration'
// Tool name from Jenkins configuration
rtMaven.tool = "Maven-3.3.9"
// Set Artifactory repositories for dependencies resolution and artifacts deployment.
rtMaven.deployer releaseRepo:'libs-release-local', snapshotRepo:'libs-snapshot-local', server: server
rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: server

stage 'Maven build'
def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'clean package'

stage 'Publish build info'
server.publishBuildInfo buildInfo

stage 'Deploying to Docker staging'
sh 'sudo -u root ssh -o "StrictHostKeyChecking no" 192.168.0.110 "mkdir /opt/infobip-message/ || true"'
sh 'sudo -u root ssh 192.168.0.110 "rm -rf /opt/infobip-message/*"'
sh 'sudo -u root ssh 192.168.0.110 "rm -rf /opt/infobip-message/.* || true"'
sh 'sudo -u root ssh 192.168.0.110 "git clone https://github.com/flyer8/infobip-message.git /opt/infobip-message/"'
sh 'sudo -u root scp -r message-gateway/ 192.168.0.110:/opt/infobip-message/'
sh 'sudo -u root scp -r message-processor/ 192.168.0.110:/opt/infobip-message/'

// Deploying to Docker from git repo with Dockerfile
sh 'sudo -u root ssh 192.168.0.110 "docker rm -f -v message || true"'
sh 'sudo -u root ssh 192.168.0.110 "docker rmi infobip/message || true"'
sh 'sudo -u root ssh 192.168.0.110 "docker build --rm -t infobip/message /opt/infobip-message"'
sh 'sudo -u root ssh 192.168.0.110 "docker run --name message -p 8888:8080 -d infobip/message"'
sh 'sudo -u root ssh 192.168.0.110 "docker exec -d message java -jar /opt/message-processor/target/message-processor-1.0-SNAPSHOT.jar /opt/message-processor/etc/config.properties"'
sh 'sudo -u root ssh 192.168.0.110 "docker exec -it message sleep 5 || true"'
sh 'sudo -u root ssh 192.168.0.110 "docker exec -d message mvn tomcat7:run -f /opt/message-gateway/pom.xml"'
sh 'sudo -u root ssh 192.168.0.110 "docker exec -it message sleep 10 || true"'
sh 'sudo -u root ssh 192.168.0.110 "docker exec -it message ./smoke-test.sh"'

stage 'HTTP Notification'
// create payload
def patchOrg = """
{
"Job name: ${env.JOB_NAME}",
"Buil number: ${env.BUILD_NUMBER}",
"Build URL: ${env.BUILD_URL}",
"Result": "${currentBuild.currentResult}",
}
"""
def response = httpRequest acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: patchOrg, url: "http://requestb.in/1d53dfv1"
}