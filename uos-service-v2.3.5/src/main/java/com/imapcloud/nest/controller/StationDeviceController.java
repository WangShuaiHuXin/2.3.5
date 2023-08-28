package com.imapcloud.nest.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.service.StationDeviceService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author daolin
 * @since 2020-11-02
 */
@RestController
@RequestMapping("/device")
public class StationDeviceController {

    @Autowired
    private StationDeviceService stationDeviceService;


    /**
     * 初始化台账信息
     * @param deviceJson    设备台账json
     * @param pointJson     巡检点json
     * @param mapsJson      map
     * @param wholeUnitJson 单元
     * @param nestId        机巢id
     * @return
     */
    @PostMapping("/init/device")
    public RestRes initDevice(MultipartFile deviceJson,
                              MultipartFile pointJson,
                              MultipartFile mapsJson,
                              MultipartFile wholeUnitJson,
                              String nestId) {
        Integer count = stationDeviceService.initDevice(deviceJson, pointJson, mapsJson, wholeUnitJson, nestId);
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SYNCHRONIZATION_COMPLETED.getContent()) + count);
    }

    @PostMapping("/list/{nestId}")
    public RestRes listDeviceByNestId(@PathVariable String nestId) {
        return stationDeviceService.selectByNestId(nestId);
    }

    /**
     * 瓶窑生成台账信息
     * @return
     */
    @GetMapping("/generate/checkPointDetail")
    public RestRes CheckPointDetail(@RequestParam Integer airLineStartId, @RequestParam Integer airLineEndId, HttpServletResponse response) {
        Integer count = stationDeviceService.checkPointDetail(airLineStartId, airLineEndId, response);
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CURRENT_PHOTO_POINTS.getContent()) + count);
    }


}

