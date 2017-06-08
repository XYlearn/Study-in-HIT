@echo off
protoc --java_out=D:\SIHServer\src --proto_path=D:\SIHServer\src\protobuffer D:\SIHServer\src\protobuffer\ClientSend.proto
protoc --java_out=D:\SIHServer\src --proto_path=D:\SIHServer\src\protobuffer D:\SIHServer\src\protobuffer\ServerResponse.proto
protoc --java_out=D:\SIHClient\src --proto_path=D:\SIHServer\src\protobuffer D:\SIHServer\src\protobuffer\ClientSend.proto
protoc --java_out=D:\SIHClient\src --proto_path=D:\SIHServer\src\protobuffer D:\SIHServer\src\protobuffer\ServerResponse.proto