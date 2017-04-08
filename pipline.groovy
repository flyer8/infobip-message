node {
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
    def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'clean install'

    stage 'Publish build info'
    server.publishBuildInfo buildInfo
	
	stage 'Deploying to Staging env'
	// sh 'mkdir /opt/infobip-message || true'
	sh 'git clone https://github.com/flyer8/infobip-message.git /opt/infobip-message'
    sh 'cp -rf message-gateway/ /opt/infobip-message/'
	sh 'cp -rf message-processor/ /opt/infobip-message/'
		
	stage 'Docker deploying'
    // Need add Dockerfile and script from git
    sh 'docker rm -f -v message || true'
	sh 'docker rmi infobip/message || true'
	sh 'docker build --rm -t infobip/message /opt/infobip-message'
	sh 'docker run --name message -p 8888:8080 -d infobip/message'
}