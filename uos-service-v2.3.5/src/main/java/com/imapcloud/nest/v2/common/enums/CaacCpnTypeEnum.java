package com.imapcloud.nest.v2.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 民航运营商类型
 * @author Vastfy
 * @date 2023/03/08 17:34
 * @since 2.2.5
 */
@Getter
public enum CaacCpnTypeEnum {

    /**
     * 未知
     */
    UNKNOWN("UNKNOWN", "未知"),

    /**
     * 001 ==> U-Cloud
     */
    CPN_001("001", "U-Cloud"),

    /**
     * 002 ==> U-Cloud
     */
    CPN_002("002", "U-Care"),

    /**
     * 009 ==> 极飞云
     */
    CPN_009("009", "极飞云"),

    /**
     * 010 ==> 拓攻云
     */
    CPN_010("010", "拓攻云"),

    /**
     * 011 ==> 中科天网
     */
    CPN_011("011", "中科天网"),

    /**
     * 012 ==> 中航空管云
     */
    CPN_012("012", "中航空管云"),

    /**
     * 013 ==> 国网云
     */
    CPN_013("013", "国网云"),

    /**
     * 014 ==> 全飞云
     */
    CPN_014("014", "全飞云"),

    /**
     * 015 ==> 亿航云
     */
    CPN_015("015", "亿航云"),
    ;

    private final String cpn;
    private final String cpnName;

    CaacCpnTypeEnum(String cpn, String cpnName) {
        this.cpn = cpn;
        this.cpnName = cpnName;
    }

    public static CaacCpnTypeEnum findMatch(String cpn){
        return Arrays.stream(CaacCpnTypeEnum.values())
                .filter(e -> Objects.equals(cpn, e.getCpn()))
                .findFirst()
                .orElse(CaacCpnTypeEnum.UNKNOWN);
    }

}
