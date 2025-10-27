package com.webappquiz.webappquiz.handler.inbound;

import com.webappquiz.webappquiz.data.ChannelInstance;
import com.webappquiz.webappquiz.data.Room;
import com.webappquiz.webappquiz.data.user.UserInstance;
import com.webappquiz.webappquiz.packetGenerator.PacketGenerator;
import com.webappquiz.webappquiz.packetGenerator.PacketTransferData;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.EnterRoomRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.LeaveChannelRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.LeaveChannelResponse;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;
import com.webappquiz.webappquiz.server.StateHandlerFactory.STATE;
import com.webappquiz.webappquiz.server.StateHandlerFactory.StateChangeArg;

import io.netty.channel.ChannelHandlerContext;

public class GameChannelHandler extends UserInboundHandler {

    public GameChannelHandler(Object obj) {
        super(obj);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketWrapper packet) {
        if (packet.getCallType() != CallType.CHANNEL)
            ctx.fireChannelRead(packet);

        var data = PacketGenerator.getInstance().createPacket(packet.getPTypeValue(),
                packet.getPayload().toByteArray());
                
        if (packet.getPType() == PacketType.LEAVE_CHANNEL_REQUEST)
            Process_LeaveChannelRequest(ctx, (LeaveChannelRequest) data);
    }

    private void Process_LeaveChannelRequest(ChannelHandlerContext ctx, LeaveChannelRequest req) {
        UserInstance user = getUser();

        ChannelInstance nowChannel = user.getNowChannel();
        if (null == nowChannel)
            return;

        Room nowRoom = user.getNowRoom();
        if (null == nowRoom)
            return;

        nowRoom.removeUser(user);
        nowChannel.removeUser(user);

        LeaveChannelResponse response = LeaveChannelResponse.newBuilder()
                .setSuccess(true)
                .setErrorCode(0)
                .build();

        PacketTransferData transferData = new PacketTransferData(CallType.CHANNEL, PacketType.LEAVE_CHANNEL_RESPONSE, response);
        ctx.channel().write(transferData);

        // < LOBBY 상태로 변경
        ctx.channel().writeAndFlush(new StateChangeArg(STATE.LOBBY, user));
    }
}
