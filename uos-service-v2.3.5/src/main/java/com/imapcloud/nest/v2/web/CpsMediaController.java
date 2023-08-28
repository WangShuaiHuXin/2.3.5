package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.v2.service.CpsMediaService;
import com.imapcloud.nest.v2.web.vo.req.CpsMediaReqVO;
import com.imapcloud.sdk.pojo.constant.Constant;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * media指令
 *
 * @author boluo
 * @date 2023-04-13
 */
@RequestMapping("v2/cps/media/")
@RestController
public class CpsMediaController {

    @Resource
    private CpsMediaService cpsMediaService;

    @NestCodeRecord(Constant.MEDIA_MANAGER_2_C11)
    @PostMapping("data/synStop")
    public Result<Object> dataSynStop(@Valid @RequestBody CpsMediaReqVO.DataSynStopReqVO dataSynStopReqVO) {

        cpsMediaService.dataSynStop(dataSynStopReqVO.getNestId(), dataSynStopReqVO.getWhich());
        return Result.ok();
    }
}
