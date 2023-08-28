package com.imapcloud.nest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 角色与idenalue对应关系
 *
 * @author boluo
 * @date 2022-07-15
 */
@Getter
@AllArgsConstructor
public enum RoleIdenValueEnum {

    /**
     * 分析统计-综合
     */
    ANALYSIS_STATISTICS_ZH("analysis-statistics-zh", 100, "分析统计-综合", 0),
    ABNORMAL_FIND_DL_QXSB("abnormal-find-dl-qxsb", 0, "分析应用-电力-缺陷识别（旧）", 0),
    ABNORMAL_FIND_DL_BJDS("abnormal-find-dl-bjds", 1, "分析应用-电力-表计读数（旧）", 0),
    ABNORMAL_FIND_DL_HWCW("abnormal-find-dl-hwcw", 2, "分析应用-电力-红外测温（旧）", 0),
    // 4：二进制100
    ABNORMAL_FIND_DL_BJDS_NEW("abnormal-find-dl-bjds-new", 101, "分析统计-电力-表计读数", 4),
    ABNORMAL_FIND_DL_QXSB_NEW("abnormal-find-dl-qxsb-new", 102, "分析统计-电力-缺陷识别", 2),
    ABNORMAL_FIND_DL_HWCW_NEW("abnormal-find-dl-hwcw-new", 103, "分析统计-电力-红外测温", 1),
    ;
    private final String roleKey;

    private final int idenValue;

    private final String desc;

    private final int bitNum;

    private static final Map<String, Integer> KEY_IDEN_VALUE_MAP;
    static {

        KEY_IDEN_VALUE_MAP = new HashMap<>();
        for (RoleIdenValueEnum value : RoleIdenValueEnum.values()) {
            KEY_IDEN_VALUE_MAP.put(value.roleKey, value.idenValue);
        }
    }

    public static Integer getIdenValue(String roleKey) {
        if (StringUtils.isBlank(roleKey)) {
            return null;
        }
        return KEY_IDEN_VALUE_MAP.get(roleKey);
    }

    public static List<RoleIdenValueEnum> bitNumToEnum(int bitNum) {
        List<RoleIdenValueEnum> result = new LinkedList<>();
        if (bitNum >= ABNORMAL_FIND_DL_BJDS_NEW.getBitNum()) {
            result.add(ABNORMAL_FIND_DL_BJDS_NEW);
            bitNum = bitNum - ABNORMAL_FIND_DL_BJDS_NEW.getBitNum();
        }
        if (bitNum >= ABNORMAL_FIND_DL_QXSB_NEW.getBitNum()) {
            result.add(ABNORMAL_FIND_DL_QXSB_NEW);
            bitNum = bitNum - ABNORMAL_FIND_DL_QXSB_NEW.getBitNum();
        }

        if (bitNum >= ABNORMAL_FIND_DL_HWCW_NEW.getBitNum()) {
            result.add(ABNORMAL_FIND_DL_HWCW_NEW);
            bitNum = bitNum - ABNORMAL_FIND_DL_HWCW_NEW.getBitNum();
        }
        return result;
    }

    public static List<RoleIdenValueEnum> idenValueToEnum(List<Integer> idenValueList) {
        List<RoleIdenValueEnum> result = new LinkedList<>();
        for (Integer integer : idenValueList) {
            if (ABNORMAL_FIND_DL_BJDS_NEW.idenValue == integer) {
                result.add(ABNORMAL_FIND_DL_BJDS_NEW);
            } else if (ABNORMAL_FIND_DL_QXSB_NEW.idenValue == integer) {
                result.add(ABNORMAL_FIND_DL_QXSB_NEW);
            } else if (ABNORMAL_FIND_DL_HWCW_NEW.idenValue == integer) {
                result.add(ABNORMAL_FIND_DL_HWCW_NEW);
            }
        }
        return result;
    }

    public static RoleIdenValueEnum idenValueToEnum(Integer idenValue) {
        if (ABNORMAL_FIND_DL_BJDS_NEW.idenValue == idenValue) {
            return ABNORMAL_FIND_DL_BJDS_NEW;
        } else if (ABNORMAL_FIND_DL_QXSB_NEW.idenValue == idenValue) {
            return ABNORMAL_FIND_DL_QXSB_NEW;
        } else if (ABNORMAL_FIND_DL_HWCW_NEW.idenValue == idenValue) {
            return ABNORMAL_FIND_DL_HWCW_NEW;
        }
        return null;
    }
}
