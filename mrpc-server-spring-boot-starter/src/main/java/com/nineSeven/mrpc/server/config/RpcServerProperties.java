package com.nineSeven.mrpc.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mrpc.server")
public class RpcServerProperties {
    private Integer port;

    private String appName;

    private String serverAddr;

    private String registryAddr;

    private Integer baseSleepTime = 3000;

    private int maxRetries = 3;

    private String basePath = "/mrpc";

}
