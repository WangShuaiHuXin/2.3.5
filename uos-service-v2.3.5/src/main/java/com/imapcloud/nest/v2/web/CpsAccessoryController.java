package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.AccessoryService;
import com.imapcloud.nest.v2.service.dto.in.AccessoryInDTO;
import com.imapcloud.nest.v2.service.dto.out.AccessoryAuthStatusOutDTO;
import com.imapcloud.nest.v2.web.vo.req.AccessoryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.AccessoryRespVO;
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
@RequestMapping("v2/cps/accessory/")
@RestController
public class CpsAccessoryController {

    @Resource
    private AccessoryService accessoryService;

    @GetMapping("/certification/status/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 1)
    @ApiOperation("查找基站LTE认证状态")
    @NestCodeRecord(Constant.ACCESSORY_MANAGER_C52)
    public Result<AccessoryRespVO.AccessoryAuthStatusRespVO> accessoryAuthStatus(@PathVariable("nestId") @NotNull(message = "基站id不能为空") @Valid String nestId) {
        AccessoryAuthStatusOutDTO outDTO = accessoryService.accessoryAuthStatus(nestId);
        if (outDTO.getFlag()) {
            AccessoryRespVO.AccessoryAuthStatusRespVO respVO = new AccessoryRespVO.AccessoryAuthStatusRespVO();
            respVO.setIsLteaAvailable(outDTO.getIsLTEAuthenticationAvailable());
            respVO.setIsLteaAuthenticated(outDTO.getIsLTEAuthenticated());
            respVO.setLteaAreaCode(outDTO.getLTEAuthenticatedPhoneAreaCode());
            respVO.setLteaPhoneNumber(outDTO.getLTEAuthenticatedPhoneNumber());
            respVO.setLteaAuthenticatedTime(outDTO.getLTELastAuthenticatedTime());
            respVO.setLteaRemainingTime(outDTO.getLTEAuthenticatedRemainingFatalism());
            return Result.ok(respVO);
        }
        throw new BusinessException(outDTO.getMsg());
    }

    @PostMapping("/certification/captcha/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 2)
    @ApiOperation("发送LTE认证验证码")
    @NestCodeRecord(Constant.ACCESSORY_MANAGER_C50)
    public Result<Object> sendCaptcha(@PathVariable("nestId") @NotNull(message = "基站id不能为空") @Valid String nestId, @RequestBody AccessoryReqVO.AccessoryCaptchaReqVO reqVO) {
        AccessoryInDTO.AccessoryCaptchaInDTO inDTO = new AccessoryInDTO.AccessoryCaptchaInDTO();
        BeanUtils.copyProperties(reqVO, inDTO);
        inDTO.setNestId(nestId);
        accessoryService.accessorySendCaptcha(inDTO);
        return Result.ok();
    }

    @PostMapping("/certification/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 3)
    @ApiOperation("发送LTE实名认证请求")
    @NestCodeRecord(Constant.ACCESSORY_MANAGER_C51)
    public Result<Object> sendCertification(@PathVariable("nestId") @NotNull(message = "基站id不能为空") @Valid String nestId, @RequestBody AccessoryReqVO.AccessoryCaptchaReqVO reqVO) {
        AccessoryInDTO.AccessoryCaptchaInDTO inDTO = new AccessoryInDTO.AccessoryCaptchaInDTO();
        BeanUtils.copyProperties(reqVO, inDTO);
        inDTO.setNestId(nestId);
        accessoryService.sendCertification(inDTO);
        return Result.ok();
    }

    @PostMapping("/transmission/status/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 4)
    @ApiOperation("accessory-设置图传状态")
    @NestCodeRecord(Constant.ACCESSORY_MANAGER_C53)
    public Result<Object> setTransmission(@PathVariable("nestId") @NotNull(message = "基站id不能为空") @Valid String nestId, @RequestBody AccessoryReqVO.AccessoryTransmissionReqVO reqVO) {
        accessoryService.setTransmission(nestId, reqVO.getEnable());
        return Result.ok();
    }

    @GetMapping("/transmission/status/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 5)
    @ApiOperation("accessory-获取图传状态")
    @NestCodeRecord(Constant.ACCESSORY_MANAGER_C54)
    public Result<AccessoryRespVO.AccessoryTransmissionRespVO> getTransmission(@PathVariable("nestId") @NotNull(message = "基站id不能为空") @Valid String nestId) {
        AccessoryRespVO.AccessoryTransmissionRespVO respVO = new AccessoryRespVO.AccessoryTransmissionRespVO();
        Boolean flag = accessoryService.getTransmission(nestId);
        respVO.setEnable(flag);
        return Result.ok(respVO);
    }

}
