package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.mapper.GridManageMapper;
import com.imapcloud.nest.v2.manager.dataobj.GridManageDO;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import com.imapcloud.nest.v2.service.GridService;
import com.imapcloud.nest.v2.service.dto.in.GridInDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import com.imapcloud.nest.v2.web.transformer.GridRegionTransformer;
import com.imapcloud.nest.v2.web.vo.req.GridReqVO;
import com.imapcloud.nest.v2.web.vo.resp.GridRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname GridRegionController
 * @Description 网格区域Controller
 * @Date 2022/12/05 9:56
 * @Author Carnival
 */

@ApiSupport(author = "liujiahua@geoai.com", order = 1)
@Api(value = "网格区域管理API", tags = "网格区域管理API")
@RequestMapping("v2/grid/region")
@RestController
public class GridController {

    @Resource
    private GridService gridService;

    @Resource
    private GridRegionTransformer gridRegionTransformer;

    @Resource
    private DataAnalysisResultService dataAnalysisResultService;



    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "文件矢量解析")
    @PostMapping("upload")
    public Result<String> uploadGridRegion(@RequestBody MultipartFile file) {
        String res = gridService.uploadGridRegion(file);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "保存网格区域")
    @PostMapping("save")
    public Result<Boolean> saveGridRegion(@RequestBody GridReqVO.RegionReqVO regionReqVO) {
        regionReqVO.getGridManageList().forEach(r -> gridRegionTransformer.transform(r));
        Boolean res = gridService.saveGridRegion(gridRegionTransformer.transform(regionReqVO));
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 3)
    @ApiOperation(value = "修改网格区域")
    @PostMapping("update")
    public Result<Boolean> updateGridRegion(@RequestBody GridReqVO.RegionReqVO regionReqVO, @RequestParam String gridRegionId) {
        regionReqVO.getGridManageList().forEach(r -> gridRegionTransformer.transform(r));
        Boolean res = gridService.updateGridRegion(gridRegionTransformer.transform(regionReqVO), gridRegionId);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 4)
    @ApiOperation(value = "根据单位获取网格区域")
    @GetMapping("/{orgCode}")
    public Result<List<GridRespVO.RegionRespVO>> queryGridRegion(@PathVariable String orgCode) {
        List<GridOutDTO.RegionOutDTO> regionOutDTOS = gridService.queryGridRegion(orgCode);
        List<GridRespVO.RegionRespVO> res = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(regionOutDTOS)) {
            for (GridOutDTO.RegionOutDTO regionOutDTO : regionOutDTOS) {
                List<GridOutDTO.GridManageOutDTO> gridManageList = regionOutDTO.getGridManageList();
                if (!CollectionUtils.isEmpty(gridManageList)) {
                    gridManageList.forEach(r -> gridRegionTransformer.transform(r));
                }
                GridRespVO.RegionRespVO regionRespVO = gridRegionTransformer.transform(regionOutDTO);
                res.add(regionRespVO);
            }
        }
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 5)
    @ApiOperation(value = "根据管理网格获取数据网格")
    @GetMapping("manage")
    public Result<List<GridRespVO.GridDataBatchVO>> queryGridData(@RequestParam List<String> gridManageIds, @RequestParam(required = false) String orgCode) {
        List<GridOutDTO.GridDataBatchDTO> gridDataBatchDTOS = gridService.queryGridData(gridManageIds, orgCode);
        if (CollectionUtils.isEmpty(gridDataBatchDTOS)) {
            return Result.ok();
        }
        List<GridRespVO.GridDataBatchVO> res = gridDataBatchDTOS.stream().map(r -> gridRegionTransformer.transform(r)).collect(Collectors.toList());
        return Result.ok(res);
    }



    @ApiOperationSupport(author = "wmin@geoai.com", order = 6)
    @ApiOperation(value = "获取某一个网格中的所有问题")
    @GetMapping("/list/problems/by/{gridManageId}")
    public Result<Object> listProblemsByGridId(@PathVariable String gridManageId) {
        List<GridOutDTO.ProblemDTO> problemDTOS = dataAnalysisResultService.listProblemsByGridId(gridManageId);
        return Result.ok(problemDTOS);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 7)
    @ApiOperation(value = "管理网格与单位关联")
    @PostMapping("rel/org")
    public Result<String> setGridManageOrgCode(@RequestBody List<GridReqVO.GridManageOrgCodeVO> list) {
        List<GridInDTO.GridManageOrgCodeDTO> collect = list.stream().map(r -> gridRegionTransformer.transform(r)).collect(Collectors.toList());
        String res = gridService.setGridManageOrgCode(collect);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 8)
    @ApiOperation(value = "重置网格")
    @PostMapping("cancel/org")
    public Result<Boolean> cancelGridManageOrgCode(@RequestBody List<String> gridManageIds) {
        gridService.cancelGridManageOrgCode(gridManageIds);
        return Result.ok(true);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 9)
    @ApiOperation(value = "获取管理网格所属单位和航线")
    @GetMapping("get/org")
    public Result<List<GridRespVO.OrgAndTaskRespVO>> queryOrgCodeAndTask(@RequestParam String gridManageId) {
        List<GridOutDTO.OrgAndTaskOutDTO> orgAndTaskOutDTOS = gridService.queryOrgCodeAndTask(gridManageId);
        List<GridRespVO.OrgAndTaskRespVO> res = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(orgAndTaskOutDTOS)) {
            res = orgAndTaskOutDTOS.stream().map(r -> gridRegionTransformer.transform(r)).collect(Collectors.toList());
        }
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 10)
    @ApiOperation(value = "删除管理网格",notes = "可批量删除")
    @PostMapping("del/manage")
    public Result<Boolean> delGridManage(@RequestBody List<String> manageIds) {
        gridService.delGridManage(manageIds);
        return Result.ok(true);
    }


    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 100)
    @ApiOperation(value = "test")
    @GetMapping("test")
    public void test() {

    }
}
