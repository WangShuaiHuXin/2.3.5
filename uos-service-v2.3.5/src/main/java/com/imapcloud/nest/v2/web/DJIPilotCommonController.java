package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.DJIPilotCommonService;
import com.imapcloud.nest.v2.service.dto.out.DJIPilotCommonResultOutDTO;
import com.imapcloud.nest.v2.web.transformer.DJIPilotCommonResultTransformer;
import com.imapcloud.nest.v2.web.vo.req.DJIPilotCommonResultReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DJIPilotCommonResultRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 大疆pilot通用控制
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosCommonFlightController.java
 * @Description UosCommonFlightController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "大疆pilot通用控制")
@RequestMapping("v2/dji/pilot/common")
@RestController
public class DJIPilotCommonController {

    @Resource
    private DJIPilotCommonService djiPilotCommonService;
    /**
     * 查询pilot开发者授权信息
     *
     * @param
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "查询pilot开发者授权信息", notes = "查询pilot开发者授权信息")
    @GetMapping("/get/license")
    public Result<DJIPilotCommonResultRespVO.PilotLicenseResultRespVO> getLicense() {
        DJIPilotCommonResultOutDTO.PilotLicenseResultOutDTO pilotLicenseResultOutDTO = this.djiPilotCommonService.getLicense();
        DJIPilotCommonResultRespVO.PilotLicenseResultRespVO pilotCommonResultRespVO = DJIPilotCommonResultTransformer.INSTANCES.transform(pilotLicenseResultOutDTO);
        return Result.ok(pilotCommonResultRespVO);
    }

    /**
     * 查询pilot对应连接的mqtt信息
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "查询pilot对应连接的mqtt信息", notes = "查询pilot对应连接的mqtt信息")
    @PostMapping("/get/mqtt/{nestUUid}")
    public Result<DJIPilotCommonResultRespVO.PilotMqttInfoRespVO> getMqttInfo(@PathVariable("nestUUid") String nestUUid) {
        DJIPilotCommonResultOutDTO.PilotMqttResultOutDTO pilotMqttResultOutDTO = this.djiPilotCommonService.getMqttInfo(nestUUid);
        DJIPilotCommonResultRespVO.PilotMqttInfoRespVO pilotMqttInfoRespVO = DJIPilotCommonResultTransformer.INSTANCES.transform(pilotMqttResultOutDTO);
        return Result.ok(pilotMqttInfoRespVO);
    }

    /**
     * 查询pilot对应连接的mqtt信息
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "绑定基站跟无人机", notes = "绑定基站跟无人机")
    @PostMapping("/bind/nest/uav")
    public Result<Boolean> bindNestUav(@RequestBody @Valid DJIPilotCommonResultReqVO reqVO) {
        return Result.ok(this.djiPilotCommonService.bindNestAndUav(reqVO.getNestSn() , reqVO.getUavSn()));
    }

}
