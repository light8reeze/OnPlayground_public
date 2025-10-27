package com.webappquiz.webappquiz.handler.inbound;

import com.google.protobuf.MessageLite;
import com.webappquiz.webappquiz.packetGenerator.PacketGenerator;
import com.webappquiz.webappquiz.packetGenerator.PacketTransferData;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.GameQuizOptionSelectRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.GameStartResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;
import com.webappquiz.webappquiz.service.GameService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

//< 임시로 게임핸들러만 컴포넌트로 처리함
public class GameHandler extends UserInboundHandler {

    public GameHandler(Object obj) {
        super(obj);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketWrapper packet) {
        if(packet.getCallType() != CallType.GAME){
            ctx.fireChannelRead(packet);
            return;
        }

        var data = PacketGenerator.getInstance().createPacket(packet.getPTypeValue(), packet.getPayload().toByteArray());
        switch(packet.getPType()){
            case GAME_START_REQUEST:
                Process_GameStart(ctx, data);
                break;
            case GAME_QUIZ_OPTION_SELECT_REQUEST:
                Process_GameQuizOptionSelect(ctx, data);
                break;
        }
    }

    private void Process_GameStart(ChannelHandlerContext ctx, MessageLite msg){
        // 채널 생성자가 아니면 게임 시작 불가
        if(ctx.channel().attr(AttributeKey.valueOf("userIndex")).get() != getUser().getNowChannel().getCreatorUserIndex())
        {
            GameStartResponse response = GameStartResponse.newBuilder()
                .setSuccess(false)
                .setErrorCode(1)
                .build();

                PacketTransferData transferData = new PacketTransferData(CallType.GAME, PacketType.GAME_START_RESPONSE, response);
                ctx.channel().write(transferData);

                return;
        }

        GameStartResponse response = GameStartResponse.newBuilder()
                .setSuccess(true)
                .setErrorCode(0)
                .build();

        PacketTransferData transferData = new PacketTransferData(CallType.GAME, PacketType.GAME_START_RESPONSE, response);
        ctx.channel().write(transferData);

        int channelId = getUser().getNowChannel().getChannelInfo().getChannelId();
        GameService.getInstance().startGame(channelId);
    }

    private void Process_GameQuizOptionSelect(ChannelHandlerContext ctx, MessageLite msg){
        GameQuizOptionSelectRequest gameQuizOptionSelectRequest = (GameQuizOptionSelectRequest)msg;

        int channelId = getUser().getNowChannel().getChannelInfo().getChannelId();
        GameService.getInstance().onUserSelectAnswer(channelId, getUser(), gameQuizOptionSelectRequest.getOptionNo());
    }
}
