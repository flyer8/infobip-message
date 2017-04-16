#!/bin/bash
echo "Verifying HTTP methods"
curl --head --silent http://localhost:8080 | head -n1
curl http://localhost:8080/message -X POST -d '{"messageId":1, "timestamp":1234, "protocolVersion":"1.0.0", "messageData":{"mMX":1234, "mPermGen":1234}}'