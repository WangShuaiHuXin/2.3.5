package com.imapcloud.nest.service.listener.flightMission;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.convert.FlightMissionVOToEntityConvert;
import com.imapcloud.nest.model.FlightMissionEntity;
import com.imapcloud.nest.pojo.vo.FlightMissionSyncAggVO;
import com.imapcloud.nest.pojo.vo.FlightMissionVO;
import com.imapcloud.nest.service.FlightMissionService;
import com.imapcloud.nest.service.event.flightMission.FlightMissionSyncEvent;
import com.imapcloud.nest.utils.LocalDateUtil;
import com.imapcloud.sdk.manager.data.entity.FlightMissionPageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FlightMissionSyncCallBackListener.java
 * @Description FlightMissionSyncCallBackListener
 * @createTime 2022年03月24日 15:46:00
 */
@Slf4j
@Service
public class FlightMissionSyncCallBackListener extends AbstractEventListener<FlightMissionSyncEvent> {

    @Resource
    private FlightMissionService flightMissionService;


    /**
     * 消息监听-处理
     *
     * @param flightMissionSyncEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(FlightMissionSyncEvent flightMissionSyncEvent) {
        log.info("【FlightMissionSyncCallBackListener】飞行架次记录监听");
        FlightMissionSyncAggVO flightMissionSyncAggVO = flightMissionSyncEvent.getSource();
        List<FlightMissionPageEntity> flightMissionPageEntityList = flightMissionSyncAggVO.getPageEntityList();
        List<FlightMissionPageEntity.MissionMsg> missionMsgList = new ArrayList<>();
        for(FlightMissionPageEntity pageEntity : flightMissionPageEntityList){
            missionMsgList.addAll(pageEntity==null?new ArrayList<>():pageEntity.getList());
        }
        //去重
        missionMsgList = missionMsgList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FlightMissionPageEntity.MissionMsg::getExecMissionId))), ArrayList::new));
        List<FlightMissionEntity> flightMissionEntityList = new ArrayList<>();
        log.info("【FlightMissionSyncCallBackListener】一共查询到{}条架次记录",missionMsgList.size());
        for(FlightMissionPageEntity.MissionMsg missionMsg : missionMsgList){
            log.info("【FlightMissionSyncCallBackListener】missionMsg:{}",missionMsg.toString());
            FlightMissionEntity flightMissionEntity = new FlightMissionEntity();
            BeanUtils.copyProperties(missionMsg,flightMissionEntity);
            //设置值
            flightMissionEntity.setMissionName(missionMsg.getName())
                                .setStartTime(LocalDateUtil.timestampToLocalDateTime(missionMsg.getStartTime()))
                                .setEndTime(LocalDateUtil.timestampToLocalDateTime(missionMsg.getEndTime()))
                                .setMissionDate((double)DateUtil.between(DateUtil.date(missionMsg.getStartTime()),DateUtil.date(missionMsg.getEndTime()), DateUnit.MINUTE))
                                .setMissionDistance(missionMsg.getDistance())
                                .setUavWhich(flightMissionSyncAggVO.getUavWhich());
            flightMissionEntity.setBaseNestId(flightMissionSyncAggVO.getNestId());
            //如果有重复数据，代表程序有异常
            List<FlightMissionEntity> flightMissionDbList = this.flightMissionService.lambdaQuery()
                    .eq(FlightMissionEntity::getExecMissionId,missionMsg.getExecMissionId())
                    .select(FlightMissionEntity::getId,FlightMissionEntity::getDeleted).list();
            log.info("【FlightMissionSyncCallBackListener】flightMissionDbList:{},{}",flightMissionDbList.size(),missionMsg.getExecMissionId());
            FlightMissionEntity flightMissionDB = flightMissionDbList.size()>0?flightMissionDbList.get(0):null;
            if(flightMissionDB!=null && flightMissionDB.getDeleted()){
                continue;
            }else if(flightMissionDB!=null && !flightMissionDB.getDeleted()){
                flightMissionEntity.setId(flightMissionDB.getId());
            }
            flightMissionEntityList.add(flightMissionEntity);
        }
        long addNum = flightMissionEntityList.stream().filter(f->f.getId() == null).count();
        log.info("【FlightMissionSyncCallBackListener】待更新数据:{},待新增数据:{}"
                , flightMissionEntityList.size() - addNum
                , addNum);
        //更新数据库
        this.flightMissionService.saveOrUpdateBatch(flightMissionEntityList);
        List<FlightMissionVO> flightMissionVOList = FlightMissionVOToEntityConvert.INSTANCES.doToDto(flightMissionEntityList);
        flightMissionSyncAggVO.setFlightMissionVOList(flightMissionVOList);
    }
}