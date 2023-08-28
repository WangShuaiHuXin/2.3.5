package com.imapcloud.sdk.pojo.accessory;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AccessoryAuthStatusOutPo implements Serializable {

    private Boolean isLTEAuthenticationAvailable;        //lte认证功能是否可用
    private Boolean isLTEAuthenticated;                 //是否已认证
    private String LTEAuthenticatedPhoneAreaCode;       //认证区号
    private String LTEAuthenticatedPhoneNumber;         //认证的手机号
    private LocalDateTime LTELastAuthenticatedTime;     //上一次认证时间
    private String LTEAuthenticatedRemainingTime;

}
