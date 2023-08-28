package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Classname AirspaceManageRespVO
 * @Description 空域管理分页展示响应类
 * @Date 2023/3/7 18:30
 * @Author Carnival
 */
@Data
public class AirspaceManageRespVO implements Serializable {

    @ApiModelProperty(value = "空域ID", example = "152335888974221")
    private String airspaceId;

    @ApiModelProperty(value = "单位编号", example = "000")
    private String orgCode;

    @ApiModelProperty(value = "单位名称", example = "中科云图")
    private String orgName;

    @ApiModelProperty(value = "范围截图地址", example = "/origin/thumbnail/xxx.jpg")
    private String photoUrl;

    @ApiModelProperty(value = "空域名称", example = "北京")
    private String airspaceName;

    @ApiModelProperty(value = "飞行开始时间", example = "2023-03-07")
    private String startTime;

    @ApiModelProperty(value = "飞行结束时间", example = "2023-04-07")
    private String endTime;

    @ApiModelProperty(value = "无人机机型", example = "御3")
    private String uavName;

    @ApiModelProperty(value = "飞行高度", example = "300")
    private Double aglAltitude;

    @ApiModelProperty(value = "是否批复", example = "0", notes = "批复状态：0、未批复 1、已批复")
    private Integer isApproval;

    /*中科天网*/
    @ApiModelProperty(value = "空域管理-申请者类型", example = "1")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer applicantType;

    @ApiModelProperty(value = "空域管理-任务类型", example = "1")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer missionType;
}
