package com.nineSeven.mrpc.core.serialization;

public interface RpcSerialization {

    <T> byte[] serialize(T object);

    <T> T deserialize(byte[] data, Class<T> clazz);

}
