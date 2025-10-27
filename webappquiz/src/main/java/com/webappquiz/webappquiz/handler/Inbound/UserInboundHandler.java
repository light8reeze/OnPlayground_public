package com.webappquiz.webappquiz.handler.inbound;

import com.webappquiz.webappquiz.data.user.UserInstance;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

//< 유저를 통해 처리가 필요한 핸들러
@Getter
public abstract class UserInboundHandler extends SimpleChannelInboundHandler<PacketWrapper> {

    private UserInstance user;
    
    protected UserInboundHandler(Object obj){
        if (obj instanceof UserInstance user)
            this.user = user;
    } 

    protected abstract void channelRead0(ChannelHandlerContext ctx, PacketWrapper msg);   
}
