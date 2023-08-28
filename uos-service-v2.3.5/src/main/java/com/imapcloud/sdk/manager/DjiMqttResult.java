package com.imapcloud.sdk.manager;


import com.imapcloud.sdk.pojo.djido.DjiCommonDO;
import lombok.Data;

@Data
public class DjiMqttResult<T> {
    private DjiCommonDO<T> commonDO;
    private String errMsg;
    private boolean success;
    private boolean timeout = false;
}
