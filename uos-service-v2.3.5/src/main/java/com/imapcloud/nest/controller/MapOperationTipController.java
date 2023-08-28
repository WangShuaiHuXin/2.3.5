package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.service.MapOperationTipService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 航线编辑地图操作提示 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2022-05-24
 */
@ApiSupport(author = "liujiahua@geoai.com", order = 1)
@Api(value = "航线编辑地图操作提示 服务实现类", tags = "航线编辑地图操作提示 服务实现类")
@RestController
@RequestMapping("/map/operation/tip")
public class MapOperationTipController {

    @Autowired
    private MapOperationTipService mapOperationTipService;

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "更新或保持航线编辑地图操作提示")
    @PostMapping("/switch/{enable}")
    public RestRes switchMapOperationTip(@PathVariable Integer enable) {
        if (enable == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return mapOperationTipService.switchMapOperationTip(enable);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "获取航线编辑地图操作提示")
    @GetMapping("/get/state")
    public RestRes getMapOperationTip() {
        return mapOperationTipService.getMapOperationTip();
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 3)
    @ApiOperation(value = "获取网格航线编辑地图操作提示")

    @PostMapping("/switch/grid/{enable}")
    public RestRes switchGridOperationTip(@PathVariable Integer enable) {
        if (enable == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return mapOperationTipService.switchGridOperationTip(enable);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 4)
    @ApiOperation(value = "更新或保存网格航线编辑地图操作提示")
    @GetMapping("/get/grid/state")
    public RestRes getGridOperationTip() {
            return mapOperationTipService.getGridOperationTip();
    }
}

