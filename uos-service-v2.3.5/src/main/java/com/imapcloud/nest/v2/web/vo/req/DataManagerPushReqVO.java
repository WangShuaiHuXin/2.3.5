package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataManagerPushReqVO implements Serializable {
    @ApiModelProperty(value = "照片ID", position = 1, required = true, example = "")
    private String photoId;
    @ApiModelProperty(value = "推送类型", position = 1, required = true, example = "")
    private String identity;

}
