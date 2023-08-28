package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName VideoQualityReqVO.java
 * @Description VideoQualityReqVO
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class VideoQualityReqVO implements Serializable {

    @ApiModelProperty(value = "videoId", position = 1, required = true, example = "")
    @NotNull(message = "videoId 不能为空！")
    private String videoId;

    @ApiModelProperty(value = "设置清晰度", position = 2, required = true, example = "{\"0\":\"自适应\",\"1\":\"流畅\",\"2\":\"标清\",\"3\":\"高清\",\"4\":\"超清\"}")
    @NotNull(message = "videoQuality 不能为空！")
    private Integer videoQuality;

}
