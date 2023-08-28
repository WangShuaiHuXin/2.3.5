package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccessoryAuthStatusOutDTO {
    //是否获取成功
    private Boolean flag;
    //CPS返回描述信息
    private String msg;
    private Boolean isLTEAuthenticationAvailable;        //lte认证功能是否可用
    private Boolean isLTEAuthenticated;                 //是否已认证
    private String LTEAuthenticatedPhoneAreaCode;       //认证区号
    private String LTEAuthenticatedPhoneNumber;         //认证的手机号
    private LocalDateTime LTELastAuthenticatedTime;     //上一次认证时间
    private String LTEAuthenticatedRemainingFatalism;
}
