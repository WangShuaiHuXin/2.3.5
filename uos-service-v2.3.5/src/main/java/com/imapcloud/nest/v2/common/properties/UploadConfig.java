package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * 上传配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 * @deprecated 2.2.3，将在2023/06/30后删除
 */
@Deprecated
@Data
public class UploadConfig {

    /**
     * cps1.9.0以前数据不分片回传接口
     */
    @Deprecated
    private String url;

    /**
     * 切片初始化路径
     * @deprecated 2.2.3，将在2023/06/30后删除
     */
    @Deprecated
    private String chunkInitPath;
    /**
     * 切片合并路径
     * @deprecated 2.2.3，将在2023/06/30后删除
     */
    @Deprecated
    private String chunkCombinePath;
    /**
     * 切片同步路径
     * @deprecated 2.2.3，将在2023/06/30后删除
     */
    @Deprecated
    private String chunkSyncPath;

}
