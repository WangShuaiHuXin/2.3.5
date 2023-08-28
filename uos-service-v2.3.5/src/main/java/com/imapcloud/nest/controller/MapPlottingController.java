package com.imapcloud.nest.controller;

import com.imapcloud.nest.pojo.dto.MapPlottingDto;
import com.imapcloud.nest.service.MapPlottingService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地图标绘 控制层
 */
@RestController
@RequestMapping("/mapPlotting")
public class MapPlottingController {

    @Resource
    private MapPlottingService mapPlottingService;

    private RestRes setResult(Object result){
        RestRes res = new RestRes();
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        res.setParam(map);
        return res;
    }

    @GetMapping("/page")
    public RestRes queryPage(@RequestParam Map<String, Object> params){
        return setResult(mapPlottingService.queryPage(params));
    }

    @GetMapping("/info/{id}")
    public RestRes queryPage(@PathVariable Integer id){
        return setResult(mapPlottingService.info(id));
    }

    /**
     * 获取当前用户的地图标绘
     */
    @GetMapping("/listByUser")
    public RestRes listByUser(){
        return setResult(mapPlottingService.listByUser());
    }

    @PostMapping("/save")
    public RestRes save(@RequestBody MapPlottingDto dto){
        return setResult(mapPlottingService.savePlotting(dto));
    }

    @PostMapping("/update")
    public RestRes update(@RequestBody MapPlottingDto dto){
        return setResult(mapPlottingService.updatePlotting(dto));
    }

    @PostMapping("/delete")
    public RestRes delete(@RequestBody Integer[] ids){
        return setResult(mapPlottingService.removeByIds(Arrays.asList(ids)));
    }

}
