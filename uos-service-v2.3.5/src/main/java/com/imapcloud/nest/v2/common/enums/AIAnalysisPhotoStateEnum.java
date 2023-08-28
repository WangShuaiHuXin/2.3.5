package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * AI分析图片状态枚举
 * @author Vastfy
 * @date 2022/11/02 15:40
 * @since 2.1.4
 */
@Getter
public enum AIAnalysisPhotoStateEnum implements ITypeEnum<AIAnalysisPhotoStateEnum> {

    /**
     * -3：排队中（UDA算法还未开始识别）
     */
    QUEUING,

    /**
     * -2：未处理（UOS仍处于待运行队列）
     */
    UNTREATED,

    /**
     * -1：已暂停
     */
    PAUSED,

    /**
     * 0：识别中
     */
    EXECUTING,

    /**
     * 1：识别成功
     */
    SUCCEED,

    /**
     * 2：识别失败
     */
    FAILED,

    ;

    @Override
    public int getType() {
        return ordinal() - 3;
    }

    public static Optional<AIAnalysisPhotoStateEnum> findMatch(int status){
        return Arrays.stream(AIAnalysisPhotoStateEnum.values())
                .filter(e -> Objects.equals(status, e.getType()))
                .findFirst();
    }

}
