package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.v2.service.UosCommonFlightService;
import com.imapcloud.sdk.pojo.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosCommonFlightController.java
 * @Description UosCommonFlightController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "通用飞行")
@RequestMapping("v2/common/flight/")
@RestController
public class UosCommonFlightController {

    @Resource
    private UosCommonFlightService uosCommonFlightService;

    /**
     *  空中回巢
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "空中回巢", notes = "空中回巢")
    @PostMapping("/flyBack/{nestId}")
    @NestCodeRecord(Constant.EACC_RC_FLIGHT_BACK)
    public Result<Boolean> flyBack(@PathVariable("nestId") @Valid @NotNull(message = "{geoai_uos_cannot_empty_nestid}") @NestId String nestId){
        Boolean bol = this.uosCommonFlightService.flightBack(nestId);
        return Result.ok(bol);
    }
}
