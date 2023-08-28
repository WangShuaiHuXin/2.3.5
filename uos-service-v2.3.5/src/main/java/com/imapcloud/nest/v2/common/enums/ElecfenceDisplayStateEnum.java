package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 电子围栏展示状态枚举
 * @author Vastfy
 * @date 2022/09/30 10:57
 * @since 2.1.0
 */
@Getter
public enum ElecfenceDisplayStateEnum implements ITypeEnum<ElecfenceDisplayStateEnum> {

    /**
     * 1: 开启
     */
    ON("on"),

    /**
     * 2: 关闭
     */
    OFF("off"),

    ;

    private final String mark;

    ElecfenceDisplayStateEnum(String mark) {
        this.mark = mark;
    }

    @Override
    public int getType() {
        return ordinal() + 1;
    }

    public static Optional<ElecfenceDisplayStateEnum> findMatch(int status){
        return Arrays.stream(ElecfenceDisplayStateEnum.values())
                .filter(e -> Objects.equals(status, e.getType()))
                .findFirst();
    }

    public static Optional<ElecfenceDisplayStateEnum> findMatch(String mark){
        return Arrays.stream(ElecfenceDisplayStateEnum.values())
                .filter(e -> Objects.equals(mark, e.getMark()))
                .findFirst();
    }

}
