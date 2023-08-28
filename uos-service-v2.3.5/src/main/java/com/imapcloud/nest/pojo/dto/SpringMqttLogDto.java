package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class SpringMqttLogDto {
    private String fileName;
    private String filePath;
    private String md5;
    private Integer finish = 0;
}
