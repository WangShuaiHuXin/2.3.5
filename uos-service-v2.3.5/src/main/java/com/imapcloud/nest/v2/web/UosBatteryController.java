package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosBatteryService;
import com.imapcloud.nest.v2.service.dto.out.BatteryEnableOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BatteryOutDTO;
import com.imapcloud.nest.v2.web.transformer.UosBatteryEnableTransformer;
import com.imapcloud.nest.v2.web.transformer.UosBatteryTransformer;
import com.imapcloud.nest.v2.web.vo.req.BatteryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.BatteryEnableRespVO;
import com.imapcloud.nest.v2.web.vo.resp.BatteryRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosBatteryController.java
 * @Description UosBatteryController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "电池")
@RequestMapping("v2/battery/")
@RestController
public class UosBatteryController {

    @Resource
    private UosBatteryService uosBatteryService;

    @Resource
    private BaseNestService baseNestService;

    /**
     *  停用启用电池
     * @param batteryReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "停用启用电池组", notes = "停用启用电池组")
    @PostMapping("/enable")
    public Result<Boolean> enable(@RequestBody @Valid BatteryReqVO batteryReqVO){
        this.uosBatteryService.enableBatteryGroup(batteryReqVO.getNestId()
                ,batteryReqVO.getGroupId(),batteryReqVO.getEnable());
        return Result.ok();
    }

    /**
     *  获取电池组列表停用启用状态
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "获取电池组停用启用状态", notes = "获取电池组停用启用状态")
    @PostMapping("/getBatteryGroupEnable/{nestId}")
    public Result<List<BatteryEnableRespVO>> getBatteryGroupEnable(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空!") String nestId ){
        List<BatteryEnableOutDTO> batteryEnableOutDTOList =  this.uosBatteryService.getBatteryGroupEnable(nestId);
        List<BatteryEnableRespVO> batteryEnableRespVOList = batteryEnableOutDTOList.stream().map(UosBatteryEnableTransformer.INSTANCES::transform).collect(Collectors.toList());
        return Result.ok(batteryEnableRespVOList);
    }

    /**
     * 获取G503电池使用信息
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "获取G503电池信息", notes = "获取G503电池信息")
    @GetMapping("/get/use/nums/{nestId}")
    public Result<List<BatteryRespVO>> getBatteryUseNums(@PathVariable @Valid @NotNull(message = "不能为空！") String nestId) {
        List<BatteryRespVO> batteryRespVOList = new ArrayList<>();
        List<BatteryOutDTO> batteryOutDTOList = this.uosBatteryService.getBatteryUseNums(nestId);
        batteryRespVOList = batteryOutDTOList.stream()
                .map(UosBatteryTransformer.INSTANCES::transform)
                .collect(Collectors.toList());
        return Result.ok(batteryRespVOList);
    }

}
