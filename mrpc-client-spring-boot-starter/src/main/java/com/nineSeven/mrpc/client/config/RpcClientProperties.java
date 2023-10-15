package com.nineSeven.mrpc.client.config;

import lombok.Data;

@Data
public class RpcClientProperties {

    private String discoveryAddr;

    private String serialization;

    private Integer timeout;

    private int baseSleepTime = 1000;

    private int maxRetries = 3;

    private String basePath = "/mrpc";

}
