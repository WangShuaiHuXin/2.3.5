package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Classname PersonInfoReqVO
 * @Description 个人信息请求类
 * @Date 2023/3/8 17:02
 * @Author Carnival
 */
@ApiModel("个人信息")
@Data
public class PersonInfoReqVO {

    @ApiModelProperty(value = "账户ID", position = 1,  example = "15954411012244")
    private String accountId;

    @ApiModelProperty(value = "身份证号", position = 1,  example = "440000199909091234")
    private String IP;

    @ApiModelProperty(value = "所属单位", position = 2,  example = "100")
    private String licenceCode;
}
