package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.ToString;

/**
 * 下载resp签证官
 *
 * @author boluo
 * @date 2023-05-08
 */
@ToString
public class DownloadRespVO {

    private DownloadRespVO() {}

    @Data
    public static class PreSignedRespVO {

        private String preSignedKey;
    }
}
