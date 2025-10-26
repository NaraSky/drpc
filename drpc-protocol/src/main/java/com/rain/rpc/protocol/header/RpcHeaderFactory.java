package com.rain.rpc.protocol.header;

import com.rain.rpc.common.id.IdFactory;
import com.rain.rpc.constants.RpcConstants;
import com.rain.rpc.protocol.enumeration.RpcType;

public class RpcHeaderFactory {

    public static RpcHeader getRequestHeader(String serializationType) {
        RpcHeader header = new RpcHeader();
        long requestId = IdFactory.getRequestId();
        header.setMagic(RpcConstants.MAGIC);
        header.setRequestId(requestId);
        header.setMessageType((byte) RpcType.REQUEST.getType());
        header.setStatus((byte) 0x1);
        header.setSerializationType(serializationType);
        return header;
    }
}
