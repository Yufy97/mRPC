package com.nineSeven.mrpc.core.protocol;

import lombok.Getter;

public enum MsgStatus {
    SUCCESS((byte) 0),
    FAIL((byte) 1)
    ;

    @Getter
    private final byte status;

    MsgStatus(byte status) {
        this.status = status;
    }

    public static boolean success(byte status){
        return MsgStatus.SUCCESS.getStatus() == status;
    }
}
