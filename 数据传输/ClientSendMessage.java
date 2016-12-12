syntax = "proto2";
option java_outer_classname = "CilentSendMessage";

//消息类型序号
enum MSG {
    LAUNCH_MESSAGE = 1000;
    LOGOUT_MESSAGE = 1001;
    SEND_MESSAGE = 1002;
    ANNOUNCEMENT_MESSAGE = 1003;
    ROOM_ENTER_MESSAGE = 1004;
    GOOD_QUESTION = 1005;
    BAD_QUESTION = 1006;
    GOOD_USER = 1007;
    BAD_USER = 1008;
}

//发送的信息序号
enum SENDTYPE {
    TEXT_MESSAGE = 10000;
    PICTURE_MESSAGE = 10001;
    VOICE_MESSAGE = 10002;
}

enum PICTURETYPE {
    JPEG = 20001;
    PNG = 20002;
    GIF = 20003;
}
//登入信息
message LaunchMessage {
    required string password = 2;
}

//登出信息
message LogoutMessage {
}

//发送信息
message SendMessage {
    required SENDTYPE send_type = 1;
    message TextMessage {
        required string text = 1;
    }
    message PictureMessage {
        required PICTURETYPE picture_type = 1;
        repeated uint64 pictureBinary = 2;
    }
    message VoiceMessage {

    }
    repeated TextMessage textMessage = 2;
    repeated PictureMessage pictureMessage = 3;
    repeated VoiceMessage voiceMessage = 4;
}

//通告信息
message AnnouncementMessage {
    required string text = 1;
}

//进入房间消息
message RoomEnterMessage {
    required uint64 roomID = 1;
}

//赞问题
message GoodQuestion {
    required uint64 questionID = 1;
    optional string reason = 2;
}

//踩问题
message BadQuestion {
    required uint64 questionID = 1;
    optional string reason = 2;
}

//赞用户
message GoodUser {
    required uint64 userID = 1;
    optional string reason = 2;
}

//踩用户
message BadUser {
    required uint64 userID = 1;
    optional string reason = 2;
}

//顶层消息
message Message {
    required MSG msg_type = 1;
    required string username = 2;
    optional LaunchMessage lauchMessage = 3;
    optional LogoutMessage logoutMessage = 4;
    optional SendMessage textMessage = 5;
    optional AnnouncementMessage announcementMessage = 6;
    optional RoomEnterMessage roomEnterMessage = 7;
    optional GoodQuestion goodQuestion = 128;
    optional BadQuestion badQuestion =129;
    optional GoodUser goodUser = 130;
    optional BadUser badUser = 131;
}
