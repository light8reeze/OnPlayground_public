package com.webappquiz.webappquiz.handler.inbound;

import java.util.ArrayList;

import com.google.protobuf.MessageLite;
import com.webappquiz.webappquiz.data.ChannelManager;
import com.webappquiz.webappquiz.data.game.GameInfo;
import com.webappquiz.webappquiz.data.user.UserInstance;
import com.webappquiz.webappquiz.packetGenerator.PacketGenerator;
import com.webappquiz.webappquiz.packetGenerator.PacketTransferData;
import com.webappquiz.webappquiz.packetGenerator.ReturnPacketGenerator;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.ChannelInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.CreateChannelRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.EnterChannelRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.GameModeInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.GameModeListResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.GameModeType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;
import com.webappquiz.webappquiz.server.StateHandlerFactory.STATE;
import com.webappquiz.webappquiz.server.StateHandlerFactory.StateChangeArg;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LobbyHandler extends UserInboundHandler {
    
    private ArrayList<GameModeInfo> gameModeInfos = new ArrayList<>();

    public LobbyHandler(Object obj) {
        super(obj);
        // < TODO : 생성자를 통해 들어온 Object가 User가 아닐경우

        gameModeInfos.add(GameModeInfo.newBuilder().setGameModeId(0).setGameModeName("OXQUIZ").setGameModeType(GameModeType.OXQUIZ).build());
        gameModeInfos.add(GameModeInfo.newBuilder().setGameModeId(1).setGameModeName("Quiz").setGameModeType(GameModeType.QUIZ).build());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketWrapper packet) {
        UserInstance user = getUser();
        if (null == user)
            return;

        // < TODO : 예외처리 필요(context가 맞지 않을경우)
        if (!user.isUserContext(ctx))
            return;

        if (packet.getCallType() != CallType.CHANNEL)
            ctx.fireChannelRead(packet);

        var data = PacketGenerator.getInstance().createPacket(packet.getPTypeValue(),
                packet.getPayload().toByteArray());
        if (packet.getPType() == PacketType.CHANNEL_LIST_REQUEST)
            Process_ChannelListRequest(ctx, data);
        else if (packet.getPType() == PacketType.CREATE_CHANNEL_REQUEST)
            Process_CreateChannelRequest(ctx, data);
        else if (packet.getPType() == PacketType.ENTER_CHANNEL_REQUEST)
            Process_EnterChannelRequest(ctx, data);
        else if(packet.getPType() == PacketType.GAME_MODE_LIST_REQUEST)
            Process_GameModeListRequest(ctx, data);
    }

    private void Process_ChannelListRequest(ChannelHandlerContext ctx, MessageLite msg) {
        ArrayList<ChannelInfo> channelInfos = ChannelManager.getInstance().getChannelList();

        ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(
                ReturnPacketGenerator.createChannelListResponse(channelInfos).toByteArray())));
    }

    private void Process_CreateChannelRequest(ChannelHandlerContext ctx, MessageLite msg) {
        var req = (CreateChannelRequest) msg;

        var gameModeInfo = gameModeInfos.get(req.getGameModeId());

        var newChannelInfo = ChannelManager.getInstance().createChannel(
                req.getChannelType(), req.getChannelName(), gameModeInfo, req.getMaxUsers(), req.getRoomCount());

        ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(
                ReturnPacketGenerator.createCreateChannelResponse(true, "success!", newChannelInfo).toByteArray())));
    }

    private void Process_EnterChannelRequest(ChannelHandlerContext ctx, MessageLite msg) {
        UserInstance user = getUser();
        var req = (EnterChannelRequest) msg;

        var channelInstance = ChannelManager.getInstance().findChannel(req.getChannelId());
        if (channelInstance != null) {
            var result = channelInstance.tryEnterUser(user);
            if (result == true) {

                ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(
                        ReturnPacketGenerator.createEnterChannelResponse(
                                true, 0,
                                user.getNowChannel().getChannelInfo(), user.getNowRoom().getRoomInfo())
                                .toByteArray())));

                //< 상태 변경
                ctx.channel().write(new StateChangeArg(STATE.ROOM, user));
                return;
            }
        }

        // < 실패
        ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(
                ReturnPacketGenerator.createEnterChannelResponse(
                        true, 0).toByteArray())));
    }

    private void Process_GameModeListRequest(ChannelHandlerContext ctx, MessageLite msg) {
        var gameModeListResponse = GameModeListResponse.newBuilder()
                .addAllGameModeList(gameModeInfos)
                .build();

        PacketTransferData transferData = new PacketTransferData(CallType.CHANNEL, PacketType.GAME_MODE_LIST_RESPONSE, gameModeListResponse);
        ctx.channel().write(transferData);
    }
}