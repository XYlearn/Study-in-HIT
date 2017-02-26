#！bin
# 
#
protoc --java_out=/home/xy16/JavaCode.d/ProjectServer ClientSend.proto
protoc --java_out=/home/xy16/JavaCode.d/ProjectServer ServerResponse.proto
cp /home/xy16/JavaCode.d/ProjectServer/com/ClientSendMessage.java /home/xy16/JavaCode.d/ProjectServer/com/ServerResponseMessage.java \
/home/xy16/JavaCode.d/ProjectServer/src/com
cp /home/xy16/JavaCode.d/ProjectServer/com/ClientSendMessage.java /home/xy16/JavaCode.d/ProjectServer/com/ServerResponseMessage.java \
/home/xy16/JavaCode.d/ProjectClientProto/src
