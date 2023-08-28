package com.imapcloud.nest.v2.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICameraEnum.java
 * @Description DJICameraEnum
 * @createTime 2022年07月08日 15:41:00
 */
@Getter
public enum DJICameraEnum {

    /**
     * M30
     */
    M30_CAMERA("M30",52,0,0),

    /**
     * M30T
     */
    M30T_CAMERA("M30T",53,0,0),

    ;

    private String code;

    private int payloadEnumValue;

    private int payloadSubEnumValue;

    private int payloadPositionIndex;

    DJICameraEnum(String code , int payloadEnumValue, int payloadSubEnumValue , int payloadPositionIndex){
        this.code = code;
        this.payloadEnumValue = payloadEnumValue;
        this.payloadSubEnumValue = payloadSubEnumValue;
        this.payloadPositionIndex = payloadPositionIndex;
    }

    public static Optional<DJICameraEnum> findMatch(String code){
        return Arrays.stream(DJICameraEnum.values())
                .filter(e -> Objects.equals(code, e.getCode()))
                .findFirst();
    }


    public static Optional<DJICameraEnum> findMatch(int payloadEnumValue){
        return Arrays.stream(DJICameraEnum.values())
                .filter(e -> Objects.equals(payloadEnumValue,e.getPayloadEnumValue()))
                .findFirst();
    }

}
