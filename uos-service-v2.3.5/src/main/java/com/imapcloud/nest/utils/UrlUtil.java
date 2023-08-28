package com.imapcloud.nest.utils;

import cn.hutool.core.net.URLEncodeUtil;

import java.nio.charset.StandardCharsets;

/**
 * url工具类
 *
 * @author boluo
 * @date 2022-06-16
 */
public final class UrlUtil {

    private UrlUtil() {

    }

    public static String encode(String value) {

        return URLEncodeUtil.encode(value, StandardCharsets.UTF_8);
    }
}
