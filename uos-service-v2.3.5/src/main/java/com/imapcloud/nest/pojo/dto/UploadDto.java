package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class UploadDto {
    /**
     * 分片序号
     */
    private Integer partNumber;

    /**
     * 上传地址
     */
    private String uploadUrl;
}
