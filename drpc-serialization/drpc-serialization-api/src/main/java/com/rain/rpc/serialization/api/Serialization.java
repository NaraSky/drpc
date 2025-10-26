package com.rain.rpc.serialization.api;

import javax.sql.rowset.serial.SerialException;

public interface Serialization {

    <T> byte[] serialize(T obj) throws SerialException;

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
