package com.webappquiz.webappquiz.handler.inbound;

import com.google.protobuf.MessageLite;
import com.webappquiz.webappquiz.data.user.UserManager;
import com.webappquiz.webappquiz.packetGenerator.PacketGenerator;
import com.webappquiz.webappquiz.packetGenerator.ReturnPacketGenerator;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.LoginRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;
import com.webappquiz.webappquiz.protobuf.Protogen.RegisterRequest;
import com.webappquiz.webappquiz.server.StateHandlerFactory.STATE;
import com.webappquiz.webappquiz.server.StateHandlerFactory.StateChangeArg;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;

public class LoginHandler extends SimpleChannelInboundHandler<PacketWrapper> 
{
    @Override
    protected void channelRead0( ChannelHandlerContext ctx, PacketWrapper packet )
    {
        if( packet.getCallType() != CallType.LOGIN ) ctx.fireChannelRead(packet); //< 본인께 아니면 패스

        //< 패킷 변환
        var data = PacketGenerator.getInstance().createPacket(packet.getPTypeValue(), packet.getPayload().toByteArray());
        switch (packet.getPType()) {
            case LOGIN_REQUEST: Process_LoginRequest( ctx, data ); break;
            case REGISTER_REQUEST : Process_RegisterRequest(ctx, data); break;
            default: break;
        }
    }


    private void Process_LoginRequest( ChannelHandlerContext ctx, MessageLite msg )
    {
        var data = (LoginRequest) msg;

        var userInfo = UserManager.getInstance().findUserInfo(data.getId());
        if(null != userInfo)
        {
            if(data.getPassword().equals(userInfo.getPassword()))
            {
                UserManager.getInstance().onUserLogin(userInfo, ctx);

                // 로그인시에 발급된 사용자 인스턴스를 가져온다
                var userInstance = UserManager.getInstance().getUserInstanceByUserId(userInfo.getUserId());
                if (userInstance == null) {
                    System.out.println("User instance not found after login");
                    return;
                }
                
                long userIndex = userInstance.getUserIndex();
                
                ctx.channel().writeAndFlush( new BinaryWebSocketFrame(Unpooled.wrappedBuffer( //< Unpooled wrrppedBuffer 는 복사 없이 포인터를 넘기기에 효율적
                    ReturnPacketGenerator.CreateLoginRespone(true, "login is success", userIndex).toByteArray()))
                );
                
                ctx.channel().write(new StateChangeArg(STATE.LOBBY, userInstance));
                ctx.channel().attr(AttributeKey.valueOf("userIndex")).set(userIndex);

                return;
            }
        }

        System.out.println("보내라고 시팔");
        ctx.channel().writeAndFlush( new BinaryWebSocketFrame(Unpooled.wrappedBuffer( //< Unpooled wrrppedBuffer 는 복사 없이 포인터를 넘기기에 효율적
            ReturnPacketGenerator.CreateLoginRespone(false, "login is fail", 0).toByteArray()))
        );
    }

    private void Process_RegisterRequest(ChannelHandlerContext ctx, MessageLite msg)
    {
        var data = (RegisterRequest)msg;

        if(UserManager.getInstance().findUserInfo(data.getId()) != null)
        {
            ctx.channel().writeAndFlush( new BinaryWebSocketFrame(Unpooled.wrappedBuffer( //< Unpooled wrrppedBuffer 는 복사 없이 포인터를 넘기기에 효율적
                ReturnPacketGenerator.CreateRegisterResponse(false, "이미 가입된 유저입니다.").toByteArray()))
            );
            
            return;
        }

        UserManager.getInstance().registerUserInfo(data.getId(), data.getPassword(), data.getId());
        ctx.channel().writeAndFlush( new BinaryWebSocketFrame(Unpooled.wrappedBuffer( //< Unpooled wrrppedBuffer 는 복사 없이 포인터를 넘기기에 효율적
            ReturnPacketGenerator.CreateRegisterResponse(true, "register is succes").toByteArray()))
        );
    }
}