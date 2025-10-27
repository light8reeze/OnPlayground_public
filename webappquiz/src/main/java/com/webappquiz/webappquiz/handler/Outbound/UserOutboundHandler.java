package com.webappquiz.webappquiz.handler.outbound;

import com.webappquiz.webappquiz.data.user.UserInstance;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;

@Getter
public abstract class UserOutboundHandler<T> extends SimpleChannelOutboundHandler<T> {
    private UserInstance user;
    
    protected UserOutboundHandler(Object userObject){
        super();

        if (userObject instanceof UserInstance user)
            this.user = user;
    }

    @Override
    protected abstract void write0(ChannelHandlerContext ctx, T msg, ChannelPromise promise) throws Exception;
}
