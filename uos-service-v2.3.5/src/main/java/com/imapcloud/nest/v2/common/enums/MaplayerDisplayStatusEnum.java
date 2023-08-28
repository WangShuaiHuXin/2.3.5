package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.IStatusEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 图层展示类型枚举
 * @author Vastfy
 * @date 2022/09/27 13:40
 * @since 2.1.0
 */
@Getter
public enum MaplayerDisplayStatusEnum implements IStatusEnum<MaplayerDisplayStatusEnum> {

    /**
     * 0: 关闭
     */
    OFF("off"),

    /**
     * 1: 开启
     */
    ON("on"),

    ;

    private final String mark;

    MaplayerDisplayStatusEnum(String mark) {
        this.mark = mark;
    }

    @Override
    public int getStatus() {
        return ordinal();
    }

    public static Optional<MaplayerDisplayStatusEnum> findMatch(int status){
        return Arrays.stream(MaplayerDisplayStatusEnum.values())
                .filter(e -> Objects.equals(status, e.getStatus()))
                .findFirst();
    }

    public static Optional<MaplayerDisplayStatusEnum> findMatch(String mark){
        return Arrays.stream(MaplayerDisplayStatusEnum.values())
                .filter(e -> Objects.equals(mark, e.getMark()))
                .findFirst();
    }

}
