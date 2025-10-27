package com.webappquiz.webappquiz.protobuf;

import com.google.protobuf.MessageLite;

public class DecodeBuffer 
{
    private DecodeBuffer() {} //< 인스턴스 X

    //< Protobuf에 생성된 메시지들은 MessageList를 상속받고 있다 제한할것
    public static <T extends MessageLite> T toProto( byte[] payLoad, T defaultInstance )
    {
        try
        {
            @SuppressWarnings("unchecked")
            T parsed = (T) defaultInstance.getParserForType().parseFrom(payLoad); //< ProtoType에서 지정하는 변환 getDefaultInctance() 받아서 처리
            return parsed;
        }
        catch(Exception e) {
            return null;
        }
    }
}