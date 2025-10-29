package com.rain.rpc.codec;

import com.rain.rpc.common.utils.SerializationUtils;
import com.rain.rpc.constants.RpcConstants;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.enumeration.RpcType;
import com.rain.rpc.protocol.header.RpcHeader;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.protocol.response.RpcResponse;
import com.rain.rpc.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * RPC decoder for decoding byte data to RPC protocol objects.
 * This decoder handles the deserialization of incoming network data into RPC messages.
 */
public class RpcDecoder extends ByteToMessageDecoder implements RpcCodec {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Check if there are enough bytes for the header
        if (in.readableBytes() < RpcConstants.HEADER_TOTAL_LEN) return;

        in.markReaderIndex();
        short magic = in.readShort();
        // Validate magic number
        if (magic != RpcConstants.MAGIC) {
            throw new IllegalArgumentException("Invalid magic number: " + magic);
        }

        byte messageType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();

        // Read serialization type
        ByteBuf serializationTypeByteBuf = in.readBytes(SerializationUtils.MAX_SERIALIZATION_TYPE_COUNT);
        String serializationType = SerializationUtils.subString(serializationTypeByteBuf.toString(StandardCharsets.UTF_8));

        int dataLength = in.readInt();
        // Check if there are enough bytes for the data
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        // Determine message type
        RpcType massageTypeEnum = RpcType.valueOf(messageType);
        if (massageTypeEnum == null) {
            return;
        }

        // Build RPC header
        RpcHeader rpcHeader = new RpcHeader();
        rpcHeader.setMagic(magic);
        rpcHeader.setStatus(status);
        rpcHeader.setRequestId(requestId);
        rpcHeader.setMessageType(messageType);
        rpcHeader.setSerializationType(serializationType);
        rpcHeader.setMessageLength(dataLength);

        // Get serialization instance
        Serialization serialization = getJdkSerialization();

        // Handle different message types
        switch (massageTypeEnum) {
            case REQUEST:
                RpcRequest request = serialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(rpcHeader);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = serialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(rpcHeader);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                // TODO: Handle heartbeat messages
                break;
        }
    }
}