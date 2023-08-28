package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.sdk.CommonNestState;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.v2.common.enums.DJITypeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.dataobj.out.PushStreamInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.MediaServiceClient;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.DJILiveService;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseUavInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DJIMediaStreamOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.DjiMqttResult;
import com.imapcloud.sdk.manager.dji.DjiDockManagerCf;
import com.imapcloud.sdk.pojo.constant.dji.DjiErrorCodeEnum;
import com.imapcloud.sdk.pojo.djido.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJILiveServiceImpl.java
 * @Description DJILiveServiceImpl
 * @createTime 2022年10月19日 15:53:00
 */
@Slf4j
@Service
public class DJILiveServiceImpl implements DJILiveService {

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private BaseUavService baseUavService;

//    @Resource
//    private MediaStreamService mediaStreamService;

    @Resource
    private MediaServiceClient mediaServiceClient;

    /**
     * 设置视频分辨率
     * @param type
     * @param videoId
     * @param quality
     */
    @Override
    public void setVideoQuality(Integer type ,String uuid , String videoId, Integer quality) {
        DJICommonResultOutDTO.LiveResultOutDTO outDTO =  new DJICommonResultOutDTO.LiveResultOutDTO();
        outDTO.setResult(0);
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm == null) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_DOCK_OFFLINE.getContent() , new Object[]{uuid},""));
        }
        LiveSetQualityDO liveSetQualityDO  = new LiveSetQualityDO();
        liveSetQualityDO.setVideoId(videoId);
        liveSetQualityDO.setVideoQuality(quality);
        DjiMqttResult<DjiCommonDataDO> result = new DjiMqttResult<>();
        DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();
        if(DJITypeEnum.DJI_DOCK.getCode().equals(type)){
            result = djiDockManagerCf.setLiveQuality(liveSetQualityDO);

        }else if(DJITypeEnum.AIR_CRAFT.getCode().equals(type)){
            result = djiDockManagerCf.setLiveQuality(liveSetQualityDO);

        }
        if(!result.isSuccess()){
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_LIVE_QUALITY_ERR.getContent() , new Object[]{uuid,result.getErrMsg()},""));
        }
    }

    /**
     * 开启/关闭 直播
     *
     * @param uuid
     * @param open
     */
    @Override
    public DJICommonResultOutDTO.LiveResultOutDTO livePush(String uuid, Integer type ,Boolean open ,String videoId , Integer quality) {
        DJICommonResultOutDTO.LiveResultOutDTO outDTO =  new DJICommonResultOutDTO.LiveResultOutDTO();
        outDTO.setResult(0);
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm == null) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_DOCK_OFFLINE.getContent() , new Object[]{uuid},""));
        }
        if(open){
            outDTO = this.liveStartPush(uuid, type, videoId, quality);
        }else{
            outDTO = this.liveStopPush(uuid, type, videoId);
        }
        return outDTO;
    }

    /**
     * 刷新直播
     *
     * @param uuid
     */
    @Override
    public DJICommonResultOutDTO.LiveResultOutDTO flushLivePush(String uuid , Integer type , String videoId , Integer quality) {
        DJICommonResultOutDTO.LiveResultOutDTO outDTO =  new DJICommonResultOutDTO.LiveResultOutDTO();
        outDTO.setResult(0);
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm == null) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_DOCK_OFFLINE.getContent() , new Object[]{uuid},""));
        }
        //关闭直播
        outDTO = this.livePush(uuid, type, Boolean.FALSE, videoId, quality);

        //需要延迟等待5秒
        try{
            Thread.sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage(),e);
        }finally {
            log.info("打印:{}","end");
        }

        //开启直播
        outDTO = this.livePush(uuid, type, Boolean.TRUE, videoId, quality);
        return outDTO;
    }


    /**
     *  开启 直播L
     * @param uuid
     * @param type
     * @param videoId
     * @param quality
     * @return
     */
    public DJICommonResultOutDTO.LiveResultOutDTO liveStartPush(String uuid, Integer type , String videoId , Integer quality) {
        DJICommonResultOutDTO.LiveResultOutDTO outDTO =  new DJICommonResultOutDTO.LiveResultOutDTO();
        outDTO.setResult(0);
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm == null) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_DOCK_OFFLINE.getContent() , new Object[]{uuid},""));
        }
        LiveStartPushDO liveStartPushDO  = new LiveStartPushDO();
        liveStartPushDO.setVideoId(videoId);
        liveStartPushDO.setVideoQuality(quality);
        liveStartPushDO.setUrlType(LiveStartPushDO.UrlTypeEnum.RTMP.ordinal());
        //是否可以直播
