package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @author daolin
 * @Date
 */
@Data
public class AirLineAndDeviceDto {

	public String deviceName;
	public String capturePointName;
	public String airLineName;
	public Integer airLineInfoCount;  //航线信息体地址
	public Integer infoCount;  //信息体地址

	
	
	
}
