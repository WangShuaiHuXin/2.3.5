package com.imapcloud.nest.pojo;

import lombok.Data;

@Data
public class CfRes {
    private Object object;
    private boolean success;
    private String msg;
}
