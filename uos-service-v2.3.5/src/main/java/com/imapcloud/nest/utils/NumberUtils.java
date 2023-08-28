package com.imapcloud.nest.utils;

import java.math.BigDecimal;

/**
 * @author lzg
 *
 */
public class NumberUtils {
	
	public static double sum(double a, double b, int scale) {
		BigDecimal ba = BigDecimal.valueOf(a);
		BigDecimal bb = BigDecimal.valueOf(b);
		
		BigDecimal result = ba.add(bb).setScale(scale, BigDecimal.ROUND_HALF_UP);
		return result.doubleValue();
	}
	
	public static double subtract(double a, double b, int scale) {
		BigDecimal ba = BigDecimal.valueOf(a);
		BigDecimal bb = BigDecimal.valueOf(b);
		
		BigDecimal result = ba.subtract(bb).setScale(scale, BigDecimal.ROUND_HALF_UP);
		return result.doubleValue();
	}
	
	public static double multiply(double a, double b, int scale) {
		BigDecimal ba = BigDecimal.valueOf(a);
		BigDecimal bb = BigDecimal.valueOf(b);
		
		BigDecimal result = ba.multiply(bb).setScale(scale, BigDecimal.ROUND_HALF_UP);
		return result.doubleValue();
	}
	
	public static double divide(double a, double b, int scale) {
		BigDecimal ba = BigDecimal.valueOf(a);
		BigDecimal bb = BigDecimal.valueOf(b);
		
		BigDecimal result = ba.divide(bb, scale, BigDecimal.ROUND_HALF_UP);
		return result.doubleValue();
	}
	
	public static void main(String[] args) {
		System.out.println(sum(0.123d, 0.235d, 2));
		System.out.println(subtract(0.823d, 0.235d, 2));
		System.out.println(multiply(0.1256d, 10d, 2));
		System.out.println(divide(10d, 7d, 2));
		
	}

}
