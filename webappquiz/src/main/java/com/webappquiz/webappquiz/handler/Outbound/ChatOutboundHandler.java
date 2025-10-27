package com.webappquiz.webappquiz.handler.outbound;

import com.webappquiz.webappquiz.packetGenerator.PacketTransferData;
import com.webappquiz.webappquiz.packetGenerator.ReturnPacketGenerator;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.ChatResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

//< 채팅 메세지 전달하는 핸들러(쓰레드 분산하려고 별도 Outbound핸들러로 처리)
public class ChatOutboundHandler extends UserOutboundHandler<ChatInfo> {

    public ChatOutboundHandler(Object obj){
        super(obj);
    }

    @Override
    protected void write0(ChannelHandlerContext ctx, ChatInfo msg, ChannelPromise promise) throws Exception {
        //< 유저에게 체팅 매세지 전달.
        ChatResponse response = ChatResponse.newBuilder()
                                .setChatInfo(msg)
                                .build();
        
        //< 주의! 반드시 PacketWrappingOutboundHandler가 있어야함.
        ctx.writeAndFlush(ReturnPacketGenerator.CreatePacketTransferData(CallType.CHAT, PacketType.CHAT_RESPONSE, response));
    }
}
