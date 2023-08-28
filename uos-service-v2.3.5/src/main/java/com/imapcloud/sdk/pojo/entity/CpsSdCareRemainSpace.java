package com.imapcloud.sdk.pojo.entity;

import lombok.Data;

/**
 * 获取SD剩余容量(可离线查询sd剩余空间)
 *
 * @author: zhengxd
 * @create: 2021/9/13
 **/
@Data
public class CpsSdCareRemainSpace {
    /**
     * 剩余空间
     */
    private Long remainingSpaceInMB;
    /**
     * 是否为缓存值
     */
    private Boolean cache;
}
