package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.v2.service.CpsMissionService;
import com.imapcloud.nest.v2.service.dto.in.CpsMissionInDTO;
import com.imapcloud.nest.v2.service.dto.out.CpsMissionOutDTO;
import com.imapcloud.nest.v2.web.vo.req.CpsMissionReqVO;
import com.imapcloud.nest.v2.web.vo.resp.CpsMissionRespVO;
import com.imapcloud.sdk.pojo.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@ApiSupport(author = "chenjiahong@geoai.com", order = 0)
@Api(value = "基站指令")
@RequestMapping("v2/cps/mission/")
@RestController
public class CpsMissionController {

    @Resource
    private CpsMissionService cpsMissionService;

    @PostMapping("alternate/landing/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 1)
    @ApiOperation("mission-设置备降点坐标")
    @NestCodeRecord(Constant.MISSION_MANAGER_C22)
    public Result<Object> setAlternateLanding(@PathVariable("nestId") @Valid @NotNull(message = "基站ID不能为空") String nestId,
                                              @Valid @RequestBody CpsMissionReqVO.CpsMissionLandingReqVO reqVO) {
        cpsMissionService.setAlternateLanding(new CpsMissionInDTO.CpsMissionLandingInDTO(nestId, reqVO.getLatitude(), reqVO.getLongitude(), reqVO.getAltitude(), reqVO.getEnable()));
        return Result.ok();
    }

    @PostMapping("alternate/landing/status/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 2)
    @ApiOperation("mission-设置自动前往备降点")
    @NestCodeRecord(Constant.MISSION_MANAGER_C27)
    public Result<Object> setAlternateLandingStatus(@PathVariable("nestId") @Valid @NotNull(message = "基站ID不能为空") String nestId,
                                                    @Valid @RequestBody @NotNull(message = "设置自动前往备降点失败,参数异常") CpsMissionReqVO.CpsMissionLand4ReqVO enable) {
        CpsMissionInDTO.CpsMissionLandingStatusInDTO inDTO = new CpsMissionInDTO.CpsMissionLandingStatusInDTO();
        inDTO.setNestId(nestId);
        inDTO.setEnable(enable.getEnable());
        cpsMissionService.setAlternateLandingStatus(inDTO);
        return Result.ok();
    }

    @PostMapping("alternate/landing/forward/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 3)
    @ApiOperation("mission-前往备降点")
    @NestCodeRecord(Constant.MISSION_MANAGER_C24)
    public Result<Object> setAlternateLandingForward(@PathVariable("nestId") @Valid @NotNull(message = "基站ID不能为空") String nestId,
                                                     @Valid @RequestBody @NotNull(message = "设置自动前往备降点失败,参数异常") CpsMissionReqVO.CpsMissionLandAltitudeReqVO altitude) {
        CpsMissionInDTO.CpsMissionLandingInDTO inDTO = new CpsMissionInDTO.CpsMissionLandingInDTO();
        inDTO.setNestId(nestId);
        inDTO.setAltitude(altitude.getAltitude());
        cpsMissionService.setAlternateLandingForward(inDTO);
        return Result.ok();
    }

    @GetMapping("alternate/landing/info/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 4)
    @ApiOperation("mission-获取设置备降点信息")
    public Result<CpsMissionRespVO.CpsMissionAlternateLandingInfoRespVO> getAlternateLandingInfo(@PathVariable("nestId") @Valid @NotNull(message = "基站ID不能为空") String nestId) {
        CpsMissionOutDTO.CpsMissionAlternateInfoOutDTO outDTO = cpsMissionService.getAlternateLandingInfo(nestId);
        CpsMissionRespVO.CpsMissionAlternateLandingInfoRespVO vo = new CpsMissionRespVO.CpsMissionAlternateLandingInfoRespVO();
        BeanUtils.copyProperties(outDTO, vo);
        return Result.ok(vo);
    }

    @GetMapping("alternate/landing/altitude/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 5)
    @ApiOperation("mission-获取设置备降点高度信息")
    @NestCodeRecord(Constant.MISSION_MANAGER_C26)
    public Result<CpsMissionRespVO.CpsMissionAlternateAltitudeInfoRespVO> getAlternateAltitudeInfo(@PathVariable("nestId") @Valid @NotNull(message = "基站ID不能为空") String nestId) {
        Double altitude = cpsMissionService.getAlternateAltitudeInfo(nestId);
        CpsMissionRespVO.CpsMissionAlternateAltitudeInfoRespVO vo = new CpsMissionRespVO.CpsMissionAlternateAltitudeInfoRespVO();
        vo.setAltitude(altitude);
        return Result.ok(vo);
    }
}
