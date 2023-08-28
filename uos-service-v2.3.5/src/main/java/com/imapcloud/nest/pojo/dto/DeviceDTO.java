package com.imapcloud.nest.pojo.dto;

import java.util.List;
import java.util.Map;

/**
 * @author daolin
 * @Date
 */
public class DeviceDTO {
	public String name;
	public String number;
	public List<Map<String, Object>> List;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public List<Map<String, Object>> getList() {
		return List;
	}
	public void setList(List<Map<String, Object>> list) {
		List = list;
	}
	
	
	
}
