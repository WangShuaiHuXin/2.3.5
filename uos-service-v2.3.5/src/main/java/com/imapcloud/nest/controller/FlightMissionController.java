package com.imapcloud.nest.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.SysLogIgnoreParam;
import com.imapcloud.nest.pojo.dto.flightMission.FlightMissionDTO;
import com.imapcloud.nest.service.FlightMissionService;
import com.imapcloud.nest.utils.RestRes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "飞行架次记录", tags = "飞行架次记录")
@RestController
@RequestMapping("/air/flight/mission")
public class FlightMissionController {

    @Resource
    private FlightMissionService flightMissionService;

    /**
     *  分页查询
     * @param page
     * @param limit
     * @param ascList
     * @param descList
     * @param startTime
     * @param endTime
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "停用启用电池组", notes = "停用启用电池组")
    @GetMapping("/query/{nestId}")
    @ResponseBody
    @SysLogIgnoreParam(value = "飞行记录分页查询，参数nestID page limit asc desc")
    public RestRes queryFightMissionPage(@PathVariable("nestId") @NotNull(message = "{geoai_uos_base_station_Id_cannot_be_empty_clear_the_check}") String nestId
            , @RequestParam Integer page, @RequestParam Integer limit
            , @RequestParam  String ascList, @RequestParam  String descList
            , @RequestParam String startTime, @RequestParam String endTime
            , @RequestParam @LimitVal(values = {"0","1","2","3"}, message = "which值不在范围内") Integer uavWhich) {
        return this.flightMissionService.queryFlightMissionPage(nestId,page,limit,ascList,descList,startTime,endTime , uavWhich);
    }

    /**
     *  飞行架次删除接口
     * @param flightIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "停用启用电池组", notes = "停用启用电池组")
    @GetMapping("/del/{flightIds}")
    @ResponseBody
    @SysLogIgnoreParam(value = "飞行记录删除，参数flightIds")
    public RestRes deleteBatchRecord(@PathVariable("flightIds") String flightIds) {
        return this.flightMissionService.deleteBatchRecord(flightIds);
    }


    /**
     *  同步飞行架次记录
     * @param fightMissionDTO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "停用启用电池组", notes = "停用启用电池组")
    @PostMapping("/sync")
    @ResponseBody
    @SysLogIgnoreParam(value = "飞行架次同步，参数")
    public RestRes syncFlightMission(@RequestBody @Valid FlightMissionDTO fightMissionDTO) {
        return this.flightMissionService.syncFlightMission(fightMissionDTO);
    }


}

