package com.nineSeven.mrpc.client.transport;

public class NetClientTransportFactory {
    public static NetClientTransport getNetClientTransport(){
        return new NettyNetClientTransport();
    }
}
