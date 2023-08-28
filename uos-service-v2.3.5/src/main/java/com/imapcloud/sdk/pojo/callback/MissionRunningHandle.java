package com.imapcloud.sdk.pojo.callback;

import com.imapcloud.sdk.pojo.constant.MissionRunStateEnum;

/**
 * @author wmin
 */
@FunctionalInterface
public interface MissionRunningHandle {
    void handle(byte[] payload);
}
