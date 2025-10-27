package com.webappquiz.webappquiz.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.webappquiz.webappquiz.protobuf.Protogen.ChannelInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.ChannelType;
import com.webappquiz.webappquiz.protobuf.Protogen.GameModeInfo;
import com.webappquiz.webappquiz.protobuf.Protogen.GameModeType;

public class ChannelManager {

    private static ChannelManager INSTANCE = new ChannelManager();
    public static ChannelManager getInstance(){ return INSTANCE; }

    private Map<Integer, ChannelInstance> channelMap = new HashMap();
    private int  channelIdKey = 0;

    public ChannelManager(){
        
        //< 테스트용 더미 채널 생성
        ChannelInfo channelInfo = ChannelInfo.newBuilder()
                                        .setChannelId(++channelIdKey)
                                        .setChannelType(ChannelType.BTC)
                                        .setChannelName("BTC Channel")
                                        .setGameModeInfo(GameModeInfo.newBuilder().setGameModeId(0).setGameModeName("OXQUIZ").setGameModeType(GameModeType.OXQUIZ).build())
                                        .setMaxUsers(10)
                                        .setRoomCount(10)
                                        .build();

        addChannel(channelInfo);

        
        channelInfo = ChannelInfo.newBuilder()
                               .setChannelId(++channelIdKey)
                               .setChannelType(ChannelType.BTB)
                               .setChannelName("BTB Channel")
                               .setGameModeInfo(GameModeInfo.newBuilder().setGameModeId(1).setGameModeName("Quiz").setGameModeType(GameModeType.QUIZ).build())
                               .setMaxUsers(10)
                               .setRoomCount(10)
                               .build();

        addChannel(channelInfo);
    }
    
    public void addChannel(ChannelInfo channelInfo){
        ChannelInstance newChannel = new ChannelInstance(channelInfo);
        channelMap.put(channelInfo.getChannelId(), newChannel);
    }

    public ChannelInfo createChannel(ChannelType channelType, String name, GameModeInfo gameModeInfo, int maxUsers, int roomCount)
    {
        ChannelInfo channelInfo = ChannelInfo.newBuilder()
                                        .setChannelId(++channelIdKey)
                                        .setChannelType(channelType)
                                        .setChannelName(name)
                                        .setGameModeInfo(gameModeInfo)
                                        .setMaxUsers(maxUsers)
                                        .setRoomCount(roomCount)
                                        .build();

        addChannel(channelInfo);
        return channelInfo;
    }

    public ChannelInstance findChannel(int channelId){
        return channelMap.get(channelId);
    }

    public ArrayList<ChannelInfo> getChannelList(){
        ArrayList<ChannelInfo> channelInfos = new ArrayList<>();
        channelMap.forEach((key, value) -> channelInfos.add(value.getChannelInfo()));

        return channelInfos;
    }
}
