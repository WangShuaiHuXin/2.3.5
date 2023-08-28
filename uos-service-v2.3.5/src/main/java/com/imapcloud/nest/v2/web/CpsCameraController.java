package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.v2.service.CpsCameraService;
import com.imapcloud.nest.v2.service.dto.in.CpsCameraInDTO;
import com.imapcloud.nest.v2.web.vo.req.CpsCameraReqVO;
import com.imapcloud.sdk.pojo.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@ApiSupport(author = "chenjiahong@geoai.com", order = 0)
@Api(value = "camera-基站指令")
@RequestMapping("v2/cps/camera/")
@RestController
public class CpsCameraController {

    @Resource
    private CpsCameraService cpsCameraService;


    @GetMapping("/types/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 1)
    @ApiOperation("camera-获取拍照/录像源类型设置")
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C52)
    public Result<Object> getCameraTypes(@PathVariable("nestId") @NotNull(message = "基站id不能为空") @Valid String nestId) {
        Map<String, Object> formats = cpsCameraService.getCameraTypes(nestId);
        return Result.ok(formats);
    }

    @PostMapping("/types/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 2)
    @ApiOperation("camera-设置拍照/录像源类型")
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C41)
    public Result<Object> setCameraTypes(@PathVariable("nestId") @NotNull(message = "基站id不能为空") @Valid String nestId,
                                         @RequestBody @NotNull(message = "设置拍照/录像源类型出错,参数异常") @Valid CpsCameraReqVO.CpsCameraStatusReqVO formats) {
        CpsCameraInDTO.CpsCameraSetTypeInDTO inDTO = new CpsCameraInDTO.CpsCameraSetTypeInDTO();
        inDTO.setFormats(formats.getFormats());
        inDTO.setNestId(nestId);
        cpsCameraService.setCameraTypes(inDTO);
        return Result.ok();
    }

    @PostMapping("/zoom/{nestId}")
    @ApiOperationSupport(author = "chenjiahong@geoai.com", order = 3)
    @ApiOperation("camera-设置变焦")
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C48)
    public Result<Object> setCameraZooms(@PathVariable("nestId") @NotNull(message = "基站id不能为空") @Valid String nestId,
                                         @RequestBody @NotNull(message = "设置拍照/录像源类型出错,参数异常") @Valid CpsCameraReqVO.CpsCameraZoomReqVO zoom) {
        CpsCameraInDTO.CpsCameraSetZoomInDTO inDTO = new CpsCameraInDTO.CpsCameraSetZoomInDTO();
        inDTO.setSource(zoom.getSource());
        inDTO.setNestId(nestId);
        cpsCameraService.setCameraZoom(inDTO);
        return Result.ok();
    }
}
