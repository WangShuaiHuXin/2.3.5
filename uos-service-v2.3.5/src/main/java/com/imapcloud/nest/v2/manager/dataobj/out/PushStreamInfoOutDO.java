package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * @Classname PushStreamInfoDO
 * @Description 普通推流信息
 * @Date 2023/4/6 17:29
 * @Author Carnival
 */
@Data
public class PushStreamInfoOutDO {

    private String streamId;

    private String serverId;

    private String pushUrl;

    private String pullUrl;
}
