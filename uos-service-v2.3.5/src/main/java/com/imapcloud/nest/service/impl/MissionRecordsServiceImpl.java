package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.enums.DataTypeEnum;
import com.imapcloud.nest.mapper.MissionRecordsMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.reqDto.MissionRecordsReqDto;
import com.imapcloud.nest.pojo.dto.respDto.MissionRecordsStatisticsRespDto;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.mongo.service.MongoNestAndAirService;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 架次记录表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-26
 */
@Service
@Slf4j
public class MissionRecordsServiceImpl extends ServiceImpl<MissionRecordsMapper, MissionRecordsEntity> implements MissionRecordsService {

    @Resource
    private MissionRecordsMapper missionRecordsMapper;

    @Autowired
    private MissionPhotoService missionPhotoService;

    @Autowired
    private MissionVideoService missionVideoService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SysTaskTagService sysTaskTagService;
    @Autowired
    private MissionService missionService;
    @Autowired
    private MongoNestAndAirService mongoNestAndAirService;

    @Resource
    private MissionVideoPhotoService missionVideoPhotoService;

    @Autowired
    private BaseNestService baseNestService;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private MissionAirService  missionAirServicel;
    @Override
    public int updateBatchByExecId(List<MissionRecordsEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return missionRecordsMapper.updateBatchByExecId(list);
    }

    @Override
    public int updateByExecId(MissionRecordsEntity missionRecordsEntity) {
        if (missionRecordsEntity != null) {
            return missionRecordsMapper.updatePart5ByExecId(missionRecordsEntity);
        }
        return 0;
    }

    @Override
    public List<Integer> listIdByExecIds(List<String> execIds) {
        return null;
    }

    @Override
    public Integer saveOrUpdateByExecId(MissionRecordsEntity missionRecordsEntity) {
        String execId = missionRecordsEntity.getExecId();
        if (execId != null) {
            MissionRecordsEntity mre = this.lambdaQuery().eq(MissionRecordsEntity::getExecId, execId)
                    .eq(MissionRecordsEntity::getDeleted, false)
                    .select(MissionRecordsEntity::getId)
                    .one();

            if (mre != null) {
                missionRecordsEntity.setId(mre.getId());
            }
            return this.saveOrUpdate(missionRecordsEntity) ? 1 : 0;
        }
        return 0;
    }

    @Override
    public Integer countMissionRecords(String execId) {
        return null;
    }

    @Override
    public MissionRecordsEntity getMissionRecordById(Integer id) {
        return missionRecordsMapper.getMissionRecordById(id);
    }

    @Override
    public void updateDataStatusById(Integer dataStatus, Integer id) {
        missionRecordsMapper.updateDataStatusById(dataStatus, id);
    }

    @Override
    public MissionRecordsEntity getByExecId(String execId) {
        return missionRecordsMapper.getByExecId(execId);
    }

    @Override
    public Integer getDataStatusById(Integer id) {
        return missionRecordsMapper.getDataStatusById(id);
    }

    @Override
    public Integer getGainDataModeById(Integer id) {
        return missionRecordsMapper.getGainDataModeById(id);
    }

    @Override
    public String getDataPathById(Integer id) {
        return baseMapper.getDataPathById(id);
    }

    @Override
    public String getVideoPathById(Integer id) {
        return baseMapper.getVideoPathById(id);
    }

    @Override
    public String getExecIdById(Integer id) {
        return baseMapper.getExecIdById(id);
    }

    @Override
    public NestEntity getNestUuidById(Integer id) {
        return baseMapper.getNestUuidById(id);
    }

    @Override
    public String getNestUuidByRecordId(Integer id) {
        return baseMapper.getNestUuidByRecordsId(id);
    }

    @Override
    public String getNestUuidByAppId(Integer missionRecordsId) {
        return baseMapper.getNestUuidByAppId(missionRecordsId);
    }

