package com.webappquiz.webappquiz.packetGenerator;

import com.google.protobuf.MessageLite;
import com.webappquiz.webappquiz.protobuf.Protogen.CallType;
import com.webappquiz.webappquiz.protobuf.Protogen.PacketType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PacketTransferData {
    public CallType callType;
    public PacketType pType;
    public MessageLite message;
}
