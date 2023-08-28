package com.imapcloud.nest.controller;


import com.imapcloud.nest.model.MapParamEntity;
import com.imapcloud.nest.service.MapParamService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-08-17
 */
@RestController
@RequestMapping("/mapParam")
public class MapParamController {


    @Resource
    private MapParamService mapParamService;

    @PostMapping("/saveMapParam")
    public RestRes saveMapParam(@RequestBody MapParamEntity mapParamEntity) {
        return mapParamService.saveMapParam(mapParamEntity);
    }

    @GetMapping("/getMapParam")
    public RestRes getMapParam(String orgCode) {
        MapParamEntity mapParamEntity = mapParamService.getMapParam(orgCode);
        Map map = new HashMap(2);
        map.put("mapParam", mapParamEntity);
        return RestRes.ok(map);
    }

}

