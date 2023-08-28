package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.exception.SynDataValidateException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.DataSynService;
import com.imapcloud.nest.v2.service.MissionMediaErrLogService;
import com.imapcloud.nest.v2.service.PubDataSynService;
import com.imapcloud.nest.v2.service.factory.DataSynFactory;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class DataSynServiceImpl implements DataSynService {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private MissionRecordsService missionRecordsService;
    @Lazy
    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private MissionMediaErrLogService missionMediaErrLogService;
    final int number = 1;
    volatile AtomicInteger useNumber = new AtomicInteger(0);

    @Override
    public boolean synDataList(String nestId, List<Integer> recordIdList) {
        String nestUUid = "";
        //计算发起同步的数量 ，点击算一次
        useNumber.set(useNumber.get() + 1);
        nestUUid = this.baseNestService.getNestUuidByNestIdInCache(nestId);
        if(useNumber.get() > this.geoaiUosProperties.getDataSyn().getSynMaxNumber()){
            String message = WebSocketRes.err().msg("【DataSynServiceImpl】同步正忙，请稍后再试").data(new HashMap<>()).topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            pushPhotoTransMsgByWs(nestUUid,message);
            throw new BusinessException("【DataSynServiceImpl】同步正忙，请稍后再试");
        }
        try{
            //主方法
            synDataMain(nestUUid , recordIdList);
        }catch (Exception e){
            log.error("【DataSynServiceImpl】同步报错："+e);
        }finally {
            if(useNumber.get() > 0){
                useNumber.set(useNumber.get() - 1 );
            }
            this.missionPhotoService.closeNest(nestUUid);
        }
        return true;
    }

    /**
     * 同步数据主方法
     * @param nestUUid
     * @param recordIdList
     * @return
     */
    private boolean synDataMain(String nestUUid, List<Integer> recordIdList){
        //批量任务同步 - 一定会走完这几个架次 ，数据结果通过ws推送
        log.info("【DataSynServiceImpl】同步数据-nestUUid -> {},recordIdList -> {}", nestUUid,recordIdList);
        StringBuffer str = new StringBuffer();
        Map<String, Object> wsData = new HashMap<>(2);
        wsData.put("uavWhich", 0);
        for(Integer recordId : recordIdList){
            try {
                CountDownLatch countDownLatch  = new CountDownLatch(number);
                wsData.put("recordId",recordId);
                str.setLength(0);
                this.synData(nestUUid , recordId , countDownLatch);
                execErr(nestUUid , recordId);
                String message = WebSocketRes.ok().msg("当前架次任务同步结束").data(wsData).topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
                pushPhotoTransMsgByWs(nestUUid,message);
                //回写状态
                missionRecordsService.updateDataStatusById(MissionConstant.MissionExecDataStatus.GAIN_TO_SERVER, recordId);
                //架次与架次之间预留2秒 ，预防
                Thread.sleep(2000);
            }catch (BusinessException e){
                log.info("【DataSynServiceImpl】同步架次失败：{} , {}" , recordId , e.getMessage());
                str.append("架次："+ recordId + ",失败：" + e.getMessage() + ";");
                String message = WebSocketRes.err().msg(str.toString()).data(wsData).topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
                pushPhotoTransMsgByWs(nestUUid,message);
                missionRecordsService.updateDataStatusById(MissionConstant.MissionExecDataStatus.SERVER_ERROR, recordId);
            }catch (SynDataValidateException e){
                log.info("【DataSynServiceImpl】同步架次结束：{} , {}" , recordId , e.getMessage());
                str.append("架次："+ recordId + " " + e.getMessage() + ";");
                String message = WebSocketRes.err().msg(str.toString()).data(wsData).topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
                pushPhotoTransMsgByWs(nestUUid,message);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        return true;
    }


    /**
     * 同步数据方法
     * @param nestUUid
     * @param recordId
     * @return
     */
    private boolean synData(String nestUUid , Integer recordId , CountDownLatch countDownLatch){
        //判断基站类型
        NestTypeEnum nestTypeEnum = this.baseNestService.getNestTypeByUuidCache(nestUUid);
        String threadName = Thread.currentThread().getName();

        PubDataSynService pubDataSynService = DataSynFactory.getDataSynService(nestTypeEnum.getValue());
        //1、校验数据同步状态（判断基站状态以及数据状态）
        log.info("【DataSynServiceImpl】校验状态 ,recordId -> {}", recordId);
        pubDataSynService.checkStatus(nestUUid , recordId);

        //2、查询媒体数据
        log.info("【DataSynServiceImpl】查询数据 ,recordId -> {}" ,recordId);
        List<String> fileIdList = pubDataSynService.getMediaData(nestUUid , recordId);

        //3、获取媒体列表数据
        log.info("【DataSynServiceImpl】查询媒体数据 ,recordId -> {}" ,recordId);
        List<String> mediaList = pubDataSynService.handleSyn(nestUUid , recordId);

        //4、按架次列表下载
        //5、按文件列表下载
        missionMediaErrLogService.deleteErrLog(recordId);
        //比较是否同步过文件，没同步过则，发起全部同步，同步过则发起部分同步
        log.info("【DataSynServiceImpl】发起同步指令 ,recordId -> {}" ,recordId);
        String code = pubDataSynService.sendCommand( nestUUid , recordId , fileIdList ,  mediaList);

        //6、阻塞等待回调，直到收到数据结果为1001，则认为当前架次结束
        log.info("【DataSynServiceImpl】等待架次记录同步 ,recordId -> {}" ,recordId);
        pubDataSynService.callback(nestUUid , recordId , countDownLatch);

        //设置超时时间
        new Thread(()->{
            try {
                //超时60分钟， 如果超过60分钟，则释放
                log.info("【DataSynServiceImpl】【{}】异步记录开始 ,recordId -> {} , countDownLatch -> {} " , threadName , recordId , countDownLatch);
                Thread.sleep(1000 * this.geoaiUosProperties.getDataSyn().getWatchTimeout());
                log.info("【DataSynServiceImpl】【{}】异步记时结束 ,recordId -> {} , countDownLatch -> {} " , threadName , recordId , countDownLatch);
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                throw new BusinessException(e.getMessage());
            }
        }, String.format("watch_syn_%d",recordId)).start();
        try {
            countDownLatch.await();
            log.info("【DataSynServiceImpl】同步结束释放阻塞 ,recordId -> {}" ,recordId);
        } catch (InterruptedException e) {
            throw new BusinessException(e.getMessage());
        }
        return true;
    }

    /**
     * 推送
     * @param uuid
     * @param message
     */
    private void pushPhotoTransMsgByWs(String uuid, String message) {
        ChannelService.sendMessageByType3Channel(uuid, message);
        ChannelService.sendMessageByType4Channel(uuid, message);
    }

    /**
     * 优化提示
     */
    private void execErr(String nestUUid , Integer recordId){
        //处理异常报错
        String key = String.format("%s-%d", nestUUid , recordId);
        List<String> dataSynErrList =  PubDataSynService.dataSynErrMap.get(key);
        List<String> fileErrList = PubDataSynService.fileErrMap.get(key);
        if(CollectionUtil.isNotEmpty(fileErrList)){
            String message = String.format("失败文件数(%d) , 原因：%s ", fileErrList.size() ,fileErrList.get(0));
            log.info("【DataSynServiceImpl】execErr ,recordId -> {} , message -> {}" ,recordId ,message);
            PubDataSynService.fileErrMap.remove(key);
            throw new BusinessException(message);
        }else if(CollectionUtil.isNotEmpty(dataSynErrList)){
            String message = WebSocketRes.err()
                    .msg(String.format(dataSynErrList.get(0)))
                    .topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            log.info("【DataSynServiceImpl】execErr ,recordId -> {} , message -> {}" ,recordId ,message);
            PubDataSynService.dataSynErrMap.remove(key);
            throw new BusinessException(message);
        }


    }

}
