package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.enums.MqttLogTypeEnum;
import com.imapcloud.nest.model.MqttLogRecordsEntity;
import com.imapcloud.nest.pojo.dto.ParseEmqxLogDto;
import com.imapcloud.nest.pojo.dto.QueryMqttLogFromMongoDto;
import com.imapcloud.nest.pojo.dto.SpringMqttLogDto;
import com.imapcloud.nest.service.MqttLogParseService;
import com.imapcloud.nest.service.MqttLogRecordsService;
import com.imapcloud.nest.utils.ExcelUtil;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.iotdb.IotDBService;
import com.imapcloud.nest.utils.mongo.pojo.MongoPage;
import com.imapcloud.nest.utils.mongo.pojo.MqttLogEntity;
import com.imapcloud.nest.utils.mongo.service.MongoMqttLogService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.CpsLogConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
public class MqttLogParseServiceImpl implements MqttLogParseService {

    @Autowired
    private MongoMqttLogService mongoMqttLogService;

    @Autowired
    private MqttLogRecordsService mqttLogRecordsService;

    @Autowired
    private IotDBService iotDBService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Autowired
    private ExecutorService logExecutorService;

    @Autowired
    private BaseNestService baseNestService;

    @Resource
    private FileManager fileManager;

    @Override
    public RestRes lsSpringLog() {
        Map<String, Integer> md5IdMap = getMd5IdMap(MqttLogTypeEnum.SPRING);
        File[] ls = FileUtil.ls(geoaiUosProperties.getLogPath());
        List<SpringMqttLogDto> logList = listLogFiles(ls);
        logList.forEach(log -> {
            log.setFinish(md5IdMap.get(log.getMd5()) != null ? 1 : 0);
        });
        return RestRes.ok("logList", logList);
    }

    /**
     * 解析spring后台日志
     * (1)、
     *
     * @param pathList
     * @return
     */
    @Override
    public RestRes parseSpringLog(List<String> pathList) {
        Map<String, Integer> md5IdMap = getMd5IdMap(MqttLogTypeEnum.SPRING);
        List<MqttLogRecordsEntity> mqttLogList = new ArrayList<>(pathList.size());
        for (String path : pathList) {
            Path file = Paths.get(path);
            String md5 = getFileMd5(file.toFile());
            String suffix = StringUtils.getFilenameExtension(path);
            List<MqttLogEntity> logs = null;
            if ("log".equals(suffix)) {
                logs = parseSpringLogFile(file.toFile());
            }
            if ("gz".equals(suffix)) {
                logs = parseSpringGzFile(file);
            }
            if (CollectionUtil.isNotEmpty(logs)) {
                MqttLogRecordsEntity mqttLogRecordsEntity = new MqttLogRecordsEntity()
                        .setPath(path)
                        .setSource(MqttLogTypeEnum.SPRING.getVal())
                        .setMd5(md5)
                        .setFinished(1)
                        .setId(md5IdMap.get(md5));

                mqttLogList.add(mqttLogRecordsEntity);
                batchSave2Mongo(logs);
            }
        }
        mqttLogRecordsService.saveOrUpdateBatch(mqttLogList);
        return RestRes.ok();
    }

