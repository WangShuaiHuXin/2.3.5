package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Classname AirspaceManageReqVO
 * @Description 空域管理申请请求类
 * @Date 2023/3/7 16:15
 * @Author Carnival
 */

@ApiModel("空域申请信息")
@Data
public class AirspaceManageReqVO implements Serializable {

    @ApiModelProperty(value = "空域名称", position = 1, required = true, example = "北京")
    @NotBlank(message = "空域名称不能为空")
    private String airspaceName;

    @ApiModelProperty(value = "所属单位", position = 2, required = true, example = "中科云图")
    @NotBlank(message = "单位不能为空")
    private String orgCode;

    @ApiModelProperty(value = "所属单位单位名称", position = 3, required = true, example = "中科云图")
    @NotBlank(message = "所属单位单位名称不能为空")
    private String orgName;

    @ApiModelProperty(value = "无人机编号", position = 4, required = true, example = "001")
    @NotBlank(message = "无人机编号不能为空")
    private String uavCode;

    @ApiModelProperty(value = "无人机数量", position = 5, example = "2")
    private Integer uavCount;

    @ApiModelProperty(value = "飞行开始时间", position = 6, required = true, example = "2023-03-07")
    @NotBlank(message = "飞行开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "飞行结束时间", position = 7, required = true, example = "2023-04-07")
    @NotBlank(message = "飞行结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "海拔", position = 8, example = "400")
    private Double altitude;

    @ApiModelProperty(value = "飞行高度", position = 9, required = true, example = "300")
    @NotNull(message = "飞行高度不能为空")
    private Double aglAltitude;

    @ApiModelProperty(value = "地址", position = 10, required = true, example = "广州")
    @NotBlank(message = "地址不能为空")
    private String address;

    @ApiModelProperty(value = "边界范围", position = 11, required = true, example = "112.9444058161261,23.02305950131119")
    @NotBlank(message = "边界范围地址不能为空")
    private String airCoor;

    @ApiModelProperty(value = "边界范围点", position = 12, required = true, example = "6")
    @NotNull(message = "边界范围点地址不能为空")
    private Integer airCoorCount;

    @ApiModelProperty(value = "范围截图地址", position = 13, required = true, example = "/origin/thumbnail/xxx.jpg")
    @NotBlank(message = "地址不能为空")
    private String photoUrl;

    /*中科天网*/
    @ApiModelProperty(value = "空域管理-申请者类型", position = 14, required = true, example = "1")
    @NotNull(message = "申请者类型不能为空")
    private Integer applicantType;

    @ApiModelProperty(value = "空域管理-任务类型", position = 15, required = true, example = "1")
    @NotNull(message = "任务类型不能为空")
    private Integer missionType;
}
