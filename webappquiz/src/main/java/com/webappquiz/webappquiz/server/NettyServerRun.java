package com.webappquiz.webappquiz.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.webappquiz.webappquiz.packetGenerator.PacketGenerator;
import com.webappquiz.webappquiz.protobuf.Protogen.ChannelListRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.CreateChannelRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.EnterChannelRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.EnterRoomRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.GameModeListRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.GameQuizOptionSelectRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.GameStartRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.LeaveChannelRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.LoginRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.MovePlayerRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.RegisterRequest;

@Component
public class NettyServerRun implements CommandLineRunner //< CommandLineRunner의 run 함수는 Spring이 다 되고나서 실행된다. 
{
    private final NettyServer nettyServer;

    public NettyServerRun( NettyServer nettyServer )
    {
        this.nettyServer = nettyServer;

        //< 상태 핸들러 리턴 팩토리 미리 초기화
        StateHandlerFactory.getInstance();

        //< 패킷 변환 미리 등록
        PacketGenerator.getInstance().registerPacket(PacketType.LOGIN_REQUEST_VALUE, LoginRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.REGISTER_REQUEST_VALUE, RegisterRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.CHANNEL_LIST_REQUEST_VALUE, ChannelListRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.CREATE_CHANNEL_REQUEST_VALUE, CreateChannelRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.GAME_MODE_LIST_REQUEST_VALUE, GameModeListRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.ENTER_CHANNEL_REQUEST_VALUE, EnterChannelRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.ENTER_ROOM_REQUEST_VALUE, EnterRoomRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.CHAT_REQUEST_VALUE, ChatRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.LEAVE_CHANNEL_REQUEST_VALUE, LeaveChannelRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.GAME_START_REQUEST_VALUE, GameStartRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.GAME_QUIZ_OPTION_SELECT_REQUEST_VALUE, GameQuizOptionSelectRequest.getDefaultInstance());
        PacketGenerator.getInstance().registerPacket(PacketType.MOVE_PLAYER_REQUEST_VALUE, MovePlayerRequest.getDefaultInstance());
    }
    @Override
    public void run( String... arg  ) throws Exception
    {
        nettyServer.start();
    }
}