package com.imapcloud.nest.v2.common.enums;

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
public enum AccountStatusEnum implements ITypeEnum<AccountStatusEnum> {

    /**
     * 0: 停用
     */
    DISABLED("off"),

    /**
     * 1: 正常
     */
    NORMAL("on"),

    /**
     * 2: 锁定
     */
    LOCKED("locked"),

    ;

    private final String mark;

    AccountStatusEnum(String mark) {
        this.mark = mark;
    }

    @Override
    public int getType() {
        return ordinal();
    }

    public static Optional<AccountStatusEnum> findMatch(int status){
        return Arrays.stream(AccountStatusEnum.values())
                .filter(e -> Objects.equals(status, e.getType()))
                .findFirst();
    }

    public static Optional<AccountStatusEnum> findMatch(String mark){
        return Arrays.stream(AccountStatusEnum.values())
                .filter(e -> Objects.equals(mark, e.getMark()))
                .findFirst();
    }

}
