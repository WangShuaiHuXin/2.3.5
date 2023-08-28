package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * 机巢、移动终端的名称，播流地址
 *
 * @author: zhengxd
 * @create: 2021/7/28
 **/
@Data
public class NestAppNameHttpUrlDTO {
    /**
     * 机巢、移动终端名称
     */
    private String name;
    /**
     * 播放流地址
     */
    private String httpUrl;
}
