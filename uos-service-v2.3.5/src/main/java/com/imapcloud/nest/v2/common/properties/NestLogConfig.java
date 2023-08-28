package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * 基站日志配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class NestLogConfig {

    /**
     * 日志上传URL
     * @deprecated 2.2.3，上传配置统一由{@link OssConfig}管理，将在后续版本删除
     */
    @Deprecated
    private String uploadUrl;

    /**
     * 日志存储路径
     * @deprecated 2.2.3，将在后续版本删除
     */
    @Deprecated
    private String storePath;

    /**
     * 是否分片处理，默认为true
     */
    private Boolean reduce = Boolean.TRUE;

    /**
     * 基站日志上传完成，CPS同步存储数据信息回调URL
     * @since 2.2.3
     */
    private String callbackUrl;

}
