package com.imapcloud.nest.v2.manager.nacos;

import com.geoai.common.core.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * nacos配置信息
 *
 * @author boluo
 * @date 2022-05-23
 */
@Slf4j
@Configuration
@RefreshScope
public class NacosConfigurationService {

    private static Map<String, String> idenNameMap;

    static {
        String str = "{\"0\":\"变电站\",\"1\":\"变电站\",\"2\":\"变电站\",\"3\":\"水务\",\"4\":\"水务\",\"5\":\"水务\",\"6\":\"水务\",\"7\":\"城管\",\"8\":\"城管\",\"9\":\"城管\",\"10\":\"公安\",\"11\":\"公安\",\"12\":\"公安\",\"13\":\"应急\",\"14\":\"应急\",\"15\":\"应急\",\"16\":\"国土\",\"17\":\"国土\",\"18\":\"国土\",\"19\":\"环保\",\"21\":\"环保\",\"22\":\"交通\",\"23\":\"交通\",\"24\":\"国土\",\"25\":\"城管\",\"31\":\"配网\",\"32\":\"配网\",\"33\":\"输电\",\"34\":\"输电\"}{\"0\":\"变电站\",\"1\":\"变电站\",\"2\":\"变电站\",\"3\":\"水务\",\"4\":\"水务\",\"5\":\"水务\",\"6\":\"水务\",\"7\":\"城管\",\"8\":\"城管\",\"9\":\"城管\",\"10\":\"公安\",\"11\":\"公安\",\"12\":\"公安\",\"13\":\"应急\",\"14\":\"应急\",\"15\":\"应急\",\"16\":\"国土\",\"17\":\"国土\",\"18\":\"国土\",\"19\":\"环保\",\"21\":\"环保\",\"22\":\"交通\",\"23\":\"交通\",\"24\":\"国土\",\"25\":\"城管\",\"31\":\"配网\",\"32\":\"配网\",\"33\":\"输电\",\"34\":\"输电\"}";
        idenNameMap = JsonUtils.readJson(str, Map.class).get();
    }



    /**
     * 二级目录对应的idenValue值
     *
     * @return {@link List}<{@link Integer}>
     */
    public List<Integer> getIdenValueByName(String name) {
        List<Integer> result = Lists.newArrayList();
        idenNameMap.forEach((key, value) -> {
            if (value.equals(name)) {
                result.add(Integer.valueOf(key));
            }
        });
        return result;
    }

    public String getNameByIdenValue(int idenValue) {

        String name = idenNameMap.get(String.valueOf(idenValue));
        if (name == null) {
            return "未知";
        }
        return name;
    }

}
