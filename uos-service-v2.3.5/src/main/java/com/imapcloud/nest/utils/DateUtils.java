package com.imapcloud.nest.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * 抽象日期工具类
 * @author Vastfy
 * @date 2022/04/21 14:11
 * @since 1.8.9
 */
@Slf4j
public abstract class DateUtils {

    /**
     * 时区，东八区
     */
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(8);

    /**
     * 时区=Asia/Shanghai
     */
    public static final String ZONE_TIME_OF_SHANGHAI = "Asia/Shanghai";

    /**
     * 时间格式化模式 yyyyMMdd
     */
    public static final String DATE_PATTERN = "yyyyMMdd";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /**
     * 时间格式化模式 yyyyMMddHHmmss
     */
    public static final String DATE_TIME_PATTERN = "yyyyMMddHHmmss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    /**
     * 横杠形时间格式化模式 yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_TIME_PATTERN_OF_CN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER_OF_CN = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_OF_CN);

    /**
     * 日期格式化模式 yyyy-MM-dd
     */
    public static final String DATE_PATTERN_OF_CN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMATTER_OF_CN = DateTimeFormatter.ofPattern(DATE_PATTERN_OF_CN);

    /**
     * 日期格式化模式 HH:mm:ss
     */
    public static final String TIME_PATTERN_OF_CN = "HH:mm:ss";
    public static final DateTimeFormatter TIME_FORMATTER_OF_CN = DateTimeFormatter.ofPattern(TIME_PATTERN_OF_CN);

    /**
     * 时间格式化模式 HH:mm
     */
    public static final String TIME_PATTERN_OF_HM = "HH:mm";
    public static final DateTimeFormatter TIME_FORMATTER_OF_HM = DateTimeFormatter.ofPattern(TIME_PATTERN_OF_HM);


    /**
     * 返回时间格式字符串
     * @param pattern 指定格式化模式（如：yyyy-MM-dd、yyyy-MM-dd HH:mm:ss）
     * @return 格式化后的字符串
     * 如果指定格式，则默认返回yyyy-MM-dd HH:mm:ss时间格式
     * 如果根据指定模式格式化失败则返回 null
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        String formatPattern = pattern;
        if (formatPattern == null || "".equals(formatPattern)) {
            formatPattern = DATE_TIME_PATTERN;
        }
        try {
            return new SimpleDateFormat(formatPattern).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        pattern = Optional.ofNullable(pattern).orElse(DATE_TIME_PATTERN);
        // FIXME 此处使用ThreadLocal优化
        return new SimpleDateFormat(pattern).parse(dateStr);
    }

    public static Date to(LocalDate localDate){
        if(null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    public static LocalDate to(Date date){
        if(Objects.isNull(date)){
            return null;
        }
        return Objects.requireNonNull(toLocalDateTime(date)).toLocalDate();
    }
    public static LocalDate to(String dateStr, DateTimeFormatter formatter){
        if(!StringUtils.hasText(dateStr)){
            return null;
        }
        if(Objects.isNull(formatter)){
            formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        }
        return LocalDate.parse(dateStr, formatter);
    }

    public static LocalDateTime toLocalDateTime(Date date){
        if(Objects.isNull(date)){
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date toDate(LocalDateTime localDateTime){
        if(Objects.isNull(localDateTime)){
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
