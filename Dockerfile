FROM centos
MAINTAINER flyer8 "flyer8@yandex.ru"
ENV container docker

ARG JAVA_VERSION=1.8.0
ARG MAVEN_VERSION=3.3.9

ENV JAVA_HOME /usr/lib/jvm/java
ENV MAVEN_HOME /usr/share/maven

RUN yum update -y
RUN yum install -y java-$JAVA_VERSION-openjdk-devel

RUN mkdir -p $MAVEN_HOME /usr/share/maven/ref \
      && curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
      | tar -xzC $MAVEN_HOME --strip-components=1 \
      && ln -s $MAVEN_HOME/bin/mvn /usr/bin/mvn

RUN yum install epel-release -y
RUN yum install rabbitmq-server.noarch -y

RUN mkdir /opt/message-gateway
RUN mkdir /opt/message-processor
ADD message-gateway/ /opt/message-gateway/
ADD message-processor/ /opt/message-processor/
ADD start_app.sh /
ADD smoke-test.sh /
ENTRYPOINT ["/usr/sbin/rabbitmq-server"]

VOLUME ["/sys/fs/cgroup"]
EXPOSE 5672 15672 25672 4369 8080