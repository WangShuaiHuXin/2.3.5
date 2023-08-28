package com.imapcloud.nest.v2.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.pojo.NestCpsUpdateDto;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.utils.DoubleUtil;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.common.enums.FirmwarePackageTypeEnum;
import com.imapcloud.nest.v2.common.enums.FirmwareTypeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.NestFirmwareInstallStatusEnum;
import com.imapcloud.nest.v2.common.exception.FileIOReadException;
import com.imapcloud.nest.v2.common.exception.FileUploadException;
import com.imapcloud.nest.v2.common.exception.UosServiceErrorCode;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.utils.ZipFileUtils;
import com.imapcloud.nest.v2.dao.entity.FirmwarePackageEntity;
import com.imapcloud.nest.v2.dao.mapper.FirmwarePackageMapper;
import com.imapcloud.nest.v2.dao.po.FirmwarePackageQueryCriteriaPO;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosFirmwareService;
import com.imapcloud.nest.v2.service.UosNestFirmwareService;
import com.imapcloud.nest.v2.service.converter.FirmwarePackageConverter;
import com.imapcloud.nest.v2.service.dto.in.FirmwarePackageInDTO;
import com.imapcloud.nest.v2.service.dto.in.FirmwarePackageInfoInDTO;
import com.imapcloud.nest.v2.service.dto.in.FirmwareVersionInDTO;
import com.imapcloud.nest.v2.service.dto.in.NestFirmwarePackageInDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.FirmwarePackageInfoOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.system.SystemManagerCf;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import com.imapcloud.sdk.pojo.entity.CpsUpdateState;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 固件安装包业务接口实现
 *
 * @author Vastfy
 * @date 2022/7/13 10:47
 * @since 1.9.7
 */
@Slf4j
@Service
public class UosFirmwareServiceImpl implements UosFirmwareService {

    @Resource
    private FirmwarePackageMapper firmwarePackageMapper;

    @Resource
    private FirmwarePackageConverter firmwarePackageConverter;

    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private UosNestFirmwareService uosNestFirmwareService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private UploadManager uploadManager;

    @Resource
    private FileManager fileManager;

    @Override
    public PageResultInfo<FirmwarePackageInfoOutDTO> pageFirmwarePackageInfos(FirmwareVersionInDTO condition) {
        FirmwarePackageQueryCriteriaPO queryCriteria = buildFirmwarePackageCriteria(condition);
        long total = firmwarePackageMapper.countByCondition(queryCriteria);
        List<FirmwarePackageEntity> rows = null;
        if (total > 0) {
            rows = firmwarePackageMapper.selectByCondition(queryCriteria, PagingRestrictDo.getPagingRestrict(condition));
        }
        return PageResultInfo.of(total, rows)
                .map(firmwarePackageConverter::convert);
    }

    @Override
    public String getFirmwarePackageVersion(String fpFileType, InputStream inputStream) {
        // CPS安装包为apk，需要通过第三方工具获取
        Optional<FirmwarePackageTypeEnum> optional = FirmwarePackageTypeEnum.findMatch(fpFileType);
        if (optional.isPresent()) {
            FirmwarePackageTypeEnum packageTypeEnum = optional.get();
            // 解压apk文件，解析AndroidManifest.xml获取版本配置信息
            if (Objects.equals(FirmwarePackageTypeEnum.APK, packageTypeEnum)) {
                return extractApkPackageInfo(inputStream).getPackageVersion();
            }
            if (Objects.equals(FirmwarePackageTypeEnum.BIN, packageTypeEnum)) {
                // 解压zip文件，解析ZipManifest.json获取版本配置信息
                return extractZipPackageInfo(inputStream).getPackageVersion();
            }
        }
        return "unknown";
    }

