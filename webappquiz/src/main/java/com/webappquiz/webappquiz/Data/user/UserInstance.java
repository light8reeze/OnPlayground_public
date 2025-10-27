package com.webappquiz.webappquiz.data.user;

import com.webappquiz.webappquiz.data.ChannelInstance;
import com.webappquiz.webappquiz.data.Room;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInstance {
    private long userIndex;
    private UserInfo userInfo;
    private ChannelInstance nowChannel = null;
    private Room nowRoom = null;
    private UserPosition userPosition = null;
    
    private ChannelHandlerContext userContext = null;

    public boolean isUserContext(ChannelHandlerContext ctx){
        final AttributeKey<Long> USER_INDEX = AttributeKey.valueOf("userIndex");
        return userContext.channel().attr(USER_INDEX).get() == ctx.channel().attr(USER_INDEX).get();
    }

    public void setUserPosition(int xPos, int yPos) {
        userPosition.moveTo(xPos, yPos);
    }

    public void moveUserPosition(int xPos, int yPos) {
        userPosition.move(xPos, yPos);
    }
}
