package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.MD5;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.v2.common.enums.DJICameraEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.Mavic3KmzHandleDO;
import com.imapcloud.nest.v2.manager.event.Mavic3DJKmzEvent;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.AirLineProxy;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.DJITaskFileService;
import com.imapcloud.nest.v2.service.Mavic3KmzAirLineServiceImpl;
import com.imapcloud.nest.v2.service.dto.in.DJITaskFileInDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseUavInfoOutDTO;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class Mavic3KmzLineHandleListener extends AbstractEventListener<Mavic3DJKmzEvent> {
    @Resource
    private BaseUavService baseUavService;

    @Resource
    private FileManager fileManager;
    @Resource
    private DJITaskFileService djiTaskFileService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Override
    @EventListener
    public void eventListener(Mavic3DJKmzEvent mavic3DJKmzEvent) {
        log.info("【Mavic3KmzLineHandleListener】御三兼容kmz航线，航线转换：{}", mavic3DJKmzEvent.getSource());
        Mavic3KmzHandleDO source = mavic3DJKmzEvent.getSource();
        List<AirLineEntity> airLineList = source.getAirLineList();
        Map<Integer, String> djiAirLineMap = source.getDjiAirLineMap();
        List<MissionEntity> missionList = source.getMissionList();
        List<String> missionIdList = missionList.stream()
                .map(MissionEntity::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
        String nestId = source.getNestId();
        BaseUavInfoOutDTO baseUavInfoOutDTO = baseUavService.getUavInfoByNestId(nestId);
        Integer taskId = source.getTaskId();
        Map<Integer, String> lineIdToMissionMap = missionList.stream()
                .collect(Collectors.toMap(m -> m.getAirLineId()
                        , m -> m.getId().toString()
                        , (o, n) -> n));
        Map<String, String> stringStringMap = this.transMap(missionIdList, lineIdToMissionMap, djiAirLineMap);
        //是否保存文件
        Boolean save = source.getSave();
        log.info("【Mavic3KmzLineHandleListener】更新航线文件，{}", djiAirLineMap);
        handleAirLine(stringStringMap, baseUavInfoOutDTO, taskId, source.getTaskFileId(), Mavic3KmzAirLineServiceImpl.class);
    }


    private Map<String, String> transMap(List<String> missionIdList, Map<Integer, String> airLineIdToMissionMap, Map<Integer, String> djiAirLineMap) {
        Map<String, String> missionIdToAirLineMap = new ConcurrentHashMap<>();
        if (CollectionUtil.isEmpty(missionIdList) || CollectionUtil.isEmpty(djiAirLineMap)
                || missionIdList.size() != djiAirLineMap.size()) {
            if (log.isDebugEnabled()) {
                log.debug("missionIdList -> {} , djiAirLineMap -> {}", missionIdList, djiAirLineMap);
            }
            throw new BusinessException("架次与航线不能匹配，请检查");
        }
        if (djiAirLineMap.containsKey(0)) {
            //如果传入key的是自增序号，则直接按顺序进行转换
            AtomicInteger i = new AtomicInteger();
            djiAirLineMap.forEach((k, v) -> {
                missionIdToAirLineMap.put(missionIdList.get(i.get()), v);
                i.incrementAndGet();
            });
        } else {
            //如果传入key的是airLineId，则直接切换成missionId
            djiAirLineMap.forEach((k, v) -> {
                String missionId = airLineIdToMissionMap.get(k);
                if (StringUtils.isEmpty(missionId)) {
                    if (log.isDebugEnabled()) {
                        log.debug("airLineId -> {} , airLineIdToMissionMap -> {}", k, airLineIdToMissionMap.toString());
                    }
                    throw new BusinessException("架次与航线不能匹配，请检查");
                }
                missionIdToAirLineMap.put(missionId, v);
            });
        }
        return missionIdToAirLineMap;
    }

    private void handleAirLine(Map<String, String> missionIdToAirLineMap, BaseUavInfoOutDTO baseUavInfoOutDTO, Integer taskId, String taskFileId, Class clazz) {
        //循环生成，并更新，这里的次数指的是架次数，一般不会太多
        List<DJITaskFileInDTO.DJITaskFileAddInDTO> addInDTOList = new ArrayList<>();
        missionIdToAirLineMap.forEach((k, v) -> {
            log.info("【Mavic3KmzLineHandleListener】-【Mavic3KmzLineHandleListener】handleAirLine -> {}", k, v);
            String path = handleKmz(v, baseUavInfoOutDTO.getType(), baseUavInfoOutDTO.getCameraName(), clazz);
            String md5 = "";
            try (InputStream inputSteam = fileManager.getInputSteam(path)) {
                md5 = MD5.create().digestHex(inputSteam);
            } catch (IOException e) {
                log.error(String.format("御三-》kmz文件[%s]生成md5失败", path), e);
            }
            DJITaskFileInDTO.DJITaskFileAddInDTO addInDTO = new DJITaskFileInDTO.DJITaskFileAddInDTO()
                    .setTaskId(String.valueOf(taskId))
                    .setMissionId(String.valueOf(k))
                    .setFileUrl(path)
                    .setFileName(path.substring(path.lastIndexOf("/") + 1))
                    .setFileMd5(md5);
            addInDTOList.add(addInDTO);
        });
        //taskFileId 有值的时候，删除对应的记录；
        this.djiTaskFileService.deleteTaskFile(taskFileId);
        //将以前的数据标记为1；定时任务定时删除标记删除的航线包
        this.djiTaskFileService.deleteByTaskIdMissionIds(String.valueOf(taskId), null);
        //插入数据
        addInDTOList.stream().forEach(x -> {
            this.djiTaskFileService.insertTaskFile(x);
        });
    }


    private String handleKmz(String djiAirLine, String uavType, String cameraName, Class clazz) {
        Object[] objs = new Object[]{uavType == null ? String.valueOf(AircraftCodeEnum.MAVIC3.getValue()) : uavType
                , cameraName == null ? DJICameraEnum.M30_CAMERA.getCode() : cameraName};
        String path = new AirLineProxy(clazz)
                .proxyTransformJsonToKmzMainImpl(djiAirLine, objs);
        return path;
    }
}
