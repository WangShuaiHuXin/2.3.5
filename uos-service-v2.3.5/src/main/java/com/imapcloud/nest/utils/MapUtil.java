package com.imapcloud.nest.utils;


import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wmin
 */
public class MapUtil {

    public static Map<String, Object> put(List<String> keys, List<Object> values) {
        if (keys != null && values != null && keys.size() == values.size()) {
            Map<String, Object> map = new HashMap<>(keys.size());
            for (int i = 0; i < keys.size(); i++) {
                map.put(keys.get(i), values.get(i));
            }
            return map;
        }
        return null;
    }

    /**
     * 合并两个具有相同key的map为list
     * @param m1 要合并的list
     * @param mergeKey 以哪个key为基准合并
     * @return
     */
    public static List<Map<String, Object>> merge(List<Map<String, Object>> m1, String mergeKey){
        Set<String> set = new HashSet<>();
        return m1.stream()
                .filter(map->map.get(mergeKey)!=null)
                .collect(Collectors.groupingBy(o->{
                    set.addAll(o.keySet());//暂存所有key
                    return o.get(mergeKey); //按mergeKey分组
                }))
                .entrySet().stream().map(o->{
                    Map<String, Object> map = o.getValue().stream().flatMap(m->{ //合并
                        return m.entrySet().stream();
                    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->b));
                    set.stream().forEach(k->{//为没有的key赋值0
                        if(!map.containsKey(k)) {
                            map.put(k, "0");
                        }
                    });
                    return map;
                }).collect(Collectors.toList());
    }

}
