package com.imapcloud.nest.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.util.I18nMessageUtils;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.annotation.SysLogIgnoreParam;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.enums.NestLogModuleEnum;
import com.imapcloud.nest.pojo.dto.ParseCpsLogDto;
import com.imapcloud.nest.pojo.dto.ParseEmqxLogDto;
import com.imapcloud.nest.pojo.dto.QueryMqttLogFromMongoDto;
import com.imapcloud.nest.service.MqttLogParseService;
import com.imapcloud.nest.service.NestLogService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.web.UosNestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mqtt/log/parse")
public class MqttLogParseController {

    @Autowired
    private NestLogService nestLogService;

    @Autowired
    private MqttLogParseService mqttLogParseService;

    @Trace
    @PostMapping("/update/cps/mqtt/logs/{nestId}")
    public RestRes updateCpsMqttLogs(@PathVariable String nestId ,Integer uavWhich ) {
        if (nestId == null) {
            return RestRes.err(I18nMessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        boolean success = nestLogService.notifyCpsUploadNestLog(nestId, uavWhich, NestLogModuleEnum.SYSTEM_MQTT_TRACE);
        if(success){
            return RestRes.ok(I18nMessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_UPLOAD_LOG_COMMAND_SENT.getContent()));
        }
        return RestRes.err(I18nMessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD_LOG_COMMAND_SENT.getContent()));
    }

    /**
     * @deprecated 2.2.3，使用新接口{@link UosNestController#cpsSyncNestLogs(com.imapcloud.nest.v2.web.vo.req.CpsSyncNestLogReqVO)}替代，将在后续版本删除
     */
    @Deprecated
    @SysLogIgnoreParam
    @PostMapping("/upload/cps/mqtt/log/zip")
    public RestRes uploadCpsMqttLogZip(String nestUuid, Integer uavWhich , MultipartFile logFile) {
        if (nestUuid == null || logFile == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return mqttLogParseService.uploadAndSaveLogZip(nestUuid, logFile);
    }

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    @GetMapping("/list/cps/files/{nestId}")
    public RestRes listCpsFiles(@PathVariable String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return mqttLogParseService.lsCpsLog(nestId);
    }

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    @PostMapping("/parse/cps/log")
    public RestRes parseCpsLog(@RequestBody @Valid ParseCpsLogDto dto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return mqttLogParseService.parseCpsLog(dto.getNestId(), dto.getPathList());
    }

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    @GetMapping("/list/spring/files")
    public RestRes listSpringFiles() {
        return mqttLogParseService.lsSpringLog();
    }

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    @PostMapping("/parse/spring/log")
    public RestRes parseSpringLog(@RequestBody List<String> pathList) {
        if (CollectionUtil.isNotEmpty(pathList)) {
            return mqttLogParseService.parseSpringLog(pathList);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    @PostMapping("/parse/emqx/log")
    public RestRes parseEmqxLog(@RequestBody @Valid ParseEmqxLogDto dto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        if (dto.getAll()) {
            return mqttLogParseService.parseEmqxLogAsync(dto);
        }
        return mqttLogParseService.parseEmqxLog(dto);
    }

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    @GetMapping("/list/mqtt/logs/from/mongo")
    public RestRes listMqttLogsFromMongo(@Valid QueryMqttLogFromMongoDto dto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return mqttLogParseService.listMqttLogsFromMongo(dto);
    }

    /**
     * @deprecated 2.2.3，功能无用户使用，将在后续版本删除
     */
    @Deprecated
    @PostMapping("/clear/mqtt/logs")
    public RestRes clearMqttLogs() {
        return mqttLogParseService.clearMqttLogs();
    }




}
