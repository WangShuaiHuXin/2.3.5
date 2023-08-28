package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

/**
 * @Classname PushStreamCreateDO
 * @Description 普通推流新增参数
 * @Date 2023/4/6 9:44
 * @Author Carnival
 */
@Data
public class PushStreamCreateInDO {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 推流名称
     */
    private String streamName;

    /**
     * 流媒体服务ID
     */
    private String serverId;
}
