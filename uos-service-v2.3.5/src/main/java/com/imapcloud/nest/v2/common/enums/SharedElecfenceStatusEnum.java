package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.IStatusEnum;
import com.geoai.common.core.enums.ITypeEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 账号状态类型枚举
 * @author Vastfy
 * @date 2022/5/19 17:40
 * @since 1.0.0
 */
@Getter
public enum SharedElecfenceStatusEnum implements IStatusEnum<SharedElecfenceStatusEnum> {

    /**
     * 1: 正常
     */
    ON("on"),

    /**
     * 2: 停用
     */
    OFF("off"),

    ;

    private final String mark;

    SharedElecfenceStatusEnum(String mark) {
        this.mark = mark;
    }

    @Override
    public int getStatus() {
        return ordinal() + 1;
    }

    public static Optional<SharedElecfenceStatusEnum> findMatch(int status){
        return Arrays.stream(SharedElecfenceStatusEnum.values())
                .filter(e -> Objects.equals(status, e.getStatus()))
                .findFirst();
    }

    public static Optional<SharedElecfenceStatusEnum> findMatch(String mark){
        return Arrays.stream(SharedElecfenceStatusEnum.values())
                .filter(e -> Objects.equals(mark, e.getMark()))
                .findFirst();
    }

}
