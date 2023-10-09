package com.nineSeven.mrpc.core.register;

import com.nineSeven.mrpc.core.common.ServiceInfo;

import java.io.IOException;

public interface RegisterService {
    void register(ServiceInfo serviceInfo) throws Exception;

    void unRegister(ServiceInfo serviceInfo) throws Exception;

    void destroy() throws IOException;
}
