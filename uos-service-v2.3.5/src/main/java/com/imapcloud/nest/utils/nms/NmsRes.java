package com.imapcloud.nest.utils.nms;

import lombok.Data;

/**
 *
 */
@Data
public class NmsRes {
    private Integer code;
    private String error;
    private Object data;
}