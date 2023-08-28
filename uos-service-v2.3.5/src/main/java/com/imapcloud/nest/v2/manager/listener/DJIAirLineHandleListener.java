package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.enums.TaskModeEnum;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.v2.common.enums.DJICameraEnum;
import com.imapcloud.nest.v2.common.enums.DJIHeightModeEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.DJIAirLineHandleDO;
import com.imapcloud.nest.v2.manager.event.DJIAirLineHandleEvent;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.AirLineProxy;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.DJITaskFileService;
import com.imapcloud.nest.v2.service.dto.in.DJITaskFileInDTO;
import com.imapcloud.nest.v2.service.dto.in.DjiAirLineDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseUavInfoOutDTO;
import com.imapcloud.nest.v2.service.impl.DjiAirLineServiceImpl;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIAirLineHandleListener.java
 * @Description DJIAirLineHandleListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class DJIAirLineHandleListener extends AbstractEventListener<DJIAirLineHandleEvent> {

//    @Resource( name = "djiAirLineExecutor")
//    private ThreadPoolTaskExecutor djiAirLineExecutor;


    @Resource
    private DJITaskFileService djiTaskFileService;

    @Resource
    private BaseUavService baseUavService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private FileManager fileManager;

    /**
     * 消息监听-处理
     *
     * @param djiAirLineHandleEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(DJIAirLineHandleEvent djiAirLineHandleEvent) {
        log.info("【DJIAirLineHandleEvent】-【DJIAirLineHandleListener】事件:{}", djiAirLineHandleEvent.toString());
        DJIAirLineHandleDO djiAirLineHandleDO = djiAirLineHandleEvent.getSource();
        Integer taskType = djiAirLineHandleDO.getTaskType()
                , taskId = djiAirLineHandleDO.getTaskId() ;
        String baseNestId = djiAirLineHandleDO.getNestId()
                , taskFileId = djiAirLineHandleDO.getTaskFileId();
        List<String> missionIdList = djiAirLineHandleDO.getMissionList().stream()
                .map(MissionEntity::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
        Map<Integer , String> airLineIdToMissionMap = djiAirLineHandleDO.getMissionList().stream()
                .collect(Collectors.toMap(m->m.getAirLineId()
                        , m->m.getId().toString()
                        , (o ,n )->n));
        Boolean isSave = djiAirLineHandleDO.getSave();
        //airLineId -> djiAirLine / Integer自增 -> djiAirLine
        Map<Integer , String > djiAirLineMap = djiAirLineHandleDO.getDjiAirLineMap();
        NestTypeEnum nestTypeEnum = this.baseNestService.getNestTypeByNestIdCache(baseNestId);
        //大疆基站专用
        if(!NestTypeEnum.DJI_DOCK.equals(nestTypeEnum)){
            if (log.isDebugEnabled()){
                log.debug("【DJIAirLineHandleEvent】-【DJIAirLineHandleListener】航线转换只支持大疆基站 type-> {}",nestTypeEnum.getValue());
            }
            return;
        }
        BaseUavInfoOutDTO baseUavInfoOutDTO = baseUavService.getUavInfoByNestId(baseNestId);

        if(TaskModeEnum.DJI_KML.getValue().equals(taskType)){
            //大疆本地航线
            //保存
            if(isSave){
                this.handleAirLine(this.transMap(missionIdList, airLineIdToMissionMap, djiAirLineMap)
                        , baseUavInfoOutDTO , taskId , taskFileId , DjiAirLineServiceImpl.class);
                return;
            }
            //修改
            if(isAGLOrRealTime(djiAirLineMap)){
                //如果是AGL跟仿地模式，直接返回成功
                if(log.isDebugEnabled()){
                    log.debug("【DJIAirLineHandleEvent】-【DJIAirLineHandleListener】AGl/仿地模式不进行修改保存");
                }
            }else {
                this.handleAirLine(this.transMap(missionIdList, airLineIdToMissionMap, djiAirLineMap)
                        , baseUavInfoOutDTO , taskId , taskFileId , DjiAirLineServiceImpl.class);
            }

        }else if(TaskModeEnum.CUSTOM.getValue().equals(taskType) || TaskModeEnum.GRID.getValue().equals(taskType)){
            //大疆航点模式 或者 网格化
            this.handleAirLine(this.transMap(missionIdList, airLineIdToMissionMap, djiAirLineMap)
                    , baseUavInfoOutDTO , taskId , taskFileId , DjiAirLineServiceImpl.class);
        }



    }

    /**
     * 将 djiAirLineMap 转化为 missionId对应djiAirLine
     * @return
     */
    private Map<String , String> transMap(List<String> missionIdList ,Map<Integer,String> airLineIdToMissionMap , Map<Integer , String> djiAirLineMap){
        Map<String , String> missionIdToAirLineMap = new ConcurrentHashMap<>();
        if(CollectionUtil.isEmpty(missionIdList) || CollectionUtil.isEmpty(djiAirLineMap)
            || missionIdList.size() != djiAirLineMap.size()){
            if(log.isDebugEnabled()){
                log.debug("missionIdList -> {} , djiAirLineMap -> {}",missionIdList , djiAirLineMap);
            }
            throw new BusinessException("架次与航线不能匹配，请检查");
        }
        if(djiAirLineMap.containsKey(0)){
        //如果传入key的是自增序号，则直接按顺序进行转换
            AtomicInteger i = new AtomicInteger();
            djiAirLineMap.forEach((k,v)->{
                missionIdToAirLineMap.put(missionIdList.get(i.get()) , v);
                i.incrementAndGet();
            });
        }else{
        //如果传入key的是airLineId，则直接切换成missionId
            djiAirLineMap.forEach((k ,v)->{
                String missionId = airLineIdToMissionMap.get(k);
                if(StringUtils.isEmpty(missionId)){
                    if(log.isDebugEnabled()){
                        log.debug("airLineId -> {} , airLineIdToMissionMap -> {}",k , airLineIdToMissionMap.toString());
                    }
                    throw new BusinessException("架次与航线不能匹配，请检查");
                }
                missionIdToAirLineMap.put( missionId , v);
            });
        }
        return missionIdToAirLineMap;
    }

    /**
     * 是否是agl或者仿地模式
     * @return
     */
    private boolean isAGLOrRealTime(Map<Integer , String> djiAirLineMap){
        //判断是否是仿地模式/AGL模式
        Integer key = djiAirLineMap.keySet().stream().findFirst().get();
        String djiAirAirLine = djiAirLineMap.get(key);
        DjiAirLineDTO djiAirLineDTO = JSON.parseObject( djiAirAirLine, DjiAirLineDTO.class);
        String heightMode = Optional.ofNullable(djiAirLineDTO)
                .map(DjiAirLineDTO::getLineConfigs)
                .map(DjiAirLineDTO.LineConfigs::getDjiKml)
                .map(DjiAirLineDTO.DJIKml::getHeightMode)
                .orElseGet(()->"");
        if(DJIHeightModeEnum.ABOVE_GROUND_LEVEL.getCode().equals(heightMode)
                || DJIHeightModeEnum.REALTIME_FOLLOW_SURFACE.getCode().equals(heightMode)){
            return true;
        }
        return false;
    }

    /**
     *
     * @param missionIdToAirLineMap
     * @param baseUavInfoOutDTO
     * @param taskId
     */
    private void handleAirLine(Map<String , String> missionIdToAirLineMap ,  BaseUavInfoOutDTO baseUavInfoOutDTO , Integer taskId , String taskFileId , Class clazz){
        //循环生成，并更新，这里的次数指的是架次数，一般不会太多
        List<DJITaskFileInDTO.DJITaskFileAddInDTO> addInDTOList = new ArrayList<>();
        missionIdToAirLineMap.forEach((k,v)->{
            log.info("【DJIAirLineHandleEvent】-【DJIAirLineHandleListener】handleAirLine -> {}" , k , v);
            String path = handleKmz(v ,baseUavInfoOutDTO.getType() ,baseUavInfoOutDTO.getCameraName() , clazz );
            log.info("大疆航线文件路径 ==> {}", path);
            String md5 = "";
            try(InputStream inputSteam = fileManager.getInputSteam(path)){
                md5 = MD5.create().digestHex(inputSteam);
            } catch (IOException e) {
                log.error(String.format("大疆航线[%s]生成md5失败", path), e);
            }
            DJITaskFileInDTO.DJITaskFileAddInDTO addInDTO = new DJITaskFileInDTO.DJITaskFileAddInDTO()
                    .setTaskId(String.valueOf(taskId))
                    .setMissionId(String.valueOf(k))
//                    .setFileUrl(geoaiUosProperties.getStore().getOriginPath() + path)
                    .setFileName(path.substring(path.lastIndexOf("/") + 1))
                    // 使用新版文件上传接口，已经带上了虚拟路径
                    .setFileUrl(path)
                    .setFileName(StringUtils.getFilename(path))
                    .setFileMd5(md5);
            addInDTOList.add(addInDTO);
        });
        //taskFileId 有值的时候，删除对应的记录；
        this.djiTaskFileService.deleteTaskFile(taskFileId);
        //将以前的数据标记为1；定时任务定时删除标记删除的航线包
        this.djiTaskFileService.deleteByTaskIdMissionIds(String.valueOf(taskId),null);
        //插入数据
        addInDTOList.stream().forEach(x->{
            this.djiTaskFileService.insertTaskFile(x);
        });
    }

    /**
     * handleKmz
     * @param djiAirLine
     * @param uavType
     * @param cameraName
     * @return
     */
    private String handleKmz(String djiAirLine , String uavType , String cameraName , Class clazz){
        Object[] objs = new Object[]{uavType == null ? String.valueOf(AircraftCodeEnum.M30.getValue()) : uavType
                , cameraName == null ? DJICameraEnum.M30_CAMERA.getCode() : cameraName };
        String path = new AirLineProxy(clazz)
                .proxyTransformJsonToKmzMainImpl(djiAirLine , objs);
        return path;
    }


}
