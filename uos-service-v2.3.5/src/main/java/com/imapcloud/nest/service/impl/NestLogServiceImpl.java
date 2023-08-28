package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.I18nMessageUtils;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.NestLogModuleEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.mapper.NestLogMapper;
import com.imapcloud.nest.model.NestLogEntity;
import com.imapcloud.nest.pojo.dto.NestLogsDto;
import com.imapcloud.nest.service.NestLogService;
import com.imapcloud.nest.utils.MinioSavingUtil;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.nestLogsUtil.NestLogsParseUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.NestLogConfig;
import com.imapcloud.nest.v2.common.properties.OssConfig;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.in.CpsSyncNestLogInDTO;
import com.imapcloud.sdk.manager.*;
import com.imapcloud.sdk.manager.system.SystemManagerCf;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.entity.NestLogUploadState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * 基站日志表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2021-06-22
 */
@Slf4j
@Service
public class NestLogServiceImpl extends ServiceImpl<NestLogMapper, NestLogEntity> implements NestLogService {

    @Resource(name = "nestLogExecutorService")
    private ExecutorService nestLogExecutorService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Override
    public RestRes sendUploadFileOrder2Nest(String nestId, NestLogModuleEnum module, String uploadUrl, String filename, Integer uavWhich) {
//        String nestUuid = nestService.getUuidById(nestId);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String language = request.getHeader("Accept-Language");
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            if (!cm.getNestLinked(AirIndexEnum.getInstance(uavWhich)) || !cm.getMqttLinked()) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_IS_OFFLINE_AND_CANNOT_UPLOAD_LOGS.getContent()));
            }
            MqttResult<NullParam> res = cm.getSystemManagerCf().uploadLogFile(module.getValue(), uploadUrl, filename, AirIndexEnum.getInstance(uavWhich));
            if (res.isSuccess()) {
                cm.getBaseManager().listenNestLogUploadState((nestLogUploadState, isSuccess, errMsg) -> {
                    if (isSuccess) {
                        if (nestLogUploadState != null) {
                            sendNestLogsUploadProgress(nestUuid, nestLogUploadState, language);
                        }
                    }
                }, AirIndexEnum.getInstance(uavWhich));
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_UPLOAD_LOG_COMMAND_SENT.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD_LOG_COMMAND_SENT.getContent()));
    }

    @Override
    public boolean notifyCpsUploadNestLog(String nestId, Integer uavWhich, NestLogModuleEnum module) {
        Locale locale = LocaleContextHolder.getLocale();
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            if (!cm.getNestLinked(AirIndexEnum.getInstance(uavWhich)) || !cm.getMqttLinked()) {
                throw new BizException(I18nMessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_IS_OFFLINE_AND_CANNOT_UPLOAD_LOGS.getContent()));
            }
            // 保证向前兼容性，将在后续某个时间删除该兼容性
            String serverAddress = geoaiUosProperties.getNestLog().getUploadUrl();
            String filename = module.getName() + "log";
