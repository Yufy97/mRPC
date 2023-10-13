package com.nineSeven.mrpc.client.transport;

import com.nineSeven.mrpc.core.common.RpcResponse;
import com.nineSeven.mrpc.core.protocol.MessageProtocol;

public interface NetClientTransport {
    MessageProtocol<RpcResponse> sendRequest(RequestMetaData metaData) throws Exception;
}
