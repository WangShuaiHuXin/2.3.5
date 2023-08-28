package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname PersonInfoRespVO
 * @Description 个人信息响应类
 * @Date 2023/3/8 17:02
 * @Author Carnival
 */
@ApiModel("个人信息")
@Data
public class PersonInfoRespVO {

    private String accountId;

    private String IP;

    private String licenceCode;
}
