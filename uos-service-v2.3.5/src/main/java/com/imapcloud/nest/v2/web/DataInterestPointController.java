package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.DataInterestPointService;
import com.imapcloud.nest.v2.service.dto.out.DataInterestPointOutDTO;
import com.imapcloud.nest.v2.web.transformer.DataInterestPointTransformer;
import com.imapcloud.nest.v2.web.vo.req.DataInterestPointPageReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataInterestPointReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataInterestPointRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisCenter.java
 * @Description DataAnalysisCenterController
 * @createTime 2022年07月08日 17:03:00
 */
@ApiSupport(author = "zhongtaigbao@geoai.com", order = 2)
@Api(value = "数据管理-兴趣-兴趣点", tags = "数据管理-兴趣-兴趣点")
@RequestMapping("v2/data/interest")
@RestController
public class DataInterestPointController {

    @Resource
    private DataInterestPointService dataInterestPointService;

    @Resource
    private DataInterestPointTransformer dataInterestPointTransformer;

    /**
     * 兴趣点-新增
     * @param dataInterestPointReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "兴趣点新增", notes = "兴趣点新增")
    @PostMapping("/point/add")
    public Result<Boolean> addPoint(@RequestBody DataInterestPointReqVO dataInterestPointReqVO){
        Boolean res = dataInterestPointService.addPoint(dataInterestPointTransformer.transform(dataInterestPointReqVO));
        return Result.ok(res);
    }

    /**
     * 兴趣点-批量删除
     * @param pointIds
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "兴趣点-批量删除任务", notes = "兴趣点-批量删除任务")
    @DeleteMapping("/point/deleteBatch")
    public Result<Boolean> deletePoints(@RequestParam @Valid @NotNull(message = "{geoai_uos_cannot_empty_detailids}") List<String> pointIds){
        Boolean res = dataInterestPointService.deletePoints(pointIds);
        return Result.ok(res);
    }

    /**
     * 兴趣点-修改
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 3)
    @ApiOperation(value = "兴趣点修改", notes = "兴趣点修改")
    @PutMapping("/point/update/{pointId}")
    public Result<Boolean> updatePoint(@PathVariable String pointId,
                                       @RequestBody DataInterestPointReqVO dataInterestPointReqVO){
        Boolean res = dataInterestPointService.updatePoint(pointId, dataInterestPointTransformer.transform(dataInterestPointReqVO));
        return Result.ok(res);
    }

    /**
     *  兴趣点-分页查询
     * @param dataInterestPointPageReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "兴趣点-分页查询", notes = "兴趣点-分页查询功能")
    @GetMapping("/point/queryPage")
    public Result<PageResultInfo<DataInterestPointRespVO>> queryPointPage(DataInterestPointPageReqVO dataInterestPointPageReqVO){
        PageResultInfo<DataInterestPointOutDTO> pageResult = dataInterestPointService.queryPointPage(dataInterestPointTransformer.transform(dataInterestPointPageReqVO));
        PageResultInfo<DataInterestPointRespVO> res = pageResult.map(r -> dataInterestPointTransformer.transform(r));
        return Result.ok(res);
    }

    /**
     *  兴趣点-全量查询
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "兴趣点-全量查询-返回字段较少", notes = "兴趣点-全量查询-根据单位查询")
    @GetMapping("/point/queryAll")
    public Result<List<DataInterestPointRespVO>> queryAllPoints(@RequestParam  String orgCode,
                                                                @RequestParam(required = false) String pointName,
                                                                @RequestParam(required = false) String tagId){
        List<DataInterestPointOutDTO> listResult = dataInterestPointService.queryAllPoints(orgCode, pointName, tagId);
        List<DataInterestPointRespVO> res = listResult.stream()
                .map(r -> dataInterestPointTransformer.transform(r))
                .collect(Collectors.toList());
        return Result.ok(res);
    }


    /**
     *  兴趣点-指定查询-返回字段较全
     * @param pointId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "兴趣点-指定查询-返回字段较全", notes = "兴趣点-指定查询-返回字段较全")
    @GetMapping("/point/query/{pointId}")
    public Result<DataInterestPointRespVO> queryPoint(@PathVariable String pointId){
        DataInterestPointOutDTO dtoResult = dataInterestPointService.queryPoint(pointId);
        DataInterestPointRespVO res = dataInterestPointTransformer.transform(dtoResult);
        return Result.ok(res);
    }

}
