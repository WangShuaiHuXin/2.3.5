package com.imapcloud.nest.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.enums.AirLineTypeEnum;
import com.imapcloud.nest.enums.FocalModeEnum;
import com.imapcloud.nest.enums.TaskModeEnum;
import com.imapcloud.nest.enums.TaskMoldEnum;
import com.imapcloud.nest.mapper.TaskMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.reqDto.DeviceForLineDto;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.*;
import com.imapcloud.nest.pojo.vo.resp.TaskRespVO;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.utils.airline.AirLineBuildUtil;
import com.imapcloud.nest.utils.airline.AirLineParams;
import com.imapcloud.nest.utils.airline.PointCloudWaypoint;
import com.imapcloud.nest.utils.airline.WaypointUtil;
import com.imapcloud.nest.utils.mongo.service.MongoNestAndAirService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.DJIAirLineHandleDO;
import com.imapcloud.nest.v2.manager.dataobj.in.Mavic3KmzHandleDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.event.DJIAirLineHandleEvent;
import com.imapcloud.nest.v2.manager.event.Mavic3DJKmzEvent;
import com.imapcloud.nest.v2.manager.feign.OrgServiceClient;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.dto.UnitEntityDTO;
import com.imapcloud.nest.v2.service.dto.in.DJITaskFileInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaAirLineDTO;
import com.imapcloud.nest.v2.service.dto.in.GridManageInDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestLocationOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DJITaskOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DynamicAirLineOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TaskOutDTO;
import com.imapcloud.sdk.pojo.constant.ActionTypeEnum;
import com.imapcloud.sdk.pojo.constant.FlightPathModeEnum;
import com.imapcloud.sdk.pojo.constant.HeadingModeEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.service.impl.DjiAirLineServiceImpl;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.pojo.constant.*;
import com.imapcloud.sdk.pojo.entity.CpsVersionCode;
import com.imapcloud.sdk.pojo.entity.Waypoint;
import com.imapcloud.sdk.pojo.entity.WaypointAction;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 任务表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskEntity> implements TaskService {

    @Autowired
    private MissionService missionService;
    @Autowired
    private MissionParamService missionParamService;
    @Autowired
    private AirLineService airLineService;
    @Autowired
    private StationDeviceService stationDeviceService;
    @Autowired
    private StationCheckpointService stationCheckpointService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SysTaskTagService sysTaskTagService;
    @Autowired
    private SysTagService sysTagService;
    @Autowired
    private SysUnitService sysUnitService;
    @Autowired
    private TaskFineInspZipRelService taskFineInspZipRelService;
    @Autowired
    private TaskFineInspTowerRelService taskFineInspTowerRelService;
    @Autowired
    private FineInspTowerService fineInspTowerService;
    @Autowired
    private FineInspZipService fineInspZipService;
    @Autowired
    private MissionRecordsService missionRecordsService;
    @Autowired
    private MongoNestAndAirService mongoNestAndAirService;

    @Autowired
    private BaseNestService baseNestService;

    @Resource
    private DJITaskFileService djiTaskFileService;

    @Resource
    private OrgServiceClient orgServiceClient;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private BaseUavService baseUavService;

    @Resource
    private GridService gridService;

    @Resource
    private GridMissionService gridMissionService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private FileManager fileManager;

    @Transactional(rollbackFor = {NestException.class})
    @Override
    public boolean saveTask(TaskDto dto) {
        //保存task表，保存air_line表，保存mission_param表，保存mission表
        TaskEntity task = new TaskEntity();
        task.setName(dto.getTaskName());
        task.setDescription(dto.getDescription());
        task.setNestId(dto.getNestId());
        task.setType(dto.getTaskTypeId());

        //保存air_line表
        AirLineEntity airLine = new AirLineEntity();
        airLine.setName(dto.getAirLineName());
        airLine.setType(dto.getAirLineType());
        airLine.setPointCount(dto.getWaypointCount());
        airLine.setWaypoints(dto.getAirLineJson());
        if (dto.getAirLineId() != null) {
            airLine.setId(dto.getAirLineId());
            airLineService.updateById(airLine);
        }
        airLineService.save(airLine);


        //保存mission_param表
        MissionParamEntity mp = new MissionParamEntity();
        mp.setAutoFlightSpeed(dto.getAutoFlightSpeed());
        mp.setGotoFirstWaypointMode(dto.getGotoFirstWaypointMode());
        mp.setFinishAction(dto.getFinishAction());
        mp.setHeadingMode(dto.getHeadingMode());
        mp.setFlightPathMode(dto.getFlightPathMode());
        mp.setStartStopPointAltitude(dto.getStartStopPointAltitude());
        missionParamService.save(mp);

        //保存mission表
        MissionEntity me = new MissionEntity();
        me.setName(dto.getMissionName());
        me.setUuid(UuidUtil.createNoBar());
        //像这种已经解析过的航线的只有一个架次，因此架次顺序设置为1
        me.setSeqId(1);
        me.setAirLineId(airLine.getId());
        me.setTaskId(dto.getTaskTypeId());
        me.setMissionParamId(mp.getId());
        missionService.save(me);

        return save(task);
    }

    @Override
    public boolean saveTaskIsOuterAirLine(TaskDto dto) {

        return false;
    }


    @Override
    public List<TaskEntity> listTask() {
        return this.list();
    }

    @Resource
    private OrgAccountService orgAccountService;

    @Override
    public RestRes listTaskListDto(ListTaskListDtoParam param) {
        String nestId = param.getNestId();
        Integer tagId = param.getTagId();
        Integer currPage = param.getCurrPage();
        Integer pageSize = param.getPageSize();
        boolean paging = param.isPaging();
        Map<String, Object> map = new HashMap<>(2);
        if (tagId == null || nestId == null) {
            return RestRes.noDataWarn();
        }
        List<TaskEntity> taskEntityList = null;

        // 查询当前登录人的单位ID
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();

        LambdaQueryWrapper<TaskEntity> taskEntityLambdaQueryWrapper = new QueryWrapper<TaskEntity>().lambda().eq(TaskEntity::getBaseNestId, nestId).eq(TaskEntity::getDeleted, false).orderByDesc(TaskEntity::getModifyTime);
        //非超管，按单位查询
        if (org.springframework.util.StringUtils.hasText(orgCode)) {
            taskEntityLambdaQueryWrapper.likeRight(TaskEntity::getOrgCode, orgCode);
        }
        if (param.getTaskType() != null) {
            taskEntityLambdaQueryWrapper.eq(TaskEntity::getType, param.getTaskType());
        }

        if (!paging) {
            //如果是tagId为0的话，就表示需要查全部
            if (tagId == 0) {
                taskEntityList = baseMapper.selectList(taskEntityLambdaQueryWrapper);
            } else {
                List<SysTaskTagEntity> taskTagEntities = sysTaskTagService.list(new QueryWrapper<SysTaskTagEntity>().eq("tag_id", tagId));
                if (CollectionUtil.isEmpty(taskTagEntities)) {
                    return RestRes.noDataWarn();
                }
                List<Integer> tagIdList = taskTagEntities.stream().map(SysTaskTagEntity::getTaskId).collect(Collectors.toList());
                taskEntityList = baseMapper.selectList(taskEntityLambdaQueryWrapper.in(TaskEntity::getId, tagIdList));
            }
        } else {
            Page<TaskEntity> page = new Page<>(currPage, pageSize);
            Page<TaskEntity> taskEntityPage;
            if (tagId == 0) {
                taskEntityPage = baseMapper.selectPage(page, taskEntityLambdaQueryWrapper);
                taskEntityList = taskEntityPage.getRecords();
            } else {
                List<SysTaskTagEntity> taskTagEntities = sysTaskTagService.list(new QueryWrapper<SysTaskTagEntity>().eq("tag_id", tagId));
                if (CollectionUtil.isEmpty(taskTagEntities)) {
                    return RestRes.noDataWarn();
                }
                List<Integer> tagIdList = taskTagEntities.stream().map(SysTaskTagEntity::getTaskId).collect(Collectors.toList());
                taskEntityPage = baseMapper.selectPage(page, taskEntityLambdaQueryWrapper.in(TaskEntity::getId, tagIdList));
                taskEntityList = taskEntityPage.getRecords();
            }
            map.put("pages", taskEntityPage.getPages());
            map.put("size", taskEntityPage.getSize());
            map.put("total", taskEntityPage.getTotal());
            map.put("current", taskEntityPage.getCurrent());
        }

        if (CollectionUtils.isEmpty(taskEntityList)) {
            return RestRes.noDataWarn();
        }

        List<Integer> taskIdList = taskEntityList.stream().map(TaskEntity::getId).collect(Collectors.toList());
        List<MissionEntity> emList = missionService.listByTaskIds(taskIdList);
        if (CollectionUtils.isEmpty(emList)) {
            return RestRes.noDataWarn();
        }
        Map<Integer, List<MissionEntity>> meMap = emList.stream().collect(Collectors.groupingBy(MissionEntity::getTaskId));
        List<Integer> airLineIdList = emList.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
        List<Integer> missionParamIds = emList.stream().map(MissionEntity::getMissionParamId).collect(Collectors.toList());
        Map<Integer, Integer> mIdAndMpIdMap = emList.stream().collect(Collectors.toMap(MissionEntity::getId, MissionEntity::getMissionParamId));

        List<AirLineEntity> airLineEntities = airLineService.listPredicMilesAndMergeCountByIdList(airLineIdList);
        if (CollectionUtils.isEmpty(airLineEntities)) {
            return RestRes.noDataWarn();
        }


        List<MissionParamEntity> missionParamEntityList = missionParamService
                .lambdaQuery()
                .eq(MissionParamEntity::getDeleted, false)
                .select(MissionParamEntity::getStartStopPointAltitude, MissionParamEntity::getId)
                .in(MissionParamEntity::getId, missionParamIds).list();
        Map<Integer, Double> takeOffLandAltMap = missionParamEntityList.stream().collect(Collectors.toMap(MissionParamEntity::getId, MissionParamEntity::getStartStopPointAltitude));

        List<TaskListDto> tdlList = new ArrayList<>(taskEntityList.size());
        for (int i = 0; i < taskEntityList.size(); i++) {
            TaskEntity te = taskEntityList.get(i);
            TaskListDto taskListDto = new TaskListDto();
            taskListDto.setId(te.getId());
            taskListDto.setName(te.getName());
            taskListDto.setRemarks(te.getDescription());
            taskListDto.setType(te.getType());
            taskListDto.setModifyTime(te.getModifyTime());
            taskListDto.setSubType(te.getSubType());
            List<MissionEntity> mes = meMap.get(te.getId());
            if (CollectionUtils.isEmpty(mes)) {
                continue;
            }
            List<Vehicles> vehiclesList = new ArrayList<>(mes.size());
            for (int j = 0; j < mes.size(); j++) {
                Vehicles vehicles = new Vehicles();
                MissionEntity me = mes.get(j);
                vehicles.setId(me.getId());
                vehicles.setCode(me.getName());
                vehicles.setTakeOffLandAlt(takeOffLandAltMap.get(mIdAndMpIdMap.get(me.getId())).intValue());
                Optional<AirLineEntity> first = airLineEntities.stream().filter(ale -> ale.getId().equals(me.getAirLineId())).limit(1).findFirst();
                first.ifPresent(ale -> {
                    vehicles.setWaypointsNum(ale.getMergeCount());
                    vehicles.setFlightDistance(ale.getPredicMiles());
                    vehicles.setPhotoCount(ale.getPhotoCount());
                    vehicles.setVideoCount(ale.getVideoCount());
                    vehicles.setVideoLength(ale.getVideoLength());
                });
                vehiclesList.add(vehicles);
            }
            taskListDto.setVehicles(vehiclesList);
            tdlList.add(taskListDto);
        }
        if (paging) {
            map.put("records", tdlList);
        } else {
            map.put("taskList", tdlList);
        }
        return RestRes.ok(map);
    }

    @Override
    public TaskEntity selectById(Integer id) {
        return baseMapper.selectById(id);
    }

    @Override
    public RestRes listTaskOfManageTaskDtoNoPage(String nestId) {
        if (nestId != null) {
//            List<TaskEntity> list = baseMapper.batchSelectByNestId(nestId);
            String visibleOrgCode = TrustedAccessTracerHolder.get().getOrgCode();
            List<TaskEntity> list = baseMapper.batchSelectByBaseNestId(nestId, visibleOrgCode + SymbolConstants.PERCENT_SIGN);
            Map<String, Object> resultMap = new HashMap<>(2);
            resultMap.put("list", list);
            return RestRes.ok(resultMap);
        }
        return RestRes.noDataWarn();
    }

    @Override
    public RestRes listTaskOfManageTaskDtoByPages(ListTaskRequestParamDto dto) {
        IPage<TaskEntity> page = new Page<>(dto.getCurrPage(), dto.getPageSize());
        LambdaQueryWrapper<TaskEntity> lambdaQueryWrapper = new QueryWrapper<TaskEntity>()
                .lambda()
                .eq(TaskEntity::getDeleted, false)
                .orderByDesc(TaskEntity::getModifyTime);
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        if (org.springframework.util.StringUtils.hasText(orgCode)) {
            lambdaQueryWrapper.likeRight(TaskEntity::getOrgCode, orgCode);
        }
        if (dto.getKeyword() != null) {
            lambdaQueryWrapper.like(TaskEntity::getName, dto.getKeyword());
        }
        // 网格航线单独处理
        if (dto.getMold() == 0 && dto.getTaskType() != null && dto.getTaskType() == 14) {
            lambdaQueryWrapper.likeRight(TaskEntity::getOrgCode, dto.getUnitId());
            // 如果传参有基站，基站过滤
            if (dto.getNestId() != null) {
                lambdaQueryWrapper.eq(TaskEntity::getBaseNestId, dto.getNestId());
            }
            // 机巢
        } else if (dto.getMold() == 0) {
            lambdaQueryWrapper.eq(TaskEntity::getBaseNestId, dto.getNestId());
            //移动终端
        } else if (dto.getMold() == 1) {
            lambdaQueryWrapper.eq(TaskEntity::getOrgCode, dto.getUnitId());
        }
        // 通用基站分页，网格航线过滤
        if (dto.getTaskType() == null || (dto.getTaskType() != null && dto.getTaskType() != 14)) {
            lambdaQueryWrapper.ne(TaskEntity::getType, 14);
        }

        if (dto.getTagId() != null) {
            List<SysTaskTagEntity> taskTagEntities = sysTaskTagService.list(new QueryWrapper<SysTaskTagEntity>().eq("tag_id", dto.getTagId()));
            if (CollectionUtil.isNotEmpty(taskTagEntities)) {
                List<Integer> taskIds = taskTagEntities.stream().map(SysTaskTagEntity::getTaskId).collect(Collectors.toList());
                lambdaQueryWrapper.in(TaskEntity::getId, taskIds);
            } else {
                //如果没查到，则直接查出特殊的。或者换别的方案
                lambdaQueryWrapper.eq(TaskEntity::getId, 0);
            }
        }
        if (dto.getTaskType() != null) {
            lambdaQueryWrapper.eq(TaskEntity::getType, dto.getTaskType());
        }
        IPage<TaskEntity> taskEntityIPage = baseMapper.selectPage(page, lambdaQueryWrapper);
        List<TaskRespVO> taskRespVOList = Lists.newLinkedList();
        // 查询单位名称
        List<TaskEntity> taskEntityList = taskEntityIPage.getRecords();
        try {
            if (CollUtil.isNotEmpty(taskEntityList)) {
                Result<List<OrgSimpleOutDO>> listResult = orgServiceClient.listOrgDetails(taskEntityList.stream().map(TaskEntity::getOrgCode).collect(Collectors.toList()));
                List<OrgSimpleOutDO> orgSimpleOutDOList = ResultUtils.getData(listResult);
                Map<String, String> collect;
                if (CollUtil.isNotEmpty(orgSimpleOutDOList)) {
                    collect = orgSimpleOutDOList.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName, (key1, key2) -> key1));
                } else {
                    collect = Collections.emptyMap();
                }
                List<String> baseNestIds = taskEntityList.stream().map(TaskEntity::getBaseNestId).collect(Collectors.toList());
                Map<String, String> nestNameMap = baseNestService.getNestNameMap(baseNestIds);

                for (TaskEntity taskEntity : taskEntityList) {
                    TaskRespVO taskRespVO = new TaskRespVO();
                    BeanUtils.copyProperties(taskEntity, taskRespVO);
                    taskRespVO.setOrgName(collect.getOrDefault(taskRespVO.getOrgCode(), "-"));
                    taskRespVO.setBaseNestName(nestNameMap.getOrDefault(taskRespVO.getBaseNestId(), "-"));
                    taskRespVOList.add(taskRespVO);
                }
            }
        } catch (Exception e) {
            log.error("#TaskServiceImpl.listTaskOfManageTaskDtoByPages#", e);
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_UNIT.getContent()));
        }

        IPage<TaskRespVO> result = new Page<TaskRespVO>();
        result.setPages(taskEntityIPage.getPages());
        result.setRecords(taskRespVOList);
        result.setTotal(taskEntityIPage.getTotal());
        result.setSize(taskEntityIPage.getSize());
        result.setCurrent(taskEntityIPage.getCurrent());

        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("taskEntityIPage", result);
        return RestRes.ok(resultMap);
    }


    @Override
    public RestRes updateTaskDetailsDto(TaskDetailsDto dto) {
        boolean isDjiType = this.DjiType(dto.getNestId());
        boolean isDjiKml = Objects.equals(TaskModeEnum.DJI_KML.getValue(), dto.getTaskType());
        String orgCode = dto.getUnitId();
        //设置默认标签
        if (dto.getTagId() == null && orgCode != null) {
            dto.setTagId(setUnitTag(orgCode, dto.getNestId()));
        }
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(dto.getId());
        taskEntity.setName(dto.getName());
        taskEntity.setDescription(dto.getDescription());
        taskEntity.setModifyTime(LocalDateTime.now());
        boolean teRes = this.updateById(taskEntity);

        Integer tagId = dto.getTagId();
        SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", taskEntity.getId()));
        if (sysTaskTagEntity != null) {
            if (tagId != null) {
                sysTaskTagEntity.setTagId(tagId);
            } else {
                sysTaskTagEntity.setTagId(-1);
            }
            sysTaskTagService.updateById(sysTaskTagEntity);
        } else {
            SysTaskTagEntity stte = new SysTaskTagEntity();
            stte.setCreateTime(LocalDateTime.now());
            stte.setModifyTime(LocalDateTime.now());
            if (tagId != null) {
                stte.setTagId(tagId);
            } else {
                stte.setTagId(-1);
            }
            stte.setTaskId(taskEntity.getId());
            boolean stteRes = sysTaskTagService.save(stte);
        }

        List<MissionEntity> meList = missionService.list(new QueryWrapper<MissionEntity>().lambda().eq(MissionEntity::getTaskId, dto.getId()));
        if (CollectionUtils.isEmpty(meList)) {
            return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_RELEVANT_SORTIE_DATA_CANNOT_BE_CHECKED.getContent()));
        }


        Map<Integer, String> airLineMap = dto.getAirLineMap();
        Map<Integer, String> finalAirLineMap = airLineMap;
        List<AirLineEntity> aleList = meList.stream().map(me -> {
            String airLineJson = finalAirLineMap.get(me.getAirLineId());
            AirLineEntity ale = new AirLineEntity();
            ale.setId(me.getAirLineId());
            ale.setName(dto.getName() + "-航线" + me.getName().substring(me.getName().length() - 1));
            ale.setWaypoints(airLineJson);
            if (isDjiKml || isDjiType) {
                ale.setDjiWaypoints(dto.getDjiAirLineMap().get(me.getAirLineId()));
            }
            ale.setMergeCount(dto.getPointCount());
            ale.setPointCount(dto.getPointCount());
            ale.setPhotoCount(dto.getPhotoCount());
            ale.setPredicMiles(dto.getPredictMiles());
            ale.setPredicTime(dto.getPredictFlyTime());
            ale.setVideoLength(dto.getVideoLength());
            ale.setVideoCount(dto.getVideoCount());
            ale.setModifyTime(LocalDateTime.now());
            ale.setShowInfo(dto.getShowInfo());
            ale.setAbsolute(dto.getAbsolute() == null || dto.getAbsolute());
            if (dto.getFocalMode() != null) {
                ale.setFocalMode(dto.getFocalMode().getVal());
            }
            return ale;
        }).collect(Collectors.toList());


        //批量修改航线
        boolean aleRes = airLineService.updateBatchById(aleList);

        MissionParamEntity mpe = new MissionParamEntity();
        mpe.setId(meList.get(0).getMissionParamId());
        mpe.setAutoFlightSpeed(dto.getAutoFlightSpeed());
        mpe.setSpeed(dto.getSpeed());
        mpe.setHeadingMode(HeadingModeEnum.getValueByName(dto.getHeadingMode()));
        mpe.setStartStopPointAltitude(dto.getStartStopAlt());
        if (dto.getFlightPathMode() != null) {
            mpe.setFlightPathMode(dto.getFlightPathMode().getValue());
        }
        boolean mpeRes = missionParamService.updateById(mpe);

        meList.forEach(me -> {
            me.setName(dto.getName() + "-架次" + me.getName().substring(me.getName().length() - 1));
            me.setUuid(UuidUtil.createNoBar());
            me.setModifyTime(LocalDateTime.now());
        });
        missionService.saveOrUpdateBatch(meList);
        if (isDjiType) {
            DJIAirLineHandleDO djiAirLineHandleDO = new DJIAirLineHandleDO()
                    .setTaskType(dto.getTaskType())
                    .setTaskId(taskEntity.getId())
                    .setNestId(dto.getNestId())
                    .setAirLineList(aleList)
                    .setMissionList(meList)
                    .setSave(Boolean.FALSE)
                    .setTaskFileId(dto.getTaskFileId())
                    .setDjiAirLineMap(dto.getDjiAirLineMap());
            this.applicationContext.publishEvent(new DJIAirLineHandleEvent(djiAirLineHandleDO));
        }
        //御三大疆航线适配
        if (isDjiKml && NestTypeEnum.S110_MAVIC3.getValue() == baseNestService.getNestType(dto.getNestId()).getValue()) {
            Mavic3KmzHandleDO mavic3KmzHandleDO = new Mavic3KmzHandleDO();
            mavic3KmzHandleDO.setTaskType(dto.getTaskType());
            mavic3KmzHandleDO.setTaskId(taskEntity.getId());
            mavic3KmzHandleDO.setNestId(dto.getNestId());
            mavic3KmzHandleDO.setSave(Boolean.FALSE);
            mavic3KmzHandleDO.setAirLineList(aleList);
            mavic3KmzHandleDO.setMissionList(meList);
            mavic3KmzHandleDO.setTaskFileId(dto.getTaskFileId());
            mavic3KmzHandleDO.setDjiAirLineMap(dto.getDjiAirLineMap());
            this.applicationContext.publishEvent(new Mavic3DJKmzEvent(mavic3KmzHandleDO));

        }
        //本地航线、大疆本地航线
        if (isDjiKml) {
            //大疆-挂钩文件
            UnifyNestAirLineResult unifyNestAirLineResult = buildUnifyNestAirLine(aleList.get(0).getDjiWaypoints(), dto.getNestId(), taskEntity.getId());
            if (unifyNestAirLineResult.isResult()) {
                List<Waypoint> waypointsList = unifyNestAirLineResult.getWaypointList();
                List<Waypoint> waypoints = WaypointUtil.transWaypointAction(waypointsList);
                Integer aleId = aleList.get(0).getId();
                AirLineEntity airLineEntity = new AirLineEntity();
                airLineEntity.setId(aleId);
                String djiJson = JSONArray.toJSONString(waypoints);
                airLineEntity.setOriginalWaypoints(djiJson);
                airLineService.updateById(airLineEntity);
            }
        }


        if (teRes && aleRes && mpeRes) {
            Map<String, Object> resMap = new HashMap<>(2);
            resMap.put("taskId", taskEntity.getId());
            return RestRes.ok(resMap);
        }
        clearTagNameRedisCache(dto.getId());
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_TASK_SAVE_MODIFICATION.getContent()));
    }

    /**
     * 判断是否是大疆基站
     *
     * @param baseNestId
     * @return
     */
    private boolean DjiType(String baseNestId) {
        NestTypeEnum nestTypeEnum = this.baseNestService.getNestTypeByNestIdCache(baseNestId);
        if (NestTypeEnum.DJI_DOCK.equals(nestTypeEnum)) {
            return true;
        }
        return false;
    }

    private Boolean isMavicKmzType(TaskEntity taskEntity) {
        if (NestTypeEnum.S110_MAVIC3.equals(this.baseNestService.getNestTypeByNestIdCache(taskEntity.getBaseNestId())) && TaskModeEnum.DJI_KML.getValue() == taskEntity.getType()) {
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public RestRes saveTaskDetailsDto(TaskDetailsDto dto) {
        boolean isDjiType = this.DjiType(dto.getNestId());
        boolean isDjiKml = Objects.equals(TaskModeEnum.DJI_KML.getValue(), dto.getTaskType());
        String orgCode = dto.getUnitId();
        //设置默认标签
        if (dto.getTagId() == null && orgCode != null) {
            dto.setTagId(setUnitTag(orgCode, dto.getNestId()));
        }

        if (dto.getAirLineMap() == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSING_ROUTE_DATA.getContent()));
        }
        //适配大疆
        if (isDjiKml && dto.getDjiAirLineMap() == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSING_ROUTE_DATA.getContent()));
        }

        //保存task表，保存air_line表，保存mission_param表，保存mission表
        TaskEntity task = new TaskEntity();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        if (dto.getMold() == 0) {
            task.setBaseNestId(dto.getNestId());
            task.setOrgCode(orgCode);
            task.setMold(0);
        } else {
            task.setOrgCode(orgCode);
            task.setMold(1);
        }

        task.setType(dto.getTaskType());
        if (dto.getTaskType().equals(TaskModeEnum.SUBSSTATION_PLANING.getValue())) {
            task.setSubType(dto.getSubType());
        }
        task.setCreatorId(Long.valueOf(dto.getAccountId()));
        boolean taskRes = this.save(task);

        //保存missionParam表
        MissionParamEntity mpe = new MissionParamEntity();
        mpe.setAutoFlightSpeed(dto.getAutoFlightSpeed());
        mpe.setSpeed(dto.getSpeed());
        mpe.setHeadingMode(HeadingModeEnum.getValueByName(dto.getHeadingMode()));
        mpe.setStartStopPointAltitude(dto.getStartStopAlt());
        if (dto.getFlightPathMode() != null) {
            mpe.setFlightPathMode(dto.getFlightPathMode().getValue());
        }
        boolean mpRes = missionParamService.save(mpe);

        //保存SysTaskTag表
        Integer tagId = dto.getTagId();
        SysTaskTagEntity stte = new SysTaskTagEntity();
        stte.setCreateTime(LocalDateTime.now());
        stte.setModifyTime(LocalDateTime.now());
        stte.setTaskId(task.getId());
        if (tagId != null) {
            stte.setTagId(tagId);
        } else {
            stte.setTagId(-1);
        }
        sysTaskTagService.save(stte);

        Map<Integer, String> airLineMap = dto.getAirLineMap();
        if (!CollectionUtils.isEmpty(airLineMap)) {
            Set<Map.Entry<Integer, String>> entries = airLineMap.entrySet();
            //由于一个任务不能编辑架次，因此一个任务的多条航线指定同一个任务参数表即可
            List<AirLineEntity> aleList = new ArrayList<>(airLineMap.size());
            List<MissionEntity> meList = new ArrayList<>(airLineMap.size());
            int index = 1;
            for (Map.Entry<Integer, String> entry : entries) {
                String waypoints = entry.getValue();
                AirLineEntity ale = new AirLineEntity();
                ale.setType(dto.getAirLineType());
                ale.setName(dto.getName() + "-航线" + index);
                ale.setWaypoints(waypoints);
                if (isDjiKml || isDjiType) {
                    ale.setDjiWaypoints(dto.getDjiAirLineMap().get(0));
                }
                ale.setPointCount(dto.getPointCount());
                ale.setMergeCount(dto.getPointCount());
                ale.setPhotoCount(dto.getPhotoCount());
                ale.setPredicMiles(dto.getPredictMiles());
                ale.setPredicTime(dto.getPredictFlyTime());
                ale.setVideoCount(dto.getVideoCount());
                ale.setVideoLength(dto.getVideoLength());
                ale.setAbsolute(dto.getAbsolute() == null || dto.getAbsolute());
                ale.setShowInfo(dto.getShowInfo());
                if (dto.getFocalMode() != null) {
                    ale.setFocalMode(dto.getFocalMode().getVal());
                }
                aleList.add(ale);

                MissionEntity me = new MissionEntity();
                me.setName(dto.getName() + "-架次" + index);
                me.setUuid(UuidUtil.createNoBar());
                me.setSeqId(index);
                me.setTaskId(task.getId());
                me.setOrgCode(task.getOrgCode());
                meList.add(me);
                index++;
            }

            boolean aleRes = airLineService.saveBatch(aleList);

            for (int i = 0; i < meList.size(); i++) {
                MissionEntity me = meList.get(i);
                me.setMissionParamId(mpe.getId());
                me.setAirLineId(aleList.get(i).getId());
            }
            boolean meRes = missionService.saveBatch(meList);
            if (CollectionUtil.isNotEmpty(meList)) {
                List<Integer> collect = meList.stream().map(MissionEntity::getId).collect(Collectors.toList());
                if (isDjiKml && StringUtils.isNotEmpty(dto.getTaskFileId())) {
                    djiTaskFileService.updateMissionInfo(collect.get(0), dto.getTaskFileId(), task.getId());
                }
            }
            if (taskRes && mpRes && aleRes && meRes) {
                Map<String, Object> resMap = new HashMap<>(2);
                resMap.put("taskId", task.getId());
                clearTagNameRedisCache(dto.getId());
                if (isDjiType) {
                    DJIAirLineHandleDO djiAirLineHandleDO = new DJIAirLineHandleDO()
                            .setTaskType(task.getType())
                            .setTaskId(task.getId())
                            .setNestId(task.getBaseNestId())
                            .setAirLineList(aleList)
                            .setMissionList(meList)
                            .setSave(Boolean.TRUE)
                            .setTaskFileId(dto.getTaskFileId())
                            .setDjiAirLineMap(dto.getDjiAirLineMap());
                    this.applicationContext.publishEvent(new DJIAirLineHandleEvent(djiAirLineHandleDO));
                }
                if (isDjiKml) {
                    //大疆-挂钩文件

                    UnifyNestAirLineResult unifyNestAirLineResult = buildUnifyNestAirLine(aleList.get(0).getDjiWaypoints(), dto.getNestId(), task.getId());
                    if (unifyNestAirLineResult.isResult()) {
                        List<Waypoint> waypointsList = unifyNestAirLineResult.getWaypointList();
                        List<Waypoint> waypoints = WaypointUtil.transWaypointAction(waypointsList);
                        Integer aleId = aleList.get(0).getId();
                        AirLineEntity airLineEntity = new AirLineEntity();
                        airLineEntity.setId(aleId);
                        String djiJson = JSONArray.toJSONString(waypoints);
                        airLineEntity.setOriginalWaypoints(djiJson);
                        airLineService.updateById(airLineEntity);
                    }

                }

                return RestRes.ok(resMap);
            }


        } else {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_ROUTE_MISSION_SAVE_FAILED.getContent()));
        }
        return RestRes.noDataWarn();
    }

