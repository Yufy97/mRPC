package com.nineSeven.mrpc.core.register;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterConfig {

    private Integer baseSleepTime;

    private int maxRetries;

    private String basePath;

    private String registryAddr;
}
