package com.imapcloud.nest.v2.service.dto.in;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname PersonInfoInDTO
 * @Description 个人信息新西兰
 * @Date 2023/3/10 9:04
 * @Author Carnival
 */
@Data
public class PersonInfoInDTO {

    private String accountId;

    private String IP;

    private String licenceCode;
}
