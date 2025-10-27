package com.webappquiz.webappquiz.packetGenerator;

import java.util.HexFormat;

import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatType;
import com.webappquiz.webappquiz.protobuf.Protogen.EnterChannelRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.EnterRoomRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.GameQuizOptionSelectRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.GameStartRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.LeaveChannelRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;

public class TestPacketGenerator {
    public static PacketWrapper enterChannelRequest(int channelId) {
        var req = EnterChannelRequest.newBuilder()
                .setChannelId(channelId)
                .build();

        return PacketWrapper.newBuilder()
                .setCallType(CallType.CHANNEL)
                .setPType(PacketType.ENTER_CHANNEL_REQUEST)
                .setPayload(req.toByteString())
                .build();
    }

    public static PacketWrapper chatRequest(ChatType chatType, String message) {
        ChatInfo chatInfo = ChatInfo.newBuilder()
                .setChatType(chatType)
                .setMessage(message)
                .build();

        var req = ChatRequest.newBuilder()
                .setChatInfo(chatInfo)
                .build();

        return PacketWrapper.newBuilder()
                .setCallType(CallType.CHAT)
                .setPType(PacketType.CHAT_REQUEST)
                .setPayload(req.toByteString())
                .build();
    }

    public static PacketWrapper leaveChannelRequest() {
        var req = LeaveChannelRequest.newBuilder()
                .build();

        return PacketWrapper.newBuilder()
                .setCallType(CallType.CHANNEL)
                .setPType(PacketType.LEAVE_CHANNEL_REQUEST)
                .setPayload(req.toByteString())
                .build();
    }

    public static PacketWrapper enterRoomRequest() {
        var req = EnterRoomRequest.newBuilder()
                .setRoomId(1)
                .build();

        return PacketWrapper.newBuilder()
                .setCallType(CallType.ROOM)
                .setPType(PacketType.ENTER_ROOM_REQUEST)
                .setPayload(req.toByteString())
                .build();
    }

    public static PacketWrapper gameStartRequest() {
        var req = GameStartRequest.newBuilder()
                .build();

        return PacketWrapper.newBuilder()
                .setCallType(CallType.GAME)
                .setPType(PacketType.GAME_START_REQUEST)
                .setPayload(req.toByteString())
                .build();
    }

    public static PacketWrapper gameQuizOptionSelectRequest(int optionIndex) {
        var req = GameQuizOptionSelectRequest.newBuilder()
                .setOptionNo(optionIndex)
                .build();

        return PacketWrapper.newBuilder()
                .setCallType(CallType.GAME)
                .setPType(PacketType.GAME_QUIZ_OPTION_SELECT_REQUEST)
                .setPayload(req.toByteString())
                .build();
    }

    public static void PrintAllTestPacket(){
        printPacket(enterChannelRequest(1));
        printPacket(chatRequest(ChatType.ROOM_CHAT, "test"));
        printPacket(chatRequest(ChatType.CHANNEL_CHAT, "testCHANNEL"));
        printPacket(leaveChannelRequest());
        printPacket(enterRoomRequest());
        printPacket(gameStartRequest());
        printPacket(gameQuizOptionSelectRequest(1));
    }

    public static void printPacket(PacketWrapper msg) {
        System.out.println("packet type : " + msg.getPType());
        byte[] bytes = msg.toByteArray();
        String hex = HexFormat.of().formatHex(bytes);
        System.out.println("Sending Hex: " + hex);
    }
}
