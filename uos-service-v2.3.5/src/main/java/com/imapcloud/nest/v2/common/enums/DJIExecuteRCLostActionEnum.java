package com.imapcloud.nest.v2.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIExecuteRCLostActionEnum.java
 * @Description DJIExecuteRCLostActionEnum
 * @createTime 2022年07月08日 15:41:00
 */
@Getter
public enum DJIExecuteRCLostActionEnum {

    /**
     * 返航
     */
    GO_BACK("goBack","返航"),

    /**
     * 降落
     */
    LANDING("landing","降落"),

    /**
     * 悬停
     */
    HOVER("hover","悬停")

    ;

    private String code;

    private String des;

    DJIExecuteRCLostActionEnum(String code , String des){
        this.code = code;
        this.des = des;
    }

    public static Optional<DJIExecuteRCLostActionEnum> findMatch(String code){
        return Arrays.stream(DJIExecuteRCLostActionEnum.values())
                .filter(e -> Objects.equals(code, e.getCode()))
                .findFirst();
    }

}
