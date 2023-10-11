package com.nineSeven.mrpc.core.discover;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiscoverConfig {

    private int baseSleepTime;

    private int maxRetries;

    private String basePath;

    private String discoverAddr;

}
