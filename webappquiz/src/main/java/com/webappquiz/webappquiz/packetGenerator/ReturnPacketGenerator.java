package com.webappquiz.webappquiz.packetGenerator;

import java.util.ArrayList;

import com.google.protobuf.MessageLite;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.ChannelInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.ChannelListResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.CreateChannelResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.DespawnPlayerResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.EnterChannelResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.EnterRoomResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.LoginResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.MovePlayerResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;
import com.webappquiz.webappquiz.protobuf.Protogen.PlayerInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.PlayerPosition;
import com.webappquiz.webappquiz.protobuf.Protogen.RegisterResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.RoomInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.SpawnPlayerResponse;

public class ReturnPacketGenerator {

        public static PacketTransferData CreatePacketTransferData(CallType callType, PacketType pType, MessageLite payLoad) {
                return new PacketTransferData(callType, pType, payLoad);
        }

        public static PacketWrapper CreateLoginRespone(boolean success, String message, long userIndex) {
                LoginResponse response = LoginResponse.newBuilder()
                                .setSuccess(success)
                                .setMessage(message)
                                .setUserId(userIndex)
                                .build();

                return PacketWrapper.newBuilder()
                                .setCallType(CallType.LOGIN)
                                .setPType(PacketType.LOGIN_RESPONSE)
                                .setPayload(response.toByteString()) // <- 여기 중요! ByteString으로 변환해서 넣어야 함
                                .build();
        }

        public static PacketWrapper CreateRegisterResponse(boolean success, String message) {
                RegisterResponse response = RegisterResponse.newBuilder()
                                .setSuccess(success)
                                .setMessage(message)
                                .build();

                return PacketWrapper.newBuilder()
                                .setCallType(CallType.LOGIN)
                                .setPType(PacketType.REGISTER_RESPONSE)
                                .setPayload(response.toByteString()) // <- 여기 중요! ByteString으로 변환해서 넣어야 함
                                .build();
        }

        public static PacketWrapper createChannelListResponse(ArrayList<ChannelInfo> channelInfos) {
                ChannelListResponse response = ChannelListResponse.newBuilder()
                                .addAllChannelList(channelInfos)
                                .build();

                return PacketWrapper.newBuilder()
                                .setCallType(CallType.CHANNEL)
                                .setPType(PacketType.CHANNEL_LIST_RESPONSE)
                                .setPayload(response.toByteString())
                                .build();
        }

        public static PacketWrapper createCreateChannelResponse(boolean success, String message,
                        ChannelInfo newChannelInfo) {
                CreateChannelResponse response = CreateChannelResponse.newBuilder()
                                .setSuccess(success)
                                .setMessage(message)
                                .setChannelInfo(newChannelInfo)
                                .build();

                return PacketWrapper.newBuilder()
                                .setCallType(CallType.CHANNEL)
                                .setPType(PacketType.CREATE_CHANNEL_RESPONSE)
                                .setPayload(response.toByteString())
                                .build();
        }

        public static PacketWrapper createEnterChannelResponse(boolean success, int errorCode,
                        ChannelInfo newChannelInfo, RoomInfo roomInfo) {
                EnterChannelResponse response = EnterChannelResponse.newBuilder()
                                .setSuccess(success)
                                .setErrorCode(errorCode)
                                .setChannelInfo(newChannelInfo)
                                .setRoomInfo(roomInfo)
                                .build();

                return PacketWrapper.newBuilder()
                                .setCallType(CallType.CHANNEL)
                                .setPType(PacketType.ENTER_CHANNEL_RESPONSE)
                                .setPayload(response.toByteString())
                                .build();
        }

        public static PacketWrapper createEnterChannelResponse(boolean success, int errorCode) {
                EnterChannelResponse response = EnterChannelResponse.newBuilder()
                                .setSuccess(success)
                                .setErrorCode(errorCode)
                                .build();

                return PacketWrapper.newBuilder()
                                .setCallType(CallType.CHANNEL)
                                .setPType(PacketType.ENTER_CHANNEL_RESPONSE)
                                .setPayload(response.toByteString())
                                .build();
        }

        public static PacketWrapper enterRoomResponse(boolean success, int errorCode, RoomInfo roomInfo) {
                EnterRoomResponse response = EnterRoomResponse.newBuilder()
                                .setSuccess(success)
                                .setErrorCode(errorCode)
                                .setRoomInfo(roomInfo)
                                .build();

                return PacketWrapper.newBuilder()
                                .setCallType(CallType.ROOM)
                                .setPType(PacketType.ENTER_ROOM_RESPONSE)
                                .setPayload(response.toByteString())
                                .build();
        }

        public static MessageLite movePlayerResponse(long userId, int xPos, int yPos) {
                PlayerPosition playerPosition = PlayerPosition.newBuilder()
                                .setUserId(userId)
                                .setXPos(xPos)
                                .setYPos(yPos)
                                .build();

                MovePlayerResponse response = MovePlayerResponse.newBuilder()
                                .setMovePosition(playerPosition)
                                .build();

                return response;
        }

        public static PacketWrapper movePlayerResponsePacketWrapper(long userId, int xPos, int yPos) {
                return PacketWrapper.newBuilder()
                                .setCallType(CallType.ROOM)
                                .setPType(PacketType.MOVE_PLAYER_RESPONSE)
                                .setPayload(movePlayerResponse(userId, xPos, yPos).toByteString())
                                .build();
        }

        public static MessageLite spawnPlayerResponse(long userId, int xPos, int yPos, String userName, int characterType) {
                PlayerPosition playerPosition = PlayerPosition.newBuilder()
                                .setUserId(userId)
                                .setXPos(xPos)
                                .setYPos(yPos)
                                .build();

                PlayerInfo playerInfo = PlayerInfo.newBuilder()
                                .setUserId(userId)
                                .setUserName(userName)
                                .setPosition(playerPosition)
                                .setCharacterType(characterType)
                                .build();

                return SpawnPlayerResponse.newBuilder()
                        .setPlayerInfo(playerInfo)
                        .build();
        }

        public static MessageLite despawnPlayerResponse(long userId) {
                DespawnPlayerResponse response = DespawnPlayerResponse.newBuilder()
                                .setUserId(userId)
                                .build();

                return response;
        }
}