//    /**
//     * 处理大疆航线
//     *
//     * @param djiAirLine
//     * @param taskId
//     * @param taskFileId
//     */
//    public void handleDjiTask(Boolean isSave, Integer taskType, String djiAirLine, Integer taskId, String taskFileId
//            , String djiWaypoints  , String nestId , Integer missionId) {
//        //新增 大疆-航点模式、本地模式
//        //查询无人机型号、相机型号
//        BaseUavInfoOutDTO baseUavInfoOutDTO = baseUavService.getUavInfoByNestId(nestId);
//        log.info("【handleDjiTask】-> {}", baseUavInfoOutDTO.toString());
//        String path = new AirLineProxy(DjiAirLineServiceImpl.class)
//                .proxyTransformJsonToKmzMainImpl(djiAirLine
//                        , new Object[]{baseUavInfoOutDTO == null ? String.valueOf(AircraftCodeEnum.M30.getValue()) : baseUavInfoOutDTO.getType()
//                                , baseUavInfoOutDTO == null ? DJICameraEnum.M30_CAMERA.getCode() : baseUavInfoOutDTO.getCameraName()});
//        //航点模式
//        if (TaskModeEnum.CUSTOM.getValue().equals(taskType)) {
//            DJITaskOutDTO.DJITaskFileQueryOutDTO djiTaskFileInfoOutDTO = this.djiTaskFileService.queryOutDTO(
//                    new DJITaskFileInDTO.DJITaskFileQueryInDTO().setTaskId(String.valueOf(taskId)));
//            taskFileId = Optional.ofNullable(djiTaskFileInfoOutDTO).map(DJITaskOutDTO.DJITaskFileQueryOutDTO::getTaskFileId).orElseGet(() -> "");
//        }
//        String wholePath = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), path);
//        if (org.springframework.util.StringUtils.isEmpty(taskFileId)) {
//            InputStream inputStream = MinIoUnit.getObject(path);
//            DJITaskFileInDTO.DJITaskFileAddInDTO addInDTO = new DJITaskFileInDTO.DJITaskFileAddInDTO();
//            addInDTO.setTaskId(String.valueOf(taskId));
//            addInDTO.setFileUrl(wholePath);
//            addInDTO.setFileMd5(MD5.create().digestHex(inputStream));
//            addInDTO.setFileName(path.substring(path.lastIndexOf("/") + 1));
//            addInDTO.setMissionId(String.valueOf(missionId));
//            this.djiTaskFileService.insertTaskFile(addInDTO);
//            return;
//        }
//        DJITaskFileInDTO.DJITaskFileUpdateInDTO updateInDTO = new DJITaskFileInDTO.DJITaskFileUpdateInDTO();
//        updateInDTO.setTaskId(String.valueOf(taskId));
//        updateInDTO.setFileUrl(wholePath);
//        updateInDTO.setFileMd5(MD5.create().digestHex(MinIoUnit.getObject(path)));
//        updateInDTO.setTaskFileId(taskFileId);
//        updateInDTO.setFileName(path.substring(path.lastIndexOf("/") + 1));
//        //更新missionId
//        updateInDTO.setMissionId(String.valueOf(missionId));
//        this.deleteTaskFile(updateInDTO.getTaskFileId(), "");
//        this.djiTaskFileService.updateTaskFile(updateInDTO);
//
//
//    }

    /**
     * 异步删除MinIO文件
     */
    private void deleteTaskFile(String taskFileId, String taskId) {
        DJITaskOutDTO.DJITaskFileQueryOutDTO djiTaskFileInfoOutDTO = this.djiTaskFileService.queryOutDTO(
                new DJITaskFileInDTO.DJITaskFileQueryInDTO().setTaskFileId(String.valueOf(taskFileId)));
        //TODO 改为删除记录
        this.djiTaskFileService.deleteTaskFile(taskFileId);
//        Optional.ofNullable(djiTaskFileInfoOutDTO).map(DJITaskOutDTO.DJITaskFileQueryOutDTO::getFileUrl).ifPresent(x -> {
//            MinIoUnit.rmObjects(CollectionUtil.newArrayList(MinIoUnit.replacePath(x)));
//        });
    }

    @Override
    public RestRes deleteTask(Integer taskId) {
        List<MissionEntity> meList = missionService.list(new QueryWrapper<MissionEntity>().lambda().eq(MissionEntity::getTaskId, taskId).eq(MissionEntity::getDeleted, false));
        List<Integer> meIdList = meList.stream().map(MissionEntity::getId).collect(Collectors.toList());
        List<Integer> mpIdList = meList.stream().map(MissionEntity::getMissionParamId).collect(Collectors.toList());
        List<Integer> alIdList = meList.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
        int meRes = missionService.batchSoftDeleteByIds(meIdList);
        int mpRes = missionParamService.batchSoftDeleteByIds(mpIdList);
        int alRes = airLineService.batchSoftDeleteByIds(alIdList);
        int teRes = baseMapper.softDeleteById(taskId);
        List<Integer> tmp = Lists.newArrayList();
        tmp.add(taskId);
        gridService.delGridManageTask(tmp);
        //删除mongo日志
        deleteAirDroneLogs(taskId);
        this.deleteTaskFile("", String.valueOf(taskId));
        if ((meRes + mpRes + alRes + teRes) > 0) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DELETED_TASK.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_DELETED_TASK.getContent()));
    }

    @Override
    public RestRes batchDeleteTask(List<Integer> taskIdList) {
        if (!CollectionUtils.isEmpty(taskIdList)) {
            List<MissionEntity> meList = missionService.listByTaskIds(taskIdList);
            List<Integer> meIdList = meList.stream().map(MissionEntity::getId).collect(Collectors.toList());
            List<Integer> mpIdList = meList.stream().map(MissionEntity::getMissionParamId).collect(Collectors.toList());
            List<Integer> alIdList = meList.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
            int meRes = missionService.batchSoftDeleteByIds(meIdList);
            int mpRes = missionParamService.batchSoftDeleteByIds(mpIdList);
            int alRes = airLineService.batchSoftDeleteByIds(alIdList);
            int tRes = baseMapper.batchSoftDeleteByIds(taskIdList);
            gridService.delGridManageTask(taskIdList);
            if ((meRes + mpRes + alRes + tRes) > 0) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_BATCH_DELETE.getContent()));
            }
        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_DELETE_FAILED.getContent()));
    }

    @Transactional(rollbackFor = {RuntimeException.class})
    @Override
    public RestRes copyTask(Integer taskId) {
        TaskEntity te = getById(taskId);
        boolean isDjiType = this.DjiType(te.getBaseNestId());
        te.setCopyCount(te.getCopyCount() == null ? 1 : te.getCopyCount() + 1);
        updateById(te);
        te.setId(null);
        te.setName(te.getName() + "-副本" + te.getCopyCount());
        te.setCopyCount(0);
        save(te);

        // 网格化不允许复制
        if (TaskModeEnum.GRID.getValue().equals(te.getType())) {
            throw new NestException("网格化航线不允许复制，可以编辑已保存的航线或新增航线！");
        }

        List<SysTaskTagEntity> taskTagEntities = sysTaskTagService.list(new QueryWrapper<SysTaskTagEntity>().eq("task_id", taskId));
        if (CollectionUtil.isNotEmpty(taskTagEntities)) {
            Integer destTaskId = te.getId();
            List<SysTaskTagEntity> sysTaskTagEntities = taskTagEntities.stream().peek(it -> {
                it.setId(null);
                it.setTaskId(destTaskId);
            }).collect(Collectors.toList());
            sysTaskTagService.saveBatch(sysTaskTagEntities);
        }
        //这里需要复制task的便签，有多少就要复制多少，确定一下，id是否会展示出来
        List<MissionEntity> meList = missionService.list(new QueryWrapper<MissionEntity>().lambda().eq(MissionEntity::getTaskId, taskId).eq(MissionEntity::getDeleted, false));
        List<Integer> mpIdList = meList.stream().map(MissionEntity::getMissionParamId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(mpIdList)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETERS_NOT_FOUND.getContent()));
        }
        List<MissionParamEntity> mpeList = missionParamService.listByIds(mpIdList);
        mpeList.forEach(mpe -> {
            mpe.setCopyCount((mpe.getCopyCount() == null ? 1 : mpe.getCopyCount()) + 1);
        });
        missionParamService.updateBatchById(mpeList);

        mpeList.forEach(mpe -> {
            mpe.setId(null);
            mpe.setCopyCount(0);
        });

        boolean batchMpeRes = missionParamService.saveBatch(mpeList);


        List<Integer> alIdList = meList.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(alIdList)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROUTE_NOT_FOUND.getContent()));
        }
        List<AirLineEntity> aleList = airLineService.listByIds(alIdList);
        aleList.forEach(ale -> {
            ale.setCopyCount((ale.getCopyCount() == null ? 0 : ale.getCopyCount()) + 1);
        });
        airLineService.updateBatchById(aleList);

        for (int i = 0; i < aleList.size(); i++) {
            AirLineEntity ale = aleList.get(i);
            ale.setId(null);
            ale.setName(te.getName() + "-航线" + (i + 1));
            ale.setCopyCount(0);
        }

        boolean batchAleRes = airLineService.saveBatch(aleList);

        for (int i = 0; i < meList.size(); i++) {
            MissionEntity me = meList.get(i);
            me.setName(te.getName() + "-架次" + (i + 1));
            me.setTaskId(te.getId());
            me.setMissionParamId(mpeList.get(0).getId());
            me.setAirLineId(aleList.get(i).getId());
        }
        boolean batchMeRes = missionService.saveBatch(meList);

        //精细巡检 复制task与zip的关系、与tower的关系
        Boolean taskZipRelRes = null;
        if (TaskModeEnum.DELICACY.getValue().equals(te.getType())) {
            taskZipRelRes = copyTaskAndZipRel(taskId, te.getId());
        }

        //复制的时候，如果是大疆基站的，还需要拷贝kmz数据
        if (isDjiType) {
            //拷贝kmz文件
            copyKmz(taskId, te.getId(), meList.get(0).getId());
        }

        if (this.isMavicKmzType(te)) {
            copyKmz(taskId, te.getId(), meList.get(0).getId());

        }
        if (batchAleRes && batchMeRes && batchMpeRes && (taskZipRelRes == null || taskZipRelRes)) {

            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_MISSION_REPLICATION.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSION_COPY_FAILED.getContent()));
    }

    /**
     * 拷贝kmz
     *
     * @param originTaskId
     * @param newTaskId
     */
    private void copyKmz(Integer originTaskId, Integer newTaskId, Integer missionId) {
        //拷贝taskFile
        DJITaskOutDTO.DJITaskFileQueryOutDTO djiTaskFileInfoOutDTO = this.djiTaskFileService.queryOutDTO(
                new DJITaskFileInDTO.DJITaskFileQueryInDTO().setTaskId(String.valueOf(originTaskId)));
        String fileName = String.format("%s.kmz", UUID.randomUUID().toString());
//        String path = String.format("%s%s"
//                , UploadTypeEnum.TASK_FILE_PATH.getPath()
//                , fileName);
//        MinIoUnit.copy(MinIoUnit.replacePath(djiTaskFileInfoOutDTO.getFileUrl()), path);
        String replicaPath = fileManager.copyFile(djiTaskFileInfoOutDTO.getFileUrl(), null);
        this.djiTaskFileService.insertTaskFile(new DJITaskFileInDTO.DJITaskFileAddInDTO()
                .setTaskId(String.valueOf(newTaskId))
                .setMissionId(String.valueOf(missionId))
//                .setFileUrl(geoaiUosProperties.getStore().getOriginPath() + path)
                .setFileUrl(replicaPath)
                .setFileName(fileName)
//                .setFileMd5(MD5.create().digestHex(MinIoUnit.getObject(path))));
                .setFileMd5(djiTaskFileInfoOutDTO.getFileMd5()));
    }

    @Override
    public RestRes selectTaskDetails(Integer taskId) {
        //查询任务
        TaskEntity te = getOne(new QueryWrapper<TaskEntity>().lambda().eq(TaskEntity::getDeleted, false).eq(TaskEntity::getId, taskId));
        if (te == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_RELEVANT_MISSION_CANNOT_BE_QUERIED.getContent()));
        }

        HashMap<String, Object> resultMap = new HashMap<>(2);

        TaskDetailsDto taskDetailsDto = new TaskDetailsDto();

        taskDetailsDto.setId(te.getId());
        taskDetailsDto.setName(te.getName());
        SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", te.getId()));
        if (sysTaskTagEntity != null) {
            taskDetailsDto.setTagId(sysTaskTagEntity.getTagId());
        }
        taskDetailsDto.setTaskType(te.getType());
        taskDetailsDto.setDescription(te.getDescription());
        if (te.getType().equals(TaskModeEnum.SUBSSTATION_PLANING.getValue())) {
            taskDetailsDto.setSubType(te.getSubType());
        }


        List<MissionEntity> meList = missionService.listMissionByTaskId(taskId);
        if (CollectionUtil.isNotEmpty(meList)) {
            List<Integer> alIdList = meList.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
            List<AirLineEntity> aleList = airLineService.lambdaQuery()
                    .in(AirLineEntity::getId, alIdList)
                    .select(AirLineEntity::getId,
                            AirLineEntity::getMergeCount,
                            AirLineEntity::getPredicMiles,
                            AirLineEntity::getPredicTime,
                            AirLineEntity::getPhotoCount,
                            AirLineEntity::getWaypoints,
                            AirLineEntity::getType,
                            AirLineEntity::getAbsolute,
                            AirLineEntity::getVideoCount,
                            AirLineEntity::getVideoLength,
                            AirLineEntity::getFocalMode,
                            AirLineEntity::getShowInfo,
                            AirLineEntity::getDjiWaypoints
                    )
                    .list();


            double highestAlt = getAirLineHighestAlt2(aleList);
            int pointCount = aleList.stream().mapToInt(ale -> ale.getMergeCount() == null ? 0 : ale.getMergeCount()).sum();
            int photoCount = aleList.stream().mapToInt(ale -> ale.getPhotoCount() == null ? 0 : ale.getPhotoCount()).sum();
            long predicTime = aleList.stream().mapToLong(ale -> ale.getPredicTime() == null ? 0L : ale.getPredicTime()).sum();
            double predicMiles = aleList.stream().mapToDouble(ale -> ale.getPredicMiles() == null ? 0.0 : ale.getPredicMiles()).sum();
            int videoCount = aleList.stream().mapToInt(ale -> ale.getVideoCount() == null ? 0 : ale.getVideoCount()).sum();
            long videoLength = aleList.stream().mapToLong(ale -> ale.getVideoLength() == null ? 0 : ale.getVideoLength()).sum();

            taskDetailsDto.setPointCount(pointCount);
            taskDetailsDto.setPhotoCount(photoCount);
            taskDetailsDto.setPredictFlyTime(predicTime);
            taskDetailsDto.setPredictMiles(predicMiles);
            taskDetailsDto.setVideoCount(videoCount);
            taskDetailsDto.setVideoLength(videoLength);

            Map<Integer, String> airLineMap = aleList.stream().collect(Collectors.toMap(AirLineEntity::getId, AirLineEntity::getWaypoints));
            Map<Integer, String> djiAirLineMap = aleList.stream().collect(Collectors.toMap(AirLineEntity::getId,
                    x -> Optional.ofNullable(x).map(AirLineEntity::getDjiWaypoints).orElseGet(() -> "")));
            taskDetailsDto.setAirLineMap(airLineMap);
            taskDetailsDto.setDjiAirLineMap(djiAirLineMap);
            taskDetailsDto.setAirLineType(aleList.get(0).getType());
            taskDetailsDto.setAbsolute(aleList.get(0).getAbsolute());
            taskDetailsDto.setShowInfo(aleList.get(0).getShowInfo());
            taskDetailsDto.setFocalMode(FocalModeEnum.getInstance(aleList.get(0).getFocalMode()));

            MissionParamEntity mpe = missionParamService.getById(meList.get(0).getMissionParamId());
            taskDetailsDto.setHeadingMode(mpe.getHeadingMode() == -1 ? "未知" : HeadingModeEnum.getInstance(mpe.getHeadingMode()).name());
            taskDetailsDto.setStartStopAlt(mpe.getStartStopPointAltitude());
            taskDetailsDto.setAutoFlightSpeed(mpe.getAutoFlightSpeed());
            taskDetailsDto.setSpeed(mpe.getSpeed());
            taskDetailsDto.setFlightPathMode(FlightPathModeEnum.getInstance(mpe.getFlightPathMode()));

            resultMap.put("taskDetailsDto", taskDetailsDto);
            resultMap.put("highestAlt", highestAlt + 10);

            return RestRes.ok(resultMap);
        }


        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_RELEVANT_MISSION_CANNOT_BE_QUERIED.getContent()));
    }


    @Override
    public Integer getTaskTypeById(Integer id) {
        return baseMapper.selectTypeById(id);
    }

    @Override
    public RestRes getDynamicAirLine(DynamicReqDto dynamicReqDto) {
//        NestEntity nest = nestService.getById(dynamicReqDto.getNestId());
        DynamicAirLineOutDTO dynamicAirLineParam = baseNestService.getDynamicAirLineParam(dynamicReqDto.getNestId());
        List<StationDeviceEntity> deviceEntities = stationDeviceService.listByIds(dynamicReqDto.getDeviceIds());
        if (deviceEntities != null && !deviceEntities.isEmpty()) {
            //存放拍照区域的集合
            List<PhotoPos> photoPoList = new ArrayList<>();
            //存放id,uuid
            List<Map> pointUuidList = new LinkedList<>();
            //对设备进行分区
            Map<Integer, List<StationDeviceEntity>> groupUuids = deviceEntities.stream().collect(Collectors.groupingBy(StationDeviceEntity::getParentId));
            //重新组装
            for (Integer key : groupUuids.keySet()) {
                StationDeviceEntity dv = stationDeviceService.getById(key);
                //添加入口点标识
                if (dv.getEntryAssistPoint() != null && dv.getEntryAssistPoint() != "") {
                    Map entry = new HashMap();
                    entry.put("id", dv.getEntryAssistPoint());
                    pointUuidList.add(entry);
                }
                List<StationCheckpointEntity> pointList = stationCheckpointService.selectInDeviceIdList(
                        groupUuids.get(key).stream().map(StationDeviceEntity::getId).collect(Collectors.toList()));
                for (StationCheckpointEntity checkpoint : pointList) {
                    Map pointMap = new HashMap();
                    pointMap.put("id", checkpoint.getUuid());
                    pointUuidList.add(pointMap);
                }
                PhotoPos photoPo = new PhotoPos();
                photoPo.setPid(dv.getUuid());
                photoPo.setPos(dv.getAlginpoints());
                photoPo.setPhotoList(pointUuidList);
                photoPoList.add(photoPo);
            }

            DeviceForLineDto deviceForLine = new DeviceForLineDto();
            String requestId = Long.toString(System.currentTimeMillis());
            deviceForLine.setRequestId(requestId);
            deviceForLine.setSplit(1);
            deviceForLine.setPhotoPos(photoPoList);
            NestPos nestPos = new NestPos();
            nestPos.setLongitude(dynamicAirLineParam.getLng());
            nestPos.setLatitude(dynamicAirLineParam.getLat());
            nestPos.setAltitude(dynamicAirLineParam.getAlt());
            deviceForLine.setMissionType(1);
            deviceForLine.setNestPos(nestPos);
            Gson gson = new Gson();
            String json = gson.toJson(deviceForLine);
            System.out.println("动态规划参数：" + json);
            Integer mode = 0;
            String resLine = Clibrary.INSTANTCE.fnAirLinePlan(dynamicAirLineParam.getPlanCode(), json, mode);
            //判断规划结果
            JSONObject obj = JSON.parseObject(resLine);
            String code = obj.getString("code");
            if (code != null && code.equals("-1")) {
                System.out.println("规划错误：" + obj.getString("origin"));
                return RestRes.err(obj.getString("origin"));
            }
            ResRouteJsonDto resRouteJson = gson.fromJson(resLine, ResRouteJsonDto.class);
            if (resRouteJson.getData().routeLineType == -1) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROUTE_ERROR.getContent()));
            } else if (resRouteJson.getData().routeLineType == 1) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_LOW_RISK_ROUTE.getContent()));
            }
            List<List<FlyPoint>> flyPointlists = resRouteJson.getData().missions;
            List<DynamicAirLineDto> dynamicAirLineDtoList = new ArrayList<>();
            //解析航线
            int i = 1;
            double highestAlt = Double.MIN_VALUE;

            for (List<FlyPoint> points : flyPointlists) {
                //去除重合点
                List<FlyPoint> flyPointlist = distinctAdjion(points);
                double tempAlt = getAirLineHighestAlt(flyPointlist);
                highestAlt = Math.max(tempAlt, highestAlt);
                DynamicAirLineDto airLineDto = new DynamicAirLineDto();
                Map calcute = airLineService.computeFlyPointList(flyPointlist, dynamicAirLineParam.getLng(), dynamicAirLineParam.getLat(), 2.0, 100);
                airLineDto.setAirLineList(flyPointlist);
                //TODO 时间、距离估算重写
                //计算航线距离
                airLineDto.setPredictMiles((Double) calcute.get("predicMiles"));
                //计算拍照点数
                airLineDto.setPhotoCount((Integer) calcute.get("photoCount"));
                //计算航点数
                airLineDto.setPointCount(flyPointlist.size());
                //计算飞行时间
                airLineDto.setPredictFlyTime((Long) calcute.get("predicTime"));
                airLineDto.setId(i);
                airLineDto.setName(MessageUtils.getMessage(MessageEnum.TASK_MISSION_AIRLINE.getContent()) + i++);

                dynamicAirLineDtoList.add(airLineDto);
            }
            Map result = new HashMap(2);


            result.put("lineLists", dynamicAirLineDtoList);
            result.put("highestAlt", highestAlt + 10);
            return RestRes.ok(result);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EQUIPMENT_LIST_IS_EMPTY.getContent()));
    }


    @Override
    public RestRes saveDynamicTask(DynamicTaskDto dynamicTaskDto) {

        //设置默认标签
        if (dynamicTaskDto.getTagId() == null && dynamicTaskDto.getUnitId() != null) {
            dynamicTaskDto.setTagId(setUnitTag(dynamicTaskDto.getUnitId(), dynamicTaskDto.getNestId()));
        }
        //保存task表，保存air_line表，保存mission_param表，保存mission表
        TaskEntity task = new TaskEntity();
        task.setCreatorId(Long.valueOf(dynamicTaskDto.getAccountId()));
        task.setName(dynamicTaskDto.getName());
        task.setDescription(dynamicTaskDto.getDescription());
        if (dynamicTaskDto.getMold() == 0) {
            task.setBaseNestId(dynamicTaskDto.getNestId());
            task.setOrgCode(dynamicTaskDto.getUnitId());
            task.setMold(0);
        } else if (dynamicTaskDto.getMold() == 1) {
            task.setMold(1);
            task.setOrgCode(dynamicTaskDto.getUnitId());
        }

        task.setType(dynamicTaskDto.getTaskType());
        if (dynamicTaskDto.getTaskType().equals(TaskModeEnum.SUBSSTATION_PLANING.getValue())) {
//            task.setIdentificationType(dynamicTaskDto.getIdentificationType());
            task.setSubType(dynamicTaskDto.getSubType());
        }

        boolean taskRes = this.save(task);
        //保存missionParam表
        MissionParamEntity mpe = new MissionParamEntity();
        mpe.setAutoFlightSpeed(dynamicTaskDto.getAutoFlightSpeed());
        mpe.setSpeed(dynamicTaskDto.getSpeed());
        mpe.setHeadingMode(HeadingModeEnum.getValueByName(dynamicTaskDto.getHeadingMode()));
        mpe.setStartStopPointAltitude(dynamicTaskDto.getStartStopAlt());
        boolean mpRes = missionParamService.save(mpe);

        Integer tagId = dynamicTaskDto.getTagId();
        SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", task.getId()));
        if (sysTaskTagEntity != null) {
            if (tagId != null) {
                sysTaskTagEntity.setTagId(tagId);
            } else {
                sysTaskTagEntity.setTagId(-1);
            }
            sysTaskTagService.updateById(sysTaskTagEntity);
        } else {
            SysTaskTagEntity stte = new SysTaskTagEntity();
            stte.setCreateTime(LocalDateTime.now());
            stte.setModifyTime(LocalDateTime.now());
            if (tagId != null) {
                stte.setTagId(tagId);
            } else {
                stte.setTagId(-1);
            }
            stte.setTaskId(task.getId());  //上面插入成功的任务id
            boolean stteRes = sysTaskTagService.save(stte);
        }

        //解析架次任务
        List<DynamicAirLineDto> dynamicAirLineDtoList = dynamicTaskDto.getLineList();
        Gson gson = new Gson();
        Integer saveCount = 0;
        for (DynamicAirLineDto airLineDto : dynamicAirLineDtoList) {
            //构建航线实体
            AirLineEntity ale = new AirLineEntity();
            ale.setType(dynamicTaskDto.getAirLineType());
            ale.setName(dynamicTaskDto.getName() + "-航线" + airLineDto.getId());
            List<FlyPoint> flyPointList = airLineDto.getAirLineList();
            ale.setWaypoints(gson.toJson(flyPointList));
            //添加航线默认识别类型
            ale.setPointCount(flyPointList.size());
            Map<String, Object> computeMap = null;
            if (dynamicTaskDto.getMold() == 0) {
//                NestEntity nestEntity = nestService.getNestByIdIsCache(dynamicTaskDto.getNestId());
                BaseNestLocationOutDTO nestLocation = baseNestService.getNestLocation(dynamicTaskDto.getNestId());
                computeMap = airLineService.computeFlyPointList(flyPointList, nestLocation.getLng(), nestLocation.getLat(), Double.valueOf(dynamicTaskDto.getSpeed()), dynamicTaskDto.getStartStopAlt().intValue());
            } else if (dynamicTaskDto.getMold() == 1) {
                computeMap = computeDynamicIflyerTerminal(dynamicTaskDto.getStartStopAlt(), dynamicTaskDto.getSpeed(), flyPointList);
            }
            if (computeMap == null) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_RESOLUTION.getContent()));
            }

            //计算统计数据
            ale.setMergeCount((int) computeMap.get("pointCount"));
            ale.setPhotoCount((int) computeMap.get("photoCount"));
            ale.setPredicMiles((double) computeMap.get("predicMiles"));
            ale.setPredicTime((long) computeMap.get("predicTime"));
            ale.setAbsolute(true);
            if (airLineService.save(ale)) {
                //构建架次任务实体
                MissionEntity me = new MissionEntity();
                me.setName(dynamicTaskDto.getName() + "-架次" + airLineDto.getId());
                me.setUuid(UuidUtil.createNoBar());
                me.setSeqId(airLineDto.getId());
                me.setTaskId(task.getId());
                me.setOrgCode(task.getOrgCode());
                //关联任务参数
                me.setMissionParamId(mpe.getId());
                //关联航线
                me.setAirLineId(ale.getId());
                missionService.add(me);
                saveCount++;
            }
        }

        if (taskRes && mpRes && saveCount.equals(dynamicAirLineDtoList.size())) {
            Map<String, Object> resMap = new HashMap<>(2);
            resMap.put("taskId", task.getId());
            return RestRes.ok(resMap);
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SAVE_ERROR.getContent()));
        }
    }

    private Integer setUnitTag(String unitId, String nestId) {
        UnitEntityDTO byId = sysUnitService.getById(unitId);
        List<SysTagEntity> list = sysTagService.lambdaQuery()
                .eq(SysTagEntity::getName, byId.getName())
                .eq(SysTagEntity::getOrgCode, byId.getId())
                .eq(SysTagEntity::getTagType, 1).list();

        if (CollectionUtils.isEmpty(list)) {
            SysTagEntity sysTagEntity = new SysTagEntity();
            sysTagEntity.setName(byId.getName());
            sysTagEntity.setOrgCode(unitId);
            sysTagEntity.setCreateTime(LocalDateTime.now());
            sysTagEntity.setModifyTime(LocalDateTime.now());
            sysTagEntity.setTagType(1);
            sysTagEntity.setSeq(0);
            sysTagService.save(sysTagEntity);
            return sysTagEntity.getId().intValue();
        } else {
            return list.get(0).getId().intValue();
        }
    }

    @Override
    @Transactional
    public RestRes saveOrUpdateUnifyTask(UnifyTaskDto unifyTaskDto) {

        String orgCode = unifyTaskDto.getUnitId();
        //设置默认标签
        if (unifyTaskDto.getTagId() == null && orgCode != null) {
            unifyTaskDto.setTagId(setUnitTag(orgCode, unifyTaskDto.getNestId()));
        }

        //保存task表，保存air_line表，保存mission_param表，保存mission表
        //如果taskId不为null，就表示是编辑保存的
        if (TaskModeEnum.SLOPEPHOTO.getValue().equals(unifyTaskDto.getTaskType()) ||
                TaskModeEnum.ORTHOPHOTO.getValue().equals(unifyTaskDto.getTaskType()) ||
                TaskModeEnum.GRID.getValue().equals(unifyTaskDto.getTaskType())
        ) {
            return saveOrUpdateMultiMissionTask(unifyTaskDto);
        }

        Integer mpeId = null, aleId = null, meId = null;
        if (unifyTaskDto.getTaskId() != null) {
            List<MissionEntity> missionEntities = missionService.listMissionByTaskId(unifyTaskDto.getTaskId());
            if (CollectionUtil.isNotEmpty(missionEntities)) {
                MissionEntity missionEntity = missionEntities.get(0);
                mpeId = missionEntity.getMissionParamId();
                aleId = missionEntity.getAirLineId();
                meId = missionEntity.getId();
                //判断全景航线能否保存
                judgePanoramaSave(missionEntity.getId().toString(), unifyTaskDto);
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SAVE.getContent()));
            }
        }

        TaskEntity te = new TaskEntity();
        te.setId(unifyTaskDto.getTaskId());
        te.setName(unifyTaskDto.getTaskName());
        te.setDescription(unifyTaskDto.getDescription());
        if (unifyTaskDto.getMold() == 0) {
            te.setBaseNestId(unifyTaskDto.getNestId());
            te.setOrgCode(orgCode);
            te.setMold(0);
        } else if (unifyTaskDto.getMold() == 1) {
            te.setOrgCode(orgCode);
            te.setMold(1);
        }
        te.setCreatorId(Long.valueOf(unifyTaskDto.getAccountId()));
        te.setModifyTime(LocalDateTime.now());
        te.setType(unifyTaskDto.getTaskType());
        boolean taskRes = this.saveOrUpdate(te);

        String unifyAirLineJson = unifyTaskDto.getUnifyAirLineJson();

        //保存missionParam表
        MissionParamEntity mpe = new MissionParamEntity();
        mpe.setId(mpeId);
        mpe.setSpeed(unifyTaskDto.getSpeed());
        mpe.setAutoFlightSpeed(unifyTaskDto.getAutoFlightSpeed());
        mpe.setStartStopPointAltitude((double) unifyTaskDto.getTakeOffLandAlt());
        if (unifyTaskDto.getFlightPathMode() != null) {
            mpe.setFlightPathMode(unifyTaskDto.getFlightPathMode().getValue());
        }
        if (TaskModeEnum.LINEAR.getValue().equals(unifyTaskDto.getTaskType())) {
            mpe.setHeadingMode(HeadingModeEnum.USING_INITIAL_DIRECTION.getValue());
        }
        //全景-机头朝向模式
        if (TaskModeEnum.PANORAMA.getValue().equals(unifyTaskDto.getTaskType())) {
            mpe.setHeadingMode(HeadingModeEnum.USING_INITIAL_DIRECTION.getValue());
        }
        if (TaskModeEnum.AROUND_FLY.getValue().equals(unifyTaskDto.getTaskType())) {
            //环绕飞行
            // 航向模式选择始终朝兴趣点
            mpe.setHeadingMode(HeadingModeEnum.TOWARD_POINT_OF_INTEREST.getValue());
            //任务路径模式选择曲线模式
            mpe.setFlightPathMode(FlightPathModeEnum.CURVED.getValue());
        }


        boolean mpeRes = missionParamService.saveOrUpdate(mpe);

        //保存air_line航线
        AirLineEntity ale = new AirLineEntity();
        ale.setId(aleId);
        ale.setType(unifyTaskDto.getAirLineType());
        ale.setVideoCount(0);
        ale.setName(unifyTaskDto.getTaskName() + "-" + MessageUtils.getMessage(MessageEnum.TASK_MISSION_AIRLINE.getContent()));
        ale.setPointCount(unifyTaskDto.getPointCount());
        ale.setPhotoCount(unifyTaskDto.getPhotoCount());
        ale.setCopyCount(0);
        ale.setPredicTime(unifyTaskDto.getPredictFlyTime());
        ale.setPredicMiles(unifyTaskDto.getPredictMiles());
        ale.setWaypoints(unifyAirLineJson);
        if (unifyTaskDto.getMold() == 0) {
            //构建航线,如果是易飞航线不用构
            UnifyNestAirLineResult unifyNestAirLineResult = buildUnifyNestAirLine(unifyAirLineJson, unifyTaskDto.getNestId(), te.getId());
            if (!unifyNestAirLineResult.isResult()) {
                String msg = unifyNestAirLineResult.getMsg();
                throw new NestException(msg == null ? MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE.getContent()) : msg);
            }
            if (TaskModeEnum.PANORAMA.getValue().equals(unifyTaskDto.getTaskType())) {
                ale.setPhotoCount(unifyNestAirLineResult.getPhotoCount());
                //全景航线保存时新增航点ID,序号
                ale.setWaypoints(addPointId(ale.getWaypoints()));
            }
            if (TaskModeEnum.LINEAR.getValue().equals(unifyTaskDto.getTaskType()) || TaskModeEnum.AROUND_FLY.getValue().equals(unifyTaskDto.getTaskType())) {
                ale.setPhotoCount(unifyTaskDto.getPhotoCount());
                ale.setVideoCount(unifyTaskDto.getVideoCount());
                ale.setShowInfo(unifyTaskDto.getShowInfo());
                ale.setVideoLength(unifyTaskDto.getVideoLength());
                ale.setVideoCount(unifyTaskDto.getVideoCount());

                // 如果是视频拍摄模式，则视频时间=航线飞行时间即可
//                UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(unifyAirLineJson, UnifyAirLineFormatDto.class);
//                TaskModeEnum mode = unifyAirLineFormatDto.getMode();
//                JSONObject linearParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
//                LinearParam linearParam = JSONObject.parseObject(linearParamJson.toJSONString(), LinearParam.class);
//                LinearParam.TaskMode taskMode = linearParam.getTaskMode();
//                if (taskMode.equals(LinearParam.TaskMode.VIDEO_CAPTURE)) {
//                ale.setVideoLength(unifyTaskDto.getVideoLength());
//                ale.setVideoCount(unifyTaskDto.getVideoCount());
//                }
            }
            //判断是否为全景航线，且CPS版本大于24000 大于24000则MergeCount不用-2
            ale.setOriginalWaypoints(WaypointUtil.waypointListToJsonStr(unifyNestAirLineResult.getWaypointList()));
            NestTypeEnum nestType = baseNestService.getNestType(unifyTaskDto.getNestId());
            if (unifyNestAirLineResult.getIsCps240() && NestTypeEnum.S110_MAVIC3.getValue() != nestType.getValue()) {
                ale.setMergeCount(unifyNestAirLineResult.getWaypointList().size());
            } else {
                ale.setMergeCount(unifyNestAirLineResult.getWaypointList().size() - 2);
            }
        }

        boolean aleRes = airLineService.saveOrUpdate(ale);

        //保存mission表
        MissionEntity me = new MissionEntity();
        me.setId(meId);
        me.setName(unifyTaskDto.getTaskName() + "-" + MessageUtils.getMessage(MessageEnum.TASK_MISSION_FLIGHT.getContent()) + 1);
        me.setUuid(UuidUtil.createNoBar());
        me.setSeqId(1);
        me.setTaskId(te.getId());
        me.setOrgCode(te.getOrgCode());
        me.setMissionParamId(mpe.getId());
        me.setAirLineId(ale.getId());
        me.setCopyCount(0);
        boolean meRes = missionService.saveOrUpdate(me);

        Integer tagId = unifyTaskDto.getTagId();
        SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", te.getId()));
        if (sysTaskTagEntity != null) {
            if (tagId != null) {
                sysTaskTagEntity.setTagId(tagId);
            } else {
                sysTaskTagEntity.setTagId(-1);
            }
            sysTaskTagService.updateById(sysTaskTagEntity);
        } else {
            SysTaskTagEntity stte = new SysTaskTagEntity();
            if (tagId != null) {
                stte.setTagId(tagId);
            } else {
                stte.setTagId(-1);
            }
            stte.setTaskId(te.getId());
            boolean stteRes = sysTaskTagService.save(stte);
        }

        if (taskRes && mpeRes && aleRes && meRes) {
            Map<String, Object> resMap = new HashMap<>(2);
            resMap.put("taskId", te.getId());
            clearTagNameRedisCache(unifyTaskDto.getTaskId());
            return RestRes.ok(resMap, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSION_SAVED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SAVE.getContent()));
    }

    public Boolean isParamCps240(String nestId, String aidLineJson) {
        UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(aidLineJson, UnifyAirLineFormatDto.class);
        if (!(unifyAirLineFormatDto.getMode().getValue() == TaskModeEnum.PANORAMA.getValue())) {
            return false;
        }
        ComponentManager instance = ComponentManagerFactory.getInstance(baseNestService.getNestUuidByNestIdInCache(nestId));
        MqttResult<CpsVersionCode> cpsVersionCode = instance.getSystemManagerCf().getCpsVersionCode();
        if (!cpsVersionCode.isSuccess()) {
            throw new BusinessException("获取CPS版本失败");
        }
        int k = Integer.valueOf(cpsVersionCode.getRes().getVersion_code()) - Integer.valueOf(MissionServiceImpl.CPS_VERSION);
        if (k < 0) {
            return false;
        }
        return true;
    }

    @Override
    public RestRes findUnifyTaskDetail(Integer taskId) {
        TaskEntity taskEntity = this.getById(taskId);
        List<MissionEntity> missionEntities = missionService.listMissionByTaskId(taskId);
        if (CollectionUtil.isEmpty(missionEntities)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_SORTIE_QUERY.getContent()));
        }

        int airLineType = 0;
        int pointCount = 0;
        int photoCount = 0;
        long predictFlyTime = 0L;
        long videoLength = 0L;
        double predictMiles = 0.0;
        String waypointStr = "";
        String djiAirLineJson = "";
        Map<Integer, String> djiAirLineMap = new HashMap<>();
        int speed = 0;
        int autoFlightSpeed = 0;
        int takeOffLandAlt = 0;
        int deltaTime = 0;
        String showInfo = "";
        FocalModeEnum focalMode = null;
        FlightPathModeEnum flightPathMode = null;
        HeadingModeEnum headingMode = null;
        UnifyTaskDto unifyTaskDto = new UnifyTaskDto();

        if (TaskModeEnum.SLOPEPHOTO.getValue().equals(taskEntity.getType()) ||
                TaskModeEnum.ORTHOPHOTO.getValue().equals(taskEntity.getType()) ||
                TaskModeEnum.GRID.getValue().equals(taskEntity.getType())
        ) {
            //倾斜摄影有5个架次
            List<MultiMissionExtra> missionExtraList = new ArrayList<>();
            Map<Integer, Integer> airLineIdSeqIdMap = missionEntities.stream().collect(Collectors.toMap(MissionEntity::getAirLineId, MissionEntity::getSeqId));
            List<Integer> aleIdList = missionEntities.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
            List<AirLineEntity> airLineEntities = airLineService.listByIds(aleIdList);
            MissionParamEntity missionParamEntity = missionParamService.getById(missionEntities.get(0).getMissionParamId());
            for (AirLineEntity ale : airLineEntities) {
                MultiMissionExtra multiMissionExtra = new MultiMissionExtra()
                        .setPhotoCount(ale.getPhotoCount())
                        .setPointCount(ale.getPointCount())
                        .setPredictFlyTime(ale.getPredicTime())
                        .setPredictMiles(ale.getPredicMiles())
                        .setSeqId(airLineIdSeqIdMap.get(ale.getId()));

                missionExtraList.add(multiMissionExtra);
                pointCount += ale.getPointCount();
                photoCount += ale.getPhotoCount();
                predictFlyTime += ale.getPredicTime();
                predictMiles += ale.getPredicMiles();
                videoLength += ale.getVideoLength();
            }
            AirLineEntity airLineEntity = airLineEntities.get(0);
            airLineType = airLineEntity.getType();
            waypointStr = airLineEntity.getWaypoints();
            speed = missionParamEntity.getSpeed();
            autoFlightSpeed = missionParamEntity.getAutoFlightSpeed();
            deltaTime = missionParamEntity.getDeltaTime();
            focalMode = FocalModeEnum.getInstance(airLineEntity.getFocalMode());
            flightPathMode = FlightPathModeEnum.getInstance(missionParamEntity.getFlightPathMode());
            headingMode = HeadingModeEnum.getInstance(missionParamEntity.getHeadingMode());
            takeOffLandAlt = missionParamEntity.getStartStopPointAltitude() != null ? missionParamEntity.getStartStopPointAltitude().intValue() : 0;
            List<MultiMissionExtra> missionExtraSortList = missionExtraList.stream().sorted(Comparator.comparing(MultiMissionExtra::getSeqId)).collect(Collectors.toList());
            unifyTaskDto.setMultiMissionExtraList(missionExtraSortList);
        } else {
            //全景、线状都只有一个架次
            MissionEntity missionEntity = missionEntities.get(0);
            AirLineEntity airLineEntity = airLineService.getById(missionEntity.getAirLineId());
            MissionParamEntity missionParamEntity = missionParamService.getById(missionEntity.getMissionParamId());
            airLineType = airLineEntity.getType();
            pointCount = airLineEntity.getPointCount();
            photoCount = airLineEntity.getPhotoCount();
            predictFlyTime = airLineEntity.getPredicTime();
            predictMiles = airLineEntity.getPredicMiles();
            waypointStr = airLineEntity.getWaypoints();
            showInfo = airLineEntity.getShowInfo();
            videoLength = airLineEntity.getVideoLength();
            autoFlightSpeed = missionParamEntity.getAutoFlightSpeed();
            speed = missionParamEntity.getSpeed();
            takeOffLandAlt = missionParamEntity.getStartStopPointAltitude() != null ? missionParamEntity.getStartStopPointAltitude().intValue() : 0;
            deltaTime = missionParamEntity.getDeltaTime();
            focalMode = FocalModeEnum.getInstance(airLineEntity.getFocalMode());
            flightPathMode = FlightPathModeEnum.getInstance(missionParamEntity.getFlightPathMode());
            djiAirLineJson = airLineEntity.getDjiWaypoints();
            djiAirLineMap.put(missionEntity.getId(), djiAirLineJson);
        }

        List<SysTaskTagEntity> taskTagEntities = sysTaskTagService.list(new QueryWrapper<SysTaskTagEntity>().eq("task_id", taskId));

        String tagName = null;
        if (CollectionUtil.isNotEmpty(taskTagEntities)) {
            Integer tagId = taskTagEntities.get(0).getTagId();
            if (tagId != null && tagId != -1) {
                tagName = sysTagService.getById(tagId).getName();
            }
            tagName = "未定义默认标签";

        }

        unifyTaskDto.setTaskId(taskId);
        unifyTaskDto.setTaskName(taskEntity.getName());
        unifyTaskDto.setTagName(tagName);
        SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", taskId));
        if (sysTaskTagEntity != null) {
            unifyTaskDto.setTagId(sysTaskTagEntity.getTagId());
        }
        unifyTaskDto.setTaskType(taskEntity.getType());
        unifyTaskDto.setDescription(taskEntity.getDescription());
        unifyTaskDto.setNestId(taskEntity.getBaseNestId());
        unifyTaskDto.setAirLineType(airLineType);
        unifyTaskDto.setPointCount(pointCount);
        unifyTaskDto.setPhotoCount(photoCount);
        unifyTaskDto.setPredictFlyTime(predictFlyTime);
        unifyTaskDto.setPredictMiles(predictMiles);
        unifyTaskDto.setSpeed(speed);
        unifyTaskDto.setAutoFlightSpeed(autoFlightSpeed);
        unifyTaskDto.setUnifyAirLineJson(waypointStr);
        unifyTaskDto.setDjiAirLineJson(djiAirLineJson);
        unifyTaskDto.setTakeOffLandAlt(takeOffLandAlt);
        unifyTaskDto.setDeltaTime(deltaTime);
        unifyTaskDto.setFocalMode(focalMode);
        unifyTaskDto.setShowInfo(showInfo);
        unifyTaskDto.setFlightPathMode(flightPathMode);
        unifyTaskDto.setHeadingMode(headingMode);
        unifyTaskDto.setVideoLength(videoLength);

        if (TaskModeEnum.GRID.getValue().equals(taskEntity.getType())) {
            unifyTaskDto.setGridBounds(gridService.getGridBoundsByTaskId(unifyTaskDto.getTaskId()));
        }

        Map<String, Object> resMap = new HashMap<>(2);
        resMap.put("unifyTaskDto", unifyTaskDto);
        return RestRes.ok(resMap);
    }

    @Override
    public TaskEntity getByMissionId(Integer missionId) {
        MissionEntity mission = missionService.getById(missionId);
        return this.selectById(mission.getTaskId());
    }

    @Override
    public RestRes findTaskDetailLinePoints(Integer taskId) {
        //查询任务
        TaskEntity te = getOne(new QueryWrapper<TaskEntity>().lambda().eq(TaskEntity::getDeleted, false).eq(TaskEntity::getId, taskId));
        if (te == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_RELEVANT_MISSION_CANNOT_BE_QUERIED.getContent()));
        }

        HashMap<String, Object> resultMap = new HashMap<>(2);
        //任务封装
        TaskDetailsDto taskDetailsDto = new TaskDetailsDto();
        taskDetailsDto.setId(te.getId());
        taskDetailsDto.setName(te.getName());
        taskDetailsDto.setTaskType(te.getType());
        if (te.getType().equals(TaskModeEnum.SUBSSTATION_PLANING.getValue())) {
//            taskDetailsDto.setIdentificationType(te.getIdentificationType());
            taskDetailsDto.setSubType(te.getSubType());
        }
        taskDetailsDto.setDescription(te.getDescription());
        //获取标签信息
        SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", taskId));
        if (sysTaskTagEntity != null) {
            taskDetailsDto.setTagId(sysTaskTagEntity.getTagId());
        }
        //获取架次任务
        List<MissionEntity> meList = missionService.listMissionByTaskId(taskId);
        if (!CollectionUtils.isEmpty(meList)) {
            //获取航线列表
            List<Integer> alIdList = meList.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
            List<AirLineEntity> aleList = airLineService.listByIds(alIdList);
            double highestAlt = getAirLineHighestAlt2(aleList);
            //累加飞行时长、里程、拍照点、照片数
            int pointCount = aleList.stream().mapToInt(ale -> ale.getMergeCount() == null ? 0 : ale.getMergeCount()).sum();
            int photoCount = aleList.stream().mapToInt(ale -> ale.getPhotoCount() == null ? 0 : ale.getPhotoCount()).sum();
            long predicTime = aleList.stream().mapToLong(ale -> ale.getPredicTime() == null ? 0L : ale.getPredicTime()).sum();
            double predicMiles = aleList.stream().mapToDouble(ale -> ale.getPredicMiles() == null ? 0.0 : ale.getPredicMiles()).sum();

            taskDetailsDto.setAirLineType(aleList.get(0).getType());
            taskDetailsDto.setPointCount(pointCount);
            taskDetailsDto.setPhotoCount(photoCount);
            taskDetailsDto.setPredictFlyTime(predicTime);
            taskDetailsDto.setPredictMiles(predicMiles);

            //航线转换格式
            List<DynamicAirLineDto> lineList = new ArrayList<>();
            for (AirLineEntity airLineEntity : aleList) {
                DynamicAirLineDto dto = new DynamicAirLineDto();
                dto.setId(airLineEntity.getId());
                dto.setName(airLineEntity.getName());
                dto.setPhotoCount(airLineEntity.getPhotoCount());
                dto.setPredictFlyTime(airLineEntity.getPredicTime());
                dto.setPredictMiles(airLineEntity.getPredicMiles());
                dto.setPointCount(airLineEntity.getPointCount());
                dto.setAirLineList(JSON.parseArray(airLineEntity.getWaypoints(), FlyPoint.class));
                lineList.add(dto);
            }
            taskDetailsDto.setLineList(lineList);
            MissionParamEntity mpe = missionParamService.getById(meList.get(0).getMissionParamId());
            taskDetailsDto.setHeadingMode(mpe.getHeadingMode() == -1 ? "未知" : HeadingModeEnum.getInstance(mpe.getHeadingMode()).name());
            taskDetailsDto.setStartStopAlt(mpe.getStartStopPointAltitude());
            taskDetailsDto.setAutoFlightSpeed(mpe.getAutoFlightSpeed());

            resultMap.put("taskDetailsDto", taskDetailsDto);
            resultMap.put("highestAlt", highestAlt);
            return RestRes.ok(resultMap);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_RELEVANT_MISSION_CANNOT_BE_QUERIED.getContent()));
    }

    @Override
    public RestRes listIflyerTasks(IflyerParam iflyerParam) {
        IPage<TaskEntity> page = new Page<>(iflyerParam.getCurrPage(), iflyerParam.getPageSize());
        LambdaQueryWrapper<TaskEntity> lambdaQueryWrapper = new QueryWrapper<TaskEntity>()
                .lambda()
                .eq(TaskEntity::getDeleted, false)
                .eq(TaskEntity::getOrgCode, iflyerParam.getUnitId())
                .select(TaskEntity::getId)
                .orderByDesc(TaskEntity::getModifyTime);

        if (iflyerParam.getKeyword() != null) {
            lambdaQueryWrapper.like(TaskEntity::getName, iflyerParam.getKeyword());
        }

        if (iflyerParam.getTaskType() != null) {
            lambdaQueryWrapper.eq(TaskEntity::getType, iflyerParam.getTaskType());
        }

        IPage<TaskEntity> taskEntityIPage = baseMapper.selectPage(page, lambdaQueryWrapper);
        Map<String, Object> resultMap = new HashMap<>(2);
        List<TaskEntity> teList = taskEntityIPage.getRecords();
        if (CollectionUtil.isEmpty(teList)) {
            return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_CORRESPONDING_ROUTE_DATA.getContent()));
        }
        List<Integer> teIdList = teList.stream().map(TaskEntity::getId).collect(Collectors.toList());
        List<MissionEntity> meList = missionService.list(new QueryWrapper<MissionEntity>()
                .lambda()
                .select(MissionEntity::getId, MissionEntity::getName)
                .in(MissionEntity::getTaskId, teIdList)
                .orderByDesc(MissionEntity::getModifyTime));
        List<Map<String, Object>> collect = meList.stream().map(me -> {
            Map<String, Object> map = new HashMap<>(2);
            map.put("missionName", me.getName());
            map.put("missionId", me.getId());
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> pageMap = new HashMap<>(8);
        pageMap.put("records", collect);
        pageMap.put("current", taskEntityIPage.getCurrent());
        pageMap.put("pages", taskEntityIPage.getPages());
        pageMap.put("size", taskEntityIPage.getSize());
        pageMap.put("total", taskEntityIPage.getTotal());

        resultMap.put("pageDto", pageMap);
        return RestRes.ok(resultMap);
    }

    @Override
    public Integer getAPPManualFlyTaskId(String orgCode) {
        return baseMapper.getAPPManualFlyTaskId(orgCode);
    }

    @Override
    public TaskEntity getNestIdByAirLineId(Integer airLineId) {
        if (airLineId != null) {
            return baseMapper.selectNestIdByAirLineId(airLineId);
        }
        return null;
    }

    @Transactional
    @Override
    public RestRes saveOrUpdateFineInspTask(FineInspTaskDto dto) {
        //1.保存task表
        Integer taskId = saveOrUpdateTask(dto);
        dto.setTaskId(taskId);
        //2.任务与标签关系
        saveOrUpdateSysTaskTag(dto);
        //3.保存task与zip关系表,与tower关系表
        saveOrUpdateZipAndTowerRel(dto);
        //4.保存修改的杆塔数据
        updateTowerRouteData(dto.getRouteMap());
        //目前精细巡检一个任务只有一个架次
        MissionEntity missionEntity = missionService.lambdaQuery().eq(MissionEntity::getTaskId, dto.getTaskId()).select(MissionEntity::getId).one();
        Integer missionId = null;
        Integer airLineId = null;
        Integer missionParamId = null;
        if (missionEntity != null) {
            missionId = missionEntity.getId();
            airLineId = missionEntity.getAirLineId();
            missionParamId = missionEntity.getMissionParamId();
        }
        //5. 生成架次参数并保存，mission_param
        missionParamId = saveOrUpdateMissionParam(dto, missionParamId);

        //6.生成航线参数，并保存air_line
        airLineId = saveOrUpdateAirLine(dto, airLineId);
        //7. 生成架次并保存
        saveOrUpdateMission(dto, airLineId, missionParamId, missionId);

        Map<String, Object> resMap = new HashMap<>(2);
        resMap.put("taskId", taskId);
        clearTagNameRedisCache(taskId);
        return RestRes.ok(resMap);
    }

    @Override
    public RestRes selectFineInspTask(Integer taskId) {

        TaskEntity taskEntity = this.getById(taskId);
        if (taskEntity == null) {
            return RestRes.warn("查不到相关任务");
        }

        MissionEntity missionEntity = missionService.lambdaQuery().eq(MissionEntity::getTaskId, taskId).one();
        MissionParamEntity missionParamEntity = missionParamService.getById(missionEntity.getMissionParamId());
        AirLineEntity airLineEntity = airLineService.getById(missionEntity.getAirLineId());
        SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.lambdaQuery().eq(SysTaskTagEntity::getTaskId, taskEntity.getId()).one();
        TaskFineInspZipRelEntity taskFineInspZipRelEntity = taskFineInspZipRelService.lambdaQuery().eq(TaskFineInspZipRelEntity::getTaskId, taskEntity.getId()).one();
        List<TaskFineInspTowerRelEntity> taskFineInspTowerRelEntityList = taskFineInspTowerRelService.lambdaQuery()
                .eq(TaskFineInspTowerRelEntity::getTaskId, taskEntity.getId()).list();
        List<Integer> towerIdList = taskFineInspTowerRelEntityList.stream().map(TaskFineInspTowerRelEntity::getFineTowerId).collect(Collectors.toList());

        FineInspTaskDto fineInspTaskDto = new FineInspTaskDto();
        fineInspTaskDto.setTaskId(taskEntity.getId())
                .setTaskName(taskEntity.getName())
                .setTaskType(taskEntity.getType())
                .setNestId(taskEntity.getBaseNestId())
                .setMold(taskEntity.getMold())
                .setDescription(taskEntity.getDescription())
                .setSpeed(missionParamEntity.getSpeed())
                .setAutoFlightSpeed(missionParamEntity.getAutoFlightSpeed())
                .setTakeOffLandAlt(missionParamEntity.getStartStopPointAltitude())
                .setPhotoActionCount(airLineEntity.getPhotoCount())
                .setPointCount(airLineEntity.getPointCount())
                .setPredictFlyTime(airLineEntity.getPredicTime())
                .setPredictMiles(airLineEntity.getPredicMiles())
                .setShowInfo(airLineEntity.getShowInfo())
                .setTagId(sysTaskTagEntity.getTagId())
                .setZipId(taskFineInspZipRelEntity.getId())
                .setTowerIdList(towerIdList);
//                .setFocalMode(FocalModeEnum.getInstance(airLineEntity.getFocalMode()));

        List<FineInspTowerEntity> towerList = fineInspTowerService.lambdaQuery().eq(FineInspTowerEntity::getFineInspZipId, taskFineInspZipRelEntity.getFineZipId()).list();
        FineInspZipEntity fineInspZipEntity = fineInspZipService.getById(taskFineInspZipRelEntity.getFineZipId());
        Map<Integer, List<PointCloudWaypoint>> routeMap = new HashMap<>(towerIdList.size());
        for (FineInspTowerEntity fite : towerList) {
            if (towerIdList.contains(fite.getId())) {
                routeMap.put(fite.getId(), JSONArray.parseArray(fite.getUpdateRouteData(), PointCloudWaypoint.class));
            }
        }
        fineInspTaskDto.setRouteMap(routeMap);

        FineParseResDto fineParseResDto = new FineParseResDto();
        fineParseResDto.setZipId(fineInspZipEntity.getId());
        String zipName = fineInspZipEntity.getZipName();
        fineParseResDto.setZipName(zipName.substring(zipName.indexOf("-") + 1));
        List<Map<String, Object>> towerMapList = towerList.stream().map(t -> {
            Map<String, Object> map = new HashMap<>(4);
            map.put("towerId", t.getId());
            map.put("towerName", t.getTowerName());
            map.put("position", Arrays.asList(t.getTowerLng(), t.getTowerLat(), t.getTowerAlt()));
            return map;
        }).collect(Collectors.toList());
        fineParseResDto.setTowerList(towerMapList);

        Map<String, Object> resMap = new HashMap<>(2);
        resMap.put("fineInspTaskDto", fineInspTaskDto);
        resMap.put("fineParseResDto", fineParseResDto);
        return RestRes.ok(resMap);
    }

    @Override
    public RestRes updateDynamicTask(TaskDetailsDto taskDto) {
        if (taskDto.getTagId() == null && taskDto.getUnitId() != null) {
            taskDto.setTagId(setUnitTag(taskDto.getUnitId(), taskDto.getNestId()));
        }
        //保存task表
        TaskEntity task = new TaskEntity();
        task.setModifyTime(LocalDateTime.now());
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        if (taskDto.getMold() == 0) {
            task.setMold(0);
            task.setBaseNestId(taskDto.getNestId());
        } else if (taskDto.getMold() == 1) {
            task.setMold(1);
            task.setOrgCode(taskDto.getUnitId());
        }
        task.setType(taskDto.getTaskType());
        if (taskDto.getTaskType().equals(TaskModeEnum.SUBSSTATION_PLANING.getValue())) {
//            task.setIdentificationType(taskDto.getIdentificationType());
            task.setSubType(taskDto.getSubType());
        }
        task.setId(taskDto.getId());
        //更新tag信息
        SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", taskDto.getId()));
        Integer tagId = taskDto.getTagId();
        if (sysTaskTagEntity != null) {
            if (tagId != null) {
                sysTaskTagEntity.setTagId(taskDto.getTagId());
            } else {
                sysTaskTagEntity.setTagId(-1);
            }
            sysTaskTagService.updateById(sysTaskTagEntity);
        } else {
            SysTaskTagEntity tag = new SysTaskTagEntity();
            tag.setTaskId(taskDto.getId());
            if (tagId != null) {
                sysTaskTagEntity.setTagId(taskDto.getTagId());
            } else {
                sysTaskTagEntity.setTagId(-1);
            }
            sysTaskTagService.save(tag);
        }

        //查询任务参数
        List<MissionEntity> missionList = missionService.list(new QueryWrapper<MissionEntity>().eq("task_id", task.getId()));
        List<Integer> paramIds = missionList.stream().map(MissionEntity::getMissionParamId).collect(Collectors.toList());
        List<MissionParamEntity> missionParamList = missionParamService.listByIds(paramIds);

        //编辑的时候，修改uuid,这样在执行任务的时候才会重新上传航线到基站
        missionList.forEach(m -> m.setUuid(UuidUtil.createNoBar()));
        missionService.updateBatchById(missionList);

        //设置并修改参数
        missionParamList.stream().forEach(param -> {
            param.setAutoFlightSpeed(taskDto.getAutoFlightSpeed());
            param.setSpeed(taskDto.getSpeed());
            param.setStartStopPointAltitude(taskDto.getStartStopAlt());
        });
        missionParamService.updateBatchById(missionParamList);

        boolean taskRes = this.updateById(task);
        if (taskRes) {
            Map<String, Object> resMap = new HashMap<>(2);
            resMap.put("taskId", task.getId());
            clearTagNameRedisCache(taskDto.getId());
            return RestRes.ok(resMap);
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EDIT_ERROR.getContent()));
        }
    }


    public UnifyNestAirLineResult buildUnifyNestAirLine(String unifyAirLineJson, String nestId, Integer taskID) {
        UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(unifyAirLineJson, UnifyAirLineFormatDto.class);
        TaskModeEnum mode = unifyAirLineFormatDto.getMode();
        UnifyNestAirLineResult unifyNestAirLineResult = new UnifyNestAirLineResult();
        switch (mode) {
            case PANORAMA:
                return buildNestPanoramaAirLine(unifyAirLineFormatDto, nestId);
            case LINEAR:
                return buildNestLinearAirLine(unifyAirLineFormatDto, nestId);
            case ORTHOPHOTO:
                List<List<Waypoint>> waypointsList = buildNestOrthoPhotoAirLine(unifyAirLineFormatDto);
                if (CollectionUtil.isNotEmpty(waypointsList)) {
                    unifyNestAirLineResult.setResult(true);
                    unifyNestAirLineResult.setWaypointsList(waypointsList);
                } else {
                    unifyNestAirLineResult.setResult(false);
                }
                return unifyNestAirLineResult;
            case SLOPEPHOTO:
                unifyNestAirLineResult.setWaypointsList(buildNestSlopePhotoAirLine(unifyAirLineFormatDto));
                unifyNestAirLineResult.setResult(true);
                return unifyNestAirLineResult;
            case AROUND_FLY:
                return buildNestAroundFlyAirLine(unifyAirLineFormatDto);
            case DJI_KML:
                return buildDjiAirLine(unifyAirLineFormatDto);
            default:
                unifyNestAirLineResult.setResult(false);
                return unifyNestAirLineResult;
        }
    }

    public UnifyNestAirLineResult buildGirdNestAirLine(String unifyAirLineJson, Integer taskId, MissionParamEntity mpe, String orgCode, int photoCount) {
        UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(unifyAirLineJson, UnifyAirLineFormatDto.class);
        TaskModeEnum mode = unifyAirLineFormatDto.getMode();
        UnifyNestAirLineResult unifyNestAirLineResult = new UnifyNestAirLineResult();

        JSONObject gridParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
        GridPhotoParam gridPhotoParam = JSONObject.parseObject(gridParamJson.toJSONString(), GridPhotoParam.class);
        JSONArray pointsList = (JSONArray) unifyAirLineFormatDto.getMapConfigs().get("points");
        Integer isCoorTurning = gridPhotoParam.getIsCoorTurning();
        // 编辑时，判断是否同一航线
        boolean isSameRoute = false;
        if (!ObjectUtils.isEmpty(taskId)) {
            // 如果飞行模式没有改变，则不改变数据网格并且如果照片拍摄数没改变也不会改变数据网格
            Map<String, Object> map = airLineService.queryIsCoorTurning(taskId);
            Integer photoCountFromAirline = airLineService.queryAirLinePhotoCount(taskId);
            Integer isCoorTurninged = (Integer)map.get("isCoorTurning");
            if (Objects.equals(isCoorTurning, isCoorTurninged) && photoCount == photoCountFromAirline) {
                isSameRoute = true;
            }
            // 如果飞行模式不同，重新保存MissionParam
            if (!Objects.equals(isCoorTurning, isCoorTurninged)) {
                mpe.setIsCoorTurning(isCoorTurning);
                missionParamService.saveOrUpdate(mpe);
            }
        }
        unifyNestAirLineResult.setSameRoute(isSameRoute);
        List<List<Waypoint>> waypointsListFromGrid = buildNestGridPhotoAirLine(gridPhotoParam, taskId, pointsList, isSameRoute, orgCode);
        if (CollectionUtil.isNotEmpty(waypointsListFromGrid)) {
            unifyNestAirLineResult.setResult(true);
            unifyNestAirLineResult.setWaypointsList(waypointsListFromGrid);
        } else {
            unifyNestAirLineResult.setResult(false);
        }
        return unifyNestAirLineResult;
    }


    /**
     * 构建除御2外的全景航线,御2可以一键全景
     *
     * @param unifyAirLineFormatDto
     * @return
     */
    public UnifyNestAirLineResult buildNestPanoramaAirLine(UnifyAirLineFormatDto unifyAirLineFormatDto, String nestId) {
        UnifyNestAirLineResult unifyNestAirLineResult = new UnifyNestAirLineResult();

        TaskModeEnum mode = unifyAirLineFormatDto.getMode();
        JSONObject panoramaParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
        PanoramaParam panoramaParam = JSONObject.parseObject(panoramaParamJson.toJSONString(), PanoramaParam.class);
        double speed = (double) panoramaParam.getSpeed();
        PanoramaParam.ReturnMode returnMode = panoramaParam.getReturnMode();

        JSONArray points = (JSONArray) unifyAirLineFormatDto.getMapConfigs().get("points");
        List<UnifyPoint> unifyPointList = JSONArray.parseArray(points.toJSONString(), UnifyPoint.class);
        if (CollectionUtil.isEmpty(unifyPointList)) {
            unifyNestAirLineResult.setResult(false);
            return unifyNestAirLineResult;
        }
        List<Waypoint> waypointList = new ArrayList<>();

        //添加起始点
        double takeOffLandAlt = (double) panoramaParam.getTakeOffLandAlt();
        UnifyPoint unifyPointStart = unifyPointList.get(0);
        UnifyLocation locationStart = unifyPointStart.getLocation();
        Waypoint pStart = new Waypoint(locationStart.getLat(), locationStart.getLng(), takeOffLandAlt);
        waypointList.add(pStart);
        NestTypeEnum nestType = baseNestService.getNestType(nestId);
        //获取CPS版本
        ComponentManager instance = ComponentManagerFactory.getInstance(baseNestService.getNestUuidByNestIdInCache(nestId));
        MqttResult<CpsVersionCode> cpsVersionCode = instance.getSystemManagerCf().getCpsVersionCode();
        if (!cpsVersionCode.isSuccess()) {
            throw new BusinessException("获取CPS版本失败");
        }
        int k = Integer.valueOf(cpsVersionCode.getRes().getVersion_code()) - Integer.valueOf(MissionServiceImpl.CPS_VERSION);
        for (UnifyPoint point : unifyPointList) {
            //添加第一个航点
            UnifyLocation location = point.getLocation();
            Waypoint p1 = new Waypoint(location.getLat(), location.getLng());
            p1.setWayPointSpeed(speed);
            //Cps版本大于等于2.4.0且无人机版本不为御三
            if (k >= 0 && NestTypeEnum.S110_MAVIC3.getValue() != nestType.getValue()) {
                p1.addAction(ActionTypeEnum.START_TAKE_PANORAMA_PHOTO, 7);
                p1.setWayPointAltitude(location.getAlt());
                unifyNestAirLineResult.setIsCps240(Boolean.TRUE);
            } else {
                p1.setWayPointAltitude(location.getAlt() + 0.5);
                p1.addAction(ActionTypeEnum.ROTATE_AIRCRAFT, 0)
                        //每个点先停留2秒，保证云台回正
                        .addAction(ActionTypeEnum.STAY, 2000)
                        .addAction(ActionTypeEnum.GIMBAL_PITCH, -90)
                        .addAction(ActionTypeEnum.START_TAKE_PHOTO, 1)
//                    .addAction(ActionTypeEnum.STAY, 2000)
                        .addAction(ActionTypeEnum.GIMBAL_PITCH, -60)
                        .addAction(ActionTypeEnum.START_TAKE_PHOTO, 1)
//                    .addAction(ActionTypeEnum.STAY, 2000)
                        .addAction(ActionTypeEnum.GIMBAL_PITCH, -30)
                        .addAction(ActionTypeEnum.START_TAKE_PHOTO, 1)
//                    .addAction(ActionTypeEnum.STAY, 2000)
                        .addAction(ActionTypeEnum.GIMBAL_PITCH, 0)
                        .addAction(ActionTypeEnum.START_TAKE_PHOTO, 1);
//                    .addAction(ActionTypeEnum.STAY, 2000);
            }
            waypointList.add(p1);
            if (k < 0 || NestTypeEnum.S110_MAVIC3.getValue() == nestType.getValue()) {
                for (int i = 1; i < 10; i++) {
                    int angle = 36 * i;
                    //飞机转向-180°--> 180°
                    angle = angle >= 180 ? angle - 360 : angle;
                    Waypoint p;
                    //
                    if (i % 2 == 1) {
                        //0.5m上下交换
                        p = new Waypoint(location.getLat(), location.getLng(), location.getAlt() - 0.3);
                    } else {
                        p = new Waypoint(location.getLat(), location.getLng(), location.getAlt() + 0.3);
                    }
                    p.setWayPointSpeed(speed);
                    //取消拍照等待，交由拍照动作自行处理
                    p.addAction(ActionTypeEnum.ROTATE_AIRCRAFT, angle)
                            //每个点先停留2秒，保证云台回正
                            .addAction(ActionTypeEnum.STAY, 2000)
                            .addAction(ActionTypeEnum.GIMBAL_PITCH, -60)
                            .addAction(ActionTypeEnum.START_TAKE_PHOTO, 1)
//                        .addAction(ActionTypeEnum.STAY, 2000)
                            .addAction(ActionTypeEnum.GIMBAL_PITCH, -30)
                            .addAction(ActionTypeEnum.START_TAKE_PHOTO, 1)
//                        .addAction(ActionTypeEnum.STAY, 2000)
                            .addAction(ActionTypeEnum.GIMBAL_PITCH, 0)
                            .addAction(ActionTypeEnum.START_TAKE_PHOTO, 1);
//                        .addAction(ActionTypeEnum.STAY, 2000);

                    waypointList.add(p);
                }
            }
        }

        //原路返航
        if (PanoramaParam.ReturnMode.ORIGINAL.equals(returnMode)) {
            for (int j = unifyPointList.size() - 2; j >= 0; j--) {
                UnifyPoint unifyPoint = unifyPointList.get(j);
                UnifyLocation location = unifyPoint.getLocation();
                Waypoint p = new Waypoint(location.getLat(), location.getLng(), location.getAlt());
                p.setWayPointSpeed(speed);
                waypointList.add(p);
            }
            //原路返航的时候，降落点和起始点一致
            UnifyPoint unifyPointEnd2 = unifyPointList.get(0);
            UnifyLocation locationEnd2 = unifyPointEnd2.getLocation();
            Waypoint pEnd2 = new Waypoint(locationEnd2.getLat(), locationEnd2.getLng(), takeOffLandAlt);
            pEnd2.setWayPointSpeed(speed);
            waypointList.add(pEnd2);
        } else {
            //降落点
            UnifyPoint unifyPointEnd = unifyPointList.get(unifyPointList.size() - 1);
            UnifyLocation locationEnd = unifyPointEnd.getLocation();
            Waypoint pEnd = new Waypoint(locationEnd.getLat(), locationEnd.getLng(), takeOffLandAlt);
            pEnd.setWayPointSpeed(speed);
            waypointList.add(pEnd);
        }
        unifyNestAirLineResult.setResult(true);
        if (k < 0 || NestTypeEnum.S110_MAVIC3.getValue() == nestType.getValue()) {
            unifyNestAirLineResult.setPhotoCount(unifyPointList.size() * 31);
        } else {
            unifyNestAirLineResult.setPhotoCount(unifyPointList.size() * 25);
        }
        unifyNestAirLineResult.setWaypointList(waypointList);
        log.info("全景效率优化:{}", waypointList);
        return unifyNestAirLineResult;
    }

    /**
     * 构建机巢可执行的线状巡检模式
     *
     * @param unifyAirLineFormatDto
     */
    public UnifyNestAirLineResult buildNestLinearAirLine(UnifyAirLineFormatDto unifyAirLineFormatDto, String nestId) {
//        NestEntity nestEntity = nestService.lambdaQuery().eq(NestEntity::getId, nestId)
//                .eq(NestEntity::getDeleted, 0)
//                .select(NestEntity::getLatitude, NestEntity::getLongitude, NestEntity::getType)
//                .one();
        BaseNestLocationOutDTO baseNestLocation = baseNestService.getNestLocation(nestId);
        UnifyNestAirLineResult unifyNestAirLineResult = new UnifyNestAirLineResult();
        if (Objects.isNull(baseNestLocation)) {
            unifyNestAirLineResult.setResult(false);
            unifyNestAirLineResult.setMsg("基站查询不到");
            return unifyNestAirLineResult;
        }

        UnifyLocation nestLocation = new UnifyLocation();
        nestLocation.setLat(baseNestLocation.getLat());
        nestLocation.setLng(baseNestLocation.getLng());


        TaskModeEnum mode = unifyAirLineFormatDto.getMode();
        JSONObject linearParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
        LinearParam linearParam = JSONObject.parseObject(linearParamJson.toJSONString(), LinearParam.class);
        LinearParam.TaskMode taskMode = linearParam.getTaskMode();
        double takeOffLandAlt = (double) linearParam.getTakeOffLandAlt();
        LinearParam.ReturnMode returnMode = linearParam.getReturnMode();
        FlightPathModeEnum flightPathMode = linearParam.getFlightPathMode();

        JSONArray points = (JSONArray) unifyAirLineFormatDto.getMapConfigs().get("points");
        List<UnifyPoint> unifyPointList = JSONArray.parseArray(points.toJSONString(), UnifyPoint.class);
        List<Waypoint> waypointList = new ArrayList<>();

        //起飞点
        UnifyPoint unifyPointStart = unifyPointList.get(0);
        UnifyLocation locationStart = unifyPointStart.getLocation();
        Waypoint pStart = new Waypoint(locationStart.getLat(), locationStart.getLng(), takeOffLandAlt);
        linearAirLineSetSpeed(pStart, linearParam, unifyPointStart);
        /**
         * 如果是协调转弯、并且基站类型是S100系列或者是G600,则需要在起飞点添加动作
         */
        NestTypeEnum nestType = baseNestService.getNestTypeByNestIdCache(nestId);
        if (FlightPathModeEnum.CURVED.equals(flightPathMode) && (
                NestTypeEnum.G600.equals(nestType) ||
                        NestTypeEnum.S100_V1.equals(nestType) ||
                        NestTypeEnum.S100_V2.equals(nestType))
        ) {
            //添加云台俯仰动作
            pStart.addAction(ActionTypeEnum.GIMBAL_PITCH, linearParam.getGimbalPitch());
            //添加转机头动作
            pStart.addAction(ActionTypeEnum.ROTATE_AIRCRAFT, linearParam.getHeading());
            //如果是视频录制，则添加开始录像
            if (LinearParam.TaskMode.VIDEO_CAPTURE.equals(taskMode)) {
                pStart.addAction(ActionTypeEnum.START_RECORD, 0);
            }
        }


        waypointList.add(pStart);

        if (unifyPointList.size() < 2) {
            unifyNestAirLineResult.setResult(false);
            unifyNestAirLineResult.setMsg("线状巡视至少需要两个航点");
            return unifyNestAirLineResult;
        }
        //视频拍摄模式
        if (taskMode.equals(LinearParam.TaskMode.VIDEO_CAPTURE)) {
            unifyNestAirLineResult.setVideoCount(1);
            //如果是起飞录制，就在调用任务的时候调用开始录制视频命令，不用在航点中添加开始录制的动作
            Boolean takeOffRecord = linearParam.getTakeOffRecord();
            UnifyPoint unifyPointFirst = unifyPointList.get(0);
            UnifyLocation locationFirst = unifyPointFirst.getLocation();
            Waypoint pFirst = new Waypoint(locationFirst.getLat(), locationFirst.getLng(), locationFirst.getAlt());
            linearAirLineSetSpeed(pFirst, linearParam, unifyPointFirst);
            linearAirLineSetCornerRadiusInMeters(pFirst, unifyPointFirst);
            if (!takeOffRecord) {
                pFirst.addAction(ActionTypeEnum.START_RECORD, 0);
            }
            linearUnifyAction2WaypointAction(pFirst, linearParam, unifyPointFirst, null);
            waypointList.add(pFirst);
            for (int i = 1; i < unifyPointList.size() - 1; i++) {
                UnifyPoint unifyPoint1 = unifyPointList.get(i);
                UnifyPoint unifyPoint2 = unifyPointList.get(i + 1);
                UnifyLocation location1 = unifyPoint1.getLocation();
                Waypoint p = new Waypoint(location1.getLat(), location1.getLng(), location1.getAlt());
                linearAirLineSetSpeed(p, linearParam, unifyPoint1);
                linearAirLineSetCornerRadiusInMeters(p, unifyPoint1);
                linearUnifyAction2WaypointAction(p, linearParam, unifyPoint1, unifyPoint2.getLocation());
                waypointList.add(p);
            }

            UnifyPoint unifyPointLast1 = unifyPointList.get(unifyPointList.size() - 1);
            UnifyLocation locationLast = unifyPointLast1.getLocation();
            Waypoint pLast = new Waypoint(locationLast.getLat(), locationLast.getLng(), locationLast.getAlt());
            linearAirLineSetSpeed(pLast, linearParam, unifyPointLast1);
            linearAirLineSetCornerRadiusInMeters(pLast, unifyPointLast1);
            linearUnifyAction2WaypointAction(pLast, linearParam, unifyPointLast1, nestLocation);
            if (!takeOffRecord) {
                pLast.addAction(ActionTypeEnum.STOP_RECORD, 0);
            }
            waypointList.add(pLast);
        }

        //定时拍照模式,先算去照片的宽度，再根据航向重叠度算出不重叠部分的宽度，然后除于飞行速度，然后得到拍照时间间隔
        if (taskMode.equals(LinearParam.TaskMode.CAMERA_TIMER)) {
            for (int i = 0; i < unifyPointList.size() - 1; i++) {
                UnifyPoint unifyPoint1 = unifyPointList.get(i);
                UnifyPoint unifyPoint2 = unifyPointList.get(i + 1);
                UnifyLocation location1 = unifyPoint1.getLocation();
                Waypoint p = new Waypoint(location1.getLat(), location1.getLng(), location1.getAlt());
                p.setShootPhotoTimeInterval(linearParam.getShootPhotoTimeInterval());
                linearAirLineSetSpeed(p, linearParam, unifyPoint1);
                linearAirLineSetCornerRadiusInMeters(p, unifyPoint1);
                linearUnifyAction2WaypointAction(p, linearParam, unifyPoint1, unifyPoint2.getLocation());
                waypointList.add(p);
            }
            UnifyPoint unifyPointLast1 = unifyPointList.get(unifyPointList.size() - 1);
            Waypoint pLast1 = new Waypoint(unifyPointLast1.getLocation().getLat(), unifyPointLast1.getLocation().getLng(), unifyPointLast1.getLocation().getAlt());
            pLast1.setShootPhotoTimeInterval(linearParam.getShootPhotoTimeInterval());
            linearAirLineSetSpeed(pLast1, linearParam, unifyPointLast1);
            linearAirLineSetCornerRadiusInMeters(pLast1, unifyPointLast1);
            linearUnifyAction2WaypointAction(pLast1, linearParam, unifyPointLast1, nestLocation);
            waypointList.add(pLast1);
        }


        //定时拍照模式,先算去照片的宽度，再根据航向重叠度算出不重叠部分的宽度，然后除于飞行速度，然后得到拍照时间间隔
        if (taskMode.equals(LinearParam.TaskMode.CAMERA_DISTANCE)) {
            for (int i = 0; i < unifyPointList.size() - 1; i++) {
                UnifyPoint unifyPoint1 = unifyPointList.get(i);
                UnifyPoint unifyPoint2 = unifyPointList.get(i + 1);
                UnifyLocation location1 = unifyPoint1.getLocation();
                Waypoint p = new Waypoint(location1.getLat(), location1.getLng(), location1.getAlt());
                p.setShootPhotoDistanceInterval(linearParam.getShootPhotoDistanceInterval());
                linearAirLineSetSpeed(p, linearParam, unifyPoint1);
                linearAirLineSetCornerRadiusInMeters(p, unifyPoint1);
                linearUnifyAction2WaypointAction(p, linearParam, unifyPoint1, unifyPoint2.getLocation());
                waypointList.add(p);
            }
            UnifyPoint unifyPointLast1 = unifyPointList.get(unifyPointList.size() - 1);
            Waypoint pLast1 = new Waypoint(unifyPointLast1.getLocation().getLat(), unifyPointLast1.getLocation().getLng(), unifyPointLast1.getLocation().getAlt());
            pLast1.setShootPhotoDistanceInterval(linearParam.getShootPhotoDistanceInterval());
            linearAirLineSetSpeed(pLast1, linearParam, unifyPointLast1);
            linearAirLineSetCornerRadiusInMeters(pLast1, unifyPointLast1);
            linearUnifyAction2WaypointAction(pLast1, linearParam, unifyPointLast1, nestLocation);
            waypointList.add(pLast1);
        }


        //原路返航
        if (LinearParam.ReturnMode.ORIGINAL.equals(returnMode)) {
            for (int j = unifyPointList.size() - 2; j >= 0; j--) {
                UnifyPoint unifyPoint = unifyPointList.get(j);
                UnifyLocation location = unifyPoint.getLocation();
                Waypoint p = new Waypoint(location.getLat(), location.getLng(), location.getAlt());
                linearAirLineSetSpeed(p, linearParam, unifyPoint);
                waypointList.add(p);
            }
            //原路返航的时候，降落点和起始点一致
            UnifyPoint unifyPointEnd2 = unifyPointList.get(0);
            UnifyLocation locationEnd2 = unifyPointEnd2.getLocation();
            Waypoint pEnd2 = new Waypoint(locationEnd2.getLat(), locationEnd2.getLng(), takeOffLandAlt);
            linearAirLineSetSpeed(pEnd2, linearParam, unifyPointEnd2);
            waypointList.add(pEnd2);
        } else {
            //降落点
            UnifyPoint unifyPointEnd = unifyPointList.get(unifyPointList.size() - 1);
            UnifyLocation locationEnd = unifyPointEnd.getLocation();
            Waypoint pEnd = new Waypoint(locationEnd.getLat(), locationEnd.getLng(), takeOffLandAlt);
            linearAirLineSetSpeed(pEnd, linearParam, unifyPointEnd);
            waypointList.add(pEnd);
        }
        unifyNestAirLineResult.setWaypointList(waypointList);
        unifyNestAirLineResult.setResult(true);

        return unifyNestAirLineResult;
    }

    /**
     * 计算下发给易飞终端的航线
     *
     * @param dto
     * @param airLineStr
     * @return
     */
    @Override
    public Map<String, Object> computeIflyerTerminal(TaskDetailsDto dto, String airLineStr) {
        if (dto == null || dto.getStartStopAlt() == null || dto.getSpeed() == null || dto.getAirLineType() == null || airLineStr == null) {
            return null;
        }
        if (dto.getAirLineType() == 2) {
            Integer photoCount = 0;
            Integer videoCount = 0;
            double sumDistance = 0.0;
            JSONArray jsonArray = JSONArray.parseArray(airLineStr);
            for (int i = 0; i < jsonArray.size() - 1; i++) {
                JSONObject point1 = jsonArray.getJSONObject(i);
                JSONObject point2 = jsonArray.getJSONObject(i + 1);
                if (point1.getInteger("waypointType") == 0) {
                    photoCount++;
                }

                sumDistance += DistanceUtil.getMercatorDistanceViaLonLat(point1.getDouble("aircraftLocationLongitude"), point1.getDouble("aircraftLocationLatitude"), point2.getDouble("aircraftLocationLongitude"), point2.getDouble("aircraftLocationLatitude"));
            }
            Integer pointCount = jsonArray.size();
            //总长度=航线总长度+2*起降高度
            double totalDistance = sumDistance + 2 * dto.getStartStopAlt();
            double flyTime = totalDistance / dto.getSpeed() + (photoCount * 5);
            Map<String, Object> map = new HashMap<>(8);
            map.put("predicMiles", sumDistance);
            map.put("predicTime", Math.round(flyTime));
            map.put("photoCount", photoCount);
            map.put("pointCount", pointCount);
            map.put("videoCount", videoCount);
            return map;
        }
        if (dto.getAirLineType() == 3) {
            Map<String, Object> map = new HashMap<>(8);
            map.put("predicMiles", dto.getPredictMiles());
            map.put("predicTime", dto.getPredictFlyTime());
            map.put("photoCount", dto.getPhotoCount());
            map.put("pointCount", dto.getPointCount());
            map.put("videoCount", 0);
            return map;
        }
        return null;
    }

    @Override
    public RestRes selectTaskDetailsByRecordId(Integer missionRecordId) {
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.lambdaQuery().eq(MissionRecordsEntity::getId, missionRecordId)
                .eq(MissionRecordsEntity::getDeleted, false)
                .select(MissionRecordsEntity::getMissionId)
                .one();

        if (missionRecordsEntity == null) {
            return RestRes.err("查询不到对应missionRecordsEntity");
        }

        MissionEntity missionEntity = missionService.getById(missionRecordsEntity.getMissionId());
        if (missionEntity == null) {
            return RestRes.err("查询不到对应得missionEntity");
        }
        AirLineEntity airLineEntity = airLineService.getById(missionEntity.getAirLineId());
        TaskEntity taskEntity = this.getById(missionEntity.getTaskId());
        Map<String, Object> resMap = new HashMap<>(4);
        resMap.put("missionEntity", missionEntity);
        resMap.put("airLineEntity", airLineEntity);
        resMap.put("taskEntity", taskEntity);
        return RestRes.ok(resMap);
    }

    @Override
    public void clearTagNameRedisCache(Integer taskId) {
        if (taskId != null) {
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SYS_TASK_TAG, taskId);
            redisService.del(redisKey);
        }
    }

    @Override
    public String getBaseNestIdByRecordsId(Integer missionRecordsId) {
        if (Objects.nonNull(missionRecordsId)) {
            return baseMapper.selectBaseNestIdByRecordsId(missionRecordsId);
        }
        return null;
    }

    @Override
    public DjiStartTaskParamDTO getDjiStartTaskParamDTOByMissionId(Integer missionId) {
        if (Objects.nonNull(missionId)) {
            return baseMapper.selectDjiStartTaskParamDTOByMissionId(missionId);
        }
        return null;
    }

    /**
     * 根据基站信息查询任务
     *
     * @param baseNestId
     * @return
     */
    @Override
    public List<TaskOutDTO> queryTaskByNest(List<String> baseNestId, Integer type) {
        log.info("queryTaskByNest:baseNestId -> {}", baseNestId);
        List<TaskOutDTO> taskOutDTOS = new ArrayList<>();
        if (CollectionUtil.isEmpty(baseNestId)) {
            return taskOutDTOS;
        }
        LambdaQueryWrapper<TaskEntity> condition = Wrappers.lambdaQuery(TaskEntity.class)
                .in(TaskEntity::getBaseNestId, baseNestId)
                .eq(TaskEntity::getMold, 0)
                .eq(type != null, TaskEntity::getType, type)
                .eq(TaskEntity::getDeleted, false)
                .orderByDesc(TaskEntity::getModifyTime);

        List<TaskEntity> taskEntityList = this.list(condition);
        if (CollectionUtil.isEmpty(taskEntityList)) {
            return taskOutDTOS;
        }
        List<SysTaskTagEntity> tagEntityList = this.sysTaskTagService.listTaskTagAndName(taskEntityList.stream().map(TaskEntity::getId).collect(Collectors.toList()));
        Map taskToTagMap = new HashMap();
        if (CollectionUtil.isNotEmpty(tagEntityList)) {
            taskToTagMap = tagEntityList.stream().collect(Collectors.toMap(SysTaskTagEntity::getTaskId, SysTaskTagEntity::getTagId, (oldValue, newValue) -> newValue));
        }
        Map finalTaskToTagMap = taskToTagMap;
        taskOutDTOS = taskEntityList.stream().map(x -> {
            return new TaskOutDTO().setBaseNestId(x.getBaseNestId())
                    .setDataType(x.getDataType())
                    .setMold(x.getMold())
                    .setDescription(x.getDescription())
                    .setOrgCode(x.getOrgCode())
                    .setSubType(x.getSubType())
                    .setTagId(finalTaskToTagMap.get(x.getId()) == null ? "" : finalTaskToTagMap.get(x.getId()).toString())
                    .setType(x.getType())
                    .setName(x.getName())
                    .setId(x.getId() == null ? "" : x.getId().toString());
        }).collect(Collectors.toList());

        return taskOutDTOS;
    }

    @Override
    public List<TaskEntity> queryBatchTaskEntities(List<Integer> taskIds) {
        LambdaQueryWrapper<TaskEntity> con = Wrappers.lambdaQuery(TaskEntity.class)
                .in(TaskEntity::getId, taskIds);
        List<TaskEntity> taskEntityList = this.list(con);
        if (!CollectionUtils.isEmpty(taskEntityList)) {
            return taskEntityList;
        }
        return Collections.emptyList();
    }

    public Map<String, Object> computeDynamicIflyerTerminal(Double startStopAlt, Integer speed, List<FlyPoint> flyPointList) {
        if (startStopAlt != null && speed != null && CollectionUtil.isNotEmpty(flyPointList)) {
            Integer pointCount = flyPointList.size();
            Integer photoCount = 0;
            Integer videoCount = 0;
            double sumDistance = 0.0;
            for (int i = 0; i < flyPointList.size() - 1; i++) {
                FlyPoint point1 = flyPointList.get(i);
                FlyPoint point2 = flyPointList.get(i + 1);
                if (point1.getWaypointType() == 0) {
                    photoCount++;
                }
                sumDistance += DistanceUtil.getMercatorDistanceViaLonLat(point1.getAircraftLocationLongitude(), point1.getAircraftLocationLatitude(), point2.getAircraftLocationLongitude(), point2.getAircraftLocationLatitude());
            }
            double totalDistance = sumDistance + 2 * startStopAlt;
            double flyTime = totalDistance / speed + (photoCount * 5);
            Map<String, Object> map = new HashMap<>(8);
            map.put("predicMiles", sumDistance);
            map.put("predicTime", Math.round(flyTime));
            map.put("photoCount", photoCount);
            map.put("pointCount", pointCount);
            map.put("videoCount", videoCount);
            return map;
        }
        return null;
    }


    private List<Waypoint> buildWaypointList(Integer airLineType, Integer taskType, Integer nestType, Double startStopAlt, Double nestAlt, String airLine, Integer speed, Double focalLengthMin) {

        if (taskType == 3 && airLineType == 3) {
            AirLineParams airLineParams = AirLineParams.instance()
                    .airLineType(airLineType)
                    .nestType(nestType)
                    .startStopAlt(startStopAlt)
                    .nestAltitude(nestAlt)
                    .airLineJson(airLine)
                    .useDefaultSpeed(0)
                    .waypointSpeed((double) speed);
            return AirLineBuildUtil.buildAirLine(airLineParams);

        } else if (taskType == 3 && airLineType == 2) {
            JSONArray jsonArray = JSONArray.parseArray(airLine);
            AirLineParams airLineParams = AirLineParams.instance()
                    .airLineType(airLineType)
                    .nestType(nestType)
                    .startStopAlt(startStopAlt)
                    .nestAltitude(nestAlt)
                    .waypointArray(jsonArray)
                    .useDefaultSpeed(0)
                    .waypointSpeed((double) speed)
                    .focalLengthMin(focalLengthMin);
            return AirLineBuildUtil.buildAirLine(airLineParams);

        } else if (taskType == 0 && airLineType == 3) {
            AirLineParams airLineParams = AirLineParams.instance()
                    .airLineType(airLineType)
                    .nestType(nestType)
                    .startStopAlt(startStopAlt)
                    .nestAltitude(nestAlt)
                    .airLineJson(airLine)
                    .useDefaultSpeed(1)
                    .waypointSpeed((double) speed);
            return AirLineBuildUtil.buildAirLine(airLineParams);

        } else if (taskType == 10 && airLineType == 2) {
            JSONArray jsonArray = JSONArray.parseArray(airLine);
            AirLineParams airLineParams = AirLineParams.instance()
                    .airLineType(airLineType)
                    .nestType(nestType)
                    .startStopAlt(startStopAlt)
                    .nestAltitude(nestAlt)
                    .waypointArray(jsonArray)
                    .useDefaultSpeed(0)
                    .waypointSpeed((double) speed);
            return AirLineBuildUtil.buildAirLine(airLineParams);
        }
        return Collections.emptyList();
    }

    /**
     * 正射影像航线解析
     *
     * @param unifyAirLineFormatDto
     * @return
     */
    private List<List<Waypoint>> buildNestOrthoPhotoAirLine(UnifyAirLineFormatDto unifyAirLineFormatDto) {
        TaskModeEnum mode = unifyAirLineFormatDto.getMode();
        JSONObject orthophotoParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
        OrthophotoParam orthophotoParam = JSONObject.parseObject(orthophotoParamJson.toJSONString(), OrthophotoParam.class);
        double speed = (double) orthophotoParam.getSpeed();
        double takeOffLandAlt = (double) orthophotoParam.getTakeOffLandAlt();
        Boolean rotate90 = orthophotoParam.getRotate90();
        float shootPhotoTimeInterval = orthophotoParam.getShootPhotoTimeInterval();
        float shootPhotoDistanceInterval = orthophotoParam.getShootPhotoDistanceInterval();
        OrthophotoParam.TaskMode taskMode = orthophotoParam.getTaskMode();

        JSONArray pointsList = (JSONArray) unifyAirLineFormatDto.getMapConfigs().get("points");
        List<List<Waypoint>> list = new ArrayList<>(pointsList.size());
        for (int x = 0; x < pointsList.size(); x++) {
            JSONArray points = pointsList.getJSONArray(x);
            List<UnifyPoint> unifyPointList = JSONArray.parseArray(points.toJSONString(), UnifyPoint.class);
            List<Waypoint> waypointList = new ArrayList<>(unifyPointList.size());
            //起飞点
            UnifyPoint unifyPointStart = unifyPointList.get(0);
            UnifyLocation locationStart = unifyPointStart.getLocation();
            Waypoint pStart = new Waypoint(locationStart.getLat(), locationStart.getLng(), takeOffLandAlt);
            pStart.setWayPointSpeed(speed);
            waypointList.add(pStart);

            Waypoint waypointFist = new Waypoint(locationStart.getLat(), locationStart.getLng(), locationStart.getAlt());
            waypointFist.setWayPointSpeed(speed);
            if (rotate90) {
                waypointFist.addAction(ActionTypeEnum.ROTATE_AIRCRAFT, 0);
            } else {
                waypointFist.addAction(ActionTypeEnum.ROTATE_AIRCRAFT, -90);
            }
            waypointFist.addAction(ActionTypeEnum.GIMBAL_PITCH, -90);

            if (OrthophotoParam.TaskMode.CAMERA_DISTANCE.equals(taskMode)) {
                waypointFist.setShootPhotoDistanceInterval(shootPhotoDistanceInterval);
            }

            if (OrthophotoParam.TaskMode.CAMERA_TIMER.equals(taskMode) || Objects.isNull(taskMode)) {
                waypointFist.setShootPhotoTimeInterval(shootPhotoTimeInterval);
            }

            waypointList.add(waypointFist);
            for (int i = 1; i < unifyPointList.size(); i++) {
                UnifyLocation location = unifyPointList.get(i).getLocation();
                Waypoint waypoint = new Waypoint(location.getLat(), location.getLng(), location.getAlt());
                waypoint.addAction(ActionTypeEnum.GIMBAL_PITCH, -90);
                waypoint.setWayPointSpeed(speed);
                if (OrthophotoParam.TaskMode.CAMERA_DISTANCE.equals(taskMode)) {
                    waypoint.setShootPhotoDistanceInterval(shootPhotoDistanceInterval);
                }

                if (OrthophotoParam.TaskMode.CAMERA_TIMER.equals(taskMode) || Objects.isNull(taskMode)) {
                    waypoint.setShootPhotoTimeInterval(shootPhotoTimeInterval);
                }
                waypointList.add(waypoint);
            }

            //降落点
            UnifyPoint unifyPointEnd = unifyPointList.get(unifyPointList.size() - 1);
            UnifyLocation locationEnd = unifyPointEnd.getLocation();
            Waypoint pEnd = new Waypoint(locationEnd.getLat(), locationEnd.getLng(), takeOffLandAlt);
            pEnd.setWayPointSpeed(speed);
            waypointList.add(pEnd);

            list.add(waypointList);
        }
        return list;
    }

    private List<List<Waypoint>> buildNestGridPhotoAirLine(GridPhotoParam gridPhotoParam, Integer taskId, JSONArray pointsList, boolean isSame, String orgCode) {

        double speed = (double) gridPhotoParam.getSpeed();
        double takeOffLandAlt = (double) gridPhotoParam.getTakeOffLandAlt();
        // 网格化设置
        List<GridData> gridData = gridPhotoParam.getGridData();
        String gridManageId = gridPhotoParam.getGridManageId();
        GridManageInDTO gridManageInDTO = new GridManageInDTO();
        gridManageInDTO.setGridManageId(gridManageId);
        gridManageInDTO.setGridBounds(gridPhotoParam.getGridBounds());
        gridManageInDTO.setMaxCol(gridPhotoParam.getMaxCol());
        gridManageInDTO.setMaxLine(gridPhotoParam.getMaxLine());
        gridManageInDTO.setOrgCode(orgCode);
        gridManageInDTO.setTaskId(taskId);

        if (!isSame) {
            log.info("#TaskServiceImpl.buildNestGridPhotoAirLine# orgCode:{}", orgCode);
            gridService.saveGridData(gridData, gridManageId, orgCode);
            gridService.setGridManage(gridManageInDTO);
        }

        List<List<Waypoint>> list = new ArrayList<>(pointsList.size());
        for (int x = 0; x < pointsList.size(); x++) {
            JSONArray points = pointsList.getJSONArray(x);
            List<GridPoint> unifyPointList = JSONArray.parseArray(points.toJSONString(), GridPoint.class);
            List<Waypoint> waypointList = new ArrayList<>(unifyPointList.size());
            // 设置起飞点
            GridPoint unifyPointStart = unifyPointList.get(0);
            UnifyLocation locationStart = unifyPointStart.getLocation();
            Waypoint pStart = new Waypoint(locationStart.getLat(), locationStart.getLng(), takeOffLandAlt);
            pStart.setWayPointSpeed(speed);
            pStart.setCornerRadiusInMeters(0.0);
            waypointList.add(pStart);
            // 设置其余航点
            for (GridPoint gridPoint : unifyPointList) {
                // 位置
                UnifyLocation location = gridPoint.getLocation();
                Waypoint waypoint = new Waypoint(location.getLat(), location.getLng(), location.getAlt());
                // 动作
                List<ActionCustoms> actionCustoms = gridPoint.getActionCustoms();
                for (ActionCustoms actionCustom : actionCustoms) {
                    waypoint.addAction(actionCustom.getActionType(), actionCustom.getValue());
                    if (ActionTypeEnum.START_TAKE_PHOTO.equals(actionCustom.getActionType())) {
                        waypoint.setPhotoPropList(actionCustom.getPhotoPropList());
                        waypoint.setByname(actionCustom.getByname());
                    }
                }
                Double cornerRadiusInMeters = gridPoint.getCornerRadiusInMeters();
                waypoint.setCornerRadiusInMeters(cornerRadiusInMeters);
                waypoint.setWayPointSpeed(speed);
                waypointList.add(waypoint);
            }
            // 设置降落点
            GridPoint unifyPointEnd = unifyPointList.get(unifyPointList.size() - 1);
            UnifyLocation locationEnd = unifyPointEnd.getLocation();
            Waypoint pEnd = new Waypoint(locationEnd.getLat(), locationEnd.getLng(), takeOffLandAlt);
            pEnd.setWayPointSpeed(speed);
            pEnd.setCornerRadiusInMeters(0.0);
            waypointList.add(pEnd);

            list.add(waypointList);
        }
        return list;
    }

    /**
     * 构建倾斜摄影航线
     *
     * @param unifyAirLineFormatDto
     * @return
     */
    private List<List<Waypoint>> buildNestSlopePhotoAirLine(UnifyAirLineFormatDto unifyAirLineFormatDto) {
        //倾斜摄影需要飞5个架次，就是说需要构建5条航线
        TaskModeEnum mode = unifyAirLineFormatDto.getMode();
        JSONObject slopePhotoParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
        SlopePhotoParam slopePhotoParam = JSONObject.parseObject(slopePhotoParamJson.toJSONString(), SlopePhotoParam.class);
        double speed = (double) slopePhotoParam.getSpeed();
        double takeOffLandAlt = (double) slopePhotoParam.getTakeOffLandAlt();
        float shootPhotoTimeInterval = slopePhotoParam.getShootPhotoTimeInterval();
        float shootPhotoDistanceInterval = slopePhotoParam.getShootPhotoDistanceInterval();
        SlopePhotoParam.TaskMode taskMode = slopePhotoParam.getTaskMode();
        //倾斜角度
        Integer tiltAngle = slopePhotoParam.getTiltAngle();
        List<List<Waypoint>> waypointsList = new ArrayList<>(5);
        JSONArray pointsList = (JSONArray) unifyAirLineFormatDto.getMapConfigs().get("points");
        for (int i = 0; i < pointsList.size(); i++) {
            JSONArray points = pointsList.getJSONArray(i);
            List<UnifyPoint> unifyPointList = JSONArray.parseArray(points.toJSONString(), UnifyPoint.class);

            List<Waypoint> waypointList = new ArrayList<>(unifyPointList.size());
            //起飞点
            UnifyPoint unifyPointStart = unifyPointList.get(0);
            UnifyLocation locationStart = unifyPointStart.getLocation();
            Waypoint pStart = new Waypoint(locationStart.getLat(), locationStart.getLng(), takeOffLandAlt);
            pStart.setWayPointSpeed(speed);
            waypointList.add(pStart);

            Waypoint waypointFist = new Waypoint(locationStart.getLat(), locationStart.getLng(), locationStart.getAlt());
            waypointFist.setWayPointSpeed(speed);
//            if (i < 3) {
//                waypointFist.addAction(ActionTypeEnum.ROTATE_AIRCRAFT, -90);
//            } else {
//                waypointFist.addAction(ActionTypeEnum.ROTATE_AIRCRAFT, 180);
//            }
            waypointFist.addAction(ActionTypeEnum.GIMBAL_PITCH, tiltAngle);
            if (SlopePhotoParam.TaskMode.CAMERA_TIMER.equals(taskMode) || Objects.isNull(taskMode)) {
                waypointFist.setShootPhotoTimeInterval(shootPhotoTimeInterval);
            }
            if (SlopePhotoParam.TaskMode.CAMERA_DISTANCE.equals(taskMode)) {
                waypointFist.setShootPhotoDistanceInterval(shootPhotoDistanceInterval);
            }
            waypointList.add(waypointFist);

            for (int j = 1; j < unifyPointList.size(); j++) {
                UnifyLocation location = unifyPointList.get(j).getLocation();
                Waypoint waypoint = new Waypoint(location.getLat(), location.getLng(), location.getAlt());
                waypoint.addAction(ActionTypeEnum.GIMBAL_PITCH, tiltAngle);
                waypoint.setWayPointSpeed(speed);
                if (SlopePhotoParam.TaskMode.CAMERA_TIMER.equals(taskMode) || Objects.isNull(taskMode)) {
                    waypoint.setShootPhotoTimeInterval(shootPhotoTimeInterval);
                }
                if (SlopePhotoParam.TaskMode.CAMERA_DISTANCE.equals(taskMode)) {
                    waypoint.setShootPhotoDistanceInterval(shootPhotoDistanceInterval);
                }
                waypointList.add(waypoint);
            }

            //降落点
            UnifyPoint unifyPointEnd = unifyPointList.get(unifyPointList.size() - 1);
            UnifyLocation locationEnd = unifyPointEnd.getLocation();
            Waypoint pEnd = new Waypoint(locationEnd.getLat(), locationEnd.getLng(), takeOffLandAlt);
            waypointList.add(pEnd);

            waypointsList.add(waypointList);
        }

        return waypointsList;
    }

    private double getAirLineHighestAlt(List<FlyPoint> flyPoints) {
        double highestAlt = Double.MIN_VALUE;
        if (CollectionUtil.isNotEmpty(flyPoints)) {
            for (FlyPoint fp : flyPoints) {
                Double tempAlt = fp.getAircraftLocationAltitude();
                highestAlt = Math.max(tempAlt, highestAlt);
            }
        }
        return highestAlt;
    }

    /**
     * 根据左边点添加机头朝向
     * 先算出两个点角度,然后顺时针旋转heading角度，也就是加90。机巢的机头朝向在【-180~180】，因此需要把【0~360】转换一下。
     * 如果值大于180，就减去360
     *
     * @param waypoint       航点
     * @param unifyLocation1
     * @param unifyLocation2
     * @param heading        机头朝向值
     */
    private void linearAddHeadingAction(Waypoint waypoint, UnifyLocation unifyLocation1, UnifyLocation unifyLocation2, Double heading) {
        double angle = LatLngUtils.getABAngle(unifyLocation2.getLat(), unifyLocation2.getLng(), unifyLocation1.getLat(), unifyLocation1.getLng());
        double aircraftYaw = angle + heading;
        aircraftYaw = aircraftYaw > 180 ? aircraftYaw - 360 : aircraftYaw;
        waypoint.addAction(ActionTypeEnum.ROTATE_AIRCRAFT, (int) aircraftYaw);
    }

    private double getAirLineHighestAlt2(List<AirLineEntity> aleList) {
        if (CollectionUtil.isNotEmpty(aleList)) {
            double highestAlt = Double.MIN_VALUE;
            for (AirLineEntity ale : aleList) {
                if (2 == ale.getType()) {
                    JSONArray objects = JSONArray.parseArray(ale.getWaypoints());
                    for (int i = 0; i < objects.size(); i++) {
                        Double altitude = objects.getJSONObject(i).getDouble("aircraftLocationAltitude");
                        highestAlt = Double.max(altitude, highestAlt);
                    }
                }
            }
            return highestAlt;
        }
        return 0.0;
    }

    private RestRes saveOrUpdateMultiMissionTask(UnifyTaskDto unifyTaskDto) {
        String originBaseNest = null;
        //判断在记录表有没有记录，如果有记录，则不能修改
        if (unifyTaskDto.getTaskId() != null) {
            List<MissionEntity> list = missionService.lambdaQuery()
                    .eq(MissionEntity::getTaskId, unifyTaskDto.getTaskId())
                    .eq(MissionEntity::getDeleted, false)
                    .select(MissionEntity::getId)
                    .list();

            if (CollectionUtil.isEmpty(list)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SAVE_FAILED_NO_SORTIES_CAN_BE_SEARCHED.getContent()));
            }
            List<Integer> missionId = list.stream().map(MissionEntity::getId).collect(Collectors.toList());
            Integer count = missionRecordsService.lambdaQuery()
                    .in(MissionRecordsEntity::getMissionId, missionId)
                    .eq(MissionRecordsEntity::getDeleted, false)
                    .count();
            // 网格化允许编辑
            if (count > 0 && !TaskModeEnum.GRID.getValue().equals(unifyTaskDto.getTaskType())) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_HAS_GENERATED_ASSOCIATED_DATA.getContent()));
            }
            List<TaskEntity> taskEntities = this.queryBatchTaskEntities(Collections.singletonList(unifyTaskDto.getTaskId()));
            if (!CollectionUtils.isEmpty(taskEntities)) {
                originBaseNest = taskEntities.get(0).getBaseNestId();
            }

        }
        boolean isDjiType = this.DjiType(unifyTaskDto.getNestId());
        Integer mpeId = null;
        List<Integer> aleIdList = new ArrayList<>();
        List<Integer> meIdList = new ArrayList<>();
        if (unifyTaskDto.getTaskId() != null) {
            List<MissionEntity> missionEntities = missionService.listMissionByTaskId(unifyTaskDto.getTaskId());
            if (CollectionUtil.isNotEmpty(missionEntities)) {
                MissionEntity missionEntity = missionEntities.get(0);
                mpeId = missionEntity.getMissionParamId();
                for (MissionEntity me : missionEntities) {
                    aleIdList.add(me.getAirLineId());
                    meIdList.add(me.getId());
                }
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SAVE.getContent()));
            }
        }

        TaskEntity te = new TaskEntity();
        te.setId(unifyTaskDto.getTaskId());
        te.setOrgCode(unifyTaskDto.getUnitId());
        te.setName(unifyTaskDto.getTaskName());
        te.setDescription(unifyTaskDto.getDescription());
        if (unifyTaskDto.getMold() == 0) {
            te.setBaseNestId(unifyTaskDto.getNestId());
            te.setMold(0);
        } else if (unifyTaskDto.getMold() == 1) {
            te.setOrgCode(unifyTaskDto.getUnitId());
            te.setMold(1);
        }
        te.setCreatorId(Long.valueOf(unifyTaskDto.getAccountId()));

        te.setModifyTime(LocalDateTime.now());
        te.setType(unifyTaskDto.getTaskType());
        boolean taskRes = this.saveOrUpdate(te);

        String unifyAirLineJson = unifyTaskDto.getUnifyAirLineJson();

        //保存missionParam表
        MissionParamEntity mpe = new MissionParamEntity();
        mpe.setId(mpeId);
        mpe.setAutoFlightSpeed(unifyTaskDto.getAutoFlightSpeed());
        mpe.setStartStopPointAltitude((double) unifyTaskDto.getTakeOffLandAlt());
        mpe.setHeadingMode(HeadingModeEnum.AUTO.getValue());
        // 网格化
        if (TaskModeEnum.GRID.getValue().equals(unifyTaskDto.getTaskType())) {
            if (unifyTaskDto.getHeadingMode() != null)
                mpe.setHeadingMode(unifyTaskDto.getHeadingMode().getValue());
            if (unifyTaskDto.getFlightPathMode() != null)
                mpe.setFlightPathMode(unifyTaskDto.getFlightPathMode().getValue());
        }
        mpe.setDeltaTime(unifyTaskDto.getDeltaTime());

        boolean mpeRes = missionParamService.saveOrUpdate(mpe);
        boolean aleRes = false;
        boolean meRes = false;

        UnifyNestAirLineResult unifyNestAirLineResult;
        if (TaskModeEnum.GRID.getValue().equals(unifyTaskDto.getTaskType())) {
            List<MultiMissionExtra> multiMissionExtraList = unifyTaskDto.getMultiMissionExtraList();
            int photoCount = 0;
            if (!CollectionUtils.isEmpty(multiMissionExtraList)) {
                for (MultiMissionExtra multiMissionExtra : multiMissionExtraList) {
                    photoCount += multiMissionExtra.getPhotoCount();
                }
            }
            unifyNestAirLineResult = buildGirdNestAirLine(unifyAirLineJson, te.getId(), mpe, te.getOrgCode(), photoCount);
            if (!StringUtil.isEmpty(originBaseNest) && !originBaseNest.equals(unifyTaskDto.getNestId())) {
                unifyNestAirLineResult.setSameRoute(false);
            }
        } else {
            unifyNestAirLineResult = buildUnifyNestAirLine(unifyAirLineJson, unifyTaskDto.getNestId(), te.getId());
        }
        //构建航线,如果是易飞航线不用构
        if (unifyNestAirLineResult == null || !unifyNestAirLineResult.isResult()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_BUILD_ROUTE.getContent()));
        }
        List<List<Waypoint>> waypointsList = unifyNestAirLineResult.getWaypointsList();
        List<AirLineEntity> aleList = new ArrayList<>(5);
        List<MissionEntity> meList = new ArrayList<>(5);
        List<MultiMissionExtra> multiMissionExtraList = unifyTaskDto.getMultiMissionExtraList();
        for (int i = 0; i < waypointsList.size(); i++) {
            List<Waypoint> waypoints = waypointsList.get(i);
            //保存air_line航线
            MultiMissionExtra multiMissionExtra = multiMissionExtraList.get(i);
            AirLineEntity ale = new AirLineEntity();
            ale.setType(unifyTaskDto.getAirLineType());
            ale.setVideoCount(0);
            ale.setName(unifyTaskDto.getTaskName() + "-" + MessageUtils.getMessage(MessageEnum.TASK_MISSION_FLIGHT.getContent()) + (i + 1));
            ale.setPointCount(multiMissionExtra.getPointCount());
            ale.setPhotoCount(multiMissionExtra.getPhotoCount());
            ale.setCopyCount(0);
            ale.setPredicTime(multiMissionExtra.getPredictFlyTime());
            ale.setPredicMiles(multiMissionExtra.getPredictMiles());
            ale.setWaypoints(unifyAirLineJson);
            if (isDjiType) {
                ale.setDjiWaypoints(unifyTaskDto.getDjiAirLineMap().get(i));
            }
            ale.setOriginalWaypoints(WaypointUtil.waypointListToJsonStr(waypoints));
            ale.setMergeCount(waypoints.size() - 2);
            aleList.add(ale);

            //保存mission表
            MissionEntity me = new MissionEntity();
            me.setName(unifyTaskDto.getTaskName() + "-" + MessageUtils.getMessage(MessageEnum.TASK_MISSION_FLIGHT.getContent()) + (i + 1));
            me.setUuid(UuidUtil.createNoBar());
            me.setSeqId(i + 1);
            me.setTaskId(te.getId());
            me.setOrgCode(te.getOrgCode());
            me.setMissionParamId(mpe.getId());
            me.setCopyCount(0);
            meList.add(me);
        }

        // 网格化编辑保存不通过架次数量判断
        if (TaskModeEnum.GRID.getValue().equals(unifyTaskDto.getTaskType())) {
            handleMissionAndAirlineForGrid(aleIdList, aleList, meIdList, meList, unifyNestAirLineResult.isSameRoute(), te.getId());
            // 其余航线走以前的逻辑
        } else {
            if (CollectionUtil.isNotEmpty(aleIdList)) {
                if (aleIdList.size() == aleList.size()) {
                    for (int i = 0; i < aleIdList.size(); i++) {
                        aleList.get(i).setId(aleIdList.get(i));
                    }
                } else {
                    airLineService.lambdaUpdate()
                            .in(AirLineEntity::getId, aleIdList)
                            .set(AirLineEntity::getDeleted, 1)
                            .update();
                }
            }

            if (CollectionUtil.isNotEmpty(meIdList)) {
                if (meIdList.size() == meList.size()) {
                    for (int i = 0; i < meIdList.size(); i++) {
                        meList.get(i).setId(meIdList.get(i));
                    }
                } else {
                    missionService.lambdaUpdate()
                            .in(MissionEntity::getId, meIdList)
                            .set(MissionEntity::getDeleted, 1)
                            .update();
                }
            }
        }


        aleRes = airLineService.saveOrUpdateBatch(aleList);
        for (int i = 0; i < meList.size(); i++) {
            meList.get(i).setAirLineId(aleList.get(i).getId());
        }
        meRes = missionService.saveOrUpdateBatch(meList);


        Integer tagId = unifyTaskDto.getTagId();
        if (unifyTaskDto.getTagId() != null) {
            SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.getOne(new QueryWrapper<SysTaskTagEntity>().eq("task_id", te.getId()));
            if (sysTaskTagEntity != null) {
                sysTaskTagEntity.setTagId(tagId);
                sysTaskTagService.updateById(sysTaskTagEntity);
            } else {
                SysTaskTagEntity stte = new SysTaskTagEntity();
                stte.setCreateTime(LocalDateTime.now());
                stte.setModifyTime(LocalDateTime.now());
                stte.setTagId(tagId);
                stte.setTaskId(te.getId());
                sysTaskTagService.save(stte);
            }
        }

        //生成大疆航线包 TODO新增按顺序 ，
        if (isDjiType && TaskModeEnum.GRID.getValue().equals(unifyTaskDto.getTaskType())) {
            DJIAirLineHandleDO djiAirLineHandleDO = new DJIAirLineHandleDO()
                    .setTaskType(unifyTaskDto.getTaskType())
                    .setTaskId(te.getId())
                    .setNestId(unifyTaskDto.getNestId())
                    .setAirLineList(aleList)
                    .setMissionList(meList)
                    .setDjiAirLineMap(unifyTaskDto.getDjiAirLineMap());
            this.applicationContext.publishEvent(new DJIAirLineHandleEvent(djiAirLineHandleDO));
        }

        if (taskRes && mpeRes && aleRes && meRes) {
            Map<String, Object> resMap = new HashMap<>(2);
            resMap.put("taskId", te.getId());
            return RestRes.ok(resMap, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSION_SAVED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SAVE.getContent()));
    }


    /**
     * 环绕飞行
     *
     * @param unifyAirLineFormatDto
     * @return
     */
    private UnifyNestAirLineResult buildNestAroundFlyAirLine(UnifyAirLineFormatDto unifyAirLineFormatDto) {
        TaskModeEnum mode = unifyAirLineFormatDto.getMode();
        JSONObject aroundFlyParamJo = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
        AroundFlyParam aroundFlyParam = JSONObject.parseObject(aroundFlyParamJo.toJSONString(), AroundFlyParam.class);
        Integer minLineAlt = aroundFlyParam.getMinLineAlt();
        Integer maxLineAlt = aroundFlyParam.getMaxLineAlt();
        Integer flyLayerNum = aroundFlyParam.getFlyLayerNum();
        double takeOffLandAlt = (double) aroundFlyParam.getTakeOffLandAlt();
        double speed = (double) aroundFlyParam.getSpeed();
        Integer gimbalPitch = aroundFlyParam.getGimbalPitch();
        float shootPhotoTimeInterval = aroundFlyParam.getShootPhotoTimeInterval();
        float shootPhotoDistanceInterval = aroundFlyParam.getShootPhotoDistanceInterval();
        AroundFlyParam.TaskMode taskMode = aroundFlyParam.getTaskMode();
        JSONArray points = (JSONArray) unifyAirLineFormatDto.getMapConfigs().get("points");
        List<UnifyPoint> unifyPointList = JSONArray.parseArray(points.toJSONString(), UnifyPoint.class);

        List<Waypoint> waypointList = new ArrayList<>();

        //起始点(入口点的上方)
        Map<String, Double> entryPoint = aroundFlyParam.getEntryPoint();
        Waypoint pStart = new Waypoint(entryPoint.get("lat"), entryPoint.get("lng"), takeOffLandAlt);
        pStart.setWayPointSpeed(speed);
        pStart.addAction(ActionTypeEnum.GIMBAL_PITCH, gimbalPitch);
        waypointList.add(pStart);

        int height = maxLineAlt - minLineAlt;
        int heightDifference = flyLayerNum == 1 ? 1 : height / (flyLayerNum - 1);
        for (int i = 0; i < flyLayerNum; i++) {
            Waypoint wps = new Waypoint(entryPoint.get("lat"), entryPoint.get("lng"), entryPoint.get("alt") - i * heightDifference);
            wps.setWayPointSpeed(speed);
            wps.setCornerRadiusInMeters(0.3);
            waypointList.add(wps);
            int startJ = 0;
            UnifyPoint p0 = unifyPointList.get(0);
            if (p0.getLocation().getAlt().equals(wps.getWayPointAltitude()) ||
                    p0.getLocation().getLat().equals(wps.getWayPointLatitude()) ||
                    p0.getLocation().getLng().equals(wps.getWayPointLongitude())
            ) {
                startJ = 1;
            }
            for (int j = startJ; j < unifyPointList.size() - 1; j++) {
                UnifyLocation location = unifyPointList.get(j).getLocation();
                double alt = location.getAlt() - i * heightDifference;
                Waypoint wp = new Waypoint(location.getLat(), location.getLng(), alt);
                wp.setWayPointSpeed(speed);
                wp.setCornerRadiusInMeters(unifyPointList.get(j).getCornerRadiusInMeters());
                waypointList.add(wp);
            }
            UnifyLocation peLocation = unifyPointList.get(unifyPointList.size() - 1).getLocation();
            double alt = peLocation.getAlt() - i * heightDifference;
            Waypoint wp = new Waypoint(peLocation.getLat(), peLocation.getLng(), alt);
            wp.setWayPointSpeed(speed);
            wp.setCornerRadiusInMeters(0.3);
            waypointList.add(wp);
        }

        //一个建筑物可能很高，所以可能有多层航线，按照最高航线高度减去最低航线高度除以层数得到层高差，依次递减
        //视频录制模式
        if (taskMode.equals(AroundFlyParam.TaskMode.VIDEO_CAPTURE)) {
            int size = waypointList.size();
            //航线第一个点添加开始录像动作
            Waypoint waypoint1 = waypointList.get(0);
            waypoint1.addAction(ActionTypeEnum.START_RECORD, 0);

            //航线最后一点添加结束录像动作
            Waypoint waypoint2 = waypointList.get(size - 1);
            waypoint2.addAction(ActionTypeEnum.STOP_RECORD, 0);
        }

        //定时拍照模式
        if (taskMode.equals(AroundFlyParam.TaskMode.CAMERA_TIMER)) {
            waypointList.forEach(wp -> wp.setShootPhotoTimeInterval(shootPhotoTimeInterval));
            waypointList.get(0).setShootPhotoTimeInterval(null);
        }

        if (AroundFlyParam.TaskMode.CAMERA_DISTANCE.equals(taskMode)) {
            waypointList.forEach(wp -> wp.setShootPhotoDistanceInterval(shootPhotoDistanceInterval));
            waypointList.get(0).setShootPhotoDistanceInterval(null);
        }

        //降落点
        Waypoint pEnd = new Waypoint(entryPoint.get("lat"), entryPoint.get("lng"), takeOffLandAlt);
        pEnd.setWayPointSpeed(speed);
        waypointList.add(pEnd);

//        int pointCount = unifyPointList.size() * flyLayerNum;
        double sumLen = 0.0;
        for (int i = 0; i < unifyPointList.size() - 1; i++) {
            UnifyPoint unifyPoint1 = unifyPointList.get(i);
            UnifyPoint unifyPoint2 = unifyPointList.get(i + 1);
            UnifyLocation location1 = unifyPoint1.getLocation();
            UnifyLocation location2 = unifyPoint2.getLocation();
            double distance = DistanceUtil.getDistance(location1.getLng(), location1.getLat(), location2.getLng(), location2.getLat());
            double distance2 = Math.abs(location1.getAlt() - location2.getAlt());
            sumLen = sumLen + distance + distance2;
        }
//        double photoCount = sumLen / speed / shootPhotoTimeInterval * flyLayerNum;
        UnifyNestAirLineResult unifyNestAirLineResult = new UnifyNestAirLineResult();
        unifyNestAirLineResult.setWaypointList(waypointList);
        unifyNestAirLineResult.setResult(true);
//        unifyNestAirLineResult.setPointCount(pointCount);
//        unifyNestAirLineResult.setPhotoCount((int) photoCount);
        return unifyNestAirLineResult;
    }

    private UnifyNestAirLineResult buildDjiAirLine(UnifyAirLineFormatDto unifyAirLineFormatDto) {
        UnifyNestAirLineResult unifyNestAirLineResult = new UnifyNestAirLineResult();
        TaskModeEnum mode = unifyAirLineFormatDto.getMode();
        JSONObject aroundFlyParamJo = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
        DjiKmlParam djiKmlParam = JSONObject.parseObject(aroundFlyParamJo.toJSONString(), DjiKmlParam.class);

        JSONArray points = (JSONArray) unifyAirLineFormatDto.getMapConfigs().get("points");
        List<UnifyPoint> unifyPointList = JSONArray.parseArray(points.toJSONString(), UnifyPoint.class);
        if (CollectionUtils.isEmpty(unifyPointList)) {
            unifyNestAirLineResult.setResult(false);
            return unifyNestAirLineResult;
        }


        List<Waypoint> waypointList = unifyPointList.stream().map(up -> {
            Waypoint waypoint = new Waypoint();
            double speed = Objects.nonNull(up.getSpeed()) ? up.getSpeed() : 2.0;
            waypoint.setWayPointSpeed(speed);
            waypoint.setWayPointLongitude(up.getLocation().getLng());
            waypoint.setWayPointLatitude(up.getLocation().getLat());
            waypoint.setWayPointAltitude(up.getLocation().getAlt());
            List<UnifyAction> customActions = up.getCustomActions();
            if (!CollectionUtils.isEmpty(customActions)) {
                List<WaypointAction> waList = customActions.stream().map(ca -> {
                    WaypointAction wa = new WaypointAction();
                    wa.setActionType(ca.getActionType());
                    wa.setActionParam(Objects.isNull(ca.getValue()) ? 0 : ca.getValue());
                    return wa;
                }).collect(Collectors.toList());
                waypoint.setWaypointActionList(waList);
            }
            return waypoint;
        }).collect(Collectors.toList());
        //起飞点
        Waypoint waypoint0 = waypointList.get(0);
        Waypoint waypointFirst = new Waypoint();
        waypointFirst.setWayPointSpeed(waypoint0.getWayPointSpeed());
        waypointFirst.setWayPointLongitude(waypoint0.getWayPointLongitude());
        waypointFirst.setWayPointLatitude(waypoint0.getWayPointLatitude());
        waypointFirst.setWayPointAltitude(djiKmlParam.getTakeOffLandAlt());
        waypointList.add(0, waypointFirst);

        //降落点
        Waypoint waypointLen = waypointList.get(waypointList.size() - 1);
        Waypoint waypointLast = new Waypoint();
        waypointLast.setWayPointSpeed(waypointLen.getWayPointSpeed());
        waypointLast.setWayPointLongitude(waypointLen.getWayPointLongitude());
        waypointLast.setWayPointLatitude(waypointLen.getWayPointLatitude());
        waypointLast.setWayPointAltitude(djiKmlParam.getTakeOffLandAlt());
        waypointList.add(waypointLast);

        unifyNestAirLineResult.setResult(true);
        unifyNestAirLineResult.setWaypointList(waypointList);

        return unifyNestAirLineResult;
    }

    private List<FlyPoint> distinctAdjion(List<FlyPoint> points) {
        List<FlyPoint> result = new ArrayList<>();
        for (int i = 1; i < points.size() - 1; i++) {
            //相同点
            if (points.get(i).equals(points.get(i - 1))) {
                continue;
            }
            result.add(points.get(i - 1));
        }
        return result;
    }

    private Integer saveOrUpdateTask(FineInspTaskDto dto) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setModifyTime(LocalDateTime.now());
        taskEntity.setCreatorId(Long.valueOf(dto.getAccountId()));
        if (dto.getTaskId() != null) {
            taskEntity.setId(dto.getTaskId());
        }
        taskEntity.setName(dto.getTaskName())
                .setType(dto.getTaskType())
                .setDescription(dto.getDescription())
                .setMold(dto.getMold());

        if (dto.getMold().equals(TaskMoldEnum.NEST.getCode())) {
            taskEntity.setBaseNestId(dto.getNestId());
            taskEntity.setOrgCode(dto.getUnitId());
        } else {
            taskEntity.setOrgCode(dto.getUnitId());
        }
        boolean b1 = this.saveOrUpdate(taskEntity);
        if (!b1) {
            throw new NestException("保存精细巡检任务保存错误");
        }
        return taskEntity.getId();
    }

    private void saveOrUpdateSysTaskTag(FineInspTaskDto dto) {
        if (dto.getTagId() == null && dto.getUnitId() != null) {
            dto.setTagId(setUnitTag(dto.getUnitId(), dto.getNestId()));
        }
        SysTaskTagEntity sysTaskTagEntity = sysTaskTagService.lambdaQuery().eq(SysTaskTagEntity::getTaskId, dto.getTaskId()).one();
        if (sysTaskTagEntity == null) {
            sysTaskTagEntity = new SysTaskTagEntity();
        }
        sysTaskTagEntity.setTagId(dto.getTagId() == null ? -1 : dto.getTagId());
        sysTaskTagEntity.setTaskId(dto.getTaskId());
        boolean b2 = sysTaskTagService.saveOrUpdate(sysTaskTagEntity);
        if (!b2) {
            throw new NestException("保存精细巡检任务标签错误");
        }
    }

    private void saveOrUpdateZipAndTowerRel(FineInspTaskDto dto) {
        //3.保存task与zip关系表,与tower关系表
        TaskFineInspZipRelEntity taskFineInspZipRelEntity = taskFineInspZipRelService.lambdaQuery().eq(TaskFineInspZipRelEntity::getTaskId, dto.getTaskId()).one();
        if (taskFineInspZipRelEntity == null) {
            taskFineInspZipRelEntity = new TaskFineInspZipRelEntity();
        }
        taskFineInspZipRelEntity.setFineZipId(dto.getZipId()).setTaskId(dto.getTaskId());
        boolean b3 = taskFineInspZipRelService.saveOrUpdate(taskFineInspZipRelEntity);
        if (!b3) {
            throw new NestException("保存精细巡检任task与zip关系错误");
        }

        //4.保存task与tower关系表
        List<Integer> towerIdList = dto.getTowerIdList();
        Map<Integer, Integer> towerTaskMap = null;
        List<TaskFineInspTowerRelEntity> taskFineInspTowerRelEntityList1 = taskFineInspTowerRelService.lambdaQuery()
                .eq(TaskFineInspTowerRelEntity::getTaskId, dto.getTaskId()).list();

        if (CollectionUtil.isNotEmpty(taskFineInspTowerRelEntityList1)) {
            towerTaskMap = taskFineInspTowerRelEntityList1.stream()
                    .collect(Collectors.toMap(TaskFineInspTowerRelEntity::getFineTowerId, TaskFineInspTowerRelEntity::getId));

            List<Integer> taskTowerIdList = taskFineInspTowerRelEntityList1
                    .stream()
                    .filter(t -> !towerIdList.contains(t.getFineTowerId()))
                    .map(TaskFineInspTowerRelEntity::getId)
                    .collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(taskTowerIdList)) {
                boolean b = taskFineInspTowerRelService.removeByIds(taskTowerIdList);
            }
        }

        List<TaskFineInspTowerRelEntity> taskFineInspTowerRelEntityList2 = new ArrayList<>(towerIdList.size());
        for (Integer towerId : towerIdList) {
            TaskFineInspTowerRelEntity taskFineInspTowerRelEntity = new TaskFineInspTowerRelEntity();
            if (towerTaskMap != null) {
                Integer relId = towerTaskMap.get(towerId);
                taskFineInspTowerRelEntity.setId(relId);
            }
            taskFineInspTowerRelEntity.setFineTowerId(towerId).setTaskId(dto.getTaskId());
            taskFineInspTowerRelEntityList2.add(taskFineInspTowerRelEntity);
        }

        boolean b4 = taskFineInspTowerRelService.saveOrUpdateBatch(taskFineInspTowerRelEntityList2);
        if (!b4) {
            throw new NestException("保存精细巡检任task与tower关系错误");
        }
    }

    private Integer saveOrUpdateMissionParam(FineInspTaskDto dto, Integer missionParamId) {
        MissionParamEntity missionParamEntity = new MissionParamEntity();
        missionParamEntity.setAutoFlightSpeed(dto.getAutoFlightSpeed())
                .setSpeed(dto.getSpeed())
                .setStartStopPointAltitude(dto.getTakeOffLandAlt())
                .setHeadingMode(HeadingModeEnum.AUTO.getValue());
        if (missionParamId != null) {
            missionParamEntity.setId(missionParamId);
        }
        boolean b5 = missionParamService.saveOrUpdate(missionParamEntity);
        if (!b5) {
            throw new NestException("保存精细巡检mission_param错误");
        }
        return missionParamEntity.getId();
    }

    private Integer saveOrUpdateAirLine(FineInspTaskDto dto, Integer airLineId) {
        AirLineEntity airLineEntity = new AirLineEntity();
        if (airLineId != null) {
            airLineEntity.setId(airLineId);
        }

        Map<Integer, List<PointCloudWaypoint>> routeMap = dto.getRouteMap();
        Set<Map.Entry<Integer, List<PointCloudWaypoint>>> entries = routeMap.entrySet();
        List<PointCloudWaypoint> route = new ArrayList<>();
        for (Map.Entry<Integer, List<PointCloudWaypoint>> me : entries) {
            route.addAll(me.getValue());
        }
        String waypoints = JacksonUtil.object2Json(route);
        int length = waypoints.getBytes().length;

        //MqSQL类型MEDIUMTEXT最多可存储16777215字节
        if (length >= 16777215) {
            throw new NestException("航线长度过大，无法保存");
        }
        airLineEntity.setName(dto.getTaskName() + "-航线")
                .setPhotoCount(dto.getPhotoActionCount())
                .setWaypoints(waypoints)
                .setPointCount(dto.getPointCount())
                .setMergeCount(dto.getPointCount())
                .setType(AirLineTypeEnum.POINT_CLOUD_AIR_LINE.getValue())
                .setPredicMiles(dto.getPredictMiles())
                .setPredicTime(dto.getPredictFlyTime())
                .setShowInfo(dto.getShowInfo())
                .setAbsolute(true);
        boolean b6 = airLineService.saveOrUpdate(airLineEntity);
        if (!b6) {
            throw new NestException("保存精细巡检air_line错误");
        }
        return airLineEntity.getId();
    }

    private Integer saveOrUpdateMission(FineInspTaskDto dto, Integer airLineId, Integer missionParamId, Integer missionId) {
        MissionEntity missionEntity = new MissionEntity();
        if (missionId != null) {
            missionEntity.setId(missionId);
        }
        missionEntity.setName(dto.getTaskName() + "-架次")
                .setUuid(UuidUtil.createNoBar())
                .setSeqId(1)
                .setAirLineId(airLineId)
                .setTaskId(dto.getTaskId())
                .setOrgCode(dto.getUnitId())
                .setMissionParamId(missionParamId);
        boolean b7 = missionService.saveOrUpdate(missionEntity);
        if (!b7) {
            throw new NestException("保存精细巡检air_line错误");
        }
        return missionEntity.getId();
    }

    private boolean updateTowerRouteData(Map<Integer, List<PointCloudWaypoint>> routeMap) {
        if (CollectionUtil.isNotEmpty(routeMap)) {
            Set<Map.Entry<Integer, List<PointCloudWaypoint>>> entries = routeMap.entrySet();
            List<FineInspTowerEntity> list = new ArrayList<>(routeMap.size());
            for (Map.Entry<Integer, List<PointCloudWaypoint>> me : entries) {
                FineInspTowerEntity tower = new FineInspTowerEntity().setId(me.getKey()).setUpdateRouteData(JSONArray.toJSONString(me.getValue()));
                list.add(tower);
            }
            boolean b = fineInspTowerService.updateBatchById(list);
            if (!b) {
                throw new NestException("更新杆塔航线数据异常");
            }
            return b;
        }
        return false;
    }

    private void addCameraZoomAction(Waypoint p, UnifyPoint unifyPoint, LinearParam linearParam) {
        if (unifyPoint.getFocalLength() != null) {
            p.addAction(ActionTypeEnum.CAMERA_ZOOM, (int) (unifyPoint.getFocalLength() * 10));
            if (FocalModeEnum.FOCAL_RECOVERY.equals(linearParam.getFocalMode())) {
                p.addAction(ActionTypeEnum.STAY, 3000);
            }
        }
    }

    private void addRecoveryFocalAction(Waypoint p, UnifyPoint unifyPoint, LinearParam linearParam, Map<String, Object> cameraParamMap) {
        if (unifyPoint.getFocalLength() != null && FocalModeEnum.FOCAL_RECOVERY.equals(linearParam.getFocalMode())) {
            if (CollectionUtil.isNotEmpty(cameraParamMap) && cameraParamMap.get("focalLengthMin") != null) {
                p.addAction(ActionTypeEnum.CAMERA_ZOOM, (int) ((Double) cameraParamMap.get("focalLengthMin") * 10));
            }
        }
    }

    private boolean copyTaskAndZipRel(Integer oldTaskId, Integer newTaskId) {
        TaskFineInspZipRelEntity taskZipRel = taskFineInspZipRelService.lambdaQuery()
                .eq(TaskFineInspZipRelEntity::getTaskId, oldTaskId)
                .select(TaskFineInspZipRelEntity::getFineZipId)
                .one();
        if (taskZipRel == null) {
            return false;
        }

        FineInspZipEntity zip = fineInspZipService
                .lambdaQuery()
                .eq(FineInspZipEntity::getId, taskZipRel.getFineZipId())
                .one();
        //去除旧id
        zip.setId(null);
        //保存新的zip列表，默认生成新的id
        boolean b = fineInspZipService.save(zip);
        //保存zip与task的关系
        TaskFineInspZipRelEntity relEntity = new TaskFineInspZipRelEntity();
        relEntity.setTaskId(newTaskId);
        relEntity.setFineZipId(zip.getId());
        boolean b1 = taskFineInspZipRelService.save(relEntity);
        boolean b2 = copyTaskAndTowerRel(oldTaskId, newTaskId, zip.getId(), taskZipRel.getFineZipId());
        if (b && b1 && b2) {
            return true;
        }
        return false;
    }

    private boolean copyTaskAndTowerRel(Integer oldTaskId, Integer newTaskId, Integer newZipId, Integer oldZipId) {

        List<TaskFineInspTowerRelEntity> list = taskFineInspTowerRelService
                .lambdaQuery()
                .eq(TaskFineInspTowerRelEntity::getTaskId, oldTaskId)
                .select(TaskFineInspTowerRelEntity::getFineTowerId)
                .list();

        if (CollectionUtil.isEmpty(list)) {
            return false;
        }
        List<Integer> towerIdList = list.stream().map(TaskFineInspTowerRelEntity::getFineTowerId).collect(Collectors.toList());
        List<FineInspTowerEntity> towerList = fineInspTowerService.lambdaQuery().eq(FineInspTowerEntity::getFineInspZipId, oldZipId).list();
        Map<Integer, Integer> oldAndNewTowerIdMap = new HashMap<>();
        for (FineInspTowerEntity tower : towerList) {
            Integer oldId = tower.getId();
            tower.setFineInspZipId(newZipId);
            fineInspTowerService.save(tower);
            Integer newId = tower.getId();
            oldAndNewTowerIdMap.put(oldId, newId);
        }
        //保存zip与task的关系
        List<TaskFineInspTowerRelEntity> newTowerTaskRelList = towerIdList.stream().map(towerId -> {
            TaskFineInspTowerRelEntity relEntity = new TaskFineInspTowerRelEntity();
            relEntity.setFineTowerId(oldAndNewTowerIdMap.get(towerId));
            relEntity.setTaskId(newTaskId);
            return relEntity;
        }).collect(Collectors.toList());

        boolean b1 = taskFineInspTowerRelService.saveBatch(newTowerTaskRelList);
        if (b1) {
            return true;
        }
        return false;
    }

    private void linearUnifyAction2WaypointAction(Waypoint waypoint, LinearParam linearParam, UnifyPoint unifyPoint1, UnifyLocation unifyLocation) {
        if (waypoint == null || linearParam == null || unifyPoint1 == null) {
            return;
        }
        List<UnifyAction> unifyActionList = unifyPoint1.getCustomActions();
        //新航线
        if (CollectionUtil.isNotEmpty(unifyActionList)) {
            List<WaypointAction> waypointActions = unifyActionList.stream().map(ua -> {
                WaypointAction waypointAction = new WaypointAction();
                waypointAction.setActionType(ua.getActionType());
                waypointAction.setActionParam(ua.getValue());
                return waypointAction;
            }).collect(Collectors.toList());
            waypoint.addActions(waypointActions);
        } else {
            //旧航线
            //1、增加云台俯仰动作
            waypoint.addAction(ActionTypeEnum.GIMBAL_PITCH, linearParam.getGimbalPitch());
            //2、增加转机头动作
            if (unifyLocation != null) {
                linearAddHeadingAction(waypoint, unifyPoint1.getLocation(), unifyLocation, Double.valueOf(linearParam.getHeading()));
            }
        }
    }

    private void linearAirLineSetSpeed(Waypoint waypoint, LinearParam linearParam, UnifyPoint unifyPoint) {
        if (waypoint != null && linearParam != null && unifyPoint != null) {
            Double speed = unifyPoint.getSpeed() == null ? linearParam.getSpeed() : unifyPoint.getSpeed();
            waypoint.setWayPointSpeed(speed);
        }
    }

    private void linearAirLineSetCornerRadiusInMeters(Waypoint waypoint, UnifyPoint unifyPoint) {
        if (waypoint != null && unifyPoint != null) {
            if (unifyPoint.getCornerRadiusInMeters() != null) {
                waypoint.setCornerRadiusInMeters(unifyPoint.getCornerRadiusInMeters());
            }
        }
    }

    private void deleteAirDroneLogs(Integer taskId) {
        List<Integer> missionRecordsIdList = missionRecordsService.getMissionRecordsIdByTaskId(taskId);
        long l = mongoNestAndAirService.batchDeleteByMissionRecordsIdList(missionRecordsIdList);
        log.info("删除任务taskId={},相关飞行记录{}条", taskId, l);
    }

    /**
     * 判断是否生成过航点Id
     *
     * @param jsonStr
     * @return
     */
    private Boolean hadPointId(String jsonStr) {
        Gson gson = new Gson();
        if (ObjectUtils.isEmpty(jsonStr)) {
            return Boolean.FALSE;
        }
        DataPanoramaAirLineDTO dto = gson.fromJson(jsonStr, DataPanoramaAirLineDTO.class);
        List<DataPanoramaAirLineDTO.Points> locationList = Optional.ofNullable(dto)
                .map(DataPanoramaAirLineDTO::getMapConfigs)
                .map(DataPanoramaAirLineDTO.MapConfigs::getPoints)
                .orElseGet(() -> CollectionUtil.newArrayList());
        return locationList.stream()
                .map(DataPanoramaAirLineDTO.Points::getLocation)
                .anyMatch(x -> org.springframework.util.StringUtils.hasText(x.getAirPointId()));
    }

    /**
     * 往json里加唯一id
     *
     * @param jsonStr
     * @return
     */
    private String addPointId(String jsonStr) {
        Gson gson = new Gson();
        if (ObjectUtils.isEmpty(jsonStr)) {
            throw new BusinessException("航线JSON为空，请检查！");
        }
        DataPanoramaAirLineDTO dto = gson.fromJson(jsonStr, DataPanoramaAirLineDTO.class);
        List<DataPanoramaAirLineDTO.Points> locationList = Optional.ofNullable(dto)
                .map(DataPanoramaAirLineDTO::getMapConfigs)
                .map(DataPanoramaAirLineDTO.MapConfigs::getPoints)
                .orElseGet(() -> CollectionUtil.newArrayList());
        int i = 1;
        for (DataPanoramaAirLineDTO.Points point : locationList) {
            if (StringUtils.isEmpty(point.getLocation().getAirPointId())) {
                point.getLocation().setAirPointId(String.valueOf(BizIdUtils.snowflakeId()));
            }
            point.getLocation().setAirPointIndex(i++);
        }
        return gson.toJson(dto, DataPanoramaAirLineDTO.class);
    }

    /**
     * 飞行过的全景航线且已经生成了航点ID的航线 ， 不允许编辑
     *
     * @param missionId
     * @param unifyTaskDto
     */
    private void judgePanoramaSave(String missionId, UnifyTaskDto unifyTaskDto) {
        if (log.isDebugEnabled()) {
            log.debug("judgePanoramaSave:missionId -> {},unifyTaskDto -> {}", missionId, unifyTaskDto);
        }
        if (!TaskModeEnum.PANORAMA.getValue().equals(unifyTaskDto.getTaskType())) {
            return;
        }
        Integer count = missionRecordsService.lambdaQuery()
                .in(MissionRecordsEntity::getMissionId, missionId)
                .eq(MissionRecordsEntity::getDeleted, false)
                .count();
        if (count > 0 && hadPointId(unifyTaskDto.getUnifyAirLineJson())) {
            throw new NestException("该架次任务已产生关联数据，不允许编辑，请复制该任务进行编辑");
        }
    }

    private void unifyJson2cpsJson() {

    }

    private void handleMissionAndAirlineForGrid(List<Integer> aleIdList, List<AirLineEntity> aleList,
                                                List<Integer> meIdList, List<MissionEntity> meList,
                                                Boolean isSameRoute, Integer taskId) {
        // 如果aleIdList为空，意味新建航线
        if (CollectionUtils.isEmpty(aleIdList) && CollectionUtils.isEmpty(meIdList)) {
            for (int i = 0; i < aleIdList.size(); i++) {
                aleList.get(i).setId(aleIdList.get(i));
            }
            for (int i = 0; i < meIdList.size(); i++) {
                meList.get(i).setId(meIdList.get(i));
            }
        }

        // 如果aleIdList不为空且确定是之前的航线，修改架次信息
        if (!CollectionUtils.isEmpty(aleIdList) && !CollectionUtils.isEmpty(meIdList) && isSameRoute) {
            if (aleIdList.size() == aleList.size() && meIdList.size() == meList.size()) {
                for (int i = 0; i < aleIdList.size(); i++) {
                    aleList.get(i).setId(aleIdList.get(i));
                }
                for (int i = 0; i < meIdList.size(); i++) {
                    meList.get(i).setId(meIdList.get(i));
                }
            } else {
                // 如果架次数量不对，重新建之前的航线
                airLineService.lambdaUpdate()
                        .in(AirLineEntity::getId, aleIdList)
                        .set(AirLineEntity::getDeleted, 1)
                        .update();
                missionService.lambdaUpdate()
                        .in(MissionEntity::getId, meIdList)
                        .set(MissionEntity::getDeleted, 1)
                        .update();
                gridMissionService.updateIsNewestInspect(Collections.singletonList(taskId));
            }


        }
        // 如果aleIdList不为空且确定是新的航线，先删除再重新新增新的航线
        if (!CollectionUtils.isEmpty(aleIdList) && !CollectionUtils.isEmpty(meIdList) && !isSameRoute) {
            airLineService.lambdaUpdate()
                    .in(AirLineEntity::getId, aleIdList)
                    .set(AirLineEntity::getDeleted, 1)
                    .update();
            missionService.lambdaUpdate()
                    .in(MissionEntity::getId, meIdList)
                    .set(MissionEntity::getDeleted, 1)
                    .update();
            gridMissionService.updateIsNewestInspect(Collections.singletonList(taskId));
        }
    }
}
