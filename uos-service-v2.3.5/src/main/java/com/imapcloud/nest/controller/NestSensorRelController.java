package com.imapcloud.nest.controller;


import com.imapcloud.nest.model.NestSensorRelEntity;
import com.imapcloud.nest.model.SensorEntity;
import com.imapcloud.nest.service.NestSensorRelService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.TransformENUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 传感器表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-09-22
 */
@RestController
@RequestMapping("/nestSensor")
public class NestSensorRelController {

    @Resource
    private NestSensorRelService nestSensorRelService;

    @PostMapping("/sensorListByNestId/{nestId}")
    public RestRes getSensorByNestId(@PathVariable String nestId) {
        List<NestSensorRelEntity> sensorEntityList = nestSensorRelService.getSensorByNestId(nestId);
        Map map = new HashMap();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String language = request.getHeader("Accept-Language");
        if ("en-US".equals(language)) {
            sensorEntityList.forEach(r -> r.setName(TransformENUtils.transformSensor(r.getName())));
        }
        map.put("sensorEntity", sensorEntityList);
        return RestRes.ok(map);
    }

}

