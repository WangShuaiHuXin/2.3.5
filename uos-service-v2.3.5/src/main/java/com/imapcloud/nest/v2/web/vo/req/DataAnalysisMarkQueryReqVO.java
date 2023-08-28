package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
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
public class DataAnalysisMarkQueryReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "照片ID", position = 1, required = false, example = "")
    private String photoId;
    @ApiModelProperty(value = "明细", position = 2, required = true, example = "")
    @NotNull(message = "{geoai_uos_detail_id_cannot_be_empty}")
    private String detailId;

}
