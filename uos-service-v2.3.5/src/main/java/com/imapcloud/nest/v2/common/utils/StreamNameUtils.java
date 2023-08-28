package com.imapcloud.nest.v2.common.utils;

public class StreamNameUtils {

    public static String buildAppName(String uuid) {
        String streamName = "";
        if (uuid.contains("-")) {
            //前面加in，兼容~~
            streamName = "in" + uuid.substring(0, uuid.indexOf("-"));
        } else {
            streamName = "in" + uuid;
        }
        return streamName;
    }

}
