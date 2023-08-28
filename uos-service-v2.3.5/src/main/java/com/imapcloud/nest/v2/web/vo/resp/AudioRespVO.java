package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName AudioRespVO.java
 * @Description AudioRespVO
 * @createTime 2022年08月16日 14:25:00
 */
@Data
@ApiModel("喊话器音频返回VO")
public class AudioRespVO {
    @ApiModelProperty(value = "文件名", position = 1, required = true, example = "10000")
    private String fileName;

    @ApiModelProperty(value = "文件序号", position = 2, required = true, example = "10000")
    private Integer fileIndex;

    @ApiModelProperty(value = "文件类型", position = 1, required = true, example = "10000")
    private Integer fileType;

}
