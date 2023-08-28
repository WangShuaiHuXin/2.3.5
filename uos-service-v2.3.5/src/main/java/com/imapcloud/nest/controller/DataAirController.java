package com.imapcloud.nest.controller;


import com.imapcloud.nest.pojo.dto.DataAirSerialDTO;
import com.imapcloud.nest.service.DataAirService;
import com.imapcloud.nest.service.NestExternalEquipService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-08-02
 */
@RestController
@RequestMapping("/dataAir")
public class DataAirController {

    @Resource
    private DataAirService dataAirService;
    @Resource
    private NestExternalEquipService nestExternalEquipService;

    /**
     * 保存设备号
     */
    @PostMapping("/serial/save")
    @ResponseBody
    public RestRes save(@RequestBody DataAirSerialDTO dataAirSerialDTO) {
        this.dataAirService.setSerialId(dataAirSerialDTO.getNestUuid() , dataAirSerialDTO.getSerialId());
        return RestRes.ok();
    }

    /**
     * 获取设备号
     */
    @GetMapping("/serial/get")
    @ResponseBody
    public RestRes getSerial(@RequestParam("nestUuid") String nestUUID) {
        String result = this.dataAirService.getSerialId(nestUUID);
        return RestRes.ok("serialId",result);
    }

    /**
     * 查询设备号是否有绑定,除去当前已基站
     */
    @GetMapping("/serial/getNest")
    @ResponseBody
    public RestRes getNestBind(@RequestParam("serialId") String serialId , @RequestParam("nestUuid") String nestUUID) {
        List<String> nestUuidList = this.dataAirService.getNestBySerialId(serialId , nestUUID);
        return RestRes.ok("nestUuid",nestUuidList);
    }

}

