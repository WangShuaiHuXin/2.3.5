package com.imapcloud.nest.v2.web;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.service.PowerDataService;
import com.imapcloud.nest.v2.service.PowerMeterDetailService;
import com.imapcloud.nest.v2.service.PowerMeterDataService;
import com.imapcloud.nest.v2.service.dto.in.MeterDataDetailQueryDTO;
import com.imapcloud.nest.v2.service.dto.in.MeterDataQueryDTO;
import com.imapcloud.nest.v2.service.dto.out.AIAutoDiscernSettingsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DiscernFunctionSettingOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDataDetailInfoOutDTO;
import com.imapcloud.nest.v2.web.transformer.PowerDiscernFunctionTransformer;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 电力行业数据分析API
 *
 * @author Vastfy
 * @date 2022/11/25 16:41
 * @since 2.1.5
 */
@ApiSupport(author = "wumiao@geoai.com", order = 11)
@Api(value = "电力行业数据分析API", tags = "电力行业数据分析API")
@RequestMapping("v2/powers")
@RestController
public class PowerDataController {

    @Resource
    private PowerDataService powerDataService;

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Resource
    private PowerMeterDetailService powerMeterDetailService;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = -3)
    @ApiOperation(value = "查询表计数据信息（分页）")
    @GetMapping("meters/data")
    public Result<PageResultInfo<MeterDataRespVO>> queryMeterData(MeterDataQueryReqVO condition) {
        MeterDataQueryDTO transform = PowerDiscernFunctionTransformer.INSTANCE.transform(condition);
        PageResultInfo<MeterDataRespVO> pageInfo = powerMeterDataService.queryMeterData(transform)
                .map(PowerDiscernFunctionTransformer.INSTANCE::transform);
        return Result.ok(pageInfo);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = -2)
    @ApiOperation(value = "获取表计详情设备状态统计数据")
    @GetMapping("meters/data/{dataId}/details/device/stats")
    public Result<List<MeterDeviceStateStatsRespVO>> getMeterDeviceStateStatistics(@PathVariable String dataId) {
        List<MeterDeviceStateStatsRespVO> stats = powerMeterDetailService.getMeterDeviceStateStatistics(dataId)
                .stream()
                .map(PowerDiscernFunctionTransformer.INSTANCE::transform)
                .collect(Collectors.toList());
        return Result.ok(stats);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = -1)
    @ApiOperation(value = "查询表计数据详情信息（分页）")
    @GetMapping("meters/data/{dataId}/details")
    public Result<PageResultInfo<MeterDataDetailRespVO>> queryMeterDataDetails(@PathVariable String dataId,
                                                                               MeterDataDetailQueryReqVO condition) {
        condition.setDataId(dataId);
        MeterDataDetailQueryDTO transform = PowerDiscernFunctionTransformer.INSTANCE.transform(condition);
        transform.setVerificationStatus(condition.getVerificationStatus());
        PageResultInfo<MeterDataDetailRespVO> pageInfo = powerMeterDetailService.queryMeterDataDetails(transform)
                .map(PowerDiscernFunctionTransformer.INSTANCE::transform);
        return Result.ok(pageInfo);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "删除表计读数分析数据详情")
    @DeleteMapping("meters/details")
    public Result<Void> deleteMeterDetails(@RequestBody List<String> meterDetailIds) {
        powerMeterDetailService.deleteMeterDetails(meterDetailIds);
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "修改表计读数信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "meterDetailId", value = "表计读数数据详情ID", paramType = "path", required = true)
    })
    @PatchMapping("meters/details/{meterDetailId}")
    public Result<Void> modifyMeterReadingInfo(@PathVariable String meterDetailId,
                                               @Validated @RequestBody MeterReadingInfoReqVO body) {
        powerMeterDetailService.modifyMeterReadingInfo(meterDetailId, PowerDiscernFunctionTransformer.INSTANCE.transform(body));
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "查询单位是否开启巡检数据自动识别功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgCode", value = "单位编码", paramType = "path", required = true)
    })
    @GetMapping("{orgCode}/ai/auto/settings")
    public Result<AIAutoDiscernSettingsReqVO> getOrgAIAutoDiscernSettings(@PathVariable String orgCode) {
        AIAutoDiscernSettingsOutDTO dto = powerDataService.getOrgAIAutoDiscernSettings(orgCode);
        AIAutoDiscernSettingsReqVO vo = new AIAutoDiscernSettingsReqVO();
        BeanUtils.copyProperties(dto, vo);
        return Result.ok(vo);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 4)
    @ApiOperation(value = "设置单位是否开启巡检数据自动识别功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgCode", value = "单位编码", paramType = "path", required = true)
    })
    @PatchMapping("{orgCode}/ai/auto/settings")
    public Result<Void> modifyOrgAIAutoDiscernSettings(@PathVariable String orgCode) {
        powerDataService.modifyOrgAIAutoDiscernSettings(orgCode);
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 5)
    @ApiOperation(value = "查询单位已配置的识别功能信息列表", notes = "该接口会自动过滤算法侧不存在的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgCode", value = "单位编码", paramType = "path", required = true)
    })
    @GetMapping("{orgCode}/discern/functions/all")
    public Result<List<DiscernFunctionSettingRespVO>> getAllDiscernFunctionInfos(@PathVariable String orgCode) {
        List<DiscernFunctionSettingOutDTO> dto = powerDataService.getAllDiscernFunctionInfos(orgCode);
        List<DiscernFunctionSettingRespVO> respVOS = dto.stream().map(e -> {
            DiscernFunctionSettingRespVO vo = new DiscernFunctionSettingRespVO();
            vo.setDiscernType(e.getDiscernType());
            vo.setLastModifierId(e.getLastModifierId());
            vo.setLastModifierName(e.getLastModifierName());
            vo.setLastModifiedTime(e.getLastModifiedTime());
            List<DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO> discernFunctionInfos = e.getDiscernFunctionInfos();
            if (!CollectionUtils.isEmpty(discernFunctionInfos)) {
                List<DiscernFunctionInfoRespVO> collect = e.getDiscernFunctionInfos().stream().map(item -> {
                    DiscernFunctionInfoRespVO transform = PowerDiscernFunctionTransformer.INSTANCE.transform(item);
                    return transform;
                }).collect(Collectors.toList());
                vo.setDiscernFunctionInfos(collect);
            }
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(respVOS);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 6)
    @ApiOperation(value = "查询单位已配置的指定巡检类型识别功能信息列表", notes = "该接口会自动过滤算法侧不存在的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgCode", value = "单位编码", paramType = "path", required = true),
            @ApiImplicitParam(name = "discernType", value = "巡检类型，取字典``数据项值", paramType = "query", required = true)
    })
    @GetMapping("{orgCode}/discern/functions")
    public Result<List<DiscernFunctionInfoRespVO>> getDiscernFunctionInfos(@PathVariable String orgCode,
                                                                           @Validated @NotNull(message = "识别类型不能为空") @RequestParam Integer discernType) {
        List<DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO> dtos = powerDataService.getDiscernFunctionInfos(orgCode, discernType);
        List<DiscernFunctionInfoRespVO> respVOS = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dtos)) {
            respVOS = dtos.stream().map(e -> {
                DiscernFunctionInfoRespVO vo = new DiscernFunctionInfoRespVO();
                BeanUtils.copyProperties(e, vo);
                return vo;
            }).collect(Collectors.toList());
        }
        return Result.ok(respVOS);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 7)
    @ApiOperation(value = "保存单位配置的识别功能信息")
    @PutMapping("{orgCode}/discern/functions")
    public Result<Void> saveDiscernFunctionInfos(@PathVariable String orgCode,
                                                 @Validated @RequestBody AIDiscernFunctionInfoReqVO body) {
        powerDataService.saveDiscernFunctionInfos(orgCode, body);
        return Result.ok();
    }

    @PostMapping("meters/data/delete")
    public Result<Object> deleteMeterData(@RequestBody List<String> dataIdList) {
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        powerMeterDataService.deleteMeterData(dataIdList, accountId);
        return Result.ok(dataIdList);
    }

    /**
     * AI任务暂停
     *
     * @param dataId 数据id
     * @return {@link Result}<{@link Object}>
     */
    @PostMapping("meters/task/pause/{dataId}")
    public Result<Object> pause(@PathVariable String dataId) {
        powerMeterDataService.taskChange(dataId, PowerTaskStateEnum.TASK_PAUSE);
        return Result.ok();
    }

    /**
     * AI任务取消暂停
     *
     * @param dataId 数据id
     * @return {@link Result}<{@link Object}>
     */
    @PostMapping("meters/task/unpause/{dataId}")
    public Result<Object> unpause(@PathVariable String dataId) {
        powerMeterDataService.taskChange(dataId, PowerTaskStateEnum.TASK_PRE);
        return Result.ok();
    }

    /**
     * AI任务终止
     *
     * @param dataId 数据id
     * @return {@link Result}<{@link Object}>
     */
    @PostMapping("meters/task/stop/{dataId}")
    public Result<Object> stop(@PathVariable String dataId) {
        powerMeterDataService.taskChange(dataId, PowerTaskStateEnum.TASK_STOP);
        return Result.ok();
    }
}
