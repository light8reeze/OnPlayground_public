package com.webappquiz.webappquiz.handler.inbound;

import com.webappquiz.webappquiz.server.StateHandlerFactory.STATE;
import com.webappquiz.webappquiz.server.StateHandlerFactory.StateChangeArg;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

/*
 * 초기 AcceptHandler 여기서 핸들러를 연결하고 처리한다. 핸들러는 한번 성공하면 연결은 그대로 살아있다.
 */
public class AcceptHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) 
    {
        //URI uriObject   = URI.create(req.uri());    //< string 으로 온 객체를 URI 객체에 저장

        //String clientIP = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        //int clientPort  = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();

        try {
            ctx.channel().write(new StateChangeArg(STATE.ACCEPTED, null));
        }  catch ( Exception e ) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
        }

        //< 일부 클라이언트 라이브러리 호환성 문제로 안정성의 이유로 형식적인 처리 / 과거 Draft Sec-WebSocket-Location 같은게 필요했다.
        //< Websocket의 handshake는 tcp low level handshake와는 다르다 아래는 TCP 위에서 동작하는 HTTP Handshake
        //< client -> http req ( upgrade : websocket ) -> server -> switching protocol -> now 
        //< 즉 http로 연결하고 그 이후에 websocket 변환을 거쳐야 하는데 이걸 websocket handshake라고 한다.
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory( getWebSocketLocation(req), null, true);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req); //< 지원 가능한 Websocket 버전인지 체크 ( 불가능 : null )

        //< 지원 안되면 다시해보라는 형식의 메시지를 보내준다.
        if (handshaker == null) WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        else                    handshaker.handshake(ctx.channel(), req);
    }

    private static String getWebSocketLocation(FullHttpRequest req) 
    {
        return "ws://" + req.headers().get(HttpHeaderNames.HOST) + req.uri();
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) 
    {
        String failString = "";
        //< 성공한것이 아니라면 리턴 처리 만들어준다.
        if (res.status().code() != 200) 
        {
            failString = "Failure: " + res.status().toString() + "\r\n";
            res.content().writeBytes(failString.getBytes());
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        //< 그리고 버퍼에 쓰고 전송
        ChannelFuture f = ctx.channel().writeAndFlush(res);

        //< 종료
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) f.addListener(ChannelFutureListener.CLOSE);
    }

    //< 강종
    public static void forceCloseConnection(ChannelHandlerContext ctx, String message) 
    {
        ctx.close();
    }
}