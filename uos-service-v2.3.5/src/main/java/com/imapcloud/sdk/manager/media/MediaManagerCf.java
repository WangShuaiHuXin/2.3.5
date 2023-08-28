package com.imapcloud.sdk.manager.media;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.media.entity.*;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.entity.MediaFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MediaManagerCf {
    private final static String FUNCTION_TOPIC = Constant.MEDIA_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public MediaManagerCf(Client client) {
        this.client = client;
    }

    /**
     * SD卡是否插入
     *
     * @param which
     */
    public MqttResult<Boolean> isSdCardInserted(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.MEDIA_MANAGER_C1)
                .clazz(String.class)
                .key("inserted")
                .which(which);

        MqttResult<String> s = ClientProxy.getMqttResult(mrp);
        MqttResult<Boolean> t = new MqttResult<>();
        MqttResult.copyProperties(s, t);
        t.setRes(s.getRes().equals("1"));
        return t;
    }

    /**
     * SD 卡是否有错误
     *
     * @param which
     */
    public MqttResult<Boolean> isSdCardHasError(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.MEDIA_MANAGER_C2)
                .clazz(String.class)
                .key("hasError")
                .which(which);

        MqttResult<String> s = ClientProxy.getMqttResult(mrp);
        MqttResult<Boolean> t = new MqttResult<>();
        MqttResult.copyProperties(s, t);
        t.setRes(s.getRes().equals("1"));
        return t;
    }

    /**
     * 获取SD卡的总容量
     *
     * @param which
     */
    public MqttResult<SdCardTotalSpaceEntity> getSdCardTotalSpace(AirIndexEnum... which) {
        MqttResParam<SdCardTotalSpaceEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MEDIA_MANAGER_C3)
                .clazz(SdCardTotalSpaceEntity.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);

    }

    /**
     * 获取SD剩余容量
     *
     * @param which
     */
    public MqttResult<SdCardRemainSpaceEntity> getSdCardRemainSpace(AirIndexEnum... which) {
        MqttResParam<SdCardRemainSpaceEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MEDIA_MANAGER_C4)
                .clazz(SdCardRemainSpaceEntity.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取SD卡容量可以存的照片数
     *
     * @param which
     */
    public MqttResult<AvailableCaptureCountEntity> getSdCardAvailableCaptureCount(AirIndexEnum... which) {
        MqttResParam<AvailableCaptureCountEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MEDIA_MANAGER_C5)
                .clazz(AvailableCaptureCountEntity.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取SD卡容量可以录制视频的长度(单位是秒)
     *
     * @param which
     */
    public MqttResult<AvailableRecordTimeEntity> getSdCardAvailableRecordTimes(AirIndexEnum... which) {
        MqttResParam<AvailableRecordTimeEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MEDIA_MANAGER_C6)
                .clazz(AvailableRecordTimeEntity.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 格式化SD卡
     *
     * @param which
     */
    public MqttResult<NullParam> formatSdCard(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MEDIA_MANAGER_C7)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 根据任务执行ID从CPS数据列表
     *
     * @param execId
     * @param which
     */
    public MqttResult<MediaFile> getMediaInfoList(String execId, AirIndexEnum... which) {
        MqttResParam<MediaFile> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.LIS)
                .code(Constant.MEDIA_MANAGER_2_C0)
                .clazz(MediaFile.class)
                .param("execMissionID", execId)
                .maxWaitTime(15)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 从无人机下载媒体文件到CPS(机巢)
     *
     * @param execId
     */
    public MqttResult<BaseResult3> downloadToNest(String execId, AirIndexEnum... which) {

        MqttResParam<BaseResult3> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MEDIA_MANAGER_2_C1)
                .clazz(BaseResult3.class)
                .param("execMissionID", execId)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 从CPS上传媒体文件到服务器(有回调)
     * 回调时间太长，不适合同步数据
     *
     * @param cpsUploadFileEntity
     */
    @Deprecated
    public MqttResult<BaseResult3> downloadToServer(CpsUploadFileEntity cpsUploadFileEntity, AirIndexEnum... which) {
        HashMap<String, Object> param = new HashMap<>(4);
        param.put("execMissionID", cpsUploadFileEntity.getExecId());
        param.put("uploadMode", 0);
        Map<String, Object> uploadParams = new HashMap<>(8);
        uploadParams.put("url", cpsUploadFileEntity.getUrl());
        uploadParams.put("chunkInitUrl", cpsUploadFileEntity.getChunkInitUrl());
        uploadParams.put("chunkCombineUrl", cpsUploadFileEntity.getChunkCombineUrl());
        uploadParams.put("chunkSyncUrl", cpsUploadFileEntity.getChunkSyncUrl());
        uploadParams.put("uploadByChunks", cpsUploadFileEntity.getUploadByChunks());
        param.put("uploadParams", uploadParams);
        param.put("autoStartUp", cpsUploadFileEntity.getAutoStartUp());
        MqttResParam<BaseResult3> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ORI)
                .code(Constant.MEDIA_MANAGER_2_C2)
                .clazz(BaseResult3.class)
                .param(param)
                .maxWaitTime(30)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 根据媒体文件ID列表从机巢上传媒体文件到服务器（若媒体文件不存在机巢，会先自动从无人机下载媒体文件）
     *
     * @param fileIdList
     */
    @Deprecated
    public MqttResult<BaseResult3> downloadToServer(List<String> fileIdList, String url, Boolean autoStartUp, AirIndexEnum... which) {
        HashMap<String, Object> param = new HashMap<>(2);
        param.put("fileIdList", fileIdList);
        param.put("uploadMode", 0);
        param.put("autoStartUp", autoStartUp);
        Map<String, Object> uploadParams = new HashMap<>(2);
        uploadParams.put("url", url);
        param.put("uploadParams", uploadParams);
        MqttResParam<BaseResult3> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ORI)
                .code(Constant.MEDIA_MANAGER_2_C7)
                .clazz(BaseResult3.class)
                .param(param)
                .maxWaitTime(30)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 根据媒体文件ID列表从飞机下载媒体文件到机巢
     *
     * @param fileIdList
     */
    public MqttResult<BaseResult3> downloadToNest(List<String> fileIdList, AirIndexEnum... which) {
        MqttResParam<BaseResult3> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MEDIA_MANAGER_2_C6)
                .clazz(BaseResult3.class)
                .param("FileIdList", fileIdList)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 重置媒体管理器(暂停同步数据)
     *
     * @param which
     */
    public MqttResult<BaseResult3> resetMediaManager(AirIndexEnum... which) {
        MqttResParam<BaseResult3> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ORI)
                .code(Constant.MEDIA_MANAGER_2_C11)
                .clazz(BaseResult3.class)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 删除基站所有媒体文件
     * callback回来过慢，这里忽略callback请求
     *
     * @param which
     * @return
     */
    public void deleteNestAllMediaFile(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MEDIA_MANAGER_2_C204)
                .which(which);
        ClientProxy.ignoreMqttResult(mrp);
    }

    /**
     * callback回来过慢，这里忽略callback请求
     *
     * @param internal: true->机身,false->SD卡,默认false
     * @param which
     * @return
     */
    public void formatAirStore(Boolean internal, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MEDIA_MANAGER_2_C203)
                .param("internal", internal)
                .which(which);
        ClientProxy.ignoreMqttResult(mrp);
    }
}

