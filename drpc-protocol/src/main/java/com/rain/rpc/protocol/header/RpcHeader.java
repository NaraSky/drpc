package com.rain.rpc.protocol.header;

import java.io.Serializable;

/**
 * RPC Protocol Header
 * 
 * Header Format:
 * +------------------------------------------------------------------------------------+
 * | Magic number 2byte | Message type 1byte | Status 1byte |     Request ID 8byte      |
 * +------------------------------------------------------------------------------------+
 * |           Serialization type 16byte                  |    Data length 4byte        |
 * +---------------------------------------------------------------+
 */
public class RpcHeader implements Serializable {

    private static final long serialVersionUID = -309546999034745437L;

    /**
     * Magic number, 2 bytes
     */
    private short magic;

    /**
     * Message type, 1 byte
     */
    private byte messageType;

    /**
     * Status, 1 byte
     */
    private byte status;

    /**
     * Request ID, 8 bytes
     */
    private long requestId;

    /**
     * Serialization type, 16 bytes. If less than 16 bytes, pad with 0s.
     * The serialization type length cannot exceed 16.
     */
    private String serializationType;

    /**
     * Message length, 4 bytes
     */
    private int messageLength;

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(String serializationType) {
        this.serializationType = serializationType;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    @Override
    public String toString() {
        return "RpcHeader{" +
                "magic=" + magic +
                ", messageType=" + messageType +
                ", status=" + status +
                ", requestId=" + requestId +
                ", serializationType='" + serializationType + '\'' +
                ", messageLength=" + messageLength +
                '}';
    }
}