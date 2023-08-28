package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.mapper.MediaRelayMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.model.media.MediaRelayEntity;
import com.imapcloud.nest.service.MediaRelayService;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.service.MissionVideoService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.NodeMediaUtil;
import com.imapcloud.nest.utils.nms.NmsRes;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.out.CreateRelayParamOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 * @deprecated 2.3.2
 * @author daolin
 * @since 2022-04-22
 */
@Deprecated
//@Service
@Slf4j
public class MediaRelayServiceImpl extends ServiceImpl<MediaRelayMapper, MediaRelayEntity> implements MediaRelayService {

//    @Resource
//    @Lazy
//    private MissionVideoService missionVideoService;
//
//    @Resource
//    MissionRecordsService missionRecordsService;
//
//    @Resource
//    private NodeMediaUtil nodeMediaUtil;
//
//    @Resource
//    private NestService nestService;
//
//    @Resource
//    private BaseNestService baseNestService;
//
//    @Resource
//    private GeoaiUosProperties geoaiUosProperties;

//    @Override
//    public void checkVideoCapture(String execMissionID, String nestId,Integer uavWhich) {
//        log.debug("正在监测录像任务");
//        QueryWrapper<MissionRecordsEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("exec_id", execMissionID).eq("deleted", 0);
//        MissionRecordsEntity missionRecord = missionRecordsService.getOne(queryWrapper);
//        if (missionRecord == null) {
//            log.error("execId:{}未关联架次记录，录像无法创建", execMissionID);
//            return;
//        }
//        Integer gainVideo = missionRecord.getGainVideo();
//        if (MissionConstant.MissionExecGainVideo.RECORD.equals(gainVideo)) {
//            String token = nodeMediaUtil.getToken();
//            String host = geoaiUosProperties.getIptv().getUrl();
////            NestEntity nest = nestService.getNestByIdIsCache(nestId);
//            CreateRelayParamOutDTO createRelayParam = baseNestService.getCreateRelayParamCache(nestId,uavWhich);
//            String appName = nodeMediaUtil.getAppName(createRelayParam.getUavStreamUrl());
//            // 先判断是否有流
//            String codec = nodeMediaUtil.getVideoCodec(appName, token, host);
//            if (!nodeMediaUtil.CODEC_ERR.equals(codec)) {
//                log.debug("获取到视频编码，检查录像任务是否存在");
//                NmsRes recRes = nodeMediaUtil.getRecord(appName, token, host);
//                // 先判断是否在录制，如果正在录制，则不创建新的录像任务
//                if (!nodeMediaUtil.CODE_SUCCESS.equals(recRes.getCode())) {
//                    log.info("{}:{}录像任务不存在，准备创建新的录像任务", createRelayParam.getNestName(), appName);
//                    missionVideoService.createVideoCapture(appName, token, host, nestId, missionRecord.getMissionId(), missionRecord.getId(), execMissionID);
//                    return;
//                }
//                log.debug("{}:{}录像任务已存在，无需创建", createRelayParam.getNestName(), appName);
//                return;
//
//            } else {
//                //删除转发并重新转发
//                delRelay(nestId, token, host,uavWhich);
//                log.error("{}:{}获取视频编码异常,删除转发", createRelayParam.getNestName(), appName);
//                createRelay(nestId,uavWhich);
//                log.info("{}:{}获取视频编码异常,重新创建转发", createRelayParam.getNestName(), appName);
//            }
//            return;
//        }
//        log.debug("execId:{}架次任务无需录像", execMissionID);
//    }

//    @Override
//    public void createRelay(String nestId,Integer uavWhich) {
//        String token = nodeMediaUtil.getToken();
//        String host = geoaiUosProperties.getIptv().getUrl();
////        NestEntity nest = nestService.getNestByIdIsCache(nestId);
//        CreateRelayParamOutDTO createRelayParam = baseNestService.getCreateRelayParamCache(nestId,uavWhich);
//        String streamUrl = createRelayParam.getUavStreamUrl();
//        //判断流是否为第三方，不是的话则无需转发
//        if (!nodeMediaUtil.needRelay(streamUrl)) {
//            log.debug("【{}】非第三方流，无需转发", streamUrl);
//            return;
//        }
//        //先判断是否有流在线，有的话无需重新转发
//        String appName = nodeMediaUtil.getAppName(streamUrl);
//        String codec = nodeMediaUtil.getVideoCodec(appName, token, host);
//        if (!Objects.equals("UNKNOWN", codec)) {
//            log.info("【{}:{}】流已存在，无需relay", createRelayParam.getNestName(), appName);
//            return;
//        }
//        //默认0模式，一直转发
//        NmsRes res = nodeMediaUtil.createRelay(0, createRelayParam.getUavStreamUrl(), "", true, token, host, createRelayParam.getNestName());
//        if (res.getCode() == 200) {
//            //保存或更新relay到数据库
//            MediaRelayEntity mediaRelay = new MediaRelayEntity();
//            Optional<JSONObject> json = Optional.of(res)
//                    .flatMap(data -> Optional.ofNullable(data.getData()))
//                    .flatMap(jsonObject -> Optional.of(new JSONObject((LinkedHashMap<String, Object>) jsonObject)));
//            String id = json.get().getString("id");
//            log.info("【{}】成功relay,ID:{}", createRelayParam.getNestName(), id);
//            mediaRelay.setRelayId(json.get().getString("id"));
//            mediaRelay.setStreamApp(appName.split("/")[0]);
//            mediaRelay.setStreamName(appName.split("/")[1]);
//            mediaRelay.setBsId(createRelayParam.getNestUuid());
//            mediaRelay.setName(createRelayParam.getNestName());
//            //TODO 关联 media_service表
//            mediaRelay.setMsId("ms_00010001");
//            this.saveOrUpdate(mediaRelay);
//            return;
//        }
//        log.error("【{}】relay失败", appName);
//    }

//    @Override
//    public void delRelay(String nestId, String token, String host,Integer uavWhich) {
////        NestEntity nest = nestService.getNestByIdIsCache(nestId);
//        CreateRelayParamOutDTO createRelayParam = baseNestService.getCreateRelayParamCache(nestId,uavWhich);
//        if (nodeMediaUtil.needRelay(createRelayParam.getUavStreamUrl())) {
//            QueryWrapper<MediaRelayEntity> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("bs_id", createRelayParam.getNestUuid());
//            List<MediaRelayEntity> list = this.list(queryWrapper);
//            if (CollectionUtil.isNotEmpty(list)) {
//                this.removeByIds(list.stream().map(MediaRelayEntity::getId).collect(Collectors.toList()));
//                nodeMediaUtil.delRelay(list.get(0).getRelayId(), token, host);
//            }
//        }
//    }

}