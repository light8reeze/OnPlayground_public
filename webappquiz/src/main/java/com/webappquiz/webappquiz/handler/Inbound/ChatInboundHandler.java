package com.webappquiz.webappquiz.handler.inbound;

import com.google.protobuf.MessageLite;
import com.webappquiz.webappquiz.data.user.UserInstance;
import com.webappquiz.webappquiz.packetGenerator.PacketGenerator;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;

import io.netty.channel.ChannelHandlerContext;

//< 채팅 관련 기능을 처리하는 핸들러(채널, 룸에 입장된 상태여야 한다.)
public class ChatInboundHandler extends UserInboundHandler {
    
    public ChatInboundHandler(Object obj) {
        super(obj);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketWrapper packet) {
        UserInstance user = getUser();
        if (null == user)
            return;

        // < TODO : 예외처리 필요(context가 맞지 않을경우)
        if (!user.isUserContext(ctx))
            return;

        if (packet.getCallType() != CallType.CHAT)
            ctx.fireChannelRead(packet);

        var data = PacketGenerator.getInstance().createPacket(packet.getPTypeValue(), packet.getPayload().toByteArray());
        if (packet.getPType() == PacketType.CHAT_REQUEST)
            Process_ChatRequest(ctx, data);
    }

    private void Process_ChatRequest(ChannelHandlerContext ctx, MessageLite msg) {
        var chatRequest = (ChatRequest)(msg);
        ChatInfo chatInfo = ChatInfo.newBuilder()
                            .setChatType(chatRequest.getChatInfo().getChatType())
                            .setMessage(chatRequest.getChatInfo().getMessage())
                            .setUserId(getUser().getUserIndex())
                            .setUserName(chatRequest.getChatInfo().getUserName())
                            .build();

        if(chatRequest.getChatInfo().getChatType() == ChatType.CHANNEL_CHAT)
            getUser().getNowChannel().broadcastChatMessage(chatInfo);
        else if(chatRequest.getChatInfo().getChatType() == ChatType.ROOM_CHAT)
            getUser().getNowRoom().broadcastChatMessage(chatInfo);
    }
}