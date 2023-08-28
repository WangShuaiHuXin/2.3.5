package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * 流媒体配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 * @deprecated 2.3.2，已废弃nms，将在后续版本中删除该类
 */
@Deprecated
@Data
public class IptvConfig {

    /**
     * URL
     */
    private String url;

    /**
     * 路径
     */
    private String path;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
