package com.imapcloud.nest.v2.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.exception.SynDataValidateException;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.media.entity.CpsUploadFileEntity;
import com.imapcloud.sdk.pojo.constant.*;
import com.imapcloud.sdk.pojo.entity.MediaStateV2;
import com.imapcloud.sdk.pojo.entity.MissionMediaErrStatus;
import com.imapcloud.sdk.pojo.entity.NestState;
import com.imapcloud.sdk.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class G503DataSynService extends PubDataSynService {

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private MissionMediaErrLogService missionMediaErrLogService;

    @Override
    public void checkStatus(String nestUuid , Integer recordId) {
        Integer uavWhich = 0;
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordId);
        if (Objects.nonNull(missionRecordsEntity)) {
            uavWhich = missionRecordsEntity.getUavWhich();
        }
        //检测基站是否在传输文件
        NestTypeEnum nestType = baseNestService.getNestTypeByNestIdCache(nestUuid);
        if (NestTypeEnum.G503.equals(nestType)) {
            MediaStateV2 mediaState1 = commonNestStateService.getMediaStateV2(nestUuid, AirIndexEnum.ONE);
            MediaStateV2 mediaState2 = commonNestStateService.getMediaStateV2(nestUuid, AirIndexEnum.TWO);
            MediaStateV2 mediaState3 = commonNestStateService.getMediaStateV2(nestUuid, AirIndexEnum.THREE);
            if (MediaStatusV2Enum.UPLOADING.getValue().equals(mediaState1.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOADING.getValue().equals(mediaState1.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOAD_UPLOAD_MEANWHILE.getValue().equals(mediaState1.getCurrentState())) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_SYNCHRONIZING_DATA_THE_STATUS_IS.getContent())
                        + "【" + mediaState1.getCurrentState() + "】");
            }
            if (MediaStatusV2Enum.UPLOADING.getValue().equals(mediaState2.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOADING.getValue().equals(mediaState2.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOAD_UPLOAD_MEANWHILE.getValue().equals(mediaState2.getCurrentState())) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_SYNCHRONIZING_DATA_THE_STATUS_IS.getContent())
                        + "【" + mediaState2.getCurrentState() + "】");
            }
            if (MediaStatusV2Enum.UPLOADING.getValue().equals(mediaState3.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOADING.getValue().equals(mediaState3.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOAD_UPLOAD_MEANWHILE.getValue().equals(mediaState3.getCurrentState())) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_SYNCHRONIZING_DATA_THE_STATUS_IS.getContent())
                        + "【" + mediaState3.getCurrentState() + "】");
            }
            NestState nestState = this.commonNestStateService.getNestState(nestUuid, AirIndexEnum.getInstance(uavWhich));
            //G503没有换电池功能
            if (nestState != null && !nestState.getAircraftConnected() && !nestState.getUsbDeviceConnected()) {
                String msg = String.format("G503，%d号无人机或遥控器未连接，请先到控制页面装载电池，打开遥控器", uavWhich);
                throw new BusinessException(msg);
            }

        }
    }

    @Override
    public List getMediaData(String nestUuid , Integer recordId) {
        return super.getMediaDataPub(nestUuid,recordId);
    }

    @Override
    public List handleSyn(String nestUuid , Integer recordId) {
        return super.handleSynPub(nestUuid,recordId);
    }

    @Override
    public void callback(String nestId , Integer recordId , CountDownLatch countDownLatch) {
        super.callbackPub(nestId,recordId ,countDownLatch);
    }

    @Override
    public String sendCommand(String nestUuid , Integer recordId , List fileIdList , List mediaList) {
        String code = "";
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordId);
        String execId = missionRecordsEntity.getExecId();
        Integer uavWhich = missionRecordsEntity.getUavWhich();
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if( cm == null ){
            throw new BusinessException("机巢离线");
        }
        List<String> lostFileIdList = ListUtils.subtract(mediaList , fileIdList);
        CpsUploadFileEntity cpsUploadFileEntity = null;
        //如果缺失文件列表为空，且已同步文件列表不为空，代表没有数据需要同步
        if( CollectionUtil.isEmpty(lostFileIdList) && CollectionUtil.isNotEmpty(fileIdList) ){
            throw new SynDataValidateException(MessageUtils.getMessageByLang(MessageEnum.GEOAI_UOS_ALL_DATA_HAS_BEEN_SYNCHRONIZED_NO_NEED_TO_REPEAT_SYNCHRONIZATION.getContent(), ""));
        }

        //缺失文件有值的时候，走按文件id同步流程；否则走架次同步
        if(CollectionUtil.isNotEmpty(lostFileIdList)){
            cpsUploadFileEntity = super.initCpsUploadFile(null, lostFileIdList, uavWhich);
        }
        //没有文件的时候
        if(CollectionUtil.isEmpty(fileIdList)){
            cpsUploadFileEntity = super.initCpsUploadFile(execId, null, uavWhich);
        }
        cpsUploadFileEntity.setAutoStartUp(false);
        log.info("调用同步指令 cpsUploadFileEntity-> {} recordId={}", cpsUploadFileEntity , recordId );
        // 上传媒体文件到服务器
        missionMediaErrLogService.deleteErrLog(recordId);
        cm.getMediaManagerV2().downloadToServer(cpsUploadFileEntity, (baseResult3, isSuccess, errMsg) -> {
            // 回调处理
            // 回调处理
            log.info("调用同步指令回调，记录异常信息 ：{}", baseResult3.getMsg());
            if (isSuccess) {
                if (ObjectUtils.isNotEmpty(baseResult3.getParam()) && DataSynEnum.isContaionCode(baseResult3.getCode())) {
                    MissionMediaErrStatus missionMediaErrStatus = JSONUtil.parseObject(baseResult3.getParam(), MissionMediaErrStatus.class);
                    missionMediaErrLogService.saveErrLog(missionMediaErrStatus, recordId, missionRecordsEntity.getMissionId(), nestUuid);
                }
            }
        });
        if(CollectionUtil.isNotEmpty(lostFileIdList)){
            code = Constant.MEDIA_MANAGER_2_C7;
        }else{
            code = Constant.MEDIA_MANAGER_2_C2;
        }
        return code;
    }
}
