package com.nineSeven.mrpc.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rpc.client")
public class RpcClientProperties {

    private String balance;

    private String discoveryAddr;

    private String serialization;

    private Integer timeout;

    private int baseSleepTime = 1000;

    private int maxRetries = 3;

    private String basePath = "/mrpc";

}
