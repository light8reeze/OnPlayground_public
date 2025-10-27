package com.webappquiz.webappquiz.handler.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.TypeParameterMatcher;
import lombok.Getter;

@Getter
public abstract class SimpleChannelOutboundHandler<T> extends ChannelOutboundHandlerAdapter {
    private final TypeParameterMatcher matcher;
    
    protected SimpleChannelOutboundHandler(){
        matcher = TypeParameterMatcher.find(this, SimpleChannelOutboundHandler.class, "T");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (matcher.match(msg)) {
            @SuppressWarnings("unchecked")
            T cast = (T) msg;
            write0(ctx, cast, promise);
        } else {
            ctx.write(msg, promise);
        }
    }

    protected abstract void write0(ChannelHandlerContext ctx, T msg, ChannelPromise promise) throws Exception;
}
