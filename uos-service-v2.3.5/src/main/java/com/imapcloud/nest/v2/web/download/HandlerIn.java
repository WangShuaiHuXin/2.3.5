package com.imapcloud.nest.v2.web.download;

import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import lombok.Data;

import java.io.Serializable;

/**
 * 下载参数
 *
 * @author boluo
 * @date 2023-05-08
 */
@Data
public class HandlerIn implements Serializable {

    /**
     * key值，用于定位处理器 {@link DownloadAnnotation}
     */
    private String key;

    /**
     * 下载的具体参数
     */
    private String param;

    private String accountId;

    private String orgCode;
}
