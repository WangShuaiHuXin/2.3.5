package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataScenePhotoReqVO {

    @ApiModelProperty(value = "地址信息", position = 5, required = true, example = "")
    @NotBlank(message = "{geoai_uos_cannot_address_information}")
    private String addr;

    /**
     * mission_records中的exec_id
     */
    @ApiModelProperty(value = "架次记录execID", position = 8, required = true, example = "")
    @NotBlank(message = "{geoai_uos_cannot_empty_execid_of_the_mission_record}")
    private String execMissionId;

    @ApiModelProperty(value = "经度", position = 9, required = true, example = "")
    @NotNull(message = "{geoai_uos_cannot_empty_longitude_cn}")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度", position = 10, required = true, example = "")
    @NotNull(message = "{geoai_uos_cannot_empty_latitude_cn}")
    private BigDecimal latitude;

    @ApiModelProperty(value = "专题等级", position = 11, required = true, example = "")
    @NotBlank(message = "{geoai_uos_cannot_empty_topic_level}")
    private String topicLevelId;

    @ApiModelProperty(value = "专题行业类型", position = 12, required = true)
    @NotNull(message = "{geoai_uos_industry_id_cannot_be_empty}")
    private Integer industryType;

    @ApiModelProperty(value = "专题行业问题", position = 13, required = true, example = "")
    @NotBlank(message = "{geoai_uos_industry_issue_id_cannot_be_empty}")
    private String topicProblemId;

    @NotNull(message = "{geoai_uos_recx_cannot_be_empty}")
    private BigDecimal recX;

    @NotNull(message = "{geoai_uos_recy_cannot_be_empty}")
    private BigDecimal recY;

    @NotNull(message = "{geoai_uos_rec_width_cannot_be_empty}")
    private BigDecimal recWidth;

    @NotNull(message = "{geoai_uos_rec_height_cannot_be_empty}")
    private BigDecimal recHeight;

    @NotNull(message = "{geoai_uos_relx_cannot_be_empty}")
    private BigDecimal relX;

    @NotNull(message = "{geoai_uos_rely_cannot_be_empty}")
    private BigDecimal relY;

    @NotNull(message = "{geoai_uos_cut_width_cannot_be_empty}")
    private BigDecimal cutWidth;

    @NotNull(message = "{geoai_uos_cut_height_cannot_be_empty}")
    private BigDecimal cutHeight;

    @NotNull(message = "{geoai_uos_pic_scale_cannot_be_empty}")
    private BigDecimal picScale;

    @ApiModelProperty(value = "截图存储路径", position = 111, required = true)
    @NotBlank(message = "截图地址不能为空")
    private String imagePath;

}
