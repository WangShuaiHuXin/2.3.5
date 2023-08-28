package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname PersonManagePageRespVO
 * @Description 人员管理响应类
 * @Date 2023/3/28 10:55
 * @Author Carnival
 */

@ApiModel("人员管理信息")
@Data
public class PersonManagePageRespVO implements Serializable {

    @ApiModelProperty(value = "人员ID", position = 1,  example = "1492004100561215")
    private String personId;

    @ApiModelProperty(value = "人员类型", position = 1,  example = "1")
    private String personType;

    @ApiModelProperty(value = "单位编码", position = 1,  example = "000")
    private String orgCode;

    @ApiModelProperty(value = "用户姓名", position = 1,  example = "张三")
    private String name;

    @ApiModelProperty(value = "手机号", position = 1,  example = "135000000000")
    private String mobile;

    @ApiModelProperty(value = "单位名称", position = 1,  example = "中科云图")
    private String orgName;

    @ApiModelProperty(value = "性别", position = 1, notes = "女：0，男：1", example = "1")
    private String gender;
}
