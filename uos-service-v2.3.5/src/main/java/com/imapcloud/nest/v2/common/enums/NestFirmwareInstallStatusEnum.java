package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.IStatusEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 基站安装状态类型枚举
 * @author Vastfy
 * @date 2022/07/13 14:42
 * @since 1.0.0
 */
@Getter
public enum NestFirmwareInstallStatusEnum implements IStatusEnum<NestFirmwareInstallStatusEnum> {

    /**
     * -1: 安装超时
     */
    INSTALLED_TIMEOUT,

    /**
     * 0: 安装中
     */
    INSTALLING,

    /**
     * 1: 安装成功
     */
    INSTALLED_SUCCESS,

    /**
     * 2: 安装失败
     */
    INSTALLED_FAILED,

    ;

    @Override
    public int getStatus() {
        return ordinal() - 1;
    }

    public static Optional<NestFirmwareInstallStatusEnum> findMatch(int status){
        return Arrays.stream(NestFirmwareInstallStatusEnum.values())
                .filter(e -> Objects.equals(status, e.getStatus()))
                .findFirst();
    }

}
