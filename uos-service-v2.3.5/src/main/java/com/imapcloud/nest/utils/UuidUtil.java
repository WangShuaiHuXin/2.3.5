package com.imapcloud.nest.utils;

import cn.hutool.core.util.IdUtil;

import java.util.UUID;

/**
 * @author wmin
 */
public class UuidUtil {
    public static String create() {
        return UUID.randomUUID().toString();
    }

    //没有-的32为uuid
    public static String createNoBar() {
        return IdUtil.fastSimpleUUID();
    }


    public static void main(String[] args) {
        System.out.println(create());
    }
}
