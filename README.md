## Deploying Java application using Jenkins Pipeline with Docker

1. Requirements
The deployment was performed on VM Linux Centos 7 (RAM 1Gb) with installed and configured:
* Jenkins ver. 2.53 with Pipeline, Maven, Artifactory, GitHib, HTTP Request plugins.
  * To match in the groovy script, please be ensure, that Maven name in “Global Tool Configuration” is “Maven-3.3.9”;
  * Also Artifactory Server ID in “Configure system” is "Artifactory_localhost".
* Artifactory Version 5.2.0
  * Created Artifactory local maven default repository: releaseRepo:'libs-release-local', snapshotRepo:'libs-snapshot-local'
* java-1.8.0-openjdk-devel.x86_64
* Apache Maven 3.3.9
* Docker version 17.03.0-ce

2. First need provide to Jenkins user root permissions on target OS:

```echo "jenkins ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers```

3. Create and build Jenkins Pipeline job with groovy script from https://github.com/flyer8/infobip-message/blob/master/pipline.groovy
