package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.imapcloud.nest.mapper.*;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultGroupEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisTopicLevelEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisTopicProblemEntity;
import com.imapcloud.nest.v2.dao.mapper.*;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisResultInPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisResultOutPO;
import com.imapcloud.nest.v2.dao.po.out.GridProblemsOutPO;
import com.imapcloud.nest.v2.manager.dataobj.GridManageDO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataAnalysisResultGroupInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisMarkMergeOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.manager.sql.DataAnalysisMarkManager;
import com.imapcloud.nest.v2.manager.sql.DataAnalysisMarkMergeManager;
import com.imapcloud.nest.v2.manager.sql.DataAnalysisResultGroupManager;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import com.imapcloud.nest.v2.service.TopicService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisResultGroupConverter;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisResultOutDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析结果服务impl
 *
 * @author boluo
 * @date 2022-07-15
 */
@Slf4j
@Service
public class DataAnalysisResultServiceImpl implements DataAnalysisResultService {

    @Resource
    private DataAnalysisResultMapper dataAnalysisResultMapper;

    @Resource
    private DataAnalysisTopicLevelMapper dataAnalysisTopicLevelMapper;

    @Resource
    private DataAnalysisTopicProblemMapper dataAnalysisTopicProblemMapper;

    @Resource
    private MissionRecordsMapper missionRecordsMapper;

    @Resource
    private MissionMapper missionMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private SysTagMapper sysTagMapper;

    @Resource
    private BaseNestManager baseNestManager;

    @Resource
    private SysTaskTagMapper sysTaskTagMapper;

    @Resource
    private UosOrgManager uosOrgManager;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private DataAnalysisResultGroupMapper dataAnalysisResultGroupMapper;

    @Resource
    private DataAnalysisMarkMergeMapper dataAnalysisMarkMergeMapper;

    @Resource
    private DataAnalysisMarkMergeManager dataAnalysisMarkMergeManager;

    @Resource
    private DataAnalysisResultGroupManager dataAnalysisResultGroupManager;

    @Resource
    private DataAnalysisMarkManager dataAnalysisMarkManager;

    @Resource
    private TopicService topicService;

    @Resource
    private GridManageMapper gridManageMapper;

    @Resource
    private FileManager fileManager;

    private String getPath(String originPath, String path, Long id) {
        if (!originPath.contains(".")) {
            return String.format("%s%s", path, id);
        }
        return String.format("%s%s.%s", path, id, originPath.split("\\.")[1]);
    }

