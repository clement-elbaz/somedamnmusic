protoc -I=protobuf --java_out=src/main/java protobuf/entities.proto
mvn clean install