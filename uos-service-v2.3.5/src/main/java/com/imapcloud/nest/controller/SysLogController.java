package com.imapcloud.nest.controller;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.pojo.dto.SysLogDelParam;
import com.imapcloud.nest.pojo.dto.SysLogQueryParam;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.mongo.service.MongoSysLogService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/sys/log")
public class SysLogController {

    @Autowired
    private MongoSysLogService mongoSysLogService;

    @GetMapping("/list/sys/logs")
    public RestRes listSysLogs(@Valid SysLogQueryParam sysLogQueryParam, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()));
        }
        return mongoSysLogService.listQuerySysLogs(sysLogQueryParam);
    }

    @DeleteMapping("/remove/sys/logs")
    public RestRes removeSysLogs(@RequestBody @Valid SysLogDelParam sysLogDelParam, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()));
        }
        return mongoSysLogService.removeSysLogs(sysLogDelParam);
    }
}
