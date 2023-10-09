package com.nineSeven.mrpc.core.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;

public class JsonSerialization implements RpcSerialization{
    @Override
    public <T> byte[] serialize(T object) {
        return JSONObject.toJSONString(object).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(new String(data), clazz);
    }
}
