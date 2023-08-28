package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

/**
 * @Classname PersonInfoOutDTO
 * @Description 个人信息信息类
 * @Date 2023/3/10 9:06
 * @Author Carnival
 */
@Data
public class PersonInfoOutDTO {

    private String accountId;

    private String IP;

    private String licenceCode;
}
