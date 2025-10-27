package com.webappquiz.webappquiz.handler.inbound;

import com.google.protobuf.MessageLite;
import com.webappquiz.webappquiz.data.ChannelInstance;
import com.webappquiz.webappquiz.data.Room;
import com.webappquiz.webappquiz.data.user.UserInstance;
import com.webappquiz.webappquiz.packetGenerator.PacketGenerator;
import com.webappquiz.webappquiz.packetGenerator.PacketTransferData;
import com.webappquiz.webappquiz.packetGenerator.ReturnPacketGenerator;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.EnterRoomRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.MovePlayerRequest;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketWrapper;
import com.webappquiz.webappquiz.protobuf.Protogen.PlayerPosition;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoomHandler extends UserInboundHandler {

    public RoomHandler(Object obj) {
        super(obj);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketWrapper packet) {
        if (packet.getCallType() != CallType.ROOM)
            ctx.fireChannelRead(packet);

        var data = PacketGenerator.getInstance().createPacket(packet.getPTypeValue(),
                packet.getPayload().toByteArray());
        if (packet.getPType() == PacketType.ENTER_ROOM_REQUEST)
            Process_EnterRoomRequest(ctx, (EnterRoomRequest) data);
        if (packet.getPType() == PacketType.MOVE_PLAYER_REQUEST)
            Process_MovePlayerRequest(ctx, (MovePlayerRequest) data);
    }

    private void Process_EnterRoomRequest(ChannelHandlerContext ctx, EnterRoomRequest req) {
        UserInstance user = getUser();
        // < TODO: 실패시 패킷처리 필요
        ChannelInstance userChannelInstance = user.getNowChannel();
        if (null == userChannelInstance)
            return;

        Room beforeRoom = user.getNowRoom();
        Room nextRoom = userChannelInstance.findRoom(req.getRoomId());
        if (null == nextRoom)
            return;

        // < TODO: 룸이 풀방인지 확인.
        if (null != beforeRoom)
            beforeRoom.removeUser(user);
        
        nextRoom.addUser(user);

        ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(
                ReturnPacketGenerator.enterRoomResponse(true, 0, nextRoom.getRoomInfo()).toByteArray())));
    }

    private void Process_MovePlayerRequest(ChannelHandlerContext ctx, MovePlayerRequest req) {
        UserInstance user = getUser();
        Room nowRoom = user.getNowRoom();
        if (null == nowRoom)
            return;

        PlayerPosition playerPosition = req.getMovePosition();

        user.moveUserPosition(playerPosition.getXPos(), playerPosition.getYPos());
        PacketWrapper response = ReturnPacketGenerator.movePlayerResponsePacketWrapper(user.getUserIndex(), playerPosition.getXPos(), playerPosition.getYPos());
        
        nowRoom.broadcastPacket(response);
    }
}