    private List<MqttLogEntity> parseSpringLogFile(File file) {
        return FileUtil.readUtf8Lines(file)
                .stream()
                .filter(s -> s.contains("mqttLogTrace"))
                .map(this::springLogStr2JavaObj)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<MqttLogEntity> parseSpringGzFile(Path path) {
        List<MqttLogEntity> list = new ArrayList<>();
        try(InputStream in = new GZIPInputStream(Files.newInputStream(path))){
            Scanner sc = new Scanner(in);
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                if (s.contains("mqttLogTrace")) {
                    MqttLogEntity mqttLogEntity = springLogStr2JavaObj(s);
                    if (mqttLogEntity != null) {
                        list.add(mqttLogEntity);
                    }
                }
            }
            return list;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public RestRes lsCpsLog(String nestId) {
//        String nestUuid = nestService.getUuidById(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (nestUuid == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        Map<String, Integer> md5IdMap = getMd5IdMap(MqttLogTypeEnum.CPS);
        CpsLogConfig cpsLog = geoaiUosProperties.getCpsLog();
        String uploadFolderZip = geoaiUosProperties.getStore().getOriginPath() + cpsLog.getStorePath() + nestUuid + "/" + cpsLog.getZipName() + ".zip";
        File unzip = null;
        try {
            unzip = ZipUtil.unzip(uploadFolderZip);
        } catch (RuntimeException e) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANT_FIND_THE_LOG_FILE_PACKAGE_PLEASE_TRY_TO_UPLOAD_THE_LOG.getContent()));
        }
        //CPS上传的文件Log/System/mqttTracelog,因此解压之后要把第三层文件列表返回
        File[] files;
        try {
            files = unzip.listFiles()[0].listFiles()[0].listFiles()[0].listFiles();
        } catch (Exception e) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPS_LOG_FILE_PARSING_FAILED.getContent()));
        }
        List<SpringMqttLogDto> logList = listLogFiles(files);
        logList.forEach(log -> {
            log.setFinish(md5IdMap.get(log.getMd5()) != null ? 1 : 0);
        });
        return RestRes.ok("logList", logList);
    }

    @Override
    public RestRes parseCpsLog(String nestId, List<String> pathList) {
        Map<String, Integer> md5IdMap = getMd5IdMap(MqttLogTypeEnum.CPS);
        List<MqttLogRecordsEntity> mqttLogRecordList = new ArrayList<>(pathList.size());
//        String nestUuid = nestService.getUuidById(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        for (String path : pathList) {
            File file = fileManager.createTempFile(path);
            String md5 = getFileMd5(file);
            List<MqttLogEntity> logList = parseCpsLog(file);
            logList.forEach(log -> {
                log.setNestUuid(nestUuid);
            });
            MqttLogRecordsEntity mqttLogRecordsEntity = new MqttLogRecordsEntity()
                    .setPath(path)
                    .setSource(MqttLogTypeEnum.CPS.getVal())
                    .setMd5(md5)
                    .setFinished(1)
                    .setId(md5IdMap.get(md5));
            mqttLogRecordList.add(mqttLogRecordsEntity);
            if (CollectionUtil.isNotEmpty(logList)) {
                batchSave2Mongo(logList);
            }
        }
        if (CollectionUtil.isNotEmpty(mqttLogRecordList)) {
            mqttLogRecordsService.saveOrUpdateBatch(mqttLogRecordList);
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPS_LOG_PARSING_COMPLETED.getContent()));
    }

    @Override
    public RestRes parseEmqxLog(ParseEmqxLogDto dto) {
        List<Map<String, String>> maps = listLogs(dto);
        List<MqttLogEntity> logList = maps.stream()
                .map(this::emqxLog2JavaObj)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(logList)) {
            batchSave2Mongo(logList);
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMPLETE_PARSING.getContent()));
    }

    @Override
    public RestRes parseEmqxLogAsync(ParseEmqxLogDto dto) {
        logExecutorService.execute(() -> {
            List<Map<String, String>> maps = listLogs(dto);
            List<MqttLogEntity> logList = maps.stream()
                    .map(this::emqxLog2JavaObj)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(logList)) {
                batchSave2Mongo(logList);
            }
        });
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ASYNCHRONOUS_PARSING_IN_THE_BACKGROUND.getContent()));
    }


    @Override
    public RestRes uploadAndSaveLogZip(String nestUuid, MultipartFile logFile) {
        try {
            String originalFilename = logFile.getOriginalFilename();
            String uploadFolder = geoaiUosProperties.getCpsLog().getStorePath() + nestUuid + "/";
            FileUtil.clean(uploadFolder);
            //调用minio上传文件
            InputStream inputStream = logFile.getInputStream();
            MinIoUnit.uploadZip(geoaiUosProperties.getMinio().getBucketName(), inputStream, originalFilename, uploadFolder);
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_UPLOAD_MQTT.getContent()));
        } catch (IOException e) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_UPLOAD_MQTT.getContent()));
        }
    }

    @Override
    public RestRes listMqttLogsFromMongo(QueryMqttLogFromMongoDto dto) {
        MongoPage<MqttLogEntity> pages = mongoMqttLogService.listMqttLog(dto);
        Map<String, Object> resMap = new HashMap<>(8);
        resMap.put("total", pages.getTotal());
        resMap.put("pages", pages.getPages());
        resMap.put("currentPage", pages.getCurrentPage());
        resMap.put("pageSize", pages.getPageSize());
        resMap.put("records", pages.getRecords());
        return RestRes.ok(resMap);
    }

    @Override
    public RestRes clearMqttLogs() {
        mongoMqttLogService.clearLog();
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_EMPTYING.getContent()));
    }

    @Override
    public List<MqttLogEntity> listMqttLogsByTimestamp(Long startTime, Long endTime) {
        return mongoMqttLogService.listLogByTimestamp(startTime, endTime);
    }

    @Override
    public void exportMqttLogs(Long startTime, Long endTime, HttpServletResponse response) {
        exportExcel(startTime, endTime, response);
    }


    private void exportExcel(Long startTime, Long endTime, HttpServletResponse response) {
        String name = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + "mqttLog";
        List<MqttLogEntity> list = this.listMqttLogsByTimestamp(startTime, endTime);
        List<String> headers = new ArrayList<>();
        headers.add("nestUuid");
        headers.add("traceId");
        headers.add("nodeId");
        headers.add("logTime");
        headers.add("timestamp");
        headers.add("body");

        List<List<String>> collect = list.stream().map(m -> {
            List<String> ls = new ArrayList<>();
            ls.add(m.getNestUuid());
            ls.add(m.getTraceId());
            ls.add(m.getNodeId());
            ls.add(m.getLogTime());
            ls.add(String.valueOf(m.getTimestamp()));
            ls.add(JSON.toJSONString(m.getBody()));
            return ls;
        }).collect(Collectors.toList());
        ExcelUtil.exportExcel2(response, name, null, headers, collect, null);
    }


    private void batchSave2Mongo(List<MqttLogEntity> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            mongoMqttLogService.batchInsert(list);
        }
    }

    private List<MqttLogEntity> parseCpsLog(File file) {
        List<MqttLogEntity> logList = FileUtil.readUtf8Lines(file)
                .stream()
                .filter(s -> s.contains("traceId"))
                .map(this::cpsLogStr2JavaObj)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return logList;
    }

    private MqttLogEntity cpsLogStr2JavaObj(String str) {
        MqttLogEntity mqttLogEntity = new MqttLogEntity();
        String logTime = str.substring(0, 23);
        boolean req = str.contains("req");
        boolean resp = str.contains("resp");
        String nodeId = "";
        int index = 0;
        if (req) {
            index = str.indexOf("req") + 5;
            nodeId = "cre_";
        }
        if (resp) {
            index = str.indexOf("resp") + 6;
            nodeId = "cse_";
        }
        String s1 = str.substring(index, str.length() - 1)
                .replaceAll("\\\\n", "")
                .replaceAll("\\\\t", "")
                .replaceAll("\\\\", "");

        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(s1);
        } catch (Exception e) {
            return null;
        }

        if (resp) {
            nodeId = nodeId + jsonObject.getString("pCode");
        }
        if (req) {
            nodeId = nodeId + jsonObject.getString("code");
        }

        mqttLogEntity.setNodeId(nodeId);
        mqttLogEntity.setLogTime(logTime);
        mqttLogEntity.setTimestamp(timeStr2timestamp(logTime));
        mqttLogEntity.setTraceId(jsonObject.getString("traceId"));
        mqttLogEntity.setBody(jsonObject);
        return mqttLogEntity;
    }

    private MqttLogEntity springLogStr2JavaObj(String str) {
        try {
            int index = str.indexOf("mqttLogTrace");
            String strChi = str.substring(index + 13);
            String logTime = str.substring(0, 23);
            String[] split = strChi.split("&");
            MqttLogEntity mqttLogEntity = new MqttLogEntity();
            mqttLogEntity.setLogTime(logTime);
            mqttLogEntity.setTimestamp(timeStr2timestamp(logTime));
            for (String sp : split) {
                String[] split1 = sp.split("=");
                Method[] methods = MqttLogEntity.class.getDeclaredMethods();
                for (Method m : methods) {
                    boolean contains = m.getName().equals("set" + captureName(split1[0]));
                    if (contains) {
                        m.invoke(mqttLogEntity, split1[1]);
                    }
                }
            }
            return mqttLogEntity;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

    private List<SpringMqttLogDto> listLogFiles(File[] files) {
        if (files != null && files.length > 0) {
            List<SpringMqttLogDto> logList = Arrays.stream(files).sorted((o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified())).map(file -> {
                SpringMqttLogDto dto = new SpringMqttLogDto();
                dto.setFileName(file.getName());
                dto.setFilePath(file.getPath());
                dto.setMd5(getFileMd5(file));
                return dto;
            }).collect(Collectors.toList());
            return logList;
        }
        return Collections.emptyList();
    }

    private List<MqttLogEntity> emqxLog2JavaObj(Map<String, String> map) {
        if (CollectionUtil.isNotEmpty(map)) {
            List<MqttLogEntity> list = new ArrayList<>(2);
            String msgTime = map.get("msgTime");
            String receivedMsg = map.get("received");
            String deliveredMsg = map.get("delivered");
            String nestUuid = map.get("uuid");
            MqttLogEntity mqttLog1 = receivedMsg2JavaObj(receivedMsg);
            if (mqttLog1 != null) {
                mqttLog1.setTimestamp(Long.parseLong(msgTime));
                mqttLog1.setLogTime(timestamp2timeStr(Long.parseLong(msgTime)));
                mqttLog1.setNestUuid(nestUuid);
                list.add(mqttLog1);
            }
            MqttLogEntity mqttLog2 = deliveredMsg2JavaObj(deliveredMsg);
            if (mqttLog2 != null) {
                mqttLog2.setTimestamp(Long.parseLong(msgTime));
                mqttLog2.setLogTime(timestamp2timeStr(Long.parseLong(msgTime)));
                mqttLog2.setNestUuid(nestUuid);
                list.add(mqttLog2);
            }
            return list;
        }
        return Collections.emptyList();
    }

    private MqttLogEntity receivedMsg2JavaObj(String receivedMsg) {
        if (StrUtil.isEmpty(receivedMsg)) {
            return null;
        }
        JSONObject receivedJb = JSONObject.parseObject(receivedMsg);
        boolean pCode = receivedMsg.contains("pCode");
        String nodeId = "";
        if (pCode) {
            nodeId = "erc_" + receivedJb.getString("pCode");
        } else {
            nodeId = "ers_" + receivedJb.getString("code");
        }
        MqttLogEntity logEntity = new MqttLogEntity();
        logEntity.setBody(receivedJb)
                .setNodeId(nodeId)
                .setTraceId(receivedJb.getString("traceId"));

        return logEntity;
    }

    private MqttLogEntity deliveredMsg2JavaObj(String deliveredMsg) {
        if (deliveredMsg == null) {
            return null;
        }
        JSONObject deliveredJb = JSONObject.parseObject(deliveredMsg);
        boolean pCode = deliveredMsg.contains("pCode");
        String nodeId = "";
        if (pCode) {
            nodeId = "ess_" + deliveredJb.getString("pCode");
        } else {
            nodeId = "esc_" + deliveredJb.getString("code");
        }
        MqttLogEntity logEntity = new MqttLogEntity();
        logEntity.setBody(deliveredJb)
                .setNodeId(nodeId)
                .setTraceId(deliveredJb.getString("traceId"));

        return logEntity;
    }

    private long timeStr2timestamp(String timeStr) {
        if (timeStr != null && timeStr.length() == 23) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime parse = LocalDateTime.parse(timeStr, formatter);
            return parse.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        }
        return 0L;
    }

    private String timestamp2timeStr(Long timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.of("+8"));
        return localDateTime.format(formatter);
    }

    private Map<String, Integer> getMd5IdMap(MqttLogTypeEnum mqttLogType) {
        List<MqttLogRecordsEntity> list = mqttLogRecordsService.lambdaQuery()
                .eq(MqttLogRecordsEntity::getSource, mqttLogType.getVal())
                .list();
        Map<String, Integer> md5IdMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(list)) {
            md5IdMap = list.stream().collect(Collectors.toMap(MqttLogRecordsEntity::getMd5, MqttLogRecordsEntity::getId));
        }
        return md5IdMap;
    }

    private String getFileMd5(File file) {
        try {
            return DigestUtil.md5Hex(file);
        } catch (Exception e) {
            return "";
        }
    }


    private List<Map<String, String>> listLogs(ParseEmqxLogDto dto) {
        if (dto.getAll()) {
//            List<NestEntity> list = nestService.lambdaQuery()
//                    .eq(NestEntity::getDeleted, false)
//                    .select(NestEntity::getUuid)
//                    .list();
//            List<String> uuidList = list.stream().map(NestEntity::getUuid).filter(Objects::nonNull).collect(Collectors.toList());
            List<String> uuidList = baseNestService.listAllUuidsCache();
            return iotDBService.listLogs(uuidList, dto.getStartTime(), dto.getEndTime());
        } else {
//            String nestUuid = nestService.getUuidById(dto.getNestId());
            String nestUuid = baseNestService.getNestUuidByNestIdInCache(dto.getNestId());
            if (nestUuid == null) {
                return Collections.emptyList();
            }
            return iotDBService.listLogs(nestUuid, dto.getStartTime(), dto.getEndTime());
        }

    }

}
