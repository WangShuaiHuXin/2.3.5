package com.imapcloud.nest.v2.common.utils;

import com.geoai.common.core.constant.SymbolConstants;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 数字格式化工具类
 *
 * @author Vastfy
 * @date 2023/3/8 17:09
 * @since 2.2.5
 */
public abstract class NumberFormatUtils {

    private NumberFormatUtils(){}

    public static BigDecimal format(long number, int precision){
        long numberAbs = Math.abs(number);
        String format = format(Long.toString(numberAbs), precision);
        if(number < 0){
            format = SymbolConstants.MINUS + format;
        }
        return new BigDecimal(format).setScale(precision);
    }

    public static String format(String str, int precision){
        if(!StringUtils.hasText(str)){
            throw new IllegalArgumentException("str is not a number");
        }
        if(precision < 0){
            throw new IllegalArgumentException("precision must non-negative number");
        }
        if(precision == 0){
            return str;
        }
        int breakPoint = str.length() - precision;
        if(breakPoint <= 0){
            return SymbolConstants.ZERO + SymbolConstants.POINT + str;
        }
        return str.substring(0, breakPoint) + SymbolConstants.POINT + str.substring(breakPoint);
    }

//    public static void main(String[] args) {
//        long number1 = 1173485816L;
//        long number2 = 367119600L;
//        long number3 = 5572L;
//        long number4 = 5572L;
//        System.out.println(number1 + "格式值 ==> " + format(number1, 7));
//        System.out.println(number2 + "格式值 ==> " + format(number2, 7));
//        System.out.println(number3 + "格式值 ==> " + format(number3, 2));
//        System.out.println(number4 + "格式值 ==> " + format(number4, 6));
//    }

}
