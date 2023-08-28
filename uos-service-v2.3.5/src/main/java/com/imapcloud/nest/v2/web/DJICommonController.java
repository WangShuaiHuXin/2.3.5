package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.DJICommonService;
import com.imapcloud.nest.v2.service.dto.in.ChargeLiveLensInDTO;
import com.imapcloud.nest.v2.service.dto.in.LimitDistanceInDTO;
import com.imapcloud.nest.v2.service.dto.in.LimitHeightInDTO;
import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;
import com.imapcloud.nest.v2.web.transformer.DJICommonResultTransformer;
import com.imapcloud.nest.v2.web.vo.req.ChargeLiveLensReqVO;
import com.imapcloud.nest.v2.web.vo.req.LimitDistanceReqVO;
import com.imapcloud.nest.v2.web.vo.req.LimitHeightReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DJICommonResultRespVO;
import com.imapcloud.sdk.pojo.constant.DjiDockTopic;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 大疆机场通用控制
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosCommonFlightController.java
 * @Description UosCommonFlightController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "大疆机场通用控制")
@RequestMapping("v2/dji/common")
@RestController
public class DJICommonController {

    @Resource
    private DJICommonService djiCommonService;

    /**
     * 调试模式开
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "调试模式开", notes = "调试模式开")
    @NestCodeRecord(DjiDockTopic.DEBUG_MODE_OPEN)
    @PostMapping("/debug/mode/open/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> debugModeOpen(@PathVariable("nestId") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.debugMode(nestId, Boolean.TRUE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 调试模式关
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "调试模式关", notes = "调试模式关")
    @NestCodeRecord(DjiDockTopic.DEBUG_MODE_CLOSE)
    @PostMapping("/debug/mode/close/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> debugModeClose(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.debugMode(nestId, Boolean.FALSE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 打开补光灯
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "打开补光灯", notes = "打开补光灯")
    @NestCodeRecord(DjiDockTopic.SUPPLEMENT_LIGHT_OPEN)
    @PostMapping("/supplement/light/open/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> supplementLightOpen(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.supplementLight(nestId, Boolean.TRUE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 关闭补光灯
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "关闭补光灯", notes = "关闭补光灯")
    @NestCodeRecord(DjiDockTopic.SUPPLEMENT_LIGHT_CLOSE)
    @PostMapping("/supplement/light/close/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> supplementLightClose(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.supplementLight(nestId, Boolean.FALSE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }

    /**
     * 一键返航
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "一键返航", notes = "一键返航")
    @NestCodeRecord(DjiDockTopic.RETURN_HOME)
    @PostMapping("/return/home/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> returnHome(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.returnHome(nestId);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 机场重启
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "机场重启", notes = "机场重启")
    @NestCodeRecord(DjiDockTopic.DEVICE_REBOOT)
    @PostMapping("/device/reboot/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> deviceReboot(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.deviceReboot(nestId);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 飞行器开机
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "飞行器开机", notes = "飞行器开机")
    @NestCodeRecord(DjiDockTopic.DRONE_OPEN)
    @PostMapping("/drone/open/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> droneOpen(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.drone(nestId, Boolean.TRUE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 飞行器关机
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "飞行器关机", notes = "飞行器关机")
    @NestCodeRecord(DjiDockTopic.DRONE_CLOSE)
    @PostMapping("/drone/close/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> droneClose(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.drone(nestId, Boolean.FALSE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 机场数据格式化
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "机场数据格式化", notes = "机场数据格式化")
    @NestCodeRecord(DjiDockTopic.DEVICE_FORMAT)
    @PostMapping("/device/format/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> deviceFormat(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.deviceFormat(nestId);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 打开舱盖
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "打开舱盖", notes = "打开舱盖")
    @NestCodeRecord(DjiDockTopic.COVER_OPEN)
    @PostMapping("/cover/open/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> coverOpen(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.cover(nestId, Boolean.TRUE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 关闭舱盖
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "关闭舱盖", notes = "关闭舱盖")
    @NestCodeRecord(DjiDockTopic.COVER_CLOSE)
    @PostMapping("/cover/close/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> coverClose(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.cover(nestId, Boolean.FALSE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 推杆展开
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "推杆展开", notes = "推杆展开")
    @NestCodeRecord(DjiDockTopic.PUTTER_OPEN)
    @PostMapping("/putter/open/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> putterOpen(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.putter(nestId, Boolean.TRUE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 推杆关闭
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "推杆关闭", notes = "推杆关闭")
    @NestCodeRecord(DjiDockTopic.PUTTER_CLOSE)
    @PostMapping("/putter/close/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> putterClose(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.putter(nestId, Boolean.FALSE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 打开充电
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "打开充电", notes = "打开充电")
    @NestCodeRecord(DjiDockTopic.CHARGE_OPEN)
    @PostMapping("/charge/open/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> chargeOpen(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.charge(nestId, Boolean.TRUE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 关闭充电
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "关闭充电", notes = "关闭充电")
    @NestCodeRecord(DjiDockTopic.CHARGE_CLOSE)
    @PostMapping("/charge/close/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> chargeClose(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.charge(nestId, Boolean.FALSE);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     * 打开图传增强
     *
     * @param nestId
     * @return
     */
    @NestCodeRecord(DjiDockTopic.SWITCH_SDR_WORKMODE_OPEN)
    @PostMapping("/open/sdr/work/mode/{nestId}")
    public Result<Boolean> openSdrWorkMode(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        this.djiCommonService.switchSdrWorkMode(nestId,Boolean.TRUE);
        return Result.ok(true);
    }

