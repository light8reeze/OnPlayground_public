package com.webappquiz.webappquiz.packetGenerator;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.MessageLite;

public class PacketGenerator 
{
    private static final PacketGenerator INSTANCE = new PacketGenerator();
    public static PacketGenerator getInstance() {return INSTANCE;}

    //< 각 타입에 대한 값과 상위 클레스 타입을 받아둔다.
    private final Map<Integer, MessageLite> packetMap = new HashMap<>();

    private PacketGenerator() {}

    //< 값을 받아서 지정되었던 타입으로 처리
    public MessageLite createPacket(int pType, byte[] payload)
    {
        MessageLite defaultInstance = packetMap.get(pType);
        if (defaultInstance == null) return null;
        
        //< 바이트 데이터를 파싱해서 실제 객체 리턴
        try {
            return defaultInstance.getParserForType().parseFrom(payload);
        } catch (Exception e) {
           return null;
        }
    }

    // 혹시 동적으로 패킷 타입 추가하고 싶으면 사용
    public void registerPacket(int pType, MessageLite defaultInstance) 
    {
        packetMap.put(pType, defaultInstance);
    }
}
