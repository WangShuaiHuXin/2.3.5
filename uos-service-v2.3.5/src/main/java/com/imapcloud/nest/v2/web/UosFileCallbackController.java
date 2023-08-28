package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.v2.service.FileCallbackHandleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 文件服务回调管理API
 * @author Vastfy
 * @date 2023/2/27 11:00
 * @since 2.2.3
 */
@ApiSupport(author = "wumiao@geoai.com", order = 18)
@Api(value = "文件服务回调管理API", tags = "文件服务回调管理API")
@RequestMapping
@RestController
public class UosFileCallbackController {

    @Resource
    private FileCallbackHandleService fileCallbackHandleService;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "文件服务回调处理")
    @PostMapping("v2/files/callback")
    public Result<Void> callbackHandle(@RequestBody String body) {
        fileCallbackHandleService.handleFileCallback(body);
        return Result.ok();
    }



}
