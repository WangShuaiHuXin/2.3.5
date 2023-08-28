package com.imapcloud.nest.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.util.I18nMessageUtils;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.SysLogIgnoreParam;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.enums.NestLogModuleEnum;
import com.imapcloud.nest.pojo.dto.NestLogsDto;
import com.imapcloud.nest.service.NestLogService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.web.UosNestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 基站日志表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2021-06-01
 */
@ApiSupport(author = "wangmin@geoai.com", order = 1)
@Api(value = "基站日志表", tags = "基站日志表")
@Slf4j
@RestController
@RequestMapping("/nest/logs")
public class NestLogsController {

    @Resource
    private NestLogService nestLogService;

    @Trace
    @GetMapping("/update/nest/logs")
    public RestRes updateNestLogs(String nestId, Integer module ,@Valid @NotNull(message = "uavWhich 不能为空！") Integer uavWhich) {
        if (nestId != null && module != null) {
            boolean success = nestLogService.notifyCpsUploadNestLog(nestId, uavWhich, NestLogModuleEnum.getInstance(module));
            if(success){
                return RestRes.ok(I18nMessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_UPLOAD_LOG_COMMAND_SENT.getContent()));
            }
            return RestRes.err(I18nMessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD_LOG_COMMAND_SENT.getContent()));
        }
        return RestRes.err(I18nMessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * @deprecated 2.2.3，使用新接口{@link UosNestController#cpsSyncNestLogs(com.imapcloud.nest.v2.web.vo.req.CpsSyncNestLogReqVO)}替代，将在后续版本删除
     */
    @Deprecated
    @ApiOperationSupport(author = "wangmin@geoai.com", order = 1)
    @ApiOperation(value = "上传日志", notes = "上传日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nestUuid", value = "基站uuid", required = true, dataType = "String", paramType = "Query"),
            @ApiImplicitParam(name = "uavWhich", value = "无人机标识 0：G503外其他基站， 1、2、3：标识G503的三个机位", required = true, dataType = "Integer", paramType = "Query"),
    })
    @PostMapping("/upload/nest/logs")
    @SysLogIgnoreParam
    public RestRes uploadNestLogs(String nestUuid, Integer uavWhich, MultipartFile logFile) {
        log.info("已经进入方法uploadNestLogs - nestUuid -> {} , uavWhich -> {}",nestUuid,uavWhich);
        if (nestUuid != null && logFile != null) {
            return nestLogService.uploadAndParseNestLogZip(nestUuid,uavWhich, logFile);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @PostMapping("/clear/nest/log/{nestId}")
    public RestRes clearNestLog(@PathVariable String nestId) {
        if (nestId != null) {
            return nestLogService.clearNestLog(nestId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));
    }

    @GetMapping("/list/nest/logs")
    public RestRes listNestLogs(@Valid NestLogsDto nestLogsDto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_VERIFICATION_PARAM.getContent()));
        }
        return nestLogService.listNestLogs(nestLogsDto);
    }

    @DeleteMapping("/batch/del/logs")
    public RestRes batchDelLogs(@RequestBody List<Integer> logIdList) {
        if (CollectionUtil.isNotEmpty(logIdList)) {
            return nestLogService.batchDelLogs(logIdList);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

}

