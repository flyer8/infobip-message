#!/bin/bash
#/usr/sbin/rabbitmq-server &
java -jar /opt/message-processor/target/message-processor-1.0-SNAPSHOT.jar /opt/message-processor/etc/config.properties &
mvn tomcat7:run -f /opt/message-gateway/pom.xml &