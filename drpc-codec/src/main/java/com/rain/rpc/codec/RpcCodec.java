package com.rain.rpc.codec;

import com.rain.rpc.serialization.api.Serialization;
import com.rain.rpc.serialization.jdk.JdkSerialization;

/**
 * Codec interface for RPC communication.
 * Provides a default implementation for getting JDK serialization.
 */
public interface RpcCodec {

    /**
     * Get the default JDK serialization implementation
     *
     * @return the JDK serialization instance
     */
    default Serialization getJdkSerialization() {
        return new JdkSerialization();
    }
}