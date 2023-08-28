package com.imapcloud.nest.controller;


import com.imapcloud.nest.model.StationInfraredThresholdEntity;
import com.imapcloud.nest.service.StationInfraredThresholdService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-04-13
 */
@RestController
@RequestMapping("/stationInfraredThreshold")
public class StationInfraredThresholdController {
    @Autowired
    private StationInfraredThresholdService stationInfraredThresholdService;

    /**
     * 设置温度
     * @return
     */
    @GetMapping("/setTemperature")
    public RestRes setTemperature(Double temperature){
        stationInfraredThresholdService.setTemperature(temperature);
        return RestRes.ok();
    }
    
    @GetMapping("/getList")
    public RestRes getList(StationInfraredThresholdEntity stationInfraredThresholdEntity) {
    	return stationInfraredThresholdService.getList(stationInfraredThresholdEntity);
    }
    
    @PostMapping("/updateData")
    public RestRes updateData(StationInfraredThresholdEntity stationInfraredThresholdEntity) {
    	return stationInfraredThresholdService.updateData(stationInfraredThresholdEntity);
    }
}