    @Override
    public void updateBackUpStatus(Integer id) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes batchUpdateMissionRecordsBy(MissionRecordsReqDto reqDto) {
        List<Integer> missionRecordIds = reqDto.getMissionRecordIds();
        if (CollectionUtils.isEmpty(missionRecordIds)) {
            return RestRes.errorParam();
        }
        //这里要考虑是否检验任务架次是不是属于任务的
        MissionRecordsEntity updateEntity = new MissionRecordsEntity();
        updateEntity.setDeleted(true);
        // 删除架次对应的图片
        missionPhotoService.deletePhoto(missionRecordIds);
        // 删除架次对应的视频
        missionVideoService.deleteVideo(missionRecordIds);

        missionVideoPhotoService.deleteVideoPhoto(missionRecordIds);
        // 删除mongo的飞行日志
        mongoNestAndAirService.batchDeleteByMissionRecordsIdList(missionRecordIds);

        missionRecordsMapper.update(updateEntity, new QueryWrapper<MissionRecordsEntity>().in("id", missionRecordIds));
        return RestRes.ok();
    }

    @Override
    public RestRes batchClearMissionRecords(MissionRecordsReqDto reqDto) {
        List<Integer> missionRecordIds = reqDto.getMissionRecordIds();
        if (CollectionUtils.isEmpty(missionRecordIds)) {
            return RestRes.errorParam();
        }

        Integer dataType = reqDto.getDataType();
        if (DataTypeEnum.PHOTO.getValue() == dataType) {
            // 删除架次对应的图片
            missionPhotoService.deletePhoto(missionRecordIds);
        } else if (DataTypeEnum.VIDEO.getValue() == dataType) {
            // 删除架次对应的视频
            missionVideoService.deleteVideo(missionRecordIds);
            missionVideoPhotoService.deleteVideoPhoto(missionRecordIds);
        }
        // 删除mongo的飞行日志
//        mongoNestAndAirService.batchDeleteByMissionRecordsIdList(missionRecordIds);
        return RestRes.ok();
    }

    @Override
    public Integer getGainVideo(Integer recordId) {
        return missionRecordsMapper.selectGainVideo(recordId);
    }

    @Override
    public String getMissionName(Integer recordId) {
        return missionRecordsMapper.selectMissionName(recordId);
    }

    @Override
    public Integer getFlyIndex(Integer recordId) {
        return missionRecordsMapper.selectFlyIndex(recordId);
    }

    @Override
    public Integer getIdByMissionId(Integer missionId) {
        return missionRecordsMapper.selectIdByMissionId(missionId);
    }

    @Override
    public Integer getMissionIdByRecordId(Integer recordId) {
        return baseMapper.selectMissionIdByRecordId(recordId);
    }

    @Override
    public String getAppPullHttpUrl(Integer recordId) {
        return baseMapper.getAppPullHttpUrl(recordId);
    }

    @Override
    public Integer updateDataStatusByExecId(String execId, Integer status) {
        return missionRecordsMapper.updateDataStatusByExecId(execId, status);
    }

    @Override
    public Integer updateBackupStatusByExecId(String execId, Integer status) {
        return missionRecordsMapper.updateBackUpStatusByExecId(status, execId);
    }

    @Override
    public Integer countFlyByMissionId(Integer missionId) {
        if (missionId != null) {
            return missionRecordsMapper.selectFlyTimesByMissionId(missionId);
        }
        return 0;
    }

    @Resource
    private UosAccountService uosAccountService;

    @Override
    public Map getTotalMilesAndTime(Integer type) {

        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        Long userId = Long.valueOf(visitorId);
        // 单位管理员查单位下数据，否则查用户直接关联的数据
        if (!uosAccountService.checkAccountHasOrgDefaultRole(visitorId)) {
            userId = null;
        }

        Map totalMilesAndTime = baseMapper.getRecordTotalMilesAndTime(type, orgCode, userId);
        String totalTimeString = "0小时 0分钟";

        if (totalMilesAndTime != null) {
            // 获取总时长，转化为**小时**分钟
            Object totalTimeObject = totalMilesAndTime.get("totalTime");
            if (totalTimeObject != null) {
                BigDecimal totalTime = (BigDecimal) totalMilesAndTime.get("totalTime");
                int time = totalTime.intValue();
                int hours = (int) Math.floor(time / 60);
                int minute = time % 60;
                totalTimeString = hours + "小时 " + minute + "分钟";
            }
        } else {
            totalMilesAndTime = new HashMap(2);
            totalMilesAndTime.put("totalMiles", 0);
        }
        totalMilesAndTime.put("totalTime", totalTimeString);

        return totalMilesAndTime;
    }

    @Override
    public RestRes getMissionRecordPageList(String startTime, String endTime, Integer page, Integer limit) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        IPage<MissionRecordsTotalDTO> totalPage = baseMapper.getMissionRecordPageList(new Page<>(page, limit), orgCode, startTime, endTime, accountId);
        // 设置账号名称
        if(!CollectionUtils.isEmpty(totalPage.getRecords())){
            Map<String, AccountOutDO> result = getAccountMap(totalPage.getRecords());
            totalPage.getRecords().forEach(r -> {
                String aId = Objects.nonNull(r.getCreateUserId()) ? r.getCreateUserId().toString() : "";
                if(!CollectionUtils.isEmpty(result) && result.containsKey(aId)){
                    r.setRealName(result.get(aId).getName());
                }
            });
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", new PageUtils(totalPage));
        return RestRes.ok(map);
    }

    private Map<String, AccountOutDO> getAccountMap(List<MissionRecordsTotalDTO> records) {
        // 批量查询账号信息
        if(!CollectionUtils.isEmpty(records)){
            List<String> accountIds = records.stream().map(MissionRecordsTotalDTO::getCreateUserId).map(Object::toString).collect(Collectors.toList());
            Result<List<AccountOutDO>> result = accountServiceClient.listAccountInfos(accountIds);
            if(result.isOk()){
                return result.getData()
                        .stream()
                        .collect(Collectors.toMap(AccountOutDO::getAccountId, r -> r));
            }
        }
        return Collections.emptyMap();
    }

    /**
     * @param
     * @param
     * @return
     */
    @Override
    public RestRes missionRecordsStatistics() {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(accountId);
        List<MissionRecordsStatisticsDto> missionRecordsStatisticsDtos = baseMapper.getMissionRescordsByNest(orgCode);
        MissionRecordsStatisticsRespDto missionRecordsStatisticsRespDto = new MissionRecordsStatisticsRespDto();
        HashMap<String, Object> hashMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(outDO) && CollectionUtil.isNotEmpty(outDO.getBaseNestId()) && CollectionUtil.isNotEmpty(missionRecordsStatisticsDtos)) {
            missionRecordsStatisticsDtos = missionRecordsStatisticsDtos.stream().filter(e -> outDO.getBaseNestId().contains(e.getBaseNestId())).collect(Collectors.toList());
            missionRecordsStatisticsRespDto.setList(missionRecordsStatisticsDtos);
        }
        hashMap.put("result", missionRecordsStatisticsRespDto);
        return RestRes.ok(hashMap);
    }

    /**
     * @param
     * @param
     * @return
     */
    @Override
    public RestRes getInspectStatisticsBy(String nestId, String startTime, String endTime) {
        HashMap<String, Object> hashMap = new HashMap<>();
        MissionRecordsStatisticsDto missionRecordsStatisticsDto = new MissionRecordsStatisticsDto();
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        QueryWrapper<TaskEntity> taskEntityQueryWrapper = new QueryWrapper<TaskEntity>().likeRight("org_code", orgCode).eq("deleted", false);
        List<MissionEntity> missionEntityList = new ArrayList<>();
        //在这里做筛选，兼容其他的接口，要做的筛选
        // 时间  1.近7天，
        // 时间  2.近30天，
        // 标签  3.还有某个标签，sysTagName
        // 标签  4.所有
        // 标签  5.未定义
        List<Integer> taskIds = new ArrayList<>();
        Map<Integer, String> taskIdNameMap = new HashMap<>();
        Map<Integer, String> taskIdNestIdMap = new HashMap<>();
        if (Objects.isNull(nestId) || nestId.isEmpty()) {
            taskEntityQueryWrapper.likeRight("org_code", orgCode);
            String accountId = TrustedAccessTracerHolder.get().getAccountId();
            BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(accountId);
            if (ObjectUtils.isNotEmpty(outDO)) {
                taskEntityQueryWrapper.in("base_nest_id", outDO.getBaseNestId());
            }else{
                taskEntityQueryWrapper.eq("base_nest_id",null);
            }
            List<TaskEntity> taskEntities = taskService.list(taskEntityQueryWrapper);
            //这里找出task的任务，且是移动终端类型的
            taskIds = taskEntities.stream().map(TaskEntity::getId).collect(Collectors.toList());
            taskIdNameMap = taskEntities.stream().collect(Collectors.toMap(TaskEntity::getId, TaskEntity::getName));
            taskIdNestIdMap = taskEntities.stream().collect(Collectors.toMap(TaskEntity::getId, TaskEntity::getBaseNestId));
        } else {
            List<TaskEntity> taskEntityList = taskService.list(new QueryWrapper<TaskEntity>().eq("deleted", false).likeRight("org_code", orgCode).eq("base_nest_id", nestId));
            taskIds = taskEntityList.stream().map(TaskEntity::getId).collect(Collectors.toList());
            taskIdNameMap = taskEntityList.stream().collect(Collectors.toMap(TaskEntity::getId, TaskEntity::getName));
            taskIdNestIdMap = taskEntityList.stream().collect(Collectors.toMap(TaskEntity::getId, TaskEntity::getBaseNestId));
        }
        if (CollectionUtil.isNotEmpty(taskIds)) {
            missionEntityList = missionService.list(new QueryWrapper<MissionEntity>().in("task_id", taskIds).eq("deleted", false));
        }
        List<Integer> missionIds = new ArrayList<>();
        Map<Integer, Integer> missionIdTaskIdMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(missionEntityList)) {
            missionIds = missionEntityList.stream().map(MissionEntity::getId).collect(Collectors.toList());
            missionIdTaskIdMap = missionEntityList.stream().collect(Collectors.toMap(MissionEntity::getId, MissionEntity::getTaskId));
        } else {
            hashMap.put("result", missionRecordsStatisticsDto);
            return RestRes.ok(hashMap);
        }

        List<MissionRecordsEntity> missionRecordsEntities = baseMapper.getMissionRecordListBy(startTime, endTime, missionIds);
        if (CollectionUtils.isEmpty(missionRecordsEntities)) {
            hashMap.put("result", missionRecordsStatisticsDto);
            return RestRes.ok(hashMap);
        }
        //这里是获取名字的，所以删除的，也得算上
