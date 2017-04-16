#!/bin/bash
docker run --name message -p 8888:8080 -d infobip/message
docker exec -d message java -jar /opt/message-processor/target/message-processor-1.0-SNAPSHOT.jar /opt/message-processor/etc/config.properties
docker exec -it message sleep 5
docker exec -d message mvn tomcat7:run -f /opt/message-gateway/pom.xml
docker exec -it message sleep 10
docker exec -it message curl --head --silent http://localhost:8080 | head -n1
docker exec -it message curl http://localhost:8080/message -X POST -d '{"messageId":1, "timestamp":1234, "protocolVersion":"1.0.0", "messageData":{"mMX":1234, "mPermGen":1234}}'
