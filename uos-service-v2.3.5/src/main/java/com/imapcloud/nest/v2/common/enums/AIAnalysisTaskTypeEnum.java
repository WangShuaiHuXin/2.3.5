package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;
import com.imapcloud.nest.enums.RoleIdenValueEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * AI识别任务类型
 * @author Vastfy
 * @date 2022/12/02 14:34
 * @since 2.1.5
 */
public enum AIAnalysisTaskTypeEnum implements ITypeEnum<AIAnalysisTaskTypeEnum> {

    /**
     * 0：综合（通用目标识别）
     */
    UNIVERSAL_TARGETS,

    /**
     * 1：缺陷识别
     */
    DEFECTS,

    /**
     * 2: 表计读数
     */
    DIALS,

    /**
     * 3: 红外测温
     */
    INFRARED,

    ;

    @Override
    public int getType() {
        return ordinal();
    }

    public static Optional<AIAnalysisTaskTypeEnum> findMatch(int type){
        return Arrays.stream(AIAnalysisTaskTypeEnum.values())
                .filter(e -> Objects.equals(type, e.getType()))
                .findFirst();
    }

    public RoleIdenValueEnum covert(){
        if(Objects.equals(this, DEFECTS)){
            return RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW;
        }
        if(Objects.equals(this, DIALS)){
            return RoleIdenValueEnum.ABNORMAL_FIND_DL_BJDS_NEW;
        }
        if(Objects.equals(this, INFRARED)){
            return RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW;
        }
        return RoleIdenValueEnum.ANALYSIS_STATISTICS_ZH;
    }

}