    /**
     * 关闭图传增强
     *
     * @param nestId
     * @return
     */
    @NestCodeRecord(DjiDockTopic.SWITCH_SDR_WORKMODE_CLOSE)
    @PostMapping("/close/sdr/work/mode/{nestId}")
    public Result<Boolean> closeSdrWorkMode(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        this.djiCommonService.switchSdrWorkMode(nestId,Boolean.FALSE);
        return Result.ok(true);
    }

    /**
     * 切换直播镜头
     * @param videoType
     * @param nestId
     * @return
     */
    @NestCodeRecord(DjiDockTopic.LIVE_LENS_CHANGE)
    @PostMapping("/charge/live/lens/{videoType}/{nestId}")
    public Result<Boolean> chargeLiveLens(@PathVariable("videoType") Integer videoType,@PathVariable("nestId") @NestId String nestId ) {
        ChargeLiveLensInDTO dto = new ChargeLiveLensInDTO();
        dto.setNestId(nestId);
        dto.setVideoType(videoType);
        this.djiCommonService.chargeLiveLens(dto);
        return Result.ok(true);
    }

    /**
     * 无人机格式化
     * @param nestId
     * @return
     */
    @NestCodeRecord(DjiDockTopic.DRONE_FORMAT)
    @PostMapping("/drone/format/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> droneFormat(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiCommonService.droneFormat(nestId);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }

    /**
     * 限制高度
     * @param height
     * @param nestId
     * @return
     */
    @NestCodeRecord(DjiDockTopic.PROPERTY_SET_LIMIT_HEIGHT)
    @PostMapping("/limit/height/{height}/{nestId}")
    public Result<Boolean> limitHeight(@PathVariable("height") @Valid @NotNull Integer height,@Valid @NotNull @PathVariable("nestId") @NestId String nestId) {

        if(height > 1500 || height < 0) {
            throw new BusinessException("参数height不在范围值（0~1500）内");
        }
        LimitHeightInDTO dto = new LimitHeightInDTO();
        dto.setHeight(height);
        dto.setNestId(nestId);
        this.djiCommonService.limitHeight(dto);
        return Result.ok(true);
    }

    /**
     * 限制距离
     * @param
     * @return
     */
    @NestCodeRecord(DjiDockTopic.PROPERTY_SET_LIMIT_DISTANCE)
    @PostMapping("/limit/distance/{distance}/{nestId}")
    public Result<Boolean> limitDistance(@PathVariable("distance") @Valid @NotNull Integer distance,@PathVariable("nestId") @Valid @NotNull @NestId String nestId) {
        if(distance > 8000 || distance < 0) {
            throw new BusinessException("参数distance不在范围值（0~8000）内");
        }
        LimitDistanceInDTO dto = new LimitDistanceInDTO();
        dto.setDistance(distance);
        dto.setNestId(nestId);
        this.djiCommonService.limitDistance(dto);
        return Result.ok(true);
    }

    /**
     * 空调模式切换
     *
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "空调模式切换", notes = "空调模式切换")
    @NestCodeRecord(DjiDockTopic.COVER_OPEN)
    @PostMapping("/air/conditioner/mode/switch/{nestId}/{mode}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> airConditionerModeSwitch(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空") @NestId String nestId
            , @PathVariable("mode") @Valid @NotNull(message = "mode 不能为空") Integer mode) {
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = new DJICommonResultRespVO.CommonResultRespVO();
        return Result.ok(djiCommonResultRespVO);
    }

}
