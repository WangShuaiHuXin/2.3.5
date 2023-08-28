package com.imapcloud.nest.v2.web;

import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.common.enums.SharedElecfenceStatusEnum;
import com.imapcloud.nest.v2.service.UosElecfenceService;
import com.imapcloud.nest.v2.web.transformer.UosElecfenceTransformer;
import com.imapcloud.nest.v2.web.vo.req.ElecfenceCreationReqVO;
import com.imapcloud.nest.v2.web.vo.req.ElecfenceUpdatedReqVO;
import com.imapcloud.nest.v2.web.vo.resp.ElecfenceInfoRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UOS电子围栏控制层接口
 * @author Vastfy
 * @date 2022/9/23 17:22
 * @since 2.1.0
 */
@ApiSupport(author = "wumiao@geoai.com", order = 10)
@Api(value = "电子围栏管理API（新）", tags = "电子围栏管理API（新）")
@RequestMapping("v2")
@RestController
public class UosElecfenceController {

    @Resource
    private UosElecfenceService uosElecfenceService;

    @Resource
    private UosElecfenceTransformer uosElecfenceTransformer;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "查询单位自定义（专属）电子围栏", notes = "该接口会对访问用户的数据返回进行过滤")
    @ApiImplicitParam(name = "orgCode", value = "单位编码", paramType = "path", required = true)
    @GetMapping("org/{orgCode}/elecfence/exclusive")
    public Result<List<ElecfenceInfoRespVO>> getOrgExclusiveElecfenceInfos(@PathVariable String orgCode){
        List<ElecfenceInfoRespVO> results = uosElecfenceService.getExclusiveElecfenceInfos(orgCode, null, false)
                .stream()
                .map(uosElecfenceTransformer::transform)
                .collect(Collectors.toList());
        return Result.ok(results);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "查询单位共享电子围栏", notes = "该接口会对访问用户的数据返回进行过滤")
    @ApiImplicitParam(name = "orgCode", value = "单位编码", paramType = "path", required = true)
    @GetMapping("org/{orgCode}/elecfence/shared")
    public Result<List<ElecfenceInfoRespVO>> getOrgSharedElecfenceInfos(@PathVariable String orgCode){
        List<ElecfenceInfoRespVO> results = uosElecfenceService.getSharedElecfenceInfos(orgCode)
                .stream()
                .map(uosElecfenceTransformer::transform)
                .collect(Collectors.toList());
        return Result.ok(results);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "查询登录用户可见电子围栏", notes = "该接口会对访问用户的数据返回进行过滤")
    @GetMapping("elecfence/list")
    public Result<List<ElecfenceInfoRespVO>> listUserElecfenceInfos(){
        List<ElecfenceInfoRespVO> results = uosElecfenceService.listVisibleElecfenceInfos()
                .stream()
                .map(uosElecfenceTransformer::transform)
                .collect(Collectors.toList());
        return Result.ok(results);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 4)
    @ApiOperation(value = "新建电子围栏")
    @PostMapping("elecfence")
    public Result<String> createElecfence(@Validated @RequestBody ElecfenceCreationReqVO body){
        String elecfenceId = uosElecfenceService.createElecfence(uosElecfenceTransformer.transform(body));
        return Result.ok(elecfenceId);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 5)
    @ApiOperation(value = "修改电子围栏")
    @ApiImplicitParam(name = "elecfenceId", value = "电子围栏ID", paramType = "path", required = true)
    @PutMapping("elecfence/{elecfenceId}")
    public Result<Void> modifyElecfence(@PathVariable Long elecfenceId, @RequestBody ElecfenceUpdatedReqVO body){
        uosElecfenceService.modifyElecfence(elecfenceId.toString(), uosElecfenceTransformer.transform(body));
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 6)
    @ApiOperation(value = "删除电子围栏")
    @ApiImplicitParam(name = "elecfenceId", value = "电子围栏ID", paramType = "path", required = true)
    @DeleteMapping("elecfence/{elecfenceId}")
    public Result<Void> deleteElecfence(@PathVariable Long elecfenceId){
        uosElecfenceService.deleteElecfence(elecfenceId.toString());
        return Result.ok();
    }

    /**
     * 该接口在2.1.0版本新增，设计评审阶段被砍掉，后续可能会使用
     */
    @ApiOperationSupport(author = "wumiao@geoai.com", order = 7)
    @ApiOperation(value = "启/禁用指定单位的共享电子围栏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgCode", value = "单位编码", paramType = "path", required = true),
            @ApiImplicitParam(name = "elecfenceId", value = "共享电子围栏ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "status", value = "开启：on；关闭：off", paramType = "path", required = true)
    })
    @PatchMapping("org/{orgCode}/elecfence/{elecfenceId}/shared/{status:on|off}")
    public Result<Void> setOrgSharedElecfenceStatus(@PathVariable String orgCode,
                                                    @PathVariable Long elecfenceId,
                                                    @PathVariable String status){
        Optional<SharedElecfenceStatusEnum> optional = SharedElecfenceStatusEnum.findMatch(status);
        if(!optional.isPresent()){
            throw new BizParameterException("共享电子围栏启禁用状态错误");
        }
        uosElecfenceService.modifyOrgElecfenceStatus(orgCode, elecfenceId.toString(), optional.get().getStatus());
        return Result.ok();
    }

}
