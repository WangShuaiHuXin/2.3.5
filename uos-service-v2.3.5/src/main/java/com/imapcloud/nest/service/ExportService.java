package com.imapcloud.nest.service;

import com.imapcloud.nest.pojo.dto.AirLineAndDeviceDto;

import java.util.List;

import javax.servlet.http.HttpServletResponse;


public interface ExportService {

	public void exportByPOI(List<AirLineAndDeviceDto> exportDataList, String[] titles, String titleValue, String fileName, HttpServletResponse response);
}