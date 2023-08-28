package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.dto.BatteryUseNumsDto;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.BatteryService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 电池信息表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@RestController
@RequestMapping("/battery")
public class BatteryController {

    @Autowired
    private NestService nestService;

    @Autowired
    private CommonNestStateService commonNestStateService;

    @Resource
    private BatteryService batteryService;

    @Resource
    private BaseNestService baseNestService;

    /**
     * 获取电池使用次数
     *
     * @param nestId
     * @return
     */
    @GetMapping("/get/battery/use/nums/{nestId}")
    public RestRes getBatteryUseNums(@PathVariable String nestId) {
        if (nestId != null) {
            BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
            if (Objects.isNull(baseNestInfo)) {
                return RestRes.warn("基站查询不到");
            }
            List<BatteryUseNumsDto> batteryUseNums = commonNestStateService.getBatteryChargeCount(baseNestInfo.getUuid(), baseNestInfo.getType());
            Map<String, Object> res = new HashMap<>(2);
            res.put("batteryUseNums", batteryUseNums);
            res.put("nestType", baseNestInfo.getType());

            return RestRes.ok(res);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }
}