//        List<NestEntity> nestEntities = nestService.list(new QueryWrapper<NestEntity>());
//        Map<Integer, String> nestIdNameMap = nestEntities.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getName));
        Map<String, String> nestIdNameMap = baseNestService.getAllNestNameMap();
        nestIdNameMap.put("0", "移动终端");

        Map<Integer, Integer> finalMissionIdTaskIdMap = missionIdTaskIdMap;
        List<SysTaskTagEntity> sysTaskTagEntities = sysTaskTagService.listTaskTagAndName(taskIds);
        Map<Integer, String> taskIdTagNameMap = new HashMap<>();
        Map<Integer, Integer> taskIdTagIdMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(sysTaskTagEntities)) {
            taskIdTagNameMap = sysTaskTagEntities.stream().collect(Collectors.toMap(SysTaskTagEntity::getTaskId, SysTaskTagEntity::getSysTagName, (entity1, entity2) -> entity1));
            taskIdTagIdMap = sysTaskTagEntities.stream().collect(Collectors.toMap(SysTaskTagEntity::getTaskId, SysTaskTagEntity::getSysTagId, (entity1, entity2) -> entity1));
        }
        Map<Integer, String> finalTaskIdTagNameMap = taskIdTagNameMap;
        Map<Integer, Integer> finalTaskIdTagIdMap = taskIdTagIdMap;
        Map<Integer, String> finalTaskIdNestIdMap = taskIdNestIdMap;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        missionRecordsEntities.forEach(it -> {
            it.setTaskId(finalMissionIdTaskIdMap.get(it.getMissionId()));
            it.setNestId(finalTaskIdNestIdMap.get(it.getTaskId()));
            it.setSysTagName(finalTaskIdTagNameMap.get(it.getTaskId()));
            it.setSysTagId(finalTaskIdTagIdMap.get(it.getTaskId()));
            it.setSysTagName(StringUtils.isNotBlank(it.getSysTagName()) ? it.getSysTagName() : "未定义");
            it.setCreateTimeStr(it.getCreateTime().format(formatter));
        });
        //在这里做筛选，兼容其他的接口，要做的筛选
        // 时间  1.近7天，
        // 时间  2.近30天，
        // 标签  3.还有某个标签，sysTagName
        // 标签  4.所有
        // 标签  5.未定义

        List<MissionRecordsTaskStatisticsDto> taskDtos = new ArrayList<>();
        Map<Integer, List<MissionRecordsEntity>> taskMissionRecordsListMap = missionRecordsEntities.stream().collect(Collectors.groupingBy(MissionRecordsEntity::getTaskId));
        for (Map.Entry<Integer, List<MissionRecordsEntity>> taskMREntry : taskMissionRecordsListMap.entrySet()) {
            Integer taskId = taskMREntry.getKey();
            List<MissionRecordsEntity> taskMREntryValue = taskMREntry.getValue();
            MissionRecordsTaskStatisticsDto newTaskDto = new MissionRecordsTaskStatisticsDto();
            //这里还得判断是否为0的，nestId为0就是移动终端的
            newTaskDto.setTaskName(taskIdNameMap.get(taskId));
            newTaskDto.setTaskId(taskId);
            //newTaskDto.setTaskMissionRecordsEntityList(taskMREntryValue);
            newTaskDto.setTaskCount(taskMREntryValue.size());
            taskDtos.add(newTaskDto);
        }

        List<MissionRecordsNestStatisticsDto> nestDtos = new ArrayList<>();
        Map<String, List<MissionRecordsEntity>> nestMissionRecordsListMap = missionRecordsEntities.stream().collect(Collectors.groupingBy(MissionRecordsEntity::getNestId));
        for (Map.Entry<String, List<MissionRecordsEntity>> nestMREntry : nestMissionRecordsListMap.entrySet()) {
            String nestId1 = nestMREntry.getKey();
            List<MissionRecordsEntity> nestMREntryValue = nestMREntry.getValue();
            MissionRecordsNestStatisticsDto newNestDto = new MissionRecordsNestStatisticsDto();
            newNestDto.setNestName(nestIdNameMap.get(nestId1));
            if (nestIdNameMap.get(nestId1) == null) {
                log.info("找不到名字的nestId" + nestId1);
            }
            //newNestDto.setNestMissionRecordsEntityList(nestMREntryValue);
            newNestDto.setNestCount(nestMREntryValue.size());
            nestDtos.add(newNestDto);
        }
        missionRecordsStatisticsDto.setNestMissionRecordsEntityList(nestDtos);
        missionRecordsStatisticsDto.setTaskMissionRecordsEntityList(taskDtos);
        missionRecordsStatisticsDto.setTotalInspectTimes(missionRecordsEntities.size());

        List<MissionRecordsDayStatisticsDto> dayStatisticsDtos = new ArrayList<>();
        Map<String, List<MissionRecordsEntity>> createTimeStrListMap = missionRecordsEntities.stream().collect(Collectors.groupingBy(MissionRecordsEntity::getCreateTimeStr));
        for (Map.Entry<String, List<MissionRecordsEntity>> createTimeStrEntry : createTimeStrListMap.entrySet()) {
            String createTimeStrEntryKey = createTimeStrEntry.getKey();
            List<MissionRecordsEntity> createTimeStrEntryValue = createTimeStrEntry.getValue();
            MissionRecordsDayStatisticsDto dayNumsDto = new MissionRecordsDayStatisticsDto();
            dayNumsDto.setDateStr(createTimeStrEntryKey);
            dayNumsDto.setRecordsCount(createTimeStrEntryValue.size());
            dayStatisticsDtos.add(dayNumsDto);
        }
        missionRecordsStatisticsDto.setDayStatisticsEntityList(dayStatisticsDtos);
        hashMap.put("result", missionRecordsStatisticsDto);
        return RestRes.ok(hashMap);
    }

    @Override
    public Integer getTaskIdByRecordsId(Integer missionRecordsId) {
        return baseMapper.getTaskIdByRecordsId(missionRecordsId);
    }

    @Override
    public List<Integer> getMissionRecordsIdByTaskId(Integer taskId) {
        return baseMapper.getMissionRecordsIdByTaskId(taskId);
    }

    @Override
    public Map getTotalInspectTimes(String startTime, String endTime) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        Map totalTime = baseMapper.getTotalInspectTimes(startTime, endTime, orgCode, accountId);
        return totalTime;
    }

    @Override
    public String getMaxRecordsIdByMissionId(Integer missionId) {
        if (missionId != null) {
            return baseMapper.selectMaxIdByMissionId(missionId);
        }
        return null;
    }

    @Override
    public MissionRecordsEntity getLastByMissionId(Integer missionId) {
        if (missionId == null) {
            return null;
        }
        return baseMapper.selectLastRecordsByMissionId(missionId);
    }

}
