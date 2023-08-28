package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class RtkInfoVO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * rtk信息id
     */
    private Integer id;

    /**
     * 机巢id
     */
    private Integer nestId;

    /**
     * 是否开启（0-开启；1-关闭）
     */
    private Integer enable;

    /**
     * rtk类型（0-未知；1-网咯RTK；2-自定义网络RTK；3-D-RTK基站 ）
     */
    private Integer type;

    /**
     * IP地址或域名
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 源节点
     */
    private String mountPoint;

    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 到期时间
     */
    private LocalDate expireTime;

    private String nestUuid;

    private String nestName;
}
