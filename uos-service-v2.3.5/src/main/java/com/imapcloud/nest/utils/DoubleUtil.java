package com.imapcloud.nest.utils;

/**
 * double数据的处理类
 *
 * @author wmin
 */
public class DoubleUtil {

    /**
     * 保留n个小数点
     *
     * @param n
     * @param d
     * @return
     */
    public static double roundKeepDec(int n, double d) {
        double i = Math.pow(10, n);
        return (double) Math.round(d * i) / i;
    }
}
