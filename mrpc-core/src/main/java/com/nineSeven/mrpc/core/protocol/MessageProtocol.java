package com.nineSeven.mrpc.core.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageProtocol<T> implements Serializable {

    private MessageHeader header;

    private T body;
}
