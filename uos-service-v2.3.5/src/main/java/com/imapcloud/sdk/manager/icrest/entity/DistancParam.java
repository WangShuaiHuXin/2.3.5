package com.imapcloud.sdk.manager.icrest.entity;

import lombok.Data;

@Data
public class DistancParam {
    private float targetDistance;
    private Integer direction;
    private float targetYaw;
    private float speedFactor;
}
