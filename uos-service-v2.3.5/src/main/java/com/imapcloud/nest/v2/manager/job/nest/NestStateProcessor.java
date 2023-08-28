package com.imapcloud.nest.v2.manager.job.nest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.google.common.collect.Lists;
import com.imapcloud.nest.common.netty.service.WsSchedulingService;
import com.imapcloud.nest.pojo.dto.AircraftLocationDto;
import com.imapcloud.nest.pojo.dto.WsNestListDto;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.MqttNestService;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosMqttService;
import com.imapcloud.nest.v2.service.dto.nest.NestAlarmDTO;
import com.imapcloud.nest.v2.service.dto.nest.NestBaseDTO;
import com.imapcloud.nest.v2.service.dto.nest.NestUavDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 基站状态定时检测
 *
 * @author boluo
 * @date 2023-02-14
 */
@Slf4j
@Component
public class NestStateProcessor implements BasicProcessor {

    private final static Map<String, WsNestListDto> WS_NEST_LIST_DTO_MAP = new ConcurrentHashMap<>();

    private static final String LOCK_KEY = "NestStateProcessor:process:run";

    private static final Integer MAX_TIME = 55 * 1000;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private WsSchedulingService wsSchedulingService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ExecutorService mqttNestExecutor;

    @Resource
    private MqttNestService mqttNestService;

    @Resource
    private RedisService redisService;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    @Resource
    private BaseNestManager baseNestManager;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private UosMqttService uosMqttService;

