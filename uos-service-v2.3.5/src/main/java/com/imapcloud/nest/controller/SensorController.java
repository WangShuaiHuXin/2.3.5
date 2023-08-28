package com.imapcloud.nest.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imapcloud.nest.model.SensorEntity;
import com.imapcloud.nest.service.SensorService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 传感器表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-09-21
 */
@RestController
@RequestMapping("/sensor")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    @GetMapping("/list/sensor")
    public RestRes listSensor() {
        List<SensorEntity> sensorList = sensorService.list(new QueryWrapper<SensorEntity>().lambda().eq(SensorEntity::getDeleted, false));
        Map<String, Object> map = new HashMap<>(2);
        map.put("sensorList", sensorList);
        return RestRes.ok(map);
    }
}