//        if(!DJITypeEnum.DJI_DOCK.getCode().equals(type) && !isStreamPush(uuid, videoId)){
//            outDTO.setInfo("无需开启直播");
//            return outDTO;
//        }
        DjiMqttResult<LiveStartPushReplyDO> result = new DjiMqttResult<LiveStartPushReplyDO>();
        DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();
        //基站-设置巢内，只有巢内
        if(DJITypeEnum.DJI_DOCK.getCode().equals(type)){
            DJIMediaStreamOutDTO djiMediaStreamOutDTO = this.getBaseUrlInfo(uuid);
            if(StringUtils.isEmpty(djiMediaStreamOutDTO.getInnerStreamPushUrl()) ){
                throw new BusinessException("查询不到对应的推流地址");
            }
            liveStartPushDO.setUrl(djiMediaStreamOutDTO.getInnerStreamPushUrl());

            result = djiDockManagerCf.startLivePush(liveStartPushDO);

            //无人机
        }else if(DJITypeEnum.AIR_CRAFT.getCode().equals(type)){
            DJIMediaStreamOutDTO djiMediaStreamOutDTO = this.getUavUrlInfo(uuid);
            if( StringUtils.isEmpty(djiMediaStreamOutDTO.getOuterStreamPushUrl())){
                throw new BusinessException("查询不到对应的推流地址");
            }
            liveStartPushDO.setUrl(djiMediaStreamOutDTO.getOuterStreamPushUrl());
            result = djiDockManagerCf.startLivePush(liveStartPushDO);

        }
        LiveStartPushReplyDO liveStartPushReplyDO = Optional.ofNullable(result)
                .map(DjiMqttResult::getCommonDO)
                .map(DjiCommonDO::getData)
                .orElseGet(()->new LiveStartPushReplyDO());
        outDTO.setResult(liveStartPushReplyDO.getResult());
        outDTO.setInfo(liveStartPushReplyDO.getInfo());
        if(!result.isSuccess()){
            //处理异常为正常情况
            if(DjiErrorCodeEnum.LIVE_MANAGER_513003.getMsg().equals(result.getErrMsg()) ||
                    DjiErrorCodeEnum.LIVE_MANAGER_613003.getMsg().equals(result.getErrMsg())){
                outDTO.setInfo(result.getErrMsg());
                return outDTO;
            }
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent() , new Object[]{result.getErrMsg()},""));
        }
        return outDTO;
    }

    /**
     *  关闭 直播
     * @param uuid
     * @param type
     * @param videoId
     * @return
     */
    public DJICommonResultOutDTO.LiveResultOutDTO liveStopPush(String uuid , Integer type , String videoId ) {
        DJICommonResultOutDTO.LiveResultOutDTO outDTO =  new DJICommonResultOutDTO.LiveResultOutDTO();
        outDTO.setResult(0);
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm == null) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_DOCK_OFFLINE.getContent() , new Object[]{uuid},""));
        }
        //是否在直播
