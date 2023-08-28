package com.imapcloud.nest.common.netty.ws;

import java.util.Objects;

public class UriParamUtil {

    public static UriParam parse(String param) {
        if (Objects.isNull(param)) {
            return new UriParam();
        }
        String[] split = param.split("/");
        if (split.length != 3) {
            return null;
        }
        // 第三层参数分割，第一是uuid，第二是额外参数
        UriParam uriParam = new UriParam();
        uriParam.setAccount(split[0])
                .setType(split[1])
                .setUuid(split[2].split("\\?")[0]);

        return uriParam;
    }
}
