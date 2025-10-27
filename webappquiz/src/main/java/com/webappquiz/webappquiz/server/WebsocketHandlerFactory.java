package com.webappquiz.webappquiz.server;

import java.util.ArrayList;
import java.util.List;

import com.webappquiz.webappquiz.handler.inbound.ChatInboundHandler;
import com.webappquiz.webappquiz.handler.inbound.GameChannelHandler;
import com.webappquiz.webappquiz.handler.inbound.GameHandler;
import com.webappquiz.webappquiz.handler.inbound.JsonDecodeHandler;
import com.webappquiz.webappquiz.handler.inbound.LobbyHandler;
import com.webappquiz.webappquiz.handler.inbound.LoginHandler;
import com.webappquiz.webappquiz.handler.inbound.ProtoBufDecodeHandler;
import com.webappquiz.webappquiz.handler.inbound.RoomHandler;
import com.webappquiz.webappquiz.handler.outbound.ChatOutboundHandler;
import com.webappquiz.webappquiz.handler.outbound.PacketWrappingOutboundHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;


public class WebsocketHandlerFactory 
{
    public static enum TYPE {
        JSONDECODE(0),
        PROTOBUFDECODE(1),
        LOGIN(2),
        LOBBY(3),
        CHAT_IN(4),
        ROOM(5),
        GAME(6),
        GAMECHANNEL(7),
        CHAT_OUT(8);

        private final int code;
        TYPE(int code) { this.code = code; }
        public int getCode() { return code; }
    }

    public static ChannelHandler createHandler(TYPE type, Object paramObject) 
    {
        if (type == TYPE.JSONDECODE)                return new JsonDecodeHandler();
        else if (type == TYPE.PROTOBUFDECODE)       return new ProtoBufDecodeHandler();
        else if (type == TYPE.LOGIN)                return new LoginHandler();
        else if (type == TYPE.LOBBY)                return new LobbyHandler(paramObject);
        else if (type == TYPE.CHAT_IN)              return new ChatInboundHandler(paramObject);
        else if (type == TYPE.ROOM)                 return new RoomHandler(paramObject);
        else if (type == TYPE.GAME)                 return new GameHandler(paramObject);
        else if (type == TYPE.GAMECHANNEL)          return new GameChannelHandler(paramObject);

        else if (type == TYPE.CHAT_OUT)             return new ChatOutboundHandler(paramObject);

        return null; // 없는 경우
    }

    public static List<ChannelHandler> createHandlerList(Object handlerParam)
    {
        List<ChannelHandler> list = new ArrayList<ChannelHandler>();

        for( TYPE type : TYPE.values())
        {
            var handler = createHandler(type, handlerParam);
            if( handler != null ) list.add(handler);
        }

        return list;
    }
}