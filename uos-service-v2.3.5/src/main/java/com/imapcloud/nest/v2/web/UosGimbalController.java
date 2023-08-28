package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.GimbalAutoFollow;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.v2.service.UosGimbalService;
import com.imapcloud.nest.v2.web.transformer.GimbalTransformer;
import com.imapcloud.nest.v2.web.vo.req.GimbalReqVO;
import com.imapcloud.sdk.pojo.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosCommonFlightController.java
 * @Description UosCommonFlightController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "云台")
@RequestMapping("v2/control/gimbal")
@RestController
public class UosGimbalController {

    @Resource
    private UosGimbalService uosGimbalService;


    /**
     *  云台俯仰角
     * @param gimbalReqVO
     * @return
     */
    @GimbalAutoFollow
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C59)
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "云台俯仰角", notes = "云台俯仰角")
    @PostMapping("/gimbal")
    public Result<Boolean> gimbal(@RequestBody @Valid GimbalReqVO gimbalReqVO){
        Boolean bol = this.uosGimbalService.gimbal(GimbalTransformer.INSTANCES.transform(gimbalReqVO));
        return Result.ok(bol);
    }

}
