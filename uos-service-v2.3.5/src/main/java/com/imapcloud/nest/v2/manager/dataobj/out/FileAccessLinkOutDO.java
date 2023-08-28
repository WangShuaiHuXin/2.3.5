package com.imapcloud.nest.v2.manager.dataobj.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname FileAccessLinkOutDO
 * @Description 文件访问链接信息
 * @Date 2023/2/17 11:30
 * @Author Carnival
 */
@Data
public class FileAccessLinkOutDO {

    /**
     * 外链访问地址
     */
    private String accessUrl;

    /**
     * 有效期【单位：秒】
     */
    private Integer expiredAt;
}
