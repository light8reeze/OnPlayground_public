package com.webappquiz.webappquiz.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import com.google.protobuf.InvalidProtocolBufferException;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;

public class ProtoBufDecodeHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> 
{
    @Override
    protected void channelRead0( ChannelHandlerContext ctx, BinaryWebSocketFrame msg )
    {
        ByteBuf content = msg.content();
        byte[] byteArray = new byte[msg.content().readableBytes()];
        content.readBytes(byteArray);

        try {
            PacketWrapper packetWrapper = PacketWrapper.parseFrom(byteArray);

            System.out.println("pType: " + packetWrapper.getPType());
            System.out.println("callType: " + packetWrapper.getCallType());

            ctx.fireChannelRead(packetWrapper);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