    @Override
    public void batchInsert(List<DataAnalysisResultInDTO.InsertInfoIn> insertInfoInList, String accountId) {
        checkBatchInsertParam(insertInfoInList);
        // 查询架次信息
        Pair<Map<Integer, MissionRecordsEntity>, List<Integer>> mapListPair = getMissionRecordsMap(insertInfoInList);
        if (CollUtil.isEmpty(mapListPair.getValue())) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_MISSION_RECORD.getContent()));
        }
        Map<Integer, MissionRecordsEntity> missionRecordsMap = mapListPair.getKey();

        Pair<Map<Integer, MissionEntity>, List<Integer>> missionMapListMap = getMissionMap(mapListPair.getValue());
        Map<Integer, MissionEntity> missionMap = missionMapListMap.getKey();

        List<String> nestIdList = Lists.newArrayList();
        List<String> orgIdList = Lists.newArrayList();
        Map<Integer, Long> taskTagMap = Maps.newHashMap();
        Map<Integer, TaskEntity> taskMap = getTaskMap(missionMapListMap.getValue(), nestIdList, orgIdList, taskTagMap);
        // 查询基站 单位 tag的名称
        Map<Integer, Pair<Long, String>> tagMap = getTagMap(taskTagMap);
        Map<String, String> nestMap = getNestMap(nestIdList);
        Map<String, String> orgMap = getOrgMap(orgIdList);

        // 查询专题信息
        Map<Long, DataAnalysisTopicLevelEntity> topicLevelMap = getTopicLevelMap(insertInfoInList);
        Map<Integer, String> topicIndustryMap = getTopicIndustryMap(insertInfoInList);
        Map<Long, String> topicProblemMap = getTopicProblemMap(insertInfoInList);

        //结果
        List<DataAnalysisResultEntity> dataAnalysisResultEntityList = Lists.newArrayList();
        List<DataAnalysisResultGroupEntity> dataAnalysisResultGroupEntities = Lists.newArrayList();
        List<String> groupIds = new ArrayList<>();
        for (DataAnalysisResultInDTO.InsertInfoIn insertInfoIn : insertInfoInList) {
            DataAnalysisResultEntity dataAnalysisResultEntity = new DataAnalysisResultEntity();
            //结果id
            Long resultId = BizIdUtils.snowflakeId();
            //分组ID 初次核实，各自成一分组
            dataAnalysisResultEntity.setResultId(resultId);
            dataAnalysisResultEntity.setPhotoId(insertInfoIn.getPhotoId());
            dataAnalysisResultEntity.setMarkId(insertInfoIn.getMarkId());
            dataAnalysisResultEntity.setAiMark(insertInfoIn.getAiMark());
//            dataAnalysisResultEntity.setGridManageId(insertInfoIn.getGridManageId());
            // 图片copy
//            String thumImagePathTarget = getPath(insertInfoIn.getThumImagePath(), UploadTypeEnum.DATA_ANALYSIS_RESULT_THUMBNAIL.getPath(), resultId);
//            MinIoUnit.copy(insertInfoIn.getThumImagePath(), thumImagePathTarget);
//            thumImagePathTarget = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), thumImagePathTarget);
            String thumImagePathTarget = fileManager.copyFile(insertInfoIn.getThumImagePath(), null);
            dataAnalysisResultEntity.setThumImagePath(thumImagePathTarget);

            String addrImagePathTarget = "";
            if (StringUtils.isNotBlank(insertInfoIn.getAddrImagePath())) {
//                addrImagePathTarget = getPath(insertInfoIn.getAddrImagePath(), UploadTypeEnum.DATA_ANALYSIS_RESULT_ADDR.getPath(), resultId);
//                MinIoUnit.copy(insertInfoIn.getAddrImagePath(), addrImagePathTarget);
//                addrImagePathTarget = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), addrImagePathTarget);
                addrImagePathTarget = fileManager.copyFile(insertInfoIn.getAddrImagePath(), null);
            }
            dataAnalysisResultEntity.setAddrImagePath(addrImagePathTarget);

//            String markImagePathTarget = getPath(insertInfoIn.getResultImagePath(), UploadTypeEnum.DATA_ANALYSIS_RESULT_MARK.getPath(), resultId);
//            MinIoUnit.copy(insertInfoIn.getResultImagePath(), markImagePathTarget);
//            markImagePathTarget = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), markImagePathTarget);
            String markImagePathTarget = fileManager.copyFile(insertInfoIn.getResultImagePath(), null);
            dataAnalysisResultEntity.setResultImagePath(markImagePathTarget);

//            String imagePathTarget = getPath(insertInfoIn.getImagePath(), UploadTypeEnum.DATA_ANALYSIS_RESULT_FROM.getPath(), resultId);
//            MinIoUnit.copy(insertInfoIn.getImagePath(), imagePathTarget);
//            imagePathTarget = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), imagePathTarget);
            String imagePathTarget = fileManager.copyFile(insertInfoIn.getImagePath(), null);
            dataAnalysisResultEntity.setImagePath(imagePathTarget);
            dataAnalysisResultEntity.setAddr(insertInfoIn.getAddr());
            dataAnalysisResultEntity.setLongitude(insertInfoIn.getLongitude());
            dataAnalysisResultEntity.setLatitude(insertInfoIn.getLatitude());
            dataAnalysisResultEntity.setTopicLevelId(insertInfoIn.getTopicLevelId());
            dataAnalysisResultEntity.setIndustryType(insertInfoIn.getIndustryType());
            dataAnalysisResultEntity.setTopicProblemId(insertInfoIn.getTopicProblemId());
            dataAnalysisResultEntity.setPhotoCreateTime(insertInfoIn.getPhotoCreateTime());
            dataAnalysisResultEntity.setSrcDataType(insertInfoIn.getSrcDataType());


            DataAnalysisTopicLevelEntity dataAnalysisTopicLevelEntity = topicLevelMap.get(insertInfoIn.getTopicLevelId());
            if (dataAnalysisTopicLevelEntity != null) {
                dataAnalysisResultEntity.setTopicKey(dataAnalysisTopicLevelEntity.getTopicKey());
                dataAnalysisResultEntity.setTopicLevelName(MessageUtils.getMessage(dataAnalysisTopicLevelEntity.getTopicLevelName()));
            } else {
                dataAnalysisResultEntity.setTopicKey("");
                dataAnalysisResultEntity.setTopicLevelName("");
            }

            dataAnalysisResultEntity.setTopicIndustryName(topicIndustryMap.getOrDefault(insertInfoIn.getIndustryType(), ""));
            dataAnalysisResultEntity.setTopicProblemName(topicProblemMap.getOrDefault(insertInfoIn.getTopicProblemId(), ""));

            MissionRecordsEntity missionRecordsEntity = missionRecordsMap.get(insertInfoIn.getMissionRecordsId().intValue());
            if (missionRecordsEntity == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_MISSION_RECORD.getContent()));
            }
            MissionEntity missionEntity = missionMap.get(missionRecordsEntity.getMissionId());
            if (missionEntity == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_MISSON.getContent()));
            }
            TaskEntity taskEntity = taskMap.get(missionEntity.getTaskId());
            if (taskEntity == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_MISSION.getContent()));
            }
            Pair<Long, String> longStringPair = tagMap.get(taskEntity.getId());
            if (longStringPair != null) {
                dataAnalysisResultEntity.setTagId(longStringPair.getKey());
                dataAnalysisResultEntity.setTagName(longStringPair.getValue());
            } else {
                dataAnalysisResultEntity.setTagId(0L);
                dataAnalysisResultEntity.setTagName("");
            }
            if (taskEntity.getBaseNestId() != null) {
                dataAnalysisResultEntity.setBaseNestId(taskEntity.getBaseNestId());
                dataAnalysisResultEntity.setNestName(nestMap.getOrDefault(taskEntity.getBaseNestId(), ""));
            } else {
                dataAnalysisResultEntity.setBaseNestId("0");
                dataAnalysisResultEntity.setNestName("");
            }
            if (StringUtils.isNotBlank(taskEntity.getOrgCode())) {
                dataAnalysisResultEntity.setOrgCode(taskEntity.getOrgCode());
                dataAnalysisResultEntity.setOrgName(orgMap.getOrDefault(taskEntity.getOrgCode(), ""));
            } else {
                dataAnalysisResultEntity.setOrgCode("0");
                dataAnalysisResultEntity.setOrgName("");
            }
            dataAnalysisResultEntity.setTaskId(taskEntity.getId().longValue());
            dataAnalysisResultEntity.setMissionId(missionEntity.getId().longValue());
            dataAnalysisResultEntity.setMissionRecordsId(insertInfoIn.getMissionRecordsId());

            dataAnalysisResultEntity.setTaskName(taskEntity.getName());
            dataAnalysisResultEntity.setTaskType(taskEntity.getType());
            dataAnalysisResultEntity.setSubType(taskEntity.getSubType());
            dataAnalysisResultEntity.setMissionName(missionEntity.getName());
            dataAnalysisResultEntity.setMissionSeqId(missionEntity.getSeqId());
            dataAnalysisResultEntity.setMissionRecordsName(missionRecordsEntity.getFlyIndex() == null ? "1" : missionRecordsEntity.getFlyIndex().toString());
            dataAnalysisResultEntity.setMissionRecordsTime(missionRecordsEntity.getEndTime());
            dataAnalysisResultEntity.setCreatorId(accountId);
            dataAnalysisResultEntity.setModifierId(accountId);

            // 设置网格Id
            log.info("#dataAnalysisResult#Longitude: {}, Latitude: {}, orgIdList: {}", insertInfoIn.getLongitude(), insertInfoIn.getLatitude(), orgIdList);
            String gridManageId = queryGridManage(insertInfoIn.getLongitude(), insertInfoIn.getLatitude(), orgIdList, taskEntity.getId());
            log.info("#dataAnalysisResult#GridManageId: {}", gridManageId);
            dataAnalysisResultEntity.setGridManageId(gridManageId);

            //v2.1.2设置分组分组GroupId
            // 1.1  检查当前mark_id是否存在分组id
            String groupId = this.checkMegerGroupExist(insertInfoIn.getMarkId());
            if (groupId == null) {
                String resultGroupId = BizIdUtils.snowflakeIdStr();
                dataAnalysisResultEntity.setResultGroupId(resultGroupId);
                //初始化resultGroup数据
                DataAnalysisResultGroupEntity dataAnalysisResultGroupEntity = DataAnalysisResultGroupConverter.INSTANCES.convertToEntity(dataAnalysisResultEntity);
                dataAnalysisResultGroupEntities.add(dataAnalysisResultGroupEntity);
                groupIds.add(resultGroupId);
            } else {
                dataAnalysisResultEntity.setResultGroupId(groupId);
                //存在分组，设置为分组的addr 和图片地址信息  经纬度信息
                groupIds.add(groupId);
            }
            dataAnalysisResultEntityList.add(dataAnalysisResultEntity);
        }
        // 批量插入 result及group_result
        log.info("dataAnalysisResultEntityList:{}", JSONArray.toJSONString(dataAnalysisResultEntityList));
        this.dataAnalysisResultMapper.batchInsert(dataAnalysisResultEntityList);
        if (CollectionUtil.isNotEmpty(dataAnalysisResultGroupEntities)) {
            // 网格关联问题组
            this.dataAnalysisResultGroupMapper.saveBatch(dataAnalysisResultGroupEntities);
        }
        this.balance(groupIds);
    }

    private String queryGridManage(BigDecimal _longitude, BigDecimal _latitude, List<String> orgIdList, Integer taskId) {
        String gridManageId = "";
        gridManageId = gridManageMapper.selectByTask(taskId);
        // // 如果没有管理网格，有可能是其他航线产生的问题，则用经纬度匹配
        if (!org.springframework.util.StringUtils.hasText(gridManageId)) {
            // 获取管理网格经纬度信息
            List<GridManageDO> gridManageDOList = gridManageMapper.selectGridManage(orgIdList);
            if (!CollectionUtils.isEmpty(gridManageDOList)) {
                Double longitude = _longitude.doubleValue();
                Double latitude = _latitude.doubleValue();
                Double east, north, south, west;
                for (GridManageDO gridManageDO : gridManageDOList) {
                    east = gridManageDO.getEast();
                    north = gridManageDO.getNorth();
                    south = gridManageDO.getSouth();
                    west = gridManageDO.getWest();
                    if (west <= longitude && longitude < east && south <= latitude && latitude < north) {
                        gridManageId = gridManageDO.getGridManageId();
                        break;
                    }
                }
            }
        }
        return gridManageId;
    }

    private Map<String, String> getOrgMap(List<String> orgIdList) {
        List<OrgSimpleOutDO> orgInfos = uosOrgManager.listOrgInfos(orgIdList);
        if (!CollectionUtils.isEmpty(orgInfos)) {
            return orgInfos.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
        }
        return Collections.emptyMap();
    }

    private Map<String, String> getNestMap(List<String> nestIdList) {

        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectListByNestIdList(nestIdList);

        return baseNestEntityOutDOList.stream()
                .collect(Collectors.toMap(BaseNestOutDO.BaseNestEntityOutDO::getNestId, BaseNestOutDO.BaseNestEntityOutDO::getName, (key1, key2) -> key1));
    }

    private Map<Integer, Pair<Long, String>> getTagMap(Map<Integer, Long> taskTagMap) {
        List<Long> tagIdList = Lists.newLinkedList();
        tagIdList.addAll(taskTagMap.values());
        if (CollUtil.isEmpty(tagIdList)) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<SysTagEntity> wrapper = Wrappers.lambdaQuery(SysTagEntity.class).in(SysTagEntity::getId, tagIdList);
        List<SysTagEntity> sysTagEntityList = sysTagMapper.selectList(wrapper);
        Map<Long, String> collect = sysTagEntityList.stream().collect(Collectors.toMap(SysTagEntity::getId, SysTagEntity::getName, (key1, key2) -> key1));
        Map<Integer, Pair<Long, String>> result = Maps.newHashMap();
        taskTagMap.forEach((key, value) -> {
            String name = collect.get(value);
            result.put(key, Pair.of(value, name == null ? "" : name));
        });
        return result;
    }

    private Map<Integer, TaskEntity> getTaskMap(List<Integer> taskIdList, List<String> nestIdList,
                                                List<String> orgIdList, Map<Integer, Long> taskTagMap) {

        LambdaQueryWrapper<TaskEntity> wrapper = Wrappers.lambdaQuery(TaskEntity.class)
                .in(TaskEntity::getId, taskIdList);
        List<TaskEntity> taskEntityList = taskMapper.selectList(wrapper);
        Map<Integer, TaskEntity> taskMap = Maps.newHashMap();
        for (TaskEntity taskEntity : taskEntityList) {
            nestIdList.add(taskEntity.getBaseNestId() == null ? "" : taskEntity.getBaseNestId());
            if (org.springframework.util.StringUtils.hasText(taskEntity.getOrgCode())) {
                orgIdList.add(taskEntity.getOrgCode());
            }
            taskMap.put(taskEntity.getId(), taskEntity);
        }

        LambdaQueryWrapper<SysTaskTagEntity> eq = Wrappers.lambdaQuery(SysTaskTagEntity.class)
                .in(SysTaskTagEntity::getTaskId, taskIdList);
        List<SysTaskTagEntity> sysTaskTagEntityList = sysTaskTagMapper.selectList(eq);
        for (SysTaskTagEntity sysTaskTagEntity : sysTaskTagEntityList) {
            taskTagMap.put(sysTaskTagEntity.getTaskId(), sysTaskTagEntity.getTagId() == null ? -1L : sysTaskTagEntity.getTagId());
        }
        return taskMap;
    }

    private Pair<Map<Integer, MissionEntity>, List<Integer>> getMissionMap(List<Integer> missionIdList) {

        LambdaQueryWrapper<MissionEntity> wrapper = Wrappers.lambdaQuery(MissionEntity.class)
                .in(MissionEntity::getId, missionIdList);
        List<MissionEntity> missionEntityList = missionMapper.selectList(wrapper);

        List<Integer> taskIdList = Lists.newArrayList();
        Map<Integer, MissionEntity> missionMap = Maps.newHashMap();
        for (MissionEntity missionEntity : missionEntityList) {
            taskIdList.add(missionEntity.getTaskId());
            missionMap.put(missionEntity.getId(), missionEntity);
        }
        return new Pair<>(missionMap, taskIdList);
    }

    private Pair<Map<Integer, MissionRecordsEntity>, List<Integer>> getMissionRecordsMap(List<DataAnalysisResultInDTO.InsertInfoIn> insertInfoInList) {

        Set<Long> collect = insertInfoInList.stream()
                .map(DataAnalysisResultInDTO.InsertInfoIn::getMissionRecordsId)
                .collect(Collectors.toSet());

        LambdaQueryWrapper<MissionRecordsEntity> wrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .in(MissionRecordsEntity::getId, collect);

        List<MissionRecordsEntity> missionRecordsEntityList = missionRecordsMapper.selectList(wrapper);

        List<Integer> missionIdList = Lists.newArrayList();
        Map<Integer, MissionRecordsEntity> missionRecordsMap = Maps.newHashMap();
        for (MissionRecordsEntity missionRecordsEntity : missionRecordsEntityList) {
            missionIdList.add(missionRecordsEntity.getMissionId());
            missionRecordsMap.put(missionRecordsEntity.getId(), missionRecordsEntity);
        }
        return new Pair<>(missionRecordsMap, missionIdList);
    }

    private Map<Long, DataAnalysisTopicLevelEntity> getTopicLevelMap(List<DataAnalysisResultInDTO.InsertInfoIn> insertInfoInList) {

        Set<Long> collect = insertInfoInList.stream().map(DataAnalysisResultInDTO.InsertInfoIn::getTopicLevelId).collect(Collectors.toSet());
        LambdaQueryWrapper<DataAnalysisTopicLevelEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisTopicLevelEntity.class)
                .in(DataAnalysisTopicLevelEntity::getTopicLevelId, collect);
        List<DataAnalysisTopicLevelEntity> dataAnalysisTopicLevelEntityList = dataAnalysisTopicLevelMapper.selectList(wrapper);
        return dataAnalysisTopicLevelEntityList.stream().collect(Collectors.toMap(DataAnalysisTopicLevelEntity::getTopicLevelId, bean -> bean, (key1, key2) -> key1));
    }

    private Map<Integer, String> getTopicIndustryMap(List<DataAnalysisResultInDTO.InsertInfoIn> insertInfoInList) {
        Map<String, String> industryMappings = topicService.getIndustryMappings();
        Set<Integer> industryTypes = insertInfoInList.stream().map(DataAnalysisResultInDTO.InsertInfoIn::getIndustryType).collect(Collectors.toSet());
        Map<Integer, String> industryMappings2 = new HashMap<>(industryTypes.size());
        for (Integer industryType : industryTypes) {
            if (industryMappings.containsKey(industryType.toString())) {
                industryMappings2.put(industryType, industryMappings.get(industryType.toString()));
            }
        }
        return industryMappings2;
    }

    private Map<Long, String> getTopicProblemMap(List<DataAnalysisResultInDTO.InsertInfoIn> insertInfoInList) {
        Set<Long> collect = insertInfoInList.stream().map(DataAnalysisResultInDTO.InsertInfoIn::getTopicProblemId).collect(Collectors.toSet());
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                .in(DataAnalysisTopicProblemEntity::getTopicProblemId, collect);
        List<DataAnalysisTopicProblemEntity> dataAnalysisTopicProblemEntityList = dataAnalysisTopicProblemMapper.selectList(wrapper);
        return dataAnalysisTopicProblemEntityList.stream()
                .collect(Collectors.toMap(DataAnalysisTopicProblemEntity::getTopicProblemId, DataAnalysisTopicProblemEntity::getTopicProblemName));
    }

    private void checkBatchInsertParam(List<DataAnalysisResultInDTO.InsertInfoIn> insertInfoInList) {

        if (CollUtil.isEmpty(insertInfoInList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_INSERTINFOINLIST.getContent()));
        }
        for (DataAnalysisResultInDTO.InsertInfoIn insertInfoIn : insertInfoInList) {
            if (insertInfoIn.getPhotoId() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_PHOTOID.getContent()));
            }
            if (insertInfoIn.getMarkId() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_MARKID.getContent()));
            }
            if (insertInfoIn.getAiMark() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_AIMARK.getContent()));
            }
            if (StringUtils.isBlank(insertInfoIn.getThumImagePath())) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_THUMIMAGEPATH.getContent()));
            }
            if (StringUtils.isBlank(insertInfoIn.getResultImagePath())) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_RESULTIMAGEPATH.getContent()));
            }
            if (StringUtils.isBlank(insertInfoIn.getImagePath())) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_IMAGEPATH.getContent()));
            }
            if (insertInfoIn.getAddr() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_ADDR.getContent()));
            }
            if (insertInfoIn.getLongitude() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_LONGITUDE.getContent()));
            }
            if (insertInfoIn.getLatitude() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_LATITUDE.getContent()));
            }
            if (insertInfoIn.getTopicLevelId() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_TOPICLEVELID.getContent()));
            }
            if (insertInfoIn.getIndustryType() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_TOPICINDUSTRYID.getContent()));
            }
            if (insertInfoIn.getTopicProblemId() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_TOPICPROBLEMID.getContent()));
            }
            if (insertInfoIn.getMissionRecordsId() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_MISSIONRECORDSID.getContent())
                );
            }
            if (insertInfoIn.getPhotoCreateTime() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_PHOTOCREATETIME.getContent()));
            }
            if (insertInfoIn.getSrcDataType() == null) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_SRCDATATYPE.getContent()));
            }
        }
    }

    @Override
    public DataAnalysisResultOutDTO.CollectSumOut collectSum(DataAnalysisResultInDTO.ProblemIn problemIn) {

        DataAnalysisResultInPO.ProblemIn problemInPo = getProblemInPo(problemIn);

        log.info("#DataAnalysisResultServiceImpl.collectSum# param={}", problemInPo);
        List<DataAnalysisResultOutPO.CollectSumOut> collectSumOutList = dataAnalysisResultMapper.collectSum(problemInPo);
        log.info("#DataAnalysisResultServiceImpl.collectSum# param={}, result={}", problemInPo, collectSumOutList);
        Map<Long, DataAnalysisResultOutPO.CollectSumOut> collectSumOutMap = collectSumOutList.stream()
                .collect(Collectors.toMap(DataAnalysisResultOutPO.CollectSumOut::getTopicLevelId, bean -> bean, (key1, key2) -> key1));

        // 查询专题级别
        LambdaQueryWrapper<DataAnalysisTopicLevelEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisTopicLevelEntity.class)
                .eq(DataAnalysisTopicLevelEntity::getTopicKey, problemIn.getTopicKey());
        List<DataAnalysisTopicLevelEntity> dataAnalysisTopicLevelEntityList = dataAnalysisTopicLevelMapper.selectList(wrapper);

        DataAnalysisResultOutDTO.CollectSumOut result = new DataAnalysisResultOutDTO.CollectSumOut();
        List<DataAnalysisResultOutDTO.ProblemNumInfo> problemNumInfoList = Lists.newLinkedList();
        result.setProblemSumNum(0L);
        result.setProblemNumInfoList(problemNumInfoList);

        for (DataAnalysisTopicLevelEntity dataAnalysisTopicLevelEntity : dataAnalysisTopicLevelEntityList) {

            DataAnalysisResultOutDTO.ProblemNumInfo problemNumInfo = new DataAnalysisResultOutDTO.ProblemNumInfo();
            problemNumInfo.setTopicLevelId(dataAnalysisTopicLevelEntity.getTopicLevelId());
            problemNumInfo.setTopicLevelName(MessageUtils.getMessage(dataAnalysisTopicLevelEntity.getTopicLevelName()));
            DataAnalysisResultOutPO.CollectSumOut collectSumOut = collectSumOutMap.get(dataAnalysisTopicLevelEntity.getTopicLevelId());
            problemNumInfo.setProblemNum(0L);
            if (collectSumOut != null) {
                problemNumInfo.setProblemNum(collectSumOut.getProblemNum());
                result.setProblemSumNum(result.getProblemSumNum() + collectSumOut.getProblemNum());
            }
            problemNumInfoList.add(problemNumInfo);
        }
        return result;
    }

    @Override
    public List<DataAnalysisResultOutDTO.ProblemTrendOut> problemTrend(DataAnalysisResultInDTO.ProblemIn problemIn) {

        DataAnalysisResultInPO.ProblemIn problemInPo = getProblemInPo(problemIn);
        log.info("#DataAnalysisResultServiceImpl.problemTrend# param={}", problemInPo);
        List<DataAnalysisResultOutPO.ProblemTrendOut> problemTrendOutList = dataAnalysisResultMapper.problemTrend(problemInPo);
        log.info("#DataAnalysisResultServiceImpl.problemTrend# param={}, result={}", problemInPo, problemTrendOutList);

        Map<String, Long> stringLongMap = problemTrendOutList.stream()
                .collect(Collectors.toMap(DataAnalysisResultOutPO.ProblemTrendOut::getLocalDate,
                        DataAnalysisResultOutPO.ProblemTrendOut::getProblemNum, (key1, key2) -> key1));
        List<DataAnalysisResultOutDTO.ProblemTrendOut> result = Lists.newLinkedList();
        LocalDate startDate = problemIn.getStartTime().toLocalDate();
        LocalDate endDate = problemIn.getEndTime().toLocalDate();
        while (!startDate.isAfter(endDate)) {
            DataAnalysisResultOutDTO.ProblemTrendOut out = new DataAnalysisResultOutDTO.ProblemTrendOut();
            String format = startDate.format(DateUtils.DATE_FORMATTER_OF_CN);
            out.setDay(format);
            Long aLong = stringLongMap.get(format);
            out.setProblemNum(aLong == null ? 0L : aLong);
            result.add(out);
            startDate = startDate.plusDays(1);
        }
        return result;
    }

    private DataAnalysisResultInPO.ProblemIn getProblemInPo(DataAnalysisResultInDTO.ProblemIn problemIn) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        return DataAnalysisResultInPO.ProblemIn.builder()
                .startTime(problemIn.getStartTime())
                .endTime(problemIn.getEndTime())
                .topicKey(problemIn.getTopicKey())
                .orgCode(problemIn.getOrgCode())
                .visibleOrgCode(orgCode)
                .industryType(problemIn.getIndustryType())
                .topicLevelId(problemIn.getTopicLevelId())
                .tagName(problemIn.getTagName())
                .topicProblemId(problemIn.getTopicProblemId())
                .build();
    }

    /**
     * 检查是否已存在分组情况
     */
    private String checkMegerGroupExist(Long markId) {
        return this.dataAnalysisMarkMergeMapper.selectIsMergeGroup(markId);
    }

    @Override
    public PageResultInfo<DataAnalysisResultOutDTO.ProblemOut> problemList(DataAnalysisResultInDTO.ProblemIn problemIn) {

        List<DataAnalysisResultOutDTO.ProblemOut> problemOutList = Lists.newLinkedList();

        DataAnalysisResultInPO.ProblemIn problemInPo = getProblemInPo(problemIn);
        long condition = dataAnalysisResultMapper.countByCondition(problemInPo);
        if (condition == 0) {
            return PageResultInfo.of(0, problemOutList);
        }
        List<DataAnalysisResultEntity> dataAnalysisResultEntityList = dataAnalysisResultMapper.selectByCondition(problemInPo, PagingRestrictDo.getPagingRestrict(problemIn));

        for (DataAnalysisResultEntity dataAnalysisResultEntity : dataAnalysisResultEntityList) {
            DataAnalysisResultOutDTO.ProblemOut problemOut = new DataAnalysisResultOutDTO.ProblemOut();
            BeanUtils.copyProperties(dataAnalysisResultEntity, problemOut);
            problemOutList.add(problemOut);
        }
        return PageResultInfo.of(condition, problemOutList);
    }

    @Override
    public List<DataAnalysisResultOutDTO.ProblemOut> problemList(List<Long> resultIdList) {
        LambdaQueryWrapper<DataAnalysisResultEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisResultEntity.class)
                .in(DataAnalysisResultEntity::getResultId, resultIdList);
        List<DataAnalysisResultEntity> dataAnalysisResultEntityList = dataAnalysisResultMapper.selectList(wrapper);

        List<DataAnalysisResultOutDTO.ProblemOut> problemOutList = Lists.newLinkedList();
        for (DataAnalysisResultEntity dataAnalysisResultEntity : dataAnalysisResultEntityList) {
            DataAnalysisResultOutDTO.ProblemOut problemOut = new DataAnalysisResultOutDTO.ProblemOut();
            BeanUtils.copyProperties(dataAnalysisResultEntity, problemOut);
            problemOutList.add(problemOut);
        }
        return problemOutList;
    }

    @Override
    public int deleteByMarkIdList(List<Long> markIdList, Boolean flag) {
        if (CollUtil.isEmpty(markIdList)) {
            return 0;
        }

        // 已核实问题组,查询所有已核实问题组  不分是否删除
        List<DataAnalysisResultEntity> dataAnalysisResultEntityList = dataAnalysisResultMapper.selectAllByMarkIdList(markIdList);

        //集合问题组ID
        Set<String> resultGroupIdSet = Sets.newHashSet();
        if (CollUtil.isNotEmpty(dataAnalysisResultEntityList)) {
            resultGroupIdSet.addAll(dataAnalysisResultEntityList.stream()
                    .map(DataAnalysisResultEntity::getResultGroupId).collect(Collectors.toSet()));
        }
        //
        // 未核实的问题组
        List<DataAnalysisMarkMergeOutDO> dataAnalysisMarkMergeOutDOList = dataAnalysisMarkMergeManager.selectAllListByMarkIdList(markIdList);
        resultGroupIdSet.addAll(dataAnalysisMarkMergeOutDOList.stream().map(DataAnalysisMarkMergeOutDO::getResultGroupId).collect(Collectors.toList()));

        // 删除问题
        dataAnalysisResultMapper.updateDeleteByMarkIdList(markIdList);

        // 删除问题合并关系
        //重置操作，才走这步
        if (!flag) {
            dataAnalysisMarkMergeManager.deleteByMarkIdList(markIdList);
        }
        this.balance(resultGroupIdSet);
        return 1;
    }

    @Override
    public void balance(Collection<String> resultGroupIdCollection) {

        log.info("#DataAnalysisResultServiceImpl.balance# resultGroupIdCollection={}", resultGroupIdCollection);
        if (CollUtil.isEmpty(resultGroupIdCollection)) {
            return;
        }
        // 问题组中问题的数量为0，则删除问题组
        LambdaQueryWrapper<DataAnalysisResultEntity> queryWrapper = Wrappers.lambdaQuery(DataAnalysisResultEntity.class)
                .in(DataAnalysisResultEntity::getResultGroupId, resultGroupIdCollection).eq(DataAnalysisResultEntity::getDeleted, false);
        List<DataAnalysisResultEntity> dataAnalysisResultEntityList = dataAnalysisResultMapper.selectList(queryWrapper);
        Set<String> haveGroupId = dataAnalysisResultEntityList.stream()
                .map(DataAnalysisResultEntity::getResultGroupId).collect(Collectors.toSet());
        // 问题组中问题=0的问题组ID
        List<String> groupIdList = resultGroupIdCollection.stream()
                .filter(groupId -> !haveGroupId.contains(groupId)).collect(Collectors.toList());
        log.info("#DataAnalysisResultServiceImpl.balance# resultGroupIdCollection={}, groupIdList={}", resultGroupIdCollection, groupIdList);
        // 删除问题组
        if (CollUtil.isNotEmpty(groupIdList)) {
            dataAnalysisResultGroupManager.deleteByResultGroupIdList(groupIdList);
            //执行删除的问题组，需要将和问题组关联的mark的add 及addrImagepath置为null  且更新最后操作时间
            dataAnalysisMarkManager.updateMarkIdByGroupIds(groupIdList);
           /* List<DataAnalysisMarkMergeOutDO> dataAnalysisMarkMergeOutDOList =  dataAnalysisMarkMergeManager.selectAllListByGroupIdList(resultGroupIdCollection);
            //查询对应删除的分组内的result结果对应的mark_id (已核实的不删除addr信息)
            LambdaQueryWrapper<DataAnalysisResultEntity> resultWrapper = Wrappers.lambdaQuery(DataAnalysisResultEntity.class)
                    .in(DataAnalysisResultEntity::getResultGroupId, groupIdList);
            List<DataAnalysisResultEntity> dataAnalysisResultEntities = dataAnalysisResultMapper.selectList(resultWrapper);

            if(CollectionUtil.isNotEmpty(dataAnalysisMarkMergeOutDOList)) {
               List<Long> markIds = dataAnalysisMarkMergeOutDOList.stream().map(DataAnalysisMarkMergeOutDO::getMarkId).collect(Collectors.toList());
               dataAnalysisMarkManager.updateMarkIdByIds(markIds);

           }*/
        }

        // 合并关系
        // 查询合并关系数量等于1
        List<DataAnalysisMarkMergeOutDO> dataAnalysisMarkMergeOutDOList = dataAnalysisMarkMergeManager.selectListByGroupIdList(resultGroupIdCollection);
        Map<String, List<DataAnalysisMarkMergeOutDO>> stringListMap = dataAnalysisMarkMergeOutDOList.stream()
                .collect(Collectors.groupingBy(DataAnalysisMarkMergeOutDO::getResultGroupId));
        Set<String> deleteGroupIdSet = Sets.newHashSet();
        stringListMap.forEach((key, value) -> {
            if (value.size() == 1) {
                deleteGroupIdSet.add(key);
            }
        });
        if (CollUtil.isNotEmpty(groupIdList)) {
            // 添加groupIdList原因，假设问题A已核实，问题B和C合并到问题A，如果问题A撤回或者问题删除，合并关系存在两条关系B和C，兼容已删除的问题组
            deleteGroupIdSet.addAll(groupIdList);
        }
        log.info("#DataAnalysisResultServiceImpl.balance# resultGroupIdCollection={}, deleteGroupIdSet={}", resultGroupIdCollection, deleteGroupIdSet);
        if (CollUtil.isNotEmpty(deleteGroupIdSet)) {
            dataAnalysisMarkMergeManager.deleteByResultGroupIdList(deleteGroupIdSet);
        }

        // 更新问题组时间
        Map<String, List<DataAnalysisResultEntity>> listMap = dataAnalysisResultEntityList.stream().collect(Collectors.groupingBy(DataAnalysisResultEntity::getResultGroupId));
        List<DataAnalysisResultGroupInDO> dataAnalysisResultGroupInDOList = Lists.newLinkedList();
        listMap.forEach((groupId, entityList) -> {
            if (CollUtil.isEmpty(entityList)) {
                return;
            }
            DataAnalysisResultGroupInDO entity = new DataAnalysisResultGroupInDO();
            entity.setResultGroupId(groupId);
            if (entityList.size() == 1) {
                LocalDateTime photoCreateTime = entityList.get(0).getPhotoCreateTime();
                entity.setEarliestTime(photoCreateTime);
                entity.setLatestTime(photoCreateTime);
            } else {
                entityList.sort(Comparator.comparing(DataAnalysisResultEntity::getPhotoCreateTime));
                entity.setEarliestTime(entityList.get(0).getPhotoCreateTime());
                entity.setLatestTime(entityList.get(entityList.size() - 1).getPhotoCreateTime());
            }
            dataAnalysisResultGroupInDOList.add(entity);
        });
        if (CollUtil.isNotEmpty(dataAnalysisResultGroupInDOList)) {
            dataAnalysisResultGroupManager.updateTime(dataAnalysisResultGroupInDOList);
        }
    }

    @Override
    public List<DataAnalysisResultOutDTO.ProblemOut> selectResultByGroupId(String resultGroupId) {
        LambdaQueryWrapper<DataAnalysisResultEntity> eq = Wrappers.lambdaQuery(DataAnalysisResultEntity.class)
                .eq(DataAnalysisResultEntity::getResultGroupId, resultGroupId)
                .eq(DataAnalysisResultEntity::getDeleted, false)
                .orderByDesc(DataAnalysisResultEntity::getPhotoCreateTime);
        List<DataAnalysisResultEntity> dataAnalysisResultEntityList = dataAnalysisResultMapper.selectList(eq);

        List<DataAnalysisResultOutDTO.ProblemOut> problemOutList = Lists.newArrayList();
        if (CollUtil.isEmpty(dataAnalysisResultEntityList)) {
            return problemOutList;
        }

        for (DataAnalysisResultEntity dataAnalysisResultEntity : dataAnalysisResultEntityList) {
            DataAnalysisResultOutDTO.ProblemOut problemOut = new DataAnalysisResultOutDTO.ProblemOut();
            BeanUtils.copyProperties(dataAnalysisResultEntity, problemOut);
            problemOutList.add(problemOut);
        }
        return problemOutList;
    }

    @Override
    public List<GridOutDTO.ProblemDTO> listProblemsByGridId(String gridManageId) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        // 先获取带有管理网格的问题组
        LambdaQueryWrapper<DataAnalysisResultGroupEntity> queryWrapper = Wrappers.lambdaQuery(DataAnalysisResultGroupEntity.class)
                .eq(DataAnalysisResultGroupEntity::getGridManageId, gridManageId)
                .likeRight(DataAnalysisResultGroupEntity::getOrgCode, orgCode);
        List<DataAnalysisResultGroupEntity> resultGroupEntityList = dataAnalysisResultGroupMapper.selectList(queryWrapper);
        List<String> resultGroupIds = resultGroupEntityList.stream().map(DataAnalysisResultGroupEntity::getResultGroupId).collect(Collectors.toList());
        // 获取问题
        if (!CollectionUtils.isEmpty(resultGroupIds)) {
            LambdaQueryWrapper<DataAnalysisResultEntity> con = Wrappers.lambdaQuery(DataAnalysisResultEntity.class)
                    .in(DataAnalysisResultEntity::getResultGroupId, resultGroupIds)
                    .orderByDesc(DataAnalysisResultEntity::getModifiedTime);
            List<DataAnalysisResultEntity> dataList = dataAnalysisResultMapper.selectList(con);

            if (!CollectionUtils.isEmpty(dataList)) {
                return dataList.stream().map(data -> {
                    GridOutDTO.ProblemDTO problemDTO = new GridOutDTO.ProblemDTO();
                    BeanUtils.copyProperties(data, problemDTO);
                    DateTimeFormatter dfDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    problemDTO.setModifiedTime(dfDateTime.format(data.getModifiedTime()));
                    return problemDTO;
                }).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Integer> statisticsGridProblems(List<String> gridManageIdList) {
        if (!CollectionUtils.isEmpty(gridManageIdList)) {
            String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();
            List<GridProblemsOutPO> gridProblemsOutPOList = dataAnalysisResultMapper.selectGroupByGridManageIds(gridManageIdList, orgCodeFromAccount);
            if (!CollectionUtils.isEmpty(gridProblemsOutPOList)) {
                return gridProblemsOutPOList.stream().collect(Collectors.toMap(GridProblemsOutPO::getGridManageId,GridProblemsOutPO::getCounts));
            }
        }
        return Collections.emptyMap();
    }
}
