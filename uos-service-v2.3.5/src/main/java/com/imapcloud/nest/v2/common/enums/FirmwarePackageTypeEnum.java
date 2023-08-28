package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 固件安装包类型
 * @author Vastfy
 * @date 2022/07/13 15:54
 * @since 1.9.7
 */
public enum FirmwarePackageTypeEnum implements ITypeEnum<FirmwarePackageTypeEnum> {

    /**
     * 1：apk文件
     */
    APK("apk"),

    /**
     * 2: bin文件
     */
    BIN("zip")
    ;

    @Getter
    private final String supportFile;

    FirmwarePackageTypeEnum(String supportFile) {
        this.supportFile = supportFile;
    }

    @Override
    public int getType() {
        return ordinal() + 1;
    }

    public static Optional<FirmwarePackageTypeEnum> findMatch(String name){
        if(StringUtils.hasText(name)){
            return Arrays.stream(FirmwarePackageTypeEnum.values())
                    .filter(e -> Objects.equals(name.toUpperCase(), e.name()))
                    .findFirst();
        }
        return Optional.empty();
    }

}
