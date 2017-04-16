#!/bin/bash
echo "Waiting 1 min for starting Tomcat with Java application"
sleep 60
echo "Verifying HTTP methods"
echo "GET response is -" $(curl --head --silent http://localhost:8080 | head -n1)
echo "POST request is -" $(curl http://localhost:8080/message -X POST -d '{"messageId":1, "timestamp":1234, "protocolVersion":"1.0.0", "messageData":{"mMX":1234, "mPermGen":1234}}') \