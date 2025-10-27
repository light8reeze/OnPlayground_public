package com.webappquiz.webappquiz.server;

import java.util.*;

import com.webappquiz.webappquiz.server.WebsocketHandlerFactory.TYPE;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

public class StateHandlerFactory {
    public enum STATE {
        ACCEPTED,
        LOBBY,
        ROOM
    }
    
    public record StateChangeArg(STATE state, Object param){};

    /*
        - 아래 핸들러들은 지우면 안됨 -
        "HttpServerCodec#0"                                 => HTTP 요청/응답을 처리하는 Codec
        "HttpObjectAggregator#0"                            => HTTP 메시지를 합쳐서 FullHttpRequest로 변환
        "DefaultChannelPipeline$TailContext#0"              => Netty 내부에서 사용하는 핸들러 (절대 삭제하면 안 됨)

        "PacketWrappingOutbound"                            => 패킷 최종 전송하는 핸들러러
        "StateChangeOutbound"                               => 컨텍스트 상태 처리 담당
     */
    private static final List<String> PROTECTED_HANDLERS = 
        Arrays.asList("HttpServerCodec", "HttpObjectAggregator", 
                        "wsdecoder", "wsencoder", "TailContext", "PacketWrappingOutbound", "StateChangeOutbound");
    Map<STATE, List<TYPE>> stateInboundHandler = new HashMap<>();
    Map<STATE, List<TYPE>> stateOutboundHandler = new HashMap<>();

    private static final StateHandlerFactory INSTANCE = new StateHandlerFactory();
    public static StateHandlerFactory getInstance() { return INSTANCE; }

    // < 생성자에서 상황에 맞는 데이터 삽입
    public StateHandlerFactory() {
        stateInboundHandler.put(STATE.ACCEPTED, Arrays.asList(TYPE.PROTOBUFDECODE, TYPE.LOGIN));
        stateInboundHandler.put(STATE.LOBBY, Arrays.asList(TYPE.PROTOBUFDECODE, TYPE.LOBBY));
        stateInboundHandler.put(STATE.ROOM, Arrays.asList(TYPE.PROTOBUFDECODE, TYPE.CHAT_OUT, TYPE.GAMECHANNEL, TYPE.GAME, TYPE.ROOM, TYPE.CHAT_IN));
    }

    // < 각 상황에 맞는 Instance를 받아온다. ( 다이나믹 캐스팅이 적용되어 ㄱㄴ )
    private List<ChannelHandler> getInboundHandlerInstance(STATE state, Object handlerParam) {
        List<ChannelHandler> temp = new ArrayList<>();
        for (TYPE t : stateInboundHandler.get(state))
            temp.add(WebsocketHandlerFactory.createHandler(t, handlerParam));
        return temp;
    }

    private List<ChannelHandler> getOutboundHandlerInstance(STATE state, Object handlerParam) {
        List<ChannelHandler> temp = new ArrayList<>();
        for (TYPE t : stateOutboundHandler.get(state))
            temp.add(WebsocketHandlerFactory.createHandler(t, handlerParam));
        return temp;
    }

    // < 핸들러를 전부 지워주고 새로 넣어준다. 기존 처리되는 핸들러는 영향 없다.
    public void changeHandler(ChannelHandlerContext ctx, STATE state, Object handlerParam) {
        if (ctx.channel().isActive() == false) return; // < 비정상 연결
        List<String> handlerNames = new ArrayList<>(ctx.pipeline().names());
        for (String name : handlerNames) {
            if ( isProtectedHandler( name ) ) continue;
            ctx.pipeline().remove(name); 
        }

        for (var h : getInboundHandlerInstance(state, handlerParam)) 
            ctx.pipeline().addLast(h);
    }
    //< 일치하는지 먼저 해봐야함
    private boolean isProtectedHandler(String handlerName) {
        return PROTECTED_HANDLERS.stream().anyMatch(handlerName::contains); // stream : 하나씩 실행 , 등등 아무튼 이거 새로운 스타일 for- contains이랑 같음
    }
}