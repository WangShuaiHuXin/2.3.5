package com.imapcloud.sdk.manager;

import lombok.Data;

/**
 * 上传参数
 * @author Vastfy
 * @date 2023/2/24 10:48
 * @since 2.2.3
 */
@Data
public class UploadV2Param {

    private String v2SmallFileUploadUrl;
    private Boolean v2ChunkUpload = Boolean.TRUE;
    private String v2ChunkInitUrl;
    private String v2ChunkUploadUrl;
    private String v2ChunkCombineCallbackUrl;
    private Long v2ChunkSize = 15 * 1024 * 1024L;
    private String v2ChunkSyncUrl;
    /**
     * 网关认证URL
     */
    private String v2AuthUrl;
}
