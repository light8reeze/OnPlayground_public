package com.webappquiz.webappquiz.handler.inbound;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

//< Json으로 처리할때, ProtoBuf 쓸건데.. 일단 작성
public class JsonDecodeHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> 
{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void channelRead0( ChannelHandlerContext ctx, TextWebSocketFrame msg )
    {
        try 
        {
            JsonNode jsonNode = objectMapper.readTree(msg.text());
            ctx.fireChannelRead(jsonNode);
        } 
        catch (Exception e) 
        {
            ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"error\":\"Invalid JSON\"}"));
        }
    }
}