//        if(isStreamPush(uuid, videoId)){
//            outDTO.setInfo("无需关闭直播");
//            return outDTO;
//        }

        DjiMqttResult<DjiCommonDataDO> result = new DjiMqttResult<>();
        DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();
        if(DJITypeEnum.DJI_DOCK.getCode().equals(type)){
            result = djiDockManagerCf.stopLivePush(videoId);


        }else if(DJITypeEnum.AIR_CRAFT.getCode().equals(type)){
            result = djiDockManagerCf.stopLivePush(videoId);


        }
        DjiCommonDataDO djiCommonDataDO = Optional.ofNullable(result)
                .map(DjiMqttResult::getCommonDO)
                .map(DjiCommonDO::getData)
                .orElseGet(()->new DjiCommonDataDO());
        if(!result.isSuccess()){
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent() , new Object[]{result.getErrMsg()},""));
        }
        outDTO.setResult(djiCommonDataDO.getResult());
        return outDTO;
    }


    /**
     * 获取巢内巢外监控地址
     * @param uuid
     * @return
     */
    public DJIMediaStreamOutDTO getBaseUrlInfo(String uuid){
        DJIMediaStreamOutDTO dto = new DJIMediaStreamOutDTO();
        BaseNestInfoOutDTO outDTO  = this.baseNestService.getBaseNestInfoByUuid(uuid);
        if(outDTO==null){
            return dto;
        }

        if(StringUtils.isEmpty(outDTO.getInnerStreamId())) {
            throw new BusinessException("找不到对应的巢内streamId");
        }

        //大疆只有巢内监控，故巢外监控不处理
        Result<PushStreamInfoOutDO> inResult = mediaServiceClient.fetchPushStreamInfo(outDTO.getInnerStreamId());
        if(inResult.isOk() && inResult.getData() != null) {
            dto.setInnerStreamPushUrl(inResult.getData().getPushUrl());
        }
        return dto;
    }

    /**
     * 获取无人机监控地址
     * @param uuid
     * @return
     */
    public DJIMediaStreamOutDTO getUavUrlInfo(String uuid){
        DJIMediaStreamOutDTO dto = new DJIMediaStreamOutDTO();
        String nestId = this.baseNestService.getNestIdByNestUuid(uuid);
        BaseUavInfoOutDTO uavInfo  = this.baseUavService.getUavInfoByNestId(nestId);

        if(uavInfo==null){
            return dto;
        }
        Result<PushStreamInfoOutDO> result = mediaServiceClient.fetchPushStreamInfo(uavInfo.getStreamId());
        if(result.isOk() && result.getData() != null) {
            dto.setOuterStreamPushUrl(result.getData().getPushUrl());
        }

        return dto;
    }

    /**
     * 是否可以开启直播
     * @return
     */
    public boolean isStreamPush(String uuid , String videoId){
        CommonNestState cns = CommonNestStateFactory.getInstance(uuid);
        if(cns == null){
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_DOCK_OFFLINE.getContent() , new Object[]{uuid},""));
        }
        DjiCommonDO<DjiDockLiveStateDO> djiDockLiveStateDO = cns.getDjiDockLiveStateDO();
        log.info("djiDockLiveStateDO->{}",djiDockLiveStateDO.toString());
        List<DjiDockLiveStateDO.LiveSta> liveStaList = Optional.ofNullable(djiDockLiveStateDO)
                .map(DjiCommonDO::getData)
                .map(DjiDockLiveStateDO::getLiveStatus)
                .orElseGet(()-> CollectionUtil.newArrayList());

        if(CollectionUtil.isEmpty(liveStaList)){
            return true;
        }
        //只要有数据，代表已经开启了直播
        boolean bol = liveStaList.stream().anyMatch( x->videoId.equals(x.getVideoId()) && 1 == x.getStatus() );
        if(bol){
            log.info("{} 已经开启了直播！", videoId);
            return false;
        }
        return true;
    }


    @Override
    public void switchSdrWorkMode(String uuid, Boolean open) {

        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if(Objects.isNull(cm)) {

        }
        DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();
        SwitchSdrWorkModeDO switchSdrWorkModeDO = new SwitchSdrWorkModeDO();
        if(open) {
            switchSdrWorkModeDO.setLinkWorkmode(SwitchSdrWorkModeDO.ModeEnum.G4.ordinal());
        }else {
            switchSdrWorkModeDO.setLinkWorkmode(SwitchSdrWorkModeDO.ModeEnum.SDR.ordinal());
        }
        DjiMqttResult<DjiCommonDataDO> djiResult = djiDockManagerCf.switchSdrWorkMode(switchSdrWorkModeDO);
        if(!djiResult.isSuccess()) {
            throw new BusinessException(String.format("%s 增强模式失败，失败原因：%s",open ? "开启" : "关闭", djiResult.getErrMsg()));
        }
    }


}
