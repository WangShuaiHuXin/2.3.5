package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.UosRegionService;
import com.imapcloud.nest.v2.service.dto.in.UosRegionPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionQueryInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionSimpleOutDTO;
import com.imapcloud.nest.v2.web.transformer.UosRegionTransformer;
import com.imapcloud.nest.v2.web.vo.req.UosRegionCreationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosRegionModificationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosRegionPageReqVO;
import com.imapcloud.nest.v2.web.vo.resp.UosRegionQueryRespVO;
import com.imapcloud.nest.v2.web.vo.resp.UosRegionSimpleRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname UosRegionController
 * @Description 区域管理API
 * @Date 2022/8/11 9:56
 * @Author Carnival
 */

@ApiSupport(author = "liujiahua@geoai.com", order = 1)
@Api(value = "区域管理API（新）", tags = "区域管理API（新）")
@RequestMapping("v2/regions")
@RestController
public class UosRegionController {

    @Resource
    private UosRegionTransformer uosRegionTransformer;

    @Resource
    private UosRegionService uosRegionService;

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "新建区域")
    @PostMapping()
    public Result<String> addRegion(@Validated @RequestBody UosRegionCreationReqVO regionCreationReqVO) {
        String RegionId = uosRegionService.addRegion(uosRegionTransformer.transform(regionCreationReqVO));
        return Result.ok(RegionId);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "删除区域")
    @DeleteMapping("/{regionId}")
    public Result<Boolean> deleteRegion(@PathVariable String regionId) {
        boolean result = uosRegionService.deleteRegion(regionId);
        return Result.ok(result);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 3)
    @ApiOperation(value = "删除批量区域")
    @DeleteMapping()
    public Result<List<String>> deleteBatchRegion(@Validated @RequestBody List<String> regionIds) {
        List<String> strings = uosRegionService.deleteBatchRegion(regionIds);
        return Result.ok(strings);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 4)
    @ApiOperation(value = "修改区域信息")
    @PutMapping("/{regionId}")
    public Result<Boolean> modifyRegionInfo(@PathVariable String regionId,@Validated @RequestBody UosRegionModificationReqVO regionModificationReqVO) {
        Boolean result = uosRegionService.modifyRegionInfo(regionId, uosRegionTransformer.transform(regionModificationReqVO));
        return Result.ok(result);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 5)
    @ApiOperation(value = "查询区域详细信息")
    @GetMapping("/{regionId}")
    public Result<UosRegionQueryRespVO> queryRegionInfo(@PathVariable String regionId) {
        UosRegionQueryRespVO vo = uosRegionTransformer.transform(uosRegionService.queryRegionInfo(regionId));
        return Result.ok(vo);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 6)
    @ApiOperation(value = "分页查询区域列表", notes = "支持区域名称模糊搜索")
    @GetMapping("page")
    public Result<PageResultInfo<UosRegionQueryRespVO>> pageRegionList(UosRegionPageReqVO regionQueryReqVO) {
        UosRegionPageInDTO regionQueryInDTO = uosRegionTransformer.transform(regionQueryReqVO);
        PageResultInfo<UosRegionQueryInfoOutDTO> pageResult = uosRegionService.pageRegionList(regionQueryInDTO);
        PageResultInfo<UosRegionQueryRespVO> pr = pageResult.map(r -> uosRegionTransformer.transform(r));
        return Result.ok(pr);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 7)
    @ApiOperation(value = "查询全部区域的简要信息")
    @GetMapping("list/simple")
    public Result<List<UosRegionSimpleRespVO>> listRegionSimpleInfo() {
        List<UosRegionSimpleOutDTO> dtoList = uosRegionService.listRegionSimpleInfo();
        List<UosRegionSimpleRespVO> voList = dtoList.stream()
                .map(r -> uosRegionTransformer.transform(r))
                .collect(Collectors.toList());
        return Result.ok(voList);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 8)
    @ApiOperation(value = "分页查询单位的简要信息")
    @GetMapping("page/simple")
    public Result<PageResultInfo<UosRegionSimpleRespVO>> pageRegionSimpleInfo(UosRegionPageReqVO regionPageReqVO) {
        UosRegionPageInDTO regionPageInDTO = uosRegionTransformer.transform(regionPageReqVO);
        PageResultInfo<UosRegionSimpleOutDTO> pageResultInfo = uosRegionService.pageRegionSimpleInfo(regionPageInDTO);
        PageResultInfo<UosRegionSimpleRespVO> resultInfo = pageResultInfo.map(r -> uosRegionTransformer.transform(r));
        return Result.ok(resultInfo);
    }
}
