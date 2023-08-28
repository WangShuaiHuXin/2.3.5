package com.imapcloud.nest.v2.common.enums;

import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIVideoQualityEnum.java
 * @Description DJIVideoQualityEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DJIVideoQualityEnum {

    SELF_ADAPTION(0, "自适应"),
    FLUENCY(1, "流畅"),
    STANDARD_DEFINITION(2, "标清"),
    HIGH_DEFINITION(3, "高清"),
    SUPER_DEFINITION(4, "超清");

    private Integer code;
    private String str;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getState() {
        return str;
    }

    public void setState(String str) {
        this.str = str;
    }

    DJIVideoQualityEnum(Integer code, String str) {
        this.code = code;
        this.str = str;
    }

    public static List toList() {
        //Lists.newArrayList()其实和new ArrayList()几乎一模
        //  一样, 唯一它帮你做的(其实是javac帮你做的), 就是自动推导(不是"倒")尖括号里的数据类型.
        List list = Lists.newArrayList();

        for (DJIVideoQualityEnum dataAnalysisPicTypeEnum : DJIVideoQualityEnum.values()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", dataAnalysisPicTypeEnum.getCode());
            map.put("name", dataAnalysisPicTypeEnum.getState());
            list.add(map);
        }
        return list;
    }

}
