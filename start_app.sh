#!/bin/bash
#/usr/sbin/rabbitmq-server &
#cd /opt/message-processor
java -jar /opt/message-processor/target/message-processor-1.0-SNAPSHOT.jar /opt/message-processor/etc/config.properties &
cd /opt/message-gateway
mvn tomcat7:run &