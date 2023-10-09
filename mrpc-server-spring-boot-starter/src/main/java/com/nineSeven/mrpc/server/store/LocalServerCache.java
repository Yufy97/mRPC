package com.nineSeven.mrpc.server.store;

import java.util.HashMap;
import java.util.Map;

public class LocalServerCache {
    private static final Map<String, Object> serverCacheMap = new HashMap<>();

    public static void store(String serverName, Object server) {
        serverCacheMap.put(serverName, server);
    }

    public static Object get(String serverName) {
        return serverCacheMap.get(serverName);
    }
}
