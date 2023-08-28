package com.imapcloud.nest.utils;

import com.baomidou.mybatisplus.core.toolkit.sql.StringEscape;
import org.springframework.util.StringUtils;

/**
 * SQL注入擦除器
 *
 * @author Vastfy
 * @date 2022/5/7 17:28
 * @since 1.8.9
 */
public abstract class SqlEscapeUtils {

    private static final String ESCAPE = "\\";
    private static final String PERCENT = "%";
    private static final String UNDER_LINE = "_";

    private static final String PERCENT_ESCAPE = ESCAPE + PERCENT;
    private static final String UNDER_LINE_ESCAPE = ESCAPE + UNDER_LINE;

    public static String escapeSql(String sql){
        if(StringUtils.hasText(sql)){
            sql = sql.replace(PERCENT, PERCENT_ESCAPE);
            sql = sql.replace(UNDER_LINE, UNDER_LINE_ESCAPE);
        }
        return sql;
    }

    private SqlEscapeUtils(){}

}
