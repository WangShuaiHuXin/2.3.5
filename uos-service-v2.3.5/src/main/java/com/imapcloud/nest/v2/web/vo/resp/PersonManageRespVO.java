package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname PersonManageRespVO
 * @Description 人员管理响应类
 * @Date 2023/3/28 10:55
 * @Author Carnival
 */

@ApiModel("人员管理信息")
@Data
public class PersonManageRespVO {

    @ApiModelProperty(value = "人员ID", position = 1, example = "1492004100561215")
    private String personId;

    @ApiModelProperty(value = "性别", position = 1, notes = "女：0，男：1", example = "1")
    private String gender;

    @ApiModelProperty(value = "人员类型", position = 1, notes = "运营人：0，驾驶员：1", example = "1")
    private String personType;

    @ApiModelProperty(value = "单位编码", position = 1, example = "000")
    private String orgCode;

    @ApiModelProperty(value = "单位名称", position = 1, example = "中科云图")
    private String orgName;

    @ApiModelProperty(value = "用户姓名", position = 1, example = "张三")
    private String name;

    @ApiModelProperty(value = "手机号", position = 1, example = "135000000000")
    private String mobile;

    @ApiModelProperty(value = "身份证号码", position = 1, example = "440000000000002020")
    private String idCard;

    @ApiModelProperty(value = "身份证类型", position = 1, example = "0")
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

    @Data
    public static class PersonManageCountRespVO {

        @ApiModelProperty(value = "总人数", position = 1, example = "0")
        private Integer total;
        @ApiModelProperty(value = "驾驶人员", position = 2, example = "0")
        private Integer driTotal;
        @ApiModelProperty(value = "运营人员总数", position = 3, example = "0")
        private Integer opeTotal;
    }
}
