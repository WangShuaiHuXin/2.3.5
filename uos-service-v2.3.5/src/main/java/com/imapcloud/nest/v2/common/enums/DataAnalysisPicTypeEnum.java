package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.web.util.MessageUtils;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisPicTypeEnum.java
 * @Description DataAnalysisPicTypeEnum
 * @createTime 2022年07月08日 15:41:00
 */
public enum DataAnalysisPicTypeEnum {

    VISIBLE(0, "可见光", "geoai_data_analysis_pic_type_visible"),
    UN_VISIBLE(1, "热红外光", "geoai_data_analysis_pic_type_infrared");

    private Integer code;
    private String state;
    private String messageKey;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    DataAnalysisPicTypeEnum(Integer code, String state, String messageKey) {
        this.code = code;
        this.state = state;
        this.messageKey = messageKey;
    }

    public static List toList() {
        //Lists.newArrayList()其实和new ArrayList()几乎一模
        //  一样, 唯一它帮你做的(其实是javac帮你做的), 就是自动推导(不是"倒")尖括号里的数据类型.
        List list = Lists.newArrayList();

        for (DataAnalysisPicTypeEnum dataAnalysisPicTypeEnum : DataAnalysisPicTypeEnum.values()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", dataAnalysisPicTypeEnum.getCode());
            map.put("name", MessageUtils.getMessage(dataAnalysisPicTypeEnum.getMessageKey()));
            list.add(map);
        }
        return list;
    }


}
