package com.imapcloud.nest.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PositionStrategyEnum {
    RTK(1), GPS(2);
    private int value;

    PositionStrategyEnum(Integer value) {
        this.value = value;
    }

    public static PositionStrategyEnum getInstance(Integer value) {
        if (value != null) {
            Optional<PositionStrategyEnum> first = Arrays.stream(PositionStrategyEnum.values()).filter(e -> e.value == value).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return RTK;
    }

    public static boolean completeDisableRtkInMission(Integer value) {
        PositionStrategyEnum instance = getInstance(value);
        if (RTK.equals(instance)) {
            return false;
        }
        return true;
    }
}
