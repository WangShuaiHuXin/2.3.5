package com.imapcloud.sdk.pojo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CpsVersionCode implements Serializable {
    
    private String version;
    private String version_code;
}
