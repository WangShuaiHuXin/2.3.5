package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.service.TransmissionTowerService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 输电线路杆塔表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2021-08-17
 */
@RestController
@RequestMapping("/transmission/tower/")
public class TransmissionTowerController {

    @Autowired
    private TransmissionTowerService transmissionTowerService;

    @PostMapping("/create/task/tower/nest/uuid")
    public RestRes createTaskByTowerAndNestUuid(@RequestBody Map<String,String> param) {
        String nestUuid =  param.get("nestUuid");
        String towerUuid =  param.get("towerUuid");
        if (nestUuid != null && towerUuid != null) {
            return transmissionTowerService.createTaskByTowerAndNestUuid(towerUuid, nestUuid);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }


}