    @Override
    public String uploadFirmwarePackage(InputStream inputStream, FirmwarePackageInDTO firmwarePackage) {
        Integer firmwareType = firmwarePackage.getType();
        // 解压apk文件，解析AndroidManifest.xml获取版本配置信息
        FirmwarePackageInfoInDTO firmwarePackageInfo;
        if (FirmwareTypeEnum.CPS.matchEquals(firmwareType)) {
            if (!FirmwarePackageTypeEnum.APK.getSupportFile().equalsIgnoreCase(StringUtils.getFilenameExtension(firmwarePackage.getFileName()))) {
                throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONLY_SUPPORT_APK_FILE.getContent()));
            }
            firmwarePackageInfo = extractApkPackageInfo(inputStream);
        } else if (FirmwareTypeEnum.MPS.matchEquals(firmwareType)) {
            if (!FirmwarePackageTypeEnum.BIN.getSupportFile().equalsIgnoreCase(StringUtils.getFilenameExtension(firmwarePackage.getFileName()))) {
                throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MPS_ONLY_SUPPORT_ZIP_FILE.getContent()));
            }
            // 解压zip文件，解析ZipManifest.json获取版本配置信息
            firmwarePackageInfo = extractZipPackageInfo(inputStream);
        } else {
            // 永远不会出现
            firmwarePackageInfo = new FirmwarePackageInfoInDTO();
            firmwarePackageInfo.setPackageType("unknown");
            firmwarePackageInfo.setPackageResource(new InputStreamResource(inputStream));
        }

        try {
            String packageStorage = getPackageStorageUrl(firmwarePackage.getFileName(), firmwarePackageInfo.getPackageResource().getInputStream());
            firmwarePackage.setFilePath(packageStorage);
        } catch (IOException e) {
            throw new FileIOReadException(UosServiceErrorCode.FILE_IO_READ_ERROR.getI18nMessage(firmwarePackage.getFileName()));
        }

        if (StringUtils.hasText(firmwarePackageInfo.getPackageVersion())) {
            firmwarePackage.setVersion(firmwarePackageInfo.getPackageVersion());
        } else {
            firmwarePackage.setVersion("未知版本");
        }
        firmwarePackage.setFileSize(firmwarePackageInfo.getPackageSize());
        return addFirmwarePackage(firmwarePackage);
    }

    private String getPackageStorageUrl(String packageName, InputStream packageInputStream) {
        CommonFileInDO commonFileInDO = new CommonFileInDO();
        commonFileInDO.setFileName(packageName);
        commonFileInDO.setInputStream(packageInputStream);
        // 上传安装包文件
        Optional<FileStorageOutDO> optional = uploadManager.uploadFile(commonFileInDO);
        if(!optional.isPresent()){
            throw new FileUploadException(UosServiceErrorCode.FILE_UPLOAD_ERROR.getI18nMessage(commonFileInDO.getFileName()));
        }
        return optional.get().getStoragePath() + SymbolConstants.SLASH_LEFT + optional.get().getFilename();
    }

    private FirmwarePackageInfoInDTO extractApkPackageInfo(InputStream inputStream) {
        Path tempPath = null;
        try {
            tempPath = Files.createTempFile("cps_", ".apk");
        } catch (IOException e) {
            log.error("生成临时文件失败", e);
        }
        if(Objects.isNull(tempPath)){
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPS_FIRMWARE_INSTALLATION_PACKAGE_DECOMPRESSION_FAILURE.getContent()));
        }
        File tempFile = tempPath.toFile();
        try (OutputStream fos = Files.newOutputStream(tempPath)) {
            FileCopyUtils.copy(inputStream, fos);
            ApkFile apkFile = new ApkFile(tempFile);
            ApkMeta apkMeta = apkFile.getApkMeta();
            if (Objects.nonNull(apkMeta)) {
                FirmwarePackageInfoInDTO apkPackage = new FirmwarePackageInfoInDTO();
                apkPackage.setPackageType(FirmwarePackageTypeEnum.APK.name().toLowerCase());
                apkPackage.setPackageVersion(apkMeta.getVersionName());
                apkPackage.setPackageSize(tempFile.length());
                apkPackage.setPackageResource(new FileSystemResource(tempFile));
                return apkPackage;
            }
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_CPS_DECOMPRESSION.getContent()));
        } catch (Exception e) {
            log.warn("CPS固件安装包解压失败", e);
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPS_FIRMWARE_INSTALLATION_PACKAGE_DECOMPRESSION_FAILURE.getContent()));
        }
    }

    private FirmwarePackageInfoInDTO extractZipPackageInfo(InputStream inputStream) {
        List<File> files;
        try {
            files = ZipFileUtils.extractFiles(inputStream);
        } catch (IOException e) {
            log.warn("MPS固件安装包解压失败", e);
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_MPS_DECOMPRESSION.getContent()));
        }
        if (CollectionUtils.isEmpty(files)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IS_EMPTY_ZIP_FILE.getContent()));
        }
        Optional<File> binFile = files.stream()
                .filter(file -> FirmwarePackageTypeEnum.BIN.name().equalsIgnoreCase(StringUtils.getFilenameExtension(file.getName())))
                .findFirst();
        if (!binFile.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_RETRIEVED_FROM_THE_ZIP_ARCHIVE.getContent()));
        }
        FirmwarePackageInfoInDTO binPackage = new FirmwarePackageInfoInDTO();
        // MPS只要bin文件
        binPackage.setPackageResource(new FileSystemResource(binFile.get()));
        binPackage.setPackageSize(binFile.get().length());
        binPackage.setPackageType(FirmwarePackageTypeEnum.BIN.name().toLowerCase());
        Optional<File> zipManifest = files.stream()
                .filter(file -> Objects.equals(file.getName(), "ZipManifest.json"))
                .findFirst();
        if (zipManifest.isPresent()) {
            File file = zipManifest.get();
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(file);
            } catch (IOException e) {
                log.warn("无法解析MPS固件版本信息");
            }
            if (Objects.nonNull(jsonNode)) {
                JsonNode versionNode = jsonNode.findValue("version");
                binPackage.setPackageVersion(versionNode.asText());
            }
        }
        return binPackage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addFirmwarePackage(FirmwarePackageInDTO firmwarePackage) {
        FirmwarePackageEntity firmwarePackageEntity = firmwarePackageConverter.convert(firmwarePackage);
        firmwarePackageEntity.setPackageId(BizIdUtils.snowflakeId());
        String name = firmwarePackageEntity.getName();
        firmwarePackageEntity.setVersion(firmwarePackage.getVersion());
        firmwarePackageMapper.insert(firmwarePackageEntity);
        return firmwarePackageEntity.getPackageId().toString();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean dropFirmwarePackages(List<String> packageIds) {
        // TODO 是否要异步删除minio安装包文件
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        int updates = firmwarePackageMapper.logicDeleteBatch(packageIds, Long.valueOf(visitorId));
        return updates > 0;
    }

    @Override
    public Boolean pushFirmwarePackage2NestInstall(String nestId, String packageId , Integer uavWhich) {
        // 校验基站信息是否存在
        BaseNestInfoOutDTO baseNestInfo = checkAndGetNestV2(nestId);
        // 校验安装包信息是否存在
        FirmwarePackageEntity firmwarePackage = checkAndGetFirmwarePackage(packageId);
        // 校验基站状态【待机】
        NestStateEnum nestStateEnum = commonNestStateService.getNestStateEnum(baseNestInfo.getUuid(),AirIndexEnum.getInstance(uavWhich));
        if (Objects.isNull(nestStateEnum)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_STATUS_IS_UNKNOWN.getContent()));
        }
        // CPS固件安装包推送更新
        Integer packageType = firmwarePackage.getType();
        ComponentManager cm = ComponentManagerFactory.getInstance(baseNestInfo.getUuid());
        if (Objects.isNull(cm)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_IS_OFFLINE_AND_THE_FIRMWARE_UPDATE_OPERATION.getContent()));
        }
        // 判断是否正在更新
        CpsUpdateState cpsUpdateState = commonNestStateService.getCpsUpdateState(baseNestInfo.getUuid(),AirIndexEnum.getInstance(uavWhich));
        if (CpsUpdateState.StateEnum.installing(cpsUpdateState.getState())) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_NEST_FIRMWARE_IS_BEING_UPDATED.getContent()));
        }
        // 插入基站固件安装包更新记录
        Long updateRecordId = increaseNfpUpdateRecord(nestId, firmwarePackage,uavWhich);
