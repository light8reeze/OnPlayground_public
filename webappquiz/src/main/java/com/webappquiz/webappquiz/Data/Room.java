package com.webappquiz.webappquiz.data;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.webappquiz.webappquiz.data.user.UserInstance;
import com.webappquiz.webappquiz.data.user.UserPosition;
import com.webappquiz.webappquiz.packetGenerator.PacketTransferData;
import com.webappquiz.webappquiz.packetGenerator.ReturnPacketGenerator;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;
import com.webappquiz.webappquiz.protobuf.Protogen.RoomInfo;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {
    private RoomInfo roomInfo;
    private ArrayList<UserInstance> userList;

    public Room(int roomId, int maxUsers, int leftRoomId, int rightRoomId) {
        var roomInfo = RoomInfo.newBuilder()
                .setRoomId(roomId)
                .setMaxUsers(maxUsers)
                .setUserCount(0)
                .setLeftRoomId(leftRoomId)
                .setRightRoomId(rightRoomId)
                .build();

        this.roomInfo = roomInfo;
        this.userList = new ArrayList<>();
    }

    public Room(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
        this.userList = new ArrayList<>();
    }

    public boolean isAvailable() {
        return (userList.size() < roomInfo.getMaxUsers());
    }

    public UserInstance FindUser(long userIndex) {
        for (UserInstance userInstance : userList) {
            if (userInstance.getUserIndex() == userIndex)
                return userInstance;
        }

        return null;
    }

    public void addUser(UserInstance user) {

        // 새로운 유저가 방에 들어올 때 다른 유저들에게 해당 유저의 캐릭터가 생성되었음을 알림
        MessageLite spawnPacket = ReturnPacketGenerator.spawnPlayerResponse(
                user.getUserIndex(),
                0, // 기본 X 위치
                0, // 기본 Y 위치
                user.getUserInfo().getUserName(), // UserInfo에서 닉네임 가져오기
                0); // 기본 캐릭터 타입

        PacketTransferData packetData = new PacketTransferData(
                CallType.ROOM, 
                PacketType.SPAWN_PLAYER_RESPONSE, 
                spawnPacket);

        List<PacketTransferData> packetTransferDataList = new ArrayList<>();

        synchronized (userList) {
            if (userList.contains(user))
                return;

            for (UserInstance otherUser : userList) {

                packetTransferDataList.add(new PacketTransferData(
                        CallType.ROOM, 
                        PacketType.SPAWN_PLAYER_RESPONSE, 
                        ReturnPacketGenerator.spawnPlayerResponse(
                                otherUser.getUserIndex(),
                                otherUser.getUserPosition().getXPos(), // 기본 X 위치
                                otherUser.getUserPosition().getYPos(), // 기본 Y 위치
                                otherUser.getUserInfo().getUserName(), // UserInfo에서 닉네임 가져오기
                                0)));

                otherUser.getUserContext().pipeline().writeAndFlush(packetData);
            }

            userList.add(user);
        }
        user.setNowRoom(this);
        user.setUserPosition(new UserPosition(0, 0));

        for (PacketTransferData packet : packetTransferDataList) {
            user.getUserContext().pipeline().writeAndFlush(packet);
        }
    }

    public void removeUser(UserInstance user) {
        synchronized (userList) {
            userList.remove(user);

            // 유저가 방에서 나갈 때 다른 유저들에게 해당 유저의 캐릭터가 사라졌음을 알림
            MessageLite despawnPacket = ReturnPacketGenerator.despawnPlayerResponse(user.getUserIndex());
            PacketTransferData packetData = new PacketTransferData(
                    CallType.ROOM, 
                    PacketType.DESPAWN_PLAYER_RESPONSE, 
                    despawnPacket);

            for (UserInstance otherUser : userList) {
                otherUser.getUserContext().pipeline().writeAndFlush(packetData);
            }
        }
        user.setNowRoom(null);
    }

    public void broadcastChatMessage(ChatInfo message) {
        synchronized (userList) {
            for (UserInstance user : userList) {
                user.getUserContext().pipeline().writeAndFlush(message);
            }
        }
    }

    public void broadcastPacket(PacketTransferData packet) {
        synchronized (userList) {
            for (UserInstance user : userList) {
                user.getUserContext().pipeline().writeAndFlush(packet);
            }
        }
    }

    public void broadcastPacket(PacketWrapper packet) {
        synchronized (userList) {
            for (UserInstance user : userList) {
                user.getUserContext().pipeline().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(packet.toByteArray())));
            }
        }
    }
}