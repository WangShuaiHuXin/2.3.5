package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.v2.common.enums.DownloadAnnotation;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 下载请求
 *
 * @author boluo
 * @date 2023-05-08
 */
@ToString
public final class DownloadReqVO {

    private DownloadReqVO() {}

    @Data
    public static class PreSignedReqVO {

        /**
         * key值，用于定位处理器 {@link DownloadAnnotation}
         */
        @NotBlank(message = "key is blank")
        private String key;

        /**
         * 下载的具体参数
         */
        private String param;
    }
}
