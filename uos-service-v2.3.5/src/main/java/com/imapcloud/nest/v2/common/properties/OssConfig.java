package com.imapcloud.nest.v2.common.properties;

import lombok.Data;
import org.springframework.util.unit.DataSize;

/**
 * 对象存储配置信息
 * @author Vastfy
 * @date 2023/02/24 10:13
 * @since 2.2.3
 */
@Data
public class OssConfig {

    /**
     * 统一上传URL
     */
    private String uploadUrl;

    /**
     * 分片初始化URL
     */
    private String chunkInitUrl;

    /**
     * 分片上传URL
     */
    private String chunkUploadUrl;

    /**
     * 分片合并完成通知URL
     */
    private String chunkComposeNotifyUrl;

    /**
     * 分片合并完成通知URL，默认为15M
     */
    private DataSize chunkSize = DataSize.ofMegabytes(15);

    /**
     * 网关认证URL
     */
    private String authUrl;

}
