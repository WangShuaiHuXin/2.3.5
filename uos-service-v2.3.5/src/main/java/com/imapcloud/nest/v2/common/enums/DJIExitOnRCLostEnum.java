package com.imapcloud.nest.v2.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIExitOnRCLostEnum.java
 * @Description DJIExitOnRCLostEnum
 * @createTime 2022年07月08日 15:41:00
 */
@Getter
public enum DJIExitOnRCLostEnum {

    /**
     * 继续执行航线
     */
    GO_CONTINUE("goContinue","继续执行航线"),

    /**
     * 退出航线，执行失控动作
     */
    EXECUTE_LOST_ACTION("executeLostAction","退出航线，执行失控动作"),

    ;

    private String code;

    private String des;

    DJIExitOnRCLostEnum(String code , String des){
        this.code = code;
        this.des = des;
    }

    public static Optional<DJIExitOnRCLostEnum> findMatch(String code){
        return Arrays.stream(DJIExitOnRCLostEnum.values())
                .filter(e -> Objects.equals(code, e.getCode()))
                .findFirst();
    }

}
