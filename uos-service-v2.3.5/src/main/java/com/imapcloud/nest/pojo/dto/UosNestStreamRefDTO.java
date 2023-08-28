package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class UosNestStreamRefDTO  {

    private String nestId;

    private String uavId;

    private String streamId;

    private Integer streamUse;
}
