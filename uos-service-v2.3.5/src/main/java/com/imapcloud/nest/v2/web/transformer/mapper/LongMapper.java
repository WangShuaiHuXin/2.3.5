package com.imapcloud.nest.v2.web.transformer.mapper;


import org.apache.commons.lang3.StringUtils;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName LongMapper.java
 * @Description LongMapper
 * @createTime 2022年07月19日 08:57:00
 */

public class LongMapper {

    public Long asLong(String str){
        return StringUtils.isEmpty(str) ?null: Long.parseLong(str);
    }

}