    public void manualProcess(String accountId) {
        log.info("#NestStateProcessor.manualProcess# start");

        mqttNestExecutor.submit(() -> {
            long start = System.currentTimeMillis();
            BaseNestAccountOutDO baseNestAccountOutDO = baseNestAccountManager.selectByUserId(accountId);
            if (baseNestAccountOutDO != null && CollUtil.isNotEmpty(baseNestAccountOutDO.getBaseNestId())) {

                List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectListByNestIdList(baseNestAccountOutDO.getBaseNestId());

                List<NestBaseDTO> nestBaseDTOList = Lists.newArrayList();
                List<NestUavDTO> nestUavDTOList = Lists.newArrayList();
                List<NestAlarmDTO> alarmDTOList = Lists.newArrayList();
                for (BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO : baseNestEntityOutDOList) {
                    WsNestListDto oldWsNestListDto = WS_NEST_LIST_DTO_MAP.get(baseNestEntityOutDO.getUuid());
                    if (oldWsNestListDto != null) {
                        nestBaseDTOList.add(toNestBaseDTO(oldWsNestListDto));
                        nestUavDTOList.add(toNestUavDTO(oldWsNestListDto));
                        alarmDTOList.add(toNestAlarmDTO(oldWsNestListDto));
                    }
                }
                if (CollUtil.isNotEmpty(nestBaseDTOList)
                        || CollUtil.isNotEmpty(nestUavDTOList) || CollUtil.isNotEmpty(alarmDTOList)) {
                    mqttNestService.batchNestAll(nestBaseDTOList, nestUavDTOList, alarmDTOList, accountId);
                }
            }
            long end = System.currentTimeMillis();
            log.info("#NestStateProcessor.manualProcess# run={}", (end - start));
        });
    }

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        log.info("#NestStateProcessor.process# start");
        run();
        return new ProcessResult(true, "执行成功");
    }

    private void run() {
        mqttNestExecutor.submit(() -> {
            log.info("#NestStateProcessor.run# run start");
            boolean tryLock = redisService.tryLock(LOCK_KEY, "1", 30, TimeUnit.SECONDS);
            if (!tryLock) {
                return;
            }

            try {

                List<String> nestUuidList = baseNestService.listAllUuidsCache();
                if (CollUtil.isEmpty(nestUuidList)) {
                    return;
                }
                int num = 0;
                long start = System.currentTimeMillis();
                int taskSleepTime = geoaiUosProperties.getIm().getTaskSleepTime();
                long runTime = 0;
                // 定时任务设置1分钟执行一次，循环执行任务，最后5秒钟退出执行
                while(runTime <= MAX_TIME) {

                    run(nestUuidList);
                    if (taskSleepTime > 0 && taskSleepTime < MAX_TIME) {
                        if (taskSleepTime + runTime < MAX_TIME) {
                            ThreadUtil.sleep(taskSleepTime);
                        }
                    }
                    long end = System.currentTimeMillis();
                    runTime = end - start;
                    log.info("#NestStateProcessor.run# run time:{}", runTime);
                    num ++;
                }
                log.info("#NestStateProcessor.run# run num:{} time:{}", num, runTime);
            } catch (Exception e) {
                log.error("#NestStateProcessor.run# error:", e);
            } finally {
                redisService.releaseLock(LOCK_KEY, "1");
            }
            log.info("#NestStateProcessor.run# run end");
        });
    }

    private void run(List<String> nestUuidList) {
        for (String nestUuid : nestUuidList) {
            WsNestListDto wsNestListDto = wsSchedulingService.buildNestListDtoMessage(nestUuid);
            WsNestListDto oldWsNestListDto = WS_NEST_LIST_DTO_MAP.get(nestUuid);
            WS_NEST_LIST_DTO_MAP.put(nestUuid, wsNestListDto);
            NestBaseDTO nestBaseDTO = nestBase(wsNestListDto, oldWsNestListDto);

            NestUavDTO nestUavDTO = nestUav(wsNestListDto, oldWsNestListDto);

            NestAlarmDTO nestAlarmDTO = nestAlarm(wsNestListDto, oldWsNestListDto);

            if (nestBaseDTO != null || nestUavDTO != null || nestAlarmDTO != null) {
                mqttNestService.nestAll(nestBaseDTO, nestUavDTO, nestAlarmDTO, null, nestUuid);
            }
        }
    }

    private NestAlarmDTO toNestAlarmDTO(WsNestListDto wsNestListDto) {
        NestAlarmDTO nestAlarmDTO = new NestAlarmDTO();
        nestAlarmDTO.setUuid(wsNestListDto.getUuid());
        nestAlarmDTO.setAlarmHandle(wsNestListDto.getAlarmHandle());
        nestAlarmDTO.setAlarmWeather(wsNestListDto.getAlarmWeather());
        nestAlarmDTO.setAlarmSpeedWeather(wsNestListDto.getAlarmSpeedWeather());
        nestAlarmDTO.setSpeed(wsNestListDto.getSpeed());
        nestAlarmDTO.setRain(wsNestListDto.getRain());
        nestAlarmDTO.setAlarmRainWeather(wsNestListDto.getAlarmRainWeather());
        return nestAlarmDTO;
    }

    private NestAlarmDTO nestAlarm(WsNestListDto wsNestListDto, WsNestListDto oldWsNestListDto) {

        NestAlarmDTO nestAlarmDTO = toNestAlarmDTO(wsNestListDto);

        try {
            if (oldWsNestListDto == null) {
                return nestAlarmDTO;
            }

            if (wsNestListDto.getAlarmHandle() == null && oldWsNestListDto.getAlarmHandle() != null) {
                return nestAlarmDTO;
            } else if (wsNestListDto.getAlarmHandle() != null
                    && !wsNestListDto.getAlarmHandle().equals(oldWsNestListDto.getAlarmHandle())) {
                return nestAlarmDTO;
            }

            if (wsNestListDto.getAlarmWeather() == null && oldWsNestListDto.getAlarmWeather() != null) {
                return nestAlarmDTO;
            } else if (wsNestListDto.getAlarmWeather() != null
                    && !wsNestListDto.getAlarmWeather().equals(oldWsNestListDto.getAlarmWeather())) {
                return nestAlarmDTO;
            }

            if (wsNestListDto.getAlarmSpeedWeather() == null && oldWsNestListDto.getAlarmSpeedWeather() != null) {
                return nestAlarmDTO;
            } else if (wsNestListDto.getAlarmSpeedWeather() != null
                    && !wsNestListDto.getAlarmSpeedWeather().equals(oldWsNestListDto.getAlarmSpeedWeather())) {
                return nestAlarmDTO;
            }

            if (wsNestListDto.getSpeed() == null && oldWsNestListDto.getSpeed() != null) {
                return nestAlarmDTO;
            } else if (wsNestListDto.getSpeed() != null && oldWsNestListDto.getSpeed() == null) {
                return nestAlarmDTO;
            } else {
                double speed = wsNestListDto.getSpeed() - oldWsNestListDto.getSpeed();
                if (speed < -1 || speed > 1) {
                    return nestAlarmDTO;
                }
            }

            if (wsNestListDto.getRain() == null && oldWsNestListDto.getRain() != null) {
                return nestAlarmDTO;
            } else if (wsNestListDto.getRain() != null
                    && !wsNestListDto.getRain().equals(oldWsNestListDto.getRain())) {
                return nestAlarmDTO;
            }

            if (wsNestListDto.getAlarmRainWeather() == null && oldWsNestListDto.getAlarmRainWeather() != null) {
                return nestAlarmDTO;
            } else if (wsNestListDto.getAlarmRainWeather() != null
                    && !wsNestListDto.getAlarmRainWeather().equals(oldWsNestListDto.getAlarmRainWeather())) {
                return nestAlarmDTO;
            }
        } catch (Exception e) {
            log.error("#NestStateProcessor.nestAlarm# wsNestListDto={}", wsNestListDto, e);
            return nestAlarmDTO;
        }
        return null;
    }

    private NestUavDTO toNestUavDTO(WsNestListDto wsNestListDto) {
        NestUavDTO nestUavDTO = new NestUavDTO();
        nestUavDTO.setUuid(wsNestListDto.getUuid());
        nestUavDTO.setTaskName(wsNestListDto.getTaskName());
        nestUavDTO.setTaskId(wsNestListDto.getTaskId());
        nestUavDTO.setTaskType(wsNestListDto.getTaskType());
        nestUavDTO.setAircraftLocation(wsNestListDto.getAircraftLocation());
        nestUavDTO.setG503AircraftLocationMap(wsNestListDto.getG503AircraftLocationMap());
        nestUavDTO.setFlying(wsNestListDto.getFlying());
        nestUavDTO.setExecutingTaskNameMap(wsNestListDto.getExecutingTaskNameMap());
        return nestUavDTO;
    }

    private NestUavDTO nestUav(WsNestListDto wsNestListDto, WsNestListDto oldWsNestListDto) {

        NestUavDTO nestUavDTO = toNestUavDTO(wsNestListDto);

        try {
            if (oldWsNestListDto == null) {
                return nestUavDTO;
            }
            if (wsNestListDto.getTaskId() == null && oldWsNestListDto.getTaskId() != null) {
                return nestUavDTO;
            } else if (wsNestListDto.getTaskId() != null && !wsNestListDto.getTaskId().equals(oldWsNestListDto.getTaskId())) {
                return nestUavDTO;
            }

            if (!checkEqual(wsNestListDto.getAircraftLocation(), oldWsNestListDto.getAircraftLocation())) {
                return nestUavDTO;
            }

            Map<String, AircraftLocationDto> g503AircraftLocationMap = wsNestListDto.getG503AircraftLocationMap();
            Map<String, AircraftLocationDto> oldG503AircraftLocationMap = oldWsNestListDto.getG503AircraftLocationMap();
            if (g503AircraftLocationMap == null && oldG503AircraftLocationMap != null) {
                return nestUavDTO;
            } else if (g503AircraftLocationMap != null && oldG503AircraftLocationMap == null) {
                return nestUavDTO;
            } else if (g503AircraftLocationMap != null) {

                if (g503AircraftLocationMap.size() != oldG503AircraftLocationMap.size()) {
                    return nestUavDTO;
                }

                for (Map.Entry<String, AircraftLocationDto> stringAircraftLocationDtoEntry : g503AircraftLocationMap.entrySet()) {

                    AircraftLocationDto entryValue = stringAircraftLocationDtoEntry.getValue();
                    AircraftLocationDto oldEntryValue = oldG503AircraftLocationMap.get(stringAircraftLocationDtoEntry.getKey());
                    if (!checkEqual(entryValue, oldEntryValue)) {
                        return nestUavDTO;
                    }
                }
            }

            // flying
            if (wsNestListDto.getFlying() != null && oldWsNestListDto.getFlying() != null
                    && !wsNestListDto.getFlying().equals(oldWsNestListDto.getFlying())) {
                return nestUavDTO;
            }

            Map<String, String> executingTaskNameMap = wsNestListDto.getExecutingTaskNameMap();
            Map<String, String> oldExecutingTaskNameMap = oldWsNestListDto.getExecutingTaskNameMap();
            if (executingTaskNameMap == null && oldExecutingTaskNameMap != null) {
                return nestUavDTO;
            } else if (executingTaskNameMap != null && oldExecutingTaskNameMap == null) {
                return nestUavDTO;
            } else if (executingTaskNameMap != null) {
                if (executingTaskNameMap.size() != oldExecutingTaskNameMap.size()) {
                    return nestUavDTO;
                }
                for (Map.Entry<String, String> stringStringEntry : executingTaskNameMap.entrySet()) {
                    String key = stringStringEntry.getKey();
                    String value = stringStringEntry.getValue();
                    String oldValue = oldExecutingTaskNameMap.get(key);
                    if (value == null && oldValue != null) {
                        return nestUavDTO;
                    } else if (value != null && !value.equals(oldValue)) {
                        return nestUavDTO;
                    }
                }
            }
        } catch (Exception e) {
            log.error("#NestStateProcessor.nestUav# wsNestListDto={}", wsNestListDto, e);
            return nestUavDTO;
        }
        return null;
    }

    private boolean checkEqual(AircraftLocationDto aircraftLocation, AircraftLocationDto oldAircraftLocation) {
        if (aircraftLocation == null && oldAircraftLocation != null) {
            return false;
        } else if (aircraftLocation != null && oldAircraftLocation == null) {
            return false;
        } else if (aircraftLocation != null) {

            if (!aircraftLocation.getLongitude().equals(oldAircraftLocation.getLongitude())) {
                return false;
            }
            if (!aircraftLocation.getLatitude().equals(oldAircraftLocation.getLatitude())) {
                return false;
            }

            if (!aircraftLocation.getAltitude().equals(oldAircraftLocation.getAltitude())) {
                return false;
            }

            if (!aircraftLocation.getRelativeAltitude().equals(oldAircraftLocation.getRelativeAltitude())) {
                return false;
            }

            if (!aircraftLocation.getHeadDirection().equals(oldAircraftLocation.getHeadDirection())) {
                return false;
            }

            if (!aircraftLocation.getMissionRecordsId().equals(oldAircraftLocation.getMissionRecordsId())) {
                return false;
            }
        }
        return true;
    }

    private NestBaseDTO toNestBaseDTO(WsNestListDto wsNestListDto) {
        NestBaseDTO nestBaseDTO = new NestBaseDTO();
        nestBaseDTO.setUuid(wsNestListDto.getUuid());
        nestBaseDTO.setState(wsNestListDto.getState());
        nestBaseDTO.setBaseState(wsNestListDto.getBaseState());
        nestBaseDTO.setMaintenanceState(wsNestListDto.getMaintenanceState());
        nestBaseDTO.setNestDebug(wsNestListDto.getNestDebug());
        nestBaseDTO.setNestConnected(wsNestListDto.getNestConnected());
        return nestBaseDTO;
    }

    private NestBaseDTO nestBase(WsNestListDto wsNestListDto, WsNestListDto oldWsNestListDto) {

        NestBaseDTO nestBaseDTO = toNestBaseDTO(wsNestListDto);

        try {
            if (oldWsNestListDto == null) {
                return nestBaseDTO;
            }
            if (!wsNestListDto.getState().equals(oldWsNestListDto.getState())) {
                return nestBaseDTO;
            }
            if (!wsNestListDto.getBaseState().equals(oldWsNestListDto.getBaseState())) {
                return nestBaseDTO;
            }
            if (!wsNestListDto.getMaintenanceState().equals(oldWsNestListDto.getMaintenanceState())) {
                return nestBaseDTO;
            }
            if (!wsNestListDto.getNestDebug().equals(oldWsNestListDto.getNestDebug())) {
                return nestBaseDTO;
            }
            if (!wsNestListDto.getNestConnected().equals(oldWsNestListDto.getNestConnected())) {
                return nestBaseDTO;
            }
        } catch (Exception e) {
            log.error("#NestStateProcessor.nestBase# wsNestListDto={}", wsNestListDto, e);
            return nestBaseDTO;
        }
        return null;
    }

    private void init() {

        List<String> nestUuidList = baseNestService.listAllUuidsCache();

        // 查询基站ID

        for (String nestUuid : nestUuidList) {

            uosMqttService.clientResetByUuid(nestUuid);
            WsNestListDto wsNestListDto = wsSchedulingService.buildNestListDtoMessage(nestUuid);
            WS_NEST_LIST_DTO_MAP.put(nestUuid, wsNestListDto);
        }
    }

    @Slf4j
    @Component
    public static class NestStateInit implements ApplicationListener<ApplicationStartedEvent> {

        @Resource
        private NestStateProcessor nestStateProcessor;

        @Resource
        private ExecutorService mqttNestExecutor;

        @Override
        public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {

            mqttNestExecutor.submit(() -> {
                log.info("#NestStateInit.onApplicationEvent# run start.");
                long start = System.currentTimeMillis();
                nestStateProcessor.init();
                long end = System.currentTimeMillis();
                log.info("#NestStateInit.onApplicationEvent# run={}", (end - start));
            });
        }
    }
}
