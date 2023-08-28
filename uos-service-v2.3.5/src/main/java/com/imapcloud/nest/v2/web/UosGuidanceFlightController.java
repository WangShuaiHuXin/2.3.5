package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.web.vo.req.GuidanceFlightReqVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosGuidanceFlightController.java
 * @Description UosGuidanceFlightController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "指点飞行")
@RequestMapping("v2/guidance/flight/")
@RestController
public class UosGuidanceFlightController {

    /**
     *  指点飞行
     * @param guidanceFlightReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "指点飞行", notes = "指点飞行")
    @PostMapping("/fly")
    public Result<Boolean> fly(@RequestBody @Valid GuidanceFlightReqVO guidanceFlightReqVO){
        return Result.ok();
    }

}
