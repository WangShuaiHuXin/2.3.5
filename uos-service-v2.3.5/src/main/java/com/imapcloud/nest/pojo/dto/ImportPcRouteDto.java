package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author wmin
 */
@Data
public class ImportPcRouteDto {
    /**
     * 0-机巢航线
     * 1-易飞app航线
     */
    @NotNull
    private Integer mold;
    /**
     * 单位id
     */
    private String nestId;
    /**
     * 移动终端id
     */
    private String sysAppId;
    /**
     * 航点类型
     */
    @NotNull
    private Integer airLineType;

    /**
     * 航线文件
     */
    @NotNull
    private MultipartFile file;

    private String orgCode;
}
