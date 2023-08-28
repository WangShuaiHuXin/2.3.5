package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisMarkStatusEnum.java
 * @Description DataAnalysisMarkStatusEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DataAnalysisMarkNameEnum implements ITypeEnum<DataAnalysisMarkNameEnum> {

    /**
     * 0：单标注图
     */
    MARK,

    /**
     * 1: 汇总标注图
     */
    MARK_ALL,

    /**
     * 2：单标注图 缩略图
     */
    THUM_MARK,

    /**
     * 3: 汇总标注图 缩略图
     */
    THUM_MARK_ALL,

    /**
     * 4: 地址缩略图
     */
    THUM_MARK_ADDR;


    @Override
    public int getType() {
        return ordinal();
    }

    public static Optional<DataAnalysisMarkNameEnum> findMatch(int type){
        return Arrays.stream(DataAnalysisMarkNameEnum.values())
                .filter(e -> Objects.equals(type, e.getType()))
                .findFirst();
    }

    public static Optional<DataAnalysisMarkNameEnum> findMatch(String name){
        if(StringUtils.hasText(name)){
            return Arrays.stream(DataAnalysisMarkNameEnum.values())
                    .filter(e -> Objects.equals(name.toUpperCase(), e.name()))
                    .findFirst();
        }
        return Optional.empty();
    }
}
