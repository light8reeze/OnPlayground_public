package com.webappquiz.webappquiz.handler.outbound;

import com.webappquiz.webappquiz.packetGenerator.PacketTransferData;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

//< PacketTransferData로 들어온 데이터를 PacketWrapper로 래핑해서 Send한다.(이곳에서 최종 Send한다.)
public class PacketWrappingOutboundHandler extends SimpleChannelOutboundHandler<PacketTransferData>{

    public PacketWrappingOutboundHandler(){
    }

    @Override
    protected void write0(ChannelHandlerContext ctx, PacketTransferData msg, ChannelPromise promise) throws Exception {
        
        PacketWrapper finalPacket = PacketWrapper.newBuilder()
                .setCallType(msg.getCallType())
                .setPType(msg.getPType())
                .setPayload(msg.message.toByteString())
                .build();

        ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(finalPacket.toByteArray())));
    }
}
