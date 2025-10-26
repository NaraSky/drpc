package com.rain.rpc.protocol.enumeration;

public enum RpcType {

    // Request message
    REQUEST(1),
    // Response message
    RESPONSE(2),
    // Heartbeat data
    HEARTBEAT(3);

    private final int type;

    RpcType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static RpcType valueOf(int type) {
        for (RpcType rpcType : RpcType.values()) {
            if (rpcType.type == type) {
                return rpcType;
            }
        }
        return null;
    }
}
