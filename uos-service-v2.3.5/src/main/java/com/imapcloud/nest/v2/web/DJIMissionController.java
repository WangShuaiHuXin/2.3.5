package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.v2.service.DJIMissionService;
import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;
import com.imapcloud.nest.v2.web.transformer.DJICommonResultTransformer;
import com.imapcloud.nest.v2.web.vo.resp.DJICommonResultRespVO;
import com.imapcloud.sdk.pojo.constant.DjiDockTopic;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosCommonFlightController.java
 * @Description UosCommonFlightController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "大疆机场任务控制")
@RequestMapping("v2/dji/mission")
@RestController
public class DJIMissionController {

    @Resource
    private DJIMissionService djiMissionService;

    /**
     * 取消任务下发
     * @param nestId
     * @param flightIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "取消任务下发", notes = "取消任务下发")
    @NestCodeRecord(DjiDockTopic.DEBUG_MODE_OPEN)
    @PostMapping("/flight/undo/{nestId}")
    public Result<DJICommonResultRespVO.CommonResultRespVO> flightTaskUndo(@PathVariable("nestId") @NestId String nestId , @RequestBody List<String> flightIds){
        DJICommonResultOutDTO.CommonResultOutDTO djiCommonResultOutDTO = this.djiMissionService.flightTaskUndo(nestId,flightIds);
        DJICommonResultRespVO.CommonResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(djiCommonResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }

}
