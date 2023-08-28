package com.imapcloud.nest.enums.message;

import com.geoai.common.web.util.MessageUtils;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PubMessageTypeEnum.java
 * @Description PubMessageTypeEnum
 * @createTime 2022年03月22日 20:36:00
 */
public enum PubMessageTypeEnum {

    MESSAGE_TYPE_0(0, "更新公告", "geoai_uos_PubMessageTypeEnum_update_announcement"),
    MESSAGE_TYPE_1(1, "其他公告", "geoai_uos_PubMessageTypeEnum_other_announcement");

    private Integer code;
    private String state;

    private String key;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getState() {
        return state;
    }

    public String getKey() {
        return key;
    }

    public void setState(String state) {
        this.state = state;
    }

    PubMessageTypeEnum(Integer code, String state, String key) {
        this.code = code;
        this.state = state;
        this.key = key;
    }


    public static List toList() {
        //Lists.newArrayList()其实和new ArrayList()几乎一模
        //  一样, 唯一它帮你做的(其实是javac帮你做的), 就是自动推导(不是"倒")尖括号里的数据类型.
        List list = Lists.newArrayList();

        for (PubMessageTypeEnum airlineTypeEnum : PubMessageTypeEnum.values()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", airlineTypeEnum.getCode());
            map.put("name", MessageUtils.getMessage(airlineTypeEnum.getKey()));
            list.add(map);
        }
        return list;
    }

}
