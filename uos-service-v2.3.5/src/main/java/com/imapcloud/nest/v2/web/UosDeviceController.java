package com.imapcloud.nest.v2.web;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.UosDeviceService;
import com.imapcloud.nest.v2.web.vo.req.DeviceQueryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DeviceInfoRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 国标设备管理API
 * @author Vastfy
 * @date 2023/03/30 19:11
 * @since 2.3.2
 */
@ApiSupport(author = "wumiao@geoai.com", order = 22)
@Api(value = "国标设备管理API", tags = "国标设备管理API")
@RequestMapping("v2/devices")
@RestController
public class UosDeviceController {

    @Resource
    private UosDeviceService uosDeviceService;

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 1)
    @ApiOperation(value = "查询设备绑定基站列表接口")
    @GetMapping("{deviceCode}/nests")
    public Result<List<String>> queryNestInfo(@PathVariable String deviceCode) {
        List<String> res = uosDeviceService.queryNestInfo(deviceCode);
        return Result.ok(res);
    }

    @ApiOperationSupport(author = "liujiahua@geoai.com", order = 2)
    @ApiOperation(value = "删除未绑定基站设备接口")
    @DeleteMapping("{deviceCode}")
    public Result<Boolean> deleteDevice(@PathVariable String deviceCode) {
        uosDeviceService.deleteDevice(deviceCode);
        return Result.ok(true);
    }
}
