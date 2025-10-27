package com.webappquiz.webappquiz.handler.outbound;

import org.springframework.data.util.Pair;

import com.webappquiz.webappquiz.server.StateHandlerFactory;
import com.webappquiz.webappquiz.server.StateHandlerFactory.STATE;
import com.webappquiz.webappquiz.server.StateHandlerFactory.StateChangeArg;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

//< context의 핸들러 상태를 바꾸는 OutboundHandler
//< 패킷 전송, 상태 변경등의 컨텍스트 이벤트시 순차적으로 실행시키기 위해서 구현 
public class StateChangeOutboundHandler extends SimpleChannelOutboundHandler<StateChangeArg> {

    @Override
    protected void write0(ChannelHandlerContext ctx, StateChangeArg msg, ChannelPromise promise) throws Exception {
        StateHandlerFactory.getInstance().changeHandler(ctx, msg.state(), msg.param());
    }
}
