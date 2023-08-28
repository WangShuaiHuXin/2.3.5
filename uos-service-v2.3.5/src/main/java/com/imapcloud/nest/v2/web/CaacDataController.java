package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.CaacDataService;
import com.imapcloud.nest.v2.service.dto.out.CaacCloudUavOutDTO;
import com.imapcloud.nest.v2.web.transformer.UosUavCodeTransformer;
import com.imapcloud.nest.v2.web.vo.resp.CaacCloudUavRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 民航局数据API接口
 * @author Vastfy
 * @date 2023/03/08 15:22
 * @since 2.2.5
 */
@ApiSupport(author = "wumiao@geoai.com", order = 19)
@Api(value = "民航局数据API", tags = "民航局数据API")
@RequestMapping("v2/caac")
@RestController
public class CaacDataController {

    @Resource
    private CaacDataService caacDataService;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "获取云端无人机信息", notes = "该接口数据会定时进行更新")
    @GetMapping("uav")
    public Result<List<CaacCloudUavRespVO>> listCloudUav(){
        List<CaacCloudUavOutDTO> data = caacDataService.listCloudUav();
        return Result.ok(UosUavCodeTransformer.INSTANCES.transform(data));
    }


}
