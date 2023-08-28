package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.UosStreamService;
import com.imapcloud.nest.v2.service.dto.out.StreamOutDTO;
import com.imapcloud.nest.v2.web.transformer.UosStreamTransformer;
import com.imapcloud.nest.v2.web.vo.req.StreamReqVO;
import com.imapcloud.nest.v2.web.vo.resp.StreamRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosStreamController.java
 * @Description UosStreamController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "推流设置")
@RequestMapping("v2/stream/model/")
@RestController
public class UosStreamController {

    @Resource
    private UosStreamService uosStreamService;

    /**
     *  设置推流模式
     * @param streamReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "设置推流模式", notes = "设置推流模式")
    @PostMapping("/setStream")
    public Result<Boolean> setStream(@RequestBody StreamReqVO streamReqVO){
        this.uosStreamService.setStream(streamReqVO.getNestId(),streamReqVO.getMode(),streamReqVO.getUavWhich());
        return Result.ok();
    }

    /**
     *  获取推流模式
     * @param nestId
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 2)
    @ApiOperation(value = "获取推流模式", notes = "获取推流模式")
    @GetMapping("/getStream/{nestId}")
    public Result<StreamRespVO> getStream(@PathVariable("nestId") @Valid @NotNull(message = "nestId 不能为空!") String nestId , Integer uavWhich){
        StreamOutDTO outDTO = this.uosStreamService.getStream(nestId , uavWhich);
        return Result.ok(UosStreamTransformer.INSTANCES.transform(outDTO));
    }

}
