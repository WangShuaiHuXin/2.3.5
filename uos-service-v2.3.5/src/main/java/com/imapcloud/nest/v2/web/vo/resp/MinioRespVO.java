package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.ToString;

/**
 * minio返回值
 *
 * @author boluo
 * @date 2022-11-25
 */
@ToString
public class MinioRespVO {

    private MinioRespVO() {}

    @Data
    public static class PictureRespVO {

        private String object;
    }
}