//        String downloadUrl = geoaiUosProperties.getDomain().getDownload() + firmwarePackage.getStorePath();
        String downloadUrl = fileManager.getDownloadLink(firmwarePackage.getStorePath(), false);
        log.info("获取到安装包下载地址为：{}", downloadUrl);
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        SystemManagerCf systemManagerCf = cm.getSystemManagerCf();
        if (FirmwareTypeEnum.CPS.matchEquals(packageType)) {

            this.pushInstallCps(nestStateEnum, downloadUrl, uavWhich, trustedAccessTracer, baseNestInfo, updateRecordId, firmwarePackage, systemManagerCf, cm);

        } else if (FirmwareTypeEnum.MPS.matchEquals(packageType)) {
            if (!Objects.equals(nestStateEnum, NestStateEnum.STANDBY) &&
                    !Objects.equals(nestStateEnum, NestStateEnum.BOOTLOADER_IDLE) &&
                    !Objects.equals(nestStateEnum, NestStateEnum.WAIT_RESET) &&
                    !Objects.equals(nestStateEnum, NestStateEnum.ERROR) &&
                    !Objects.equals(nestStateEnum, NestStateEnum.DEBUG)
            ) {
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_STATUS_CAN_ONLY_BE_STANDBY.getContent()));
            }

            systemManagerCf.updateMps(downloadUrl, firmwarePackage.getVersion(), firmwarePackage.getName());
            /*MqttResult<NullParam> installRes = systemManagerCf.updateMps(downloadUrl, firmwarePackage.getVersion(), firmwarePackage.getName());
            if(!installRes.isSuccess()) {
                throw new BizException(String.format("[下载并安装MPS固件]指令发送失败:[%s]", installRes.getMsg()));
            }*/
            // 监听下载过程
            cm.getBaseManager().listenCpsDownloadAndInstallState((cpsUs, isSuccess, errMsg) -> {
                if (isSuccess) {
                    if (Objects.nonNull(cpsUs)) {
                        pushDownloadAndInstallMsgByWs(trustedAccessTracer, cpsUs, baseNestInfo, updateRecordId, firmwarePackage , uavWhich);
                    }
                } else {
                    // 下载并更新失败处理
                    log.error("基站下载并更新MPS固件失败，原因：{}", errMsg);
                    // 更新基站固件安装状态：【安装失败】
                    uosNestFirmwareService.updateNestFirmwarePackageInstallState(updateRecordId, NestFirmwareInstallStatusEnum.INSTALLED_FAILED.getStatus(), trustedAccessTracer.getAccountId());
                }
            },AirIndexEnum.getInstance(uavWhich));
        }
        return true;
    }

    @Override
    public Boolean cancelFirmwarePackage2NestInstall(String nestId, Integer fpType,Integer uavWhich) {
//        NestEntity nestEntity = checkAndGetNest(nestId);
        BaseNestInfoOutDTO baseNestInfo = checkAndGetNestV2(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(baseNestInfo.getUuid());
        if (Objects.isNull(cm)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_IS_OFFLINE_AND_CANNOT_PERFORM_THIS_OPERATION.getContent()));
        }
        SystemManagerCf systemManagerCf = cm.getSystemManagerCf();
        MqttResult<NullParam> res;
        if (FirmwareTypeEnum.CPS.matchEquals(fpType)) {
            //TODO 待CPS方提供获取G503每个CPS状态的推送接口 ,需要改成各自CPS的状态
            CpsUpdateState cpsUpdateState = commonNestStateService.getCpsUpdateState(baseNestInfo.getUuid(),AirIndexEnum.getInstance(uavWhich));
            String state = cpsUpdateState.getState();
            if (CpsUpdateState.StateEnum.START_DOWNLOAD.getValue().equals(state) ||
                    CpsUpdateState.StateEnum.DOWNLOADING.getValue().equals(state) ||
                    CpsUpdateState.StateEnum.DOWNLOAD_COMPLETE.getValue().equals(state)
            ) {
                res = systemManagerCf.cancelCpsInstall(Objects.isNull(uavWhich)?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(uavWhich));
            } else {
                throw new BizException("当前更新状态【" + state + "】,不允许取消安装CPS");
            }

        } else {
            NestStateEnum nestStateEnum = commonNestStateService.getNestStateEnum(baseNestInfo.getUuid());
            if (NestStateEnum.BOOTLOADER_WAITING_FOR_UPGRADE_CMD.equals(nestStateEnum) ||
                    NestStateEnum.BOOTLOADER_PREPARE_TO_RECEIVE_FW.equals(nestStateEnum) ||
                    NestStateEnum.BOOTLOADER_RECEIVING_FM.equals(nestStateEnum) ||
                    NestStateEnum.BOOTLOADER_UPGRADE_ERR.equals(nestStateEnum)
            ) {
                res = systemManagerCf.cancelMpsInstall();
            } else {
                throw new BizException("当前基站状态是【" + nestStateEnum.getChinese() + "】,不允许取消安装MPS");
            }

        }
        if (res.isSuccess()) {
            return true;
        }
        throw new BizException(String.format("取消安装固件失败，原因：[%s]", res.getMsg()));
    }

    private FirmwarePackageEntity checkAndGetFirmwarePackage(String packageId) {
        LambdaQueryWrapper<FirmwarePackageEntity> con = Wrappers.lambdaQuery(FirmwarePackageEntity.class)
                .eq(FirmwarePackageEntity::getPackageId, Long.valueOf(packageId));
        FirmwarePackageEntity firmwarePackage = firmwarePackageMapper.selectOne(con);
        if (Objects.isNull(firmwarePackage)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INSTALLATION_PACKAGE_INFORMATION_DOES_NOT_EXIST.getContent()));
        }
        return firmwarePackage;
    }

    private BaseNestInfoOutDTO checkAndGetNestV2(String nestId) {
        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
        if (Objects.isNull(baseNestInfo)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BTS_INFORMATION_DOES_NOT_EXIST.getContent()));
        }
        return baseNestInfo;
    }

    private NestFirmwarePackageInDTO buildNestFpInfo(String nestId, final FirmwarePackageEntity firmwarePackage , Integer uavWhich) {
        NestFirmwarePackageInDTO nfpInfo = new NestFirmwarePackageInDTO();
        nfpInfo.setNestId(nestId);
        nfpInfo.setType(firmwarePackage.getType());
        nfpInfo.setVersion(firmwarePackage.getVersion());
        nfpInfo.setPackageId(firmwarePackage.getPackageId().toString());
        nfpInfo.setApkFileName(firmwarePackage.getName());
        nfpInfo.setApkFilePath(firmwarePackage.getStorePath());
        nfpInfo.setUavWhich(uavWhich);
        return nfpInfo;
    }

    private Long increaseNfpUpdateRecord(String nestId, FirmwarePackageEntity firmwarePackage,Integer uavWhich) {
        NestFirmwarePackageInDTO nfpInfo = buildNestFpInfo(nestId, firmwarePackage,uavWhich);
        return uosNestFirmwareService.incrNestFirmwarePackageUpdateRecord(nfpInfo);
    }

    private void pushDownloadAndInstallMsgByWs(ITrustedAccessTracer trustedAccessTracer, CpsUpdateState cpsUs
            , BaseNestInfoOutDTO baseNestInfo, Long updateRecordId
            , FirmwarePackageEntity firmwarePackage , Integer uavWhich) {
        NestCpsUpdateDto nestCpsUpdateDto = new NestCpsUpdateDto()
                .setNestName(baseNestInfo.getName())
                .setPackageName(firmwarePackage.getName())
                .setPackageSize(DoubleUtil.roundKeepDec(2, firmwarePackage.getSize() / (1024.0 * 1024.0)))
                .setDownloadProgress(cpsUs.getProgress())
                .setUpdateState(CpsUpdateState.StateEnum.getInstance(cpsUs.getState()).getExpress())
                .setUavWhich(uavWhich);

        Map<String, Object> data = new HashMap<>(2);
        data.put("dto", nestCpsUpdateDto);
        String message = WebSocketRes.ok()
                .topic(WebSocketTopicEnum.NEST_CPS_UPDATE_PROCESS)
                .data(data)
                .uuid(baseNestInfo.getUuid()).toJSONString();
        ChannelService.sendMessageByType12Channel(trustedAccessTracer.getUsername(), message);

        NestFirmwareInstallStatusEnum nfInstallStatusEnum = null;
        if (Objects.equals(CpsUpdateState.StateEnum.INSTALL_SUCCESS.getValue(), cpsUs.getState())) {
            nfInstallStatusEnum = NestFirmwareInstallStatusEnum.INSTALLED_SUCCESS;
        } else if (Objects.equals(CpsUpdateState.StateEnum.DOWNLOAD_FAIL.getValue(), cpsUs.getState())
                || Objects.equals(CpsUpdateState.StateEnum.INSTALL_FAIL.getValue(), cpsUs.getState())) {
            nfInstallStatusEnum = NestFirmwareInstallStatusEnum.INSTALLED_FAILED;
        }
        if (Objects.nonNull(nfInstallStatusEnum)) {
            // 更新基站固件安装包安装记录状态
            uosNestFirmwareService.updateNestFirmwarePackageInstallState(updateRecordId, nfInstallStatusEnum.getStatus(), trustedAccessTracer.getAccountId());
        }
    }

    private FirmwarePackageQueryCriteriaPO buildFirmwarePackageCriteria(FirmwareVersionInDTO condition) {
        return FirmwarePackageQueryCriteriaPO.builder()
                .type(condition.getType())
                .name(condition.getApkName())
                .build();
    }

    /**
     * 推送CPS安装包
     * @param nestStateEnum
     * @param downloadUrl
     * @param uavWhich
     * @param trustedAccessTracer
     * @param baseNestInfo
     * @param updateRecordId
     * @param firmwarePackage
     * @param systemManagerCf
     * @param cm
     */
    private void pushInstallCps(NestStateEnum nestStateEnum , String downloadUrl , Integer uavWhich
                            , ITrustedAccessTracer trustedAccessTracer , BaseNestInfoOutDTO baseNestInfo
                            , Long updateRecordId , FirmwarePackageEntity firmwarePackage
                            , SystemManagerCf systemManagerCf ,ComponentManager cm ){

        if (!Objects.equals(nestStateEnum, NestStateEnum.STANDBY)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_STATUS_IS_NOT_STANDBY_STATE.getContent()));
        }
        //判断是否是安装前
        MqttResult<NullParam> installRes = systemManagerCf.updateCps(downloadUrl , Objects.isNull(uavWhich)?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(uavWhich));
        if (!installRes.isSuccess()) {
            throw new BizException(String.format("指令[下载并安装CPS固件]发送失败:[%s]", installRes.getMsg()));
        }
        // 监听下载过程
        cm.getBaseManager().listenCpsDownloadAndInstallState((cpsUs, isSuccess, errMsg) -> {
            if (isSuccess) {
                if (Objects.nonNull(cpsUs)) {
                    log.info("CPS更新进度：", JSONObject.toJSONString(cpsUs));
                    pushDownloadAndInstallMsgByWs(trustedAccessTracer, cpsUs, baseNestInfo, updateRecordId, firmwarePackage , uavWhich);
                }
            } else {
                // 下载并更新失败处理
                log.error("基站下载并更新CPS固件失败，原因：{}", errMsg);
                // 更新基站固件安装状态：【安装失败】
                uosNestFirmwareService.updateNestFirmwarePackageInstallState(updateRecordId, NestFirmwareInstallStatusEnum.INSTALLED_FAILED.getStatus(), trustedAccessTracer.getAccountId());
            }
        },AirIndexEnum.getInstance(uavWhich));
    }

}
