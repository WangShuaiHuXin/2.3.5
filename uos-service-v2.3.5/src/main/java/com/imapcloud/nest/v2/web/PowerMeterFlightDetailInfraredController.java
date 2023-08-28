package com.imapcloud.nest.v2.web;


import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.po.out.PowerMeterFlightDetailInfraredOutDTO;
import com.imapcloud.nest.v2.service.PowerMeterFlightDetailInfraredService;
import com.imapcloud.nest.v2.service.dto.out.PowerMeterInfraredRecordOutDTO;
import com.imapcloud.nest.v2.web.transformer.PowerMeterFlightDetailInfraredTransformer;
import com.imapcloud.nest.v2.web.vo.req.PowerMeterFlightDetailInfraredReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PowerMeterFlightDetailInfraredRespVO;
import com.imapcloud.nest.v2.web.vo.resp.PowerMeterInfraredRecordRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 红外测温-飞行数据详情表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2022-12-28
 */
@ApiSupport(author = "wangmin@geoai.com")
@Api(value = "红外测温详情列表", tags = "红外测温详情列表")
@RestController
@RequestMapping("/power/meter/flight/detail/infrared")
public class PowerMeterFlightDetailInfraredController {

    @Resource
    private PowerMeterFlightDetailInfraredService powerMeterFlightDetailInfraredService;

    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "列表查询接口", notes = "列表查询接口")
    @GetMapping("/list/page")
    public Result<PageResultInfo<PowerMeterFlightDetailInfraredRespVO>> listPage(@Valid PowerMeterFlightDetailInfraredReqVO vo) {
        PageResultInfo<PowerMeterFlightDetailInfraredRespVO> pri = new PageResultInfo();
        pri.setRecords(Collections.emptyList());
        pri.setTotal(0);
        PageResultInfo<PowerMeterFlightDetailInfraredOutDTO> resultInfo = powerMeterFlightDetailInfraredService.listPages(PowerMeterFlightDetailInfraredTransformer.INSTANCE.transform(vo));
        List<PowerMeterFlightDetailInfraredOutDTO> records = resultInfo.getRecords();
        pri.setTotal(resultInfo.getTotal());
        List<PowerMeterFlightDetailInfraredRespVO> voList = Lists.newLinkedList();
        pri.setRecords(voList);
        for (PowerMeterFlightDetailInfraredOutDTO record : records) {
            PowerMeterFlightDetailInfraredRespVO bean = new PowerMeterFlightDetailInfraredRespVO();
            BeanUtils.copyProperties(record, bean);
            voList.add(bean);
        }
        return Result.ok(pri);
    }

    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取红外测温详情接口", notes = "获取红外测温详情接口")
    @GetMapping("/get/details/{detailsId}")
    public Result<List<PowerMeterInfraredRecordRespVO>> getDetails(@PathVariable String detailsId) {

        List<PowerMeterInfraredRecordOutDTO> detailList = powerMeterFlightDetailInfraredService.getResultDetails(detailsId);
        List<PowerMeterInfraredRecordRespVO> resultList = Lists.newArrayList();
        for (PowerMeterInfraredRecordOutDTO outDTO : detailList) {
            PowerMeterInfraredRecordRespVO powerMeterInfraredRecordRespVO = new PowerMeterInfraredRecordRespVO();
            BeanUtils.copyProperties(outDTO, powerMeterInfraredRecordRespVO);
            resultList.add(powerMeterInfraredRecordRespVO);
        }
        return Result.ok(resultList);
    }

    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "批量删除接口", notes = "批量删除接口")
    @DeleteMapping("/batch/delete")
    public Result<Boolean> batchDelete(@RequestBody List<String> detailIdList) {
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        return Result.ok(powerMeterFlightDetailInfraredService.batchDelete(detailIdList, accountId));
    }

    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设备状态统计接口", notes = "设备状态统计接口")
    @GetMapping("/device/state/statistics/{dataId}")
    public Result<Object> deviceStateStatistics(@PathVariable String dataId) {
        return Result.ok(powerMeterFlightDetailInfraredService.deviceStateStatistics(dataId));
    }
}

