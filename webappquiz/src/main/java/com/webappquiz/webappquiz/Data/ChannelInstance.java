package com.webappquiz.webappquiz.data;

import java.util.ArrayList;

import com.google.protobuf.MessageLite;
import com.webappquiz.webappquiz.data.user.UserInstance;
import com.webappquiz.webappquiz.packetGenerator.PacketTransferData;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.ChannelInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;

import lombok.Getter;

//< 채널 클래스 룸, 채널에 입장한 유저를 관리한다.
@Getter
public class ChannelInstance {
    private ChannelInfo             channelInfo;
    private ArrayList<Room>         roomList;
    private ArrayList<UserInstance> userList;
    private int                     userCapacity;
    private Long                    creatorUserIndex;

    public ChannelInstance(ChannelInfo info){
        this.channelInfo = info;
        
        //< 10% 정도 인원 여유를 둔다.
        userCapacity = info.getMaxUsers() + (info.getMaxUsers() / 10);
        this.userList = new ArrayList<>(userCapacity);
        this.roomList = new ArrayList<>(info.getRoomCount());
        for(int i = 1; i <= info.getRoomCount(); ++i){
            int leftRoomId = Math.max(0, i - 1);
            int rightRoomId = Math.min(info.getRoomCount(), i + 1);

            Room newRoom = new Room(i, 50, leftRoomId, rightRoomId);
            this.roomList.add(newRoom);
        }
    }

    public boolean tryEnterUser(UserInstance user){
        if(userCapacity <= userList.size())
            return false;

        Room enterRoom = getAvailableRoom();
        if(enterRoom == null)
            return false;
        
        enterRoom.addUser(user);
        addUser(user);

        return true;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public void addUser(UserInstance user){
        synchronized(userList){
            if(userList.isEmpty())
                creatorUserIndex = user.getUserIndex();

            userList.add(user);
        }
        user.setNowChannel(this);
    }

    public void removeUser(UserInstance user){
        synchronized(userList){
            userList.remove(user);
        }
        user.setNowChannel(null);
    }

    public final ArrayList<UserInstance> getUserList(){
        return userList;
    }

    public Room findRoom(int roomId){
        if(roomList.size() <= roomId)
            return null;

        return roomList.get(roomId);
    }

    public Room getAvailableRoom(){
        for (Room room : roomList) {
            if(room.isAvailable())
                return room;
        }
        
        return null;
    }

    public UserInstance findUser(long userIndex){
        synchronized(userList){
            for (UserInstance userInstance : userList) {
                if (userInstance.getUserIndex() == userIndex)
                    return userInstance;
            }
        }

        return null;
    }

    public void sendToUser(long userIndex, CallType callType, PacketType pType, MessageLite msg){
        UserInstance user = findUser(userIndex);
        if(user == null)
            return;

        PacketTransferData transferData = new PacketTransferData(callType, pType, msg);
        user.getUserContext().pipeline().writeAndFlush(transferData);
    }

    public void broadcastChatMessage(ChatInfo message){
        synchronized(userList){
            for (UserInstance user : userList) {
                user.getUserContext().pipeline().writeAndFlush(message);
            }
        }
    }
    public void broadcastMessage(CallType callType, PacketType pType, MessageLite msg){

        PacketTransferData transferData = new PacketTransferData(callType, pType, msg);
        synchronized(userList){
            for (UserInstance user : userList) {
                user.getUserContext().pipeline().writeAndFlush(transferData);
            }
        }
    }
}