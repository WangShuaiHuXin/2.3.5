package com.imapcloud.nest.controller;


import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 系统参数表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-12-17
 */
@RestController
@RequestMapping("/sys/args")
public class SysArgsController {

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    /**
     * 获取当前系统参数表
     *
     */
    @GetMapping("/find/curr/sys/version")
    public RestRes findCurrentSystemVersion() {
        Map<String, Object> resMap = new HashMap<>(2);
        resMap.put("currSysVersion", geoaiUosProperties.getVersion());
        resMap.put("insideStatus", geoaiUosProperties.getInsideStatus());
        resMap.put("imUrl", geoaiUosProperties.getIm().getUrl());

        return RestRes.ok(resMap);
    }

}

