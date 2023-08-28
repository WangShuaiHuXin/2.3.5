package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
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
public class DataAnalysisMarkRespVO implements Serializable {

    @ApiModelProperty(value = "标注主键", position = 1, example = "")
    private String markId;

    @ApiModelProperty(value = "照片ID", position = 2, example = "")
    private String photoId;

    @ApiModelProperty(value = "标注框横坐标", position = 3, example = "")
    private BigDecimal recX;

    @ApiModelProperty(value = "标注框纵坐标", position = 4, example = "")
    private BigDecimal recY;

    @ApiModelProperty(value = "标注框宽度", position = 5, example = "")
    private BigDecimal recWidth;

    @ApiModelProperty(value = "标注框高度", position = 6, example = "")
    private BigDecimal recHeight;

    @ApiModelProperty(value = "缩放后原图偏移横坐标", position = 7, example = "")
    private BigDecimal relX;

    @ApiModelProperty(value = "缩放后原图偏移纵坐标", position = 8, example = "")
    private BigDecimal relY;

    @ApiModelProperty(value = "裁剪框宽度", position = 9, example = "")
    private BigDecimal cutWidth;

    @ApiModelProperty(value = "裁剪框高度", position = 10, example = "")
    private BigDecimal cutHeight;

    @ApiModelProperty(value = "缩放比例", position = 11, example = "")
    private BigDecimal picScale;
    @ApiModelProperty(value = "标注状态：0-未核实，1-已核实", position = 12, example = "")
    private Integer markState;
    @ApiModelProperty(value = "是否标注, 0:否,1:是", position = 13, example = "")
    private Boolean existMark;
    @ApiModelProperty(value = "是否AI标注，0-否，1-是", position = 14, example = "")
    private Boolean aiMark;
    @ApiModelProperty(value = "标注缩略图", position = 15, example = "")
    private String thumImagePath;
    @ApiModelProperty(value = "地址缩略图", position = 16, example = "")
    private String addrImagePath;
    @ApiModelProperty(value = "地址信息", position = 17, example = "")
    private String addr;
    @ApiModelProperty(value = "问题等级", position = 18, example = "")
    private String topicLevelId;
    @ApiModelProperty(value = "行业类型", position = 19, example = "")
    private Integer industryType;
    @ApiModelProperty(value = "问题类型", position = 20, example = "")
    private String topicProblemId;
    @ApiModelProperty(value = "问题等级", position = 18, example = "")
    private String topicLevelName;
    @ApiModelProperty(value = "行业", position = 19, example = "")
    private String topicIndustryName;
    @ApiModelProperty(value = "问题类型", position = 20, example = "")
    private String topicProblemName;
    @ApiModelProperty(value = "标注序号", position = 21, example = "")
    private Integer markNo;
    @ApiModelProperty(value = "经度", position = 22, example = "")
    private BigDecimal longitude;
    @ApiModelProperty(value = "纬度", position = 23, example = "")
    private BigDecimal latitude;
    @ApiModelProperty(value = "合并组ID", position = 24, example = "")
    private String  resultGroupId;

}
