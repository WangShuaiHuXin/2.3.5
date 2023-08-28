package com.imapcloud.nest.v2.web;

import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.common.enums.MaplayerDisplayStatusEnum;
import com.imapcloud.nest.v2.common.enums.MaplayerPreloadStatusEnum;
import com.imapcloud.nest.v2.service.UosMaplayerService;
import com.imapcloud.nest.v2.web.transformer.UosMaplayerTransformer;
import com.imapcloud.nest.v2.web.vo.req.MaplayerQueryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.MaplayerInfoRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UOS地图图层控制层接口
 * @author Vastfy
 * @date 2022/9/23 17:22
 * @since 2.1.0
 */
@ApiSupport(author = "wumiao@geoai.com", order = 11)
@Api(value = "地图图层管理API（新）", tags = "地图图层管理API（新）")
@RequestMapping("v2")
@RestController
public class UosMaplayerController {

    @Resource
    private UosMaplayerService uosMaplayerService;

    @Resource
    private UosMaplayerTransformer uosMaplayerTransformer;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "设置单位对指定图层是否可见", notes = "managed=true时，代表上级单位用户在操作图层数据，即设置图层所属单位的用户查看地图时是否对该地图可见")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maplayerId", value = "地图图层ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "status", value = "开启：on；关闭：off", paramType = "path", required = true),
            @ApiImplicitParam(name = "managed", value = "是否管理图层【默认值：false】", paramType = "query", defaultValue = "false")
    })
    @PatchMapping("maplayers/{maplayerId}/display/{status:on|off}")
    public Result<Void> setUserOrgMaplayerDisplayStatus(@PathVariable Long maplayerId, @PathVariable String status,
                                                        @RequestParam(required = false, defaultValue = "false") Boolean managed){
        Optional<MaplayerDisplayStatusEnum> optional = MaplayerDisplayStatusEnum.findMatch(status);
        if(!optional.isPresent()){
            throw new BizParameterException("图层展示状态错误");
        }
        uosMaplayerService.setUserOrgMaplayerDisplayStatus(maplayerId, optional.get().getStatus(), managed);
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "设置单位对指定图层是否预加载", notes = "managed=true时，代表上级单位用户在操作图层数据，即设置图层所属单位的用户查看地图时是否预加载该图层")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maplayerId", value = "地图图层ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "status", value = "开启：on；关闭：off", paramType = "path", required = true),
            @ApiImplicitParam(name = "managed", value = "是否管理图层【默认值：false】", paramType = "query", defaultValue = "false")
    })
    @PatchMapping("maplayers/{maplayerId}/preload/{status:on|off}")
    public Result<Void> setUserOrgMaplayerPreloadStatus(@PathVariable Long maplayerId, @PathVariable String status,
                                                        @RequestParam(required = false, defaultValue = "false") Boolean managed){
        Optional<MaplayerPreloadStatusEnum> optional = MaplayerPreloadStatusEnum.findMatch(status);
        if(!optional.isPresent()){
            throw new BizParameterException("图层预加载状态错误");
        }
        uosMaplayerService.setUserOrgMaplayerPreloadStatus(maplayerId, optional.get().getStatus(), managed);
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "查询用户可见的图层数据", notes = "该接口支持对单位进行过滤")
    @GetMapping("maplayers/list")
    public Result<List<MaplayerInfoRespVO>> fetchVisibleMaplayerInfos(MaplayerQueryReqVO condition){
        List<MaplayerInfoRespVO> results = uosMaplayerService.fetchVisibleMaplayerInfos(uosMaplayerTransformer.transform(condition))
                .stream()
                .map(uosMaplayerTransformer::transform)
                .collect(Collectors.toList());
        return Result.ok(results);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 4)
    @ApiOperation(value = "查询用户可见的已展示的图层数据")
    @ApiImplicitParam(name = "type", value = "图层类型", paramType = "query", required = true)
    @GetMapping("maplayers/list/display")
    public Result<List<MaplayerInfoRespVO>> listUserOrgDisplayedMaplayerInfos(@RequestParam(required = false) Integer type) {
        List<MaplayerInfoRespVO> results = uosMaplayerService.listDisplayedMaplayerInfos(type)
                .stream()
                .map(uosMaplayerTransformer::transform)
                .collect(Collectors.toList());
        return Result.ok(results);
    }

}
