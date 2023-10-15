package com.nineSeven.mrpc.client.transport;

import com.nineSeven.mrpc.core.common.RpcRequest;
import com.nineSeven.mrpc.core.protocol.MessageProtocol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestMetaData {

    private MessageProtocol<RpcRequest> protocol;

    private String address;

    private Integer port;

    private Integer timeout;

}
