package com.nineSeven.mrpc.core.common;

public class ServiceUtil {
    public static String serviceKey(String serviceName, String version) {
        return serviceName + "-" + version;
    }
}
