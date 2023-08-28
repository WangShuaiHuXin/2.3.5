package com.imapcloud.nest.utils;


import com.sun.jna.Library;
import com.sun.jna.Native;

public interface Clibrary extends Library {

    Clibrary INSTANTCE = Native.load("/nest/libAirlinePlan.so", Clibrary.class);

    /**
     *
     * @param substationId
     * @param jsonTsk  optimal  0：结果不优化 1：结果优化 默认为1
     * @return
     */
    String fnAirLinePlan(String substationId, String jsonTsk, Integer mode);

}
