package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname PersonManageReqVO
 * @Description 人员管理请求类
 * @Date 2023/3/28 10:55
 * @Author Carnival
 */

@ApiModel("人员管理信息")
@Data
public class PersonManageReqVO {

    @ApiModelProperty(value = "人员类型 0：使用人、1：驾驶员", position = 1, required = true, example = "1")
    @NotNull(message = "人员类型不能为空")
    private String personType;

    @ApiModelProperty(value = "性别 0：女、1：男", position = 1,required = true, notes = "女：0，男：1", example = "1")
    @NotNull(message = "性别不能为空")
    private String gender;

    @ApiModelProperty(value = "单位编码", position = 1, required = true, example = "000")
    @NotBlank(message = "单位编码不能为空")
    private String orgCode;

    @ApiModelProperty(value = "单位名称", position = 1, required = true, example = "中科云图")
    @NotBlank(message = "单位名称不能为空")
    private String orgName;

    @ApiModelProperty(value = "用户姓名", position = 1, required = true, example = "张三")
    @NotBlank(message = "用户姓名不能为空")
    private String name;

    @ApiModelProperty(value = "手机号", position = 1, required = true, example = "135000000000")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "身份证号码", position = 1, required = true, example = "440000000000002020")
    @NotBlank(message = "身份证号码")
    private String idCard;

    @ApiModelProperty(value = "身份证类型", position = 1, required = true, example = "0")
    @NotNull(message = "身份证类型不能为空")
    private String idCardType;

    @ApiModelProperty(value = "身份证正面", position = 1, example = "http://data/xxx/.jpg")
    private String idCardFrontUrl;

    @ApiModelProperty(value = "身份证反面", position = 1, example = "http://data/xxx/.jpg")
    private String idCardBackUrl;

    @ApiModelProperty(value = "驾驶员执照", position = 1, example = "http://data/xxx/.jpg")
    private String idCardDriveUrl;

    @ApiModelProperty(value = "用户类型", position = 1, example = "0")
    private String userType;

    @ApiModelProperty(value = "信用代码", position = 1, example = "410550000022333")
    private String creditCode;

    @ApiModelProperty(value = "驾驶征类型", position = 1, example = "0")
    private String driveType;

    @ApiModelProperty(value = "驾驶征执照类型", position = 1, example = "0")
    private String driveLicenseType;

    @ApiModelProperty(value = "驾驶征执照编码", position = 1, example = "410550000022333")
    private String driveLicenseCode;
}
