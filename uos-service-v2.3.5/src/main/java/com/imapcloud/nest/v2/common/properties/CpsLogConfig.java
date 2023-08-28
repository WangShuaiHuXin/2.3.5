package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * cps日志配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 * @deprecated 2.2.3，基站日志和CPS日志统一上传回调接口，将在后续版本删除
 */
@Deprecated
@Data
public class CpsLogConfig {

    /**
     * 日志上传URL
     * @deprecated 2.2.3，使用统一上传接口URL替代，参见{@link OssConfig}
     */
    @Deprecated
    private String uploadUrl;

    /**
     * 日志存储路径
     * @deprecated 2.2.3，使用统一文件服务替代
     */
    @Deprecated
    private String storePath;

    /**
     * 模块压缩包名称
     * @deprecated 2.2.3，使用统一文件服务替代
     */
    @Deprecated
    private String zipName;

}
