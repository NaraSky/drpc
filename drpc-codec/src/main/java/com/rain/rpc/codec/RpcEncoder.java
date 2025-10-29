package com.rain.rpc.codec;

import com.rain.rpc.common.utils.SerializationUtils;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.header.RpcHeader;
import com.rain.rpc.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * RPC encoder for encoding RPC protocol objects to byte data.
 * This encoder handles the serialization of outgoing RPC messages into network data.
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> implements RpcCodec {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> message, ByteBuf byteBuf) throws Exception {
        RpcHeader header = message.getHeader();

        // Write header information
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getMessageType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());

        String serializationType = header.getSerializationType();

        // Get serialization instance
        // TODO: Support SPI for multiple serialization methods
        Serialization serialization = getJdkSerialization();

        // Write serialization type with padding
        byteBuf.writeBytes(SerializationUtils.paddingString(serializationType).getBytes(StandardCharsets.UTF_8));

        // Serialize and write data
        byte[] data = serialization.serialize(message.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}