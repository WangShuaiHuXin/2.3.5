package com.imapcloud.nest.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName CronUtils.java
 * @Description CronUtils 用于处理Cron表达式
 * @createTime 2022年04月18日 16:23:00
 */
public class CronUtils {

    /**
     *
     * @param date
     * @return
     */
    public static String getCronByDate(Date date) {
        String dateFormat = "ss mm HH dd MM ? yyyy";

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    /**
     *
     * @param localDateTime
     * @return
     */
    public static String getCronByLocalTime(LocalDateTime localDateTime) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        Date date = LocalDateUtil.localDateTimeToDate(localDateTime);
        return getCronByDate(date);
    }
}