//            if(Objects.equals(module, NestLogModuleEnum.SYSTEM_MQTT_TRACE)){
//                serverAddress = geoaiUosProperties.getCpsLog().getUploadUrl();
//                filename = geoaiUosProperties.getCpsLog().getZipName();
//            }
            // v2版实现
            OssConfig ossConfig = geoaiUosProperties.getOss();
            UploadV2Param uploadV2Param = new UploadV2Param();
            uploadV2Param.setV2SmallFileUploadUrl(ossConfig.getUploadUrl());
            uploadV2Param.setV2ChunkInitUrl(ossConfig.getChunkInitUrl());
            uploadV2Param.setV2ChunkUploadUrl(ossConfig.getChunkUploadUrl());
            uploadV2Param.setV2ChunkSize(ossConfig.getChunkSize().toBytes());
            uploadV2Param.setV2AuthUrl(ossConfig.getAuthUrl());
            NestLogConfig nestLogConfig = geoaiUosProperties.getNestLog();
            uploadV2Param.setV2ChunkUpload(nestLogConfig.getReduce());
            uploadV2Param.setV2ChunkSyncUrl(nestLogConfig.getCallbackUrl());
            if(log.isDebugEnabled()){
                log.debug("上传日志参数 ==> {}", uploadV2Param);
            }
            MqttResult<NullParam> res = cm.getSystemManagerCf().uploadLogFile(module.getValue(), serverAddress, filename, uploadV2Param, AirIndexEnum.getInstance(uavWhich));
            if (res.isSuccess()) {
                cm.getBaseManager().listenNestLogUploadState((nestLogUploadState, isSuccess, errMsg) -> {
                    if (isSuccess) {
                        if (nestLogUploadState != null) {
                            sendNestLogsUploadProgress(nestUuid, nestLogUploadState, locale);
                        }
                    }
                }, AirIndexEnum.getInstance(uavWhich));
                return true;
            }
        }
        return false;
    }

    @Override
    public RestRes uploadAndParseNestLogZip(String nestUuid, Integer uavWhich, MultipartFile logFile) {
        if (logFile != null) {
            NestLogsParseUtil.NestLogZip nestLogZip = NestLogsParseUtil.nestLogZip(logFile);
            nestLogExecutorService.execute(() -> {
                log.info("已经进入方法uploadNestLogs，开始将zip包上传到minio");
                String saveUrl = null;
                try {
                    saveUrl = NestLogsParseUtil.uploadZip2minio(nestUuid, nestLogZip);
                    log.info("已经进入方法uploadNestLogs，上传到minio完成");
//                Integer nestId = nestService.getIdByUuid(nestUuid);
                    String nestId = baseNestService.getNestIdByNestUuid(nestUuid);
                    if (nestId == null) {
                        log.error("查询不到对应的机巢");
                    }
                    String originalFilename = logFile.getOriginalFilename();
                    NestLogEntity nestLogEntity = new NestLogEntity();
                    nestLogEntity.setBaseNestId(nestId)
                            .setName(originalFilename)
                            .setFolder(!originalFilename.contains("-") ? originalFilename.substring(0, originalFilename.indexOf(".")) : originalFilename.substring(0, originalFilename.indexOf("-")))
                            .setUploadTime(LocalDateTime.now())
                            .setUrl(saveUrl)
                            .setUavWhich(uavWhich);
                    boolean save = this.save(nestLogEntity);
                    log.info("已经进入方法uploadNestLogs，将zip包相关数据保存到mysql");
                    if (save) {
                        log.info("日志上传成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    MinioSavingUtil.cancelSf(nestUuid);
                    MinioSavingUtil.sendMinioSavingRunnable(nestUuid, NestLogUploadState.StateEnum.MINIO_SAVE_COMPLETE);
                }
            });
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_LOGS_UPLOADED.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_LOGS_UPLOADED.getContent()));
    }

    @Override
    public String saveNestLogStorageInfo(CpsSyncNestLogInDTO data) {
        Optional<NestLogModuleEnum> optional = NestLogModuleEnum.findMatch(data.getModule());
        if(!optional.isPresent()){
            throw new BizParameterException("基站日志模块不存在");
        }
        String nestId = baseNestService.getNestIdByNestUuid(data.getNestUuid());
        if (!StringUtils.hasText(nestId)) {
            throw new BizException("基站信息不存在");
        }
        NestLogEntity nestLogEntity = new NestLogEntity();
        nestLogEntity.setBaseNestId(nestId)
                .setName(data.getLogFilename())
                .setFolder(data.getModule())
                .setUploadTime(LocalDateTime.now())
                .setUrl(data.getStorageUrl())
                .setUavWhich(data.getWhich());
        this.save(nestLogEntity);
        return nestLogEntity.getId().toString();
    }

    @Override
    public RestRes clearNestLog(String nestId) {
//        String uuid = nestService.getUuidById(nestId);
        String uuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm != null) {
            SystemManagerCf systemManagerCf = cm.getSystemManagerCf();
            MqttResult<NullParam> res = systemManagerCf.clearNestLog();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CLEAR_THE_NEST_LOG.getContent()));
            } else {
                return RestRes.err(res.getMsg());
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_ONLINE_NEST.getContent()));
    }

    @Override
    public RestRes listNestLogs(NestLogsDto nestLogsDto) {
        Page<NestLogEntity> page = new Page<>(nestLogsDto.getCurrentPage(), nestLogsDto.getPageSize());

        LambdaQueryWrapper<NestLogEntity> select = new QueryWrapper<NestLogEntity>().lambda()
                .eq(NestLogEntity::getBaseNestId, nestLogsDto.getNestId())
                .eq(Objects.nonNull(nestLogsDto.getUavWhich()), NestLogEntity::getUavWhich, nestLogsDto.getUavWhich())
                .orderByDesc(NestLogEntity::getUploadTime);

        if (nestLogsDto.getStartTime() != null && nestLogsDto.getEndTime() != null) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(nestLogsDto.getStartTime() + " 00:00:00", df);
            LocalDateTime endTime = LocalDateTime.parse(nestLogsDto.getEndTime() + " 23:59:59", df);
            select.between(NestLogEntity::getUploadTime, startTime, endTime);
        }


        Page<NestLogEntity> pageRes = baseMapper.selectPage(page, select);
        // 测试非要按照线上原样展示适配，QAQ
        if(!CollectionUtils.isEmpty(pageRes.getRecords())){
            pageRes.getRecords().forEach(r -> r.setFolder(StringUtils.hasText(r.getName()) ? r.getName().substring(0, r.getName().lastIndexOf(SymbolConstants.POINT)) : ""));
        }
        Map<String, Object> resMap = new HashMap<>(8);
        resMap.put("records", pageRes.getRecords());
        resMap.put("current", pageRes.getCurrent());
        resMap.put("pages", pageRes.getPages());
        resMap.put("size", pageRes.getSize());
        resMap.put("total", pageRes.getTotal());
        return RestRes.ok(resMap);
    }

    @Override
    public RestRes batchDelLogs(List<Integer> logIdList) {
        if (CollectionUtil.isNotEmpty(logIdList)) {
            List<NestLogEntity> list = this.lambdaQuery().in(NestLogEntity::getId, logIdList).select(NestLogEntity::getUrl, NestLogEntity::getName).list();

            for (NestLogEntity nle : list) {
                String url = nle.getUrl();
                NestLogsParseUtil.delLogZip(url);
            }
            boolean remove = this.removeByIds(logIdList);
            if (!remove) {
                return RestRes.err("数据库删除失败");
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DELETED.getContent()));
    }

    @Deprecated
    private void sendNestLogsUploadProgress(String nestUuid, NestLogUploadState state, String language) {
        Map<String, Object> wsData = new HashMap<>(2);
        if (NestLogUploadState.StateEnum.UPLOAD_COMPLETE.equals(NestLogUploadState.StateEnum.getInstance(state.getState()))) {
            MinioSavingUtil.startScheduleSendMinioSaving(nestUuid);
            wsData.put("progress", 100);
        } else {
            wsData.put("progress", state.getProgress());
        }
        wsData.put("state", MessageUtils.getMessageByLang(NestLogUploadState.StateEnum.getInstance(state.getState()).getKey(), language));
        String message = "";

        message = WebSocketRes.ok().topic(WebSocketTopicEnum.NEST_LOGS_PROCESS).data(wsData).toJSONString();

        if (NestLogUploadState.StateEnum.UPLOAD_FAIL.getValue().equals(state.getState())) {
            message = WebSocketRes.err().msg(state.getExtras()).topic(WebSocketTopicEnum.NEST_LOGS_PROCESS).data(wsData).toJSONString();
        }
        System.out.println(message);
        ChannelService.sendMessageByType11Channel(nestUuid, message);
    }

    private void sendNestLogsUploadProgress(String nestUuid, NestLogUploadState state, Locale locale) {
        Map<String, Object> wsData = new HashMap<>(2);
        // CPS更新至2.4.1版本之后，日志上传会走文件服务上传接口，该接口无需特殊处理minio上传异步问题
//        if (NestLogUploadState.StateEnum.UPLOAD_COMPLETE.equals(NestLogUploadState.StateEnum.getInstance(state.getState()))) {
//            MinioSavingUtil.startScheduleSendMinioSaving(nestUuid);
//            wsData.put("progress", 100);
//        } else {
            wsData.put("progress", state.getProgress());
//        }
        wsData.put("state", I18nMessageUtils.getMessage(NestLogUploadState.StateEnum.getInstance(state.getState()).getKey(), locale));
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.NEST_LOGS_PROCESS).data(wsData).toJSONString();
        if (NestLogUploadState.StateEnum.UPLOAD_FAIL.getValue().equals(state.getState())) {
            message = WebSocketRes.err().msg(state.getExtras()).topic(WebSocketTopicEnum.NEST_LOGS_PROCESS).data(wsData).toJSONString();
        }
        log.info(message);
        ChannelService.sendMessageByType11Channel(nestUuid, message);
        // CPS更新至2.4.1版本之后，前端会根据此状态（MINIO_SAVE_COMPLETE）关闭弹窗
        if (NestLogUploadState.StateEnum.UPLOAD_COMPLETE.equals(NestLogUploadState.StateEnum.getInstance(state.getState()))) {
            Map<String, Object> dataMap = Collections.singletonMap("state", NestLogUploadState.StateEnum.MINIO_SAVE_COMPLETE.getExpress());
            String message2 = WebSocketRes.ok().topic(WebSocketTopicEnum.NEST_LOGS_PROCESS).data(dataMap).toJSONString();
            ChannelService.sendMessageByType11Channel(nestUuid, message2);
        }
    }

}
