package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.rest.Result;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.imapcloud.nest.mapper.MissionRecordsMapper;
import com.imapcloud.nest.mapper.TaskMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.UnifyAirLineFormatDto;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.service.MissionService;
import com.imapcloud.nest.service.TaskService;
import com.imapcloud.nest.v2.dao.entity.*;
import com.imapcloud.nest.v2.dao.mapper.*;
import com.imapcloud.nest.v2.dao.po.in.GridHistoryPhotoPO;
import com.imapcloud.nest.v2.dao.po.in.GridMissionRecordCriteriaInPO;
import com.imapcloud.nest.v2.dao.po.in.GridPhotoRelQueryCriteriaInPO;
import com.imapcloud.nest.v2.dao.po.out.*;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.feign.OrgServiceClient;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import com.imapcloud.nest.v2.service.GridMissionService;
import com.imapcloud.nest.v2.service.GridService;
import com.imapcloud.nest.v2.service.converter.GridMissionConverter;
import com.imapcloud.nest.v2.service.dto.GridMissionDTO;
import com.imapcloud.nest.v2.service.dto.in.GridHistoryInDTO;
import com.imapcloud.nest.v2.service.dto.in.GridMissionRecordPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.GridDataInfoDTO;
import com.imapcloud.nest.v2.service.dto.out.GridMissionRecordPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname GridMissionServiceImpl
 * @Description 网格任务实现类
 * @Date 2022/12/18 11:14
 * @Author Carnival
 */
@Slf4j
@Service
public class GridMissionServiceImpl implements GridMissionService {


    @Resource
    private GridManageMapper gridManageMapper;

    @Resource
    private GridPhotoRelMapper gridPhotoRelMapper;

    @Resource
    private GridMissionConverter gridMissionConverter;

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private MissionRecordsMapper missionRecordsMapper;

    @Resource
    private UosOrgManager uosOrgManager;

    @Resource
    private GridService gridService;

    @Resource
    private BaseNestMapper baseNestMapper;

    @Resource
    private GridMissionRecordMapper gridMissionRecordMapper;

    @Resource
    private MissionService missionService;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private OrgServiceClient orgServiceClient;

    @Resource
    private TaskService taskService;

    @Resource
    private DataAnalysisResultService dataAnalysisResultService;

    @Resource
    private GridInspectRecordMapper gridInspectRecordMapper;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private GridManageOrgRelMapper gridManageOrgRelMapper;


    private static final Integer Fail = 0;



    @Override
    public List<GridOutDTO.PhotoDTO> queryPhotoByGridIds(List<String> gridManageIds, String gridInspectId, Integer missionRecordsId, String orgCode) {
        if (!CollectionUtils.isEmpty(gridManageIds)) {
            // 获取管理网格下各个架次最新的照片
            if (gridInspectId == null && missionRecordsId == null) {
                return getLastestMissionPhoto(gridManageIds, orgCode);
            }
            // 获取当个巡检记录下的网格照片
            else if (gridInspectId != null && missionRecordsId == null) {
                return getMissionPhotoBygridInspectId(gridInspectId);
            }
            // 获取当个架次下的网格照片
            else if (gridInspectId == null) {
                return getMissionPhotoByMissionRecordId(gridManageIds.get(0), missionRecordsId);
            }
        }
        return null;
    }


    @Override
    public Boolean relPhotoToGrid(List<Long> photoIdList, Integer taskId, Integer missionRecordId, String orgCode) {

        if (CollectionUtils.isEmpty(photoIdList)) {
            return false;
        }

        // 判断missionRecord是否已记录
        LambdaQueryWrapper<GridPhotoRelEntity> conGPR = Wrappers.lambdaQuery(GridPhotoRelEntity.class)
                .eq(GridPhotoRelEntity::getMissionRecordsId, missionRecordId);
        List<GridPhotoRelEntity> gridPhotoRelEntities = gridPhotoRelMapper.selectList(conGPR);
        List<Long> photoIdListFromGridPhotoRel = gridPhotoRelEntities.stream().map(GridPhotoRelEntity::getPhotoId).collect(Collectors.toList());

        // 获取需要关联的照片
        List<Long> relPhotoListIds = this.getRelPhotoListIds(photoIdList, photoIdListFromGridPhotoRel);
        if (CollectionUtils.isEmpty(relPhotoListIds)) {
            return false;
        }

        List<MissionPhotoEntity> photoEntityList = missionPhotoService.selectBatchPhoto(relPhotoListIds);
        photoEntityList.sort(Comparator.comparing(MissionPhotoEntity::getWaypointIndex));
        // 航点数量
        Integer wayPointEnd = photoEntityList.get(photoEntityList.size() - 1).getWaypointIndex();
        Integer wayPointStart = photoEntityList.get(0).getWaypointIndex();
        int pointCount = wayPointEnd - wayPointStart + 1;


        // 若taskId为空从架次记录中获取taskId
        if (taskId == null) {
            taskId = missionRecordsService.getTaskIdByRecordsId(missionRecordId);
        }

        // 获取航线信息
        MissionRecordsEntity missionRecord = missionRecordsService.getMissionRecordById(missionRecordId);
        Integer missionId = missionRecord.getMissionId();
        List<AirLineEntity> airLineEntityList = missionService.getAirLineByTaskId(taskId);
        // 计算航点和数据网格顺序
        int seq = missionRecordsMapper.getMissionSeqById(missionId);
        int start = 0;
        for (int i = 0; i < seq - 1; i++) {
            AirLineEntity airLineEntity = airLineEntityList.get(i);
            start += airLineEntity.getPointCount();
        }
        start += wayPointStart - 1;
        int finalStartPoint = start;
        int endPoint = finalStartPoint + pointCount - 1;

        // 根据航线寻找对应的管理网格
        LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                .eq(GridManageOrgRelEntity::getTaskId, taskId);
        Optional<GridManageOrgRelEntity> optional = Optional.ofNullable(gridManageOrgRelMapper.selectOne(con));
        if (optional.isPresent()) {
            GridManageOrgRelEntity gridManageOrgRelEntity = optional.get();
            String gridManageId = gridManageOrgRelEntity.getGridManageId();

            // 获取数据网格
            List<GridOutDTO.GridDataBatchDTO> gridDataBatchDTOS = gridService.queryGridData(Collections.singletonList(gridManageId), orgCode);
            if (CollectionUtils.isEmpty(gridDataBatchDTOS)) {
                return false;
            }
            GridOutDTO.GridDataBatchDTO gridDataBatchList = gridDataBatchDTOS.get(0);
            List<GridOutDTO.GridDataOutDTO> gridDataList = gridDataBatchList.getGridDataOutDTOS();
            List<GridOutDTO.GridDataOutDTO> gridDataListByOrder = new ArrayList<>();

            gridDataList.forEach(r -> {
                Integer gridDataSeq = r.getSeq();
                if (gridDataSeq >= finalStartPoint && gridDataSeq <= endPoint) gridDataListByOrder.add(r);
            });
            gridDataListByOrder.sort(Comparator.comparing(GridOutDTO.GridDataOutDTO::getSeq));

            // 照片和网格关联
            List<GridPhotoRelEntity> entityList = Lists.newArrayList();

            for (MissionPhotoEntity photoEntity : photoEntityList) {
                GridPhotoRelEntity entity = new GridPhotoRelEntity();
                Double longitude = photoEntity.getLongitude();
                Double latitude = photoEntity.getLatitude();
                Integer waypointIndex = photoEntity.getWaypointIndex();
                // 手动拍照不考虑
                if (waypointIndex == -100) continue;
                // 先用经纬度匹配
                String gridDataId = matchGridData(latitude, longitude, gridDataList);
                if (StringUtils.hasText(gridDataId)) {
                    entity.setGridDataId(gridDataId);
                    entity.setGridManageId(gridManageId);
                    entity.setPhotoId(photoEntity.getId());
                    entity.setMissionRecordsId(missionRecordId);
                    entityList.add(entity);
                }
                // 如果匹配不成功根据waypointindex匹配
                else {
                    if (pointCount <= gridDataListByOrder.size()) {
                        entity.setGridDataId(gridDataListByOrder.get(waypointIndex - wayPointStart).getGridDataId());
                        entity.setGridManageId(gridManageId);
                        entity.setPhotoId(photoEntity.getId());
                        entity.setMissionRecordsId(missionRecordId);
                        entityList.add(entity);
                    }
                }
            }
            return gridPhotoRelMapper.batchInsert(entityList) != Fail;
        }

        return false;
    }


    @Override
    public List<GridOutDTO.InspectRecordDTO> queryInspectByGridIds(String gridManageId) {

        // 获取账号所在的单位
        String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();

        // 获取巡检记录
        LambdaQueryWrapper<GridInspectRecordEntity> con = Wrappers.lambdaQuery(GridInspectRecordEntity.class)
                .eq(GridInspectRecordEntity::getGridManageId, gridManageId)
                .likeRight(GridInspectRecordEntity::getOrgCode, orgCodeFromAccount);
        List<GridInspectRecordEntity> gridInspectRecordEntityList = gridInspectRecordMapper.selectList(con);
        List<String> gridInspectIds = gridInspectRecordEntityList.stream()
                .map(GridInspectRecordEntity::getGridInspectId)
                .distinct().
                collect(Collectors.toList());

        List<GridOutDTO.InspectRecordDTO> res = Lists.newArrayList();
        for (String gridInspectId : gridInspectIds) {
            GridOutDTO.InspectRecordDTO dto = new GridOutDTO.InspectRecordDTO();
            List<GridInspectRecordEntity> tmp = Lists.newArrayList();
            int totalCount = 0;
            int executedCount = 0;
            for (GridInspectRecordEntity entity : gridInspectRecordEntityList) {
                if (Objects.equals(entity.getGridInspectId(), gridInspectId)) {
                    tmp.add(entity);
                    totalCount++;
                    if (entity.getExecuteStatus() == 1) {
                        executedCount++;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(tmp)) {
                dto.setGridInspectId(tmp.get(0).getGridInspectId());
                dto.setGridManageId(tmp.get(0).getGridManageId());
                dto.setCreateTime(converteLocalDateTime(tmp.get(0).getCreatedTime()));
                dto.setGridInspectId(tmp.get(0).getGridInspectId());
                dto.setExecutedCount(executedCount);
                dto.setTotalCount(totalCount);
                dto.setIsNewest(tmp.get(0).getIsNewest());
                dto.setOrgCode(tmp.get(0).getOrgCode());
                res.add(dto);
            }
        }
        // 匹配单位名称
        if (!CollectionUtils.isEmpty(res)) {
            List<String> orgCodes = res.stream().map(GridOutDTO.InspectRecordDTO::getOrgCode).collect(Collectors.toList());
            List<OrgSimpleOutDO> orgSimpleOutDOS = uosOrgManager.listOrgInfos(orgCodes);
            if (!CollectionUtils.isEmpty(orgSimpleOutDOS)) {
                Map<String, String> map = orgSimpleOutDOS.stream()
                        .filter(r -> r.getOrgName() != null)
                        .collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
                for (GridOutDTO.InspectRecordDTO dto : res) {
                    dto.setOrgName(map.get(dto.getOrgCode()));
                }
            }

        }
        return res;
    }


    @Override
    public List<GridOutDTO.GridStatisticsDTO> queryGridStatistics(List<String> gridManageIds) {
        if (CollectionUtils.isEmpty(gridManageIds)) {
            return Collections.emptyList();
        }
        String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();
        LambdaQueryWrapper<GridInspectRecordEntity> con = Wrappers.lambdaQuery(GridInspectRecordEntity.class)
                .in(GridInspectRecordEntity::getGridManageId, gridManageIds)
                .likeRight(GridInspectRecordEntity::getOrgCode, orgCodeFromAccount)
                .groupBy(GridInspectRecordEntity::getGridInspectId);
        List<GridInspectRecordEntity> gridInspectRecordEntityList = gridInspectRecordMapper.selectList(con);
        Map<String, Integer> gridProblemsMap = dataAnalysisResultService.statisticsGridProblems(gridManageIds);

        List<GridOutDTO.GridStatisticsDTO> res = CollectionUtil.newArrayList();
        // 没有巡检记录的情况
        if (CollectionUtils.isEmpty(gridInspectRecordEntityList)) {
            for (Map.Entry<String, Integer> entry : gridProblemsMap.entrySet()) {
                GridOutDTO.GridStatisticsDTO dto = new GridOutDTO.GridStatisticsDTO();
                dto.setProblemCount(entry.getValue());
                dto.setGridManageId(entry.getKey());
                res.add(dto);
            }
            return res;
        }
        // 有巡检记录的情况
        else {
            for (String gridManageId : gridManageIds) {
                GridOutDTO.GridStatisticsDTO dto = new GridOutDTO.GridStatisticsDTO();
                int missionCount = countInpectRecords(gridManageId, gridInspectRecordEntityList);
                dto.setGridManageId(gridManageId);
                dto.setMissionCount(missionCount);
                dto.setProblemCount(gridProblemsMap.get(gridManageId));
                gridProblemsMap.remove(gridManageId);
                res.add(dto);
            }
            // 剩余的问题统计
            if (!CollectionUtils.isEmpty(gridProblemsMap)) {
                for (Map.Entry<String, Integer> entry : gridProblemsMap.entrySet()) {
                    GridOutDTO.GridStatisticsDTO dto = new GridOutDTO.GridStatisticsDTO();
                    dto.setProblemCount(entry.getValue());
                    dto.setGridManageId(entry.getKey());
                    res.add(dto);
                }
            }
            return res;
        }
    }


    @Override
    public List<GridOutDTO.Photo> queryHistoryByGridDataId(GridHistoryInDTO dto) {
        dto.setEndTime(addDay(dto.getEndTime()));
        GridPhotoRelQueryCriteriaInPO po = buildqueryHistoryCriteria(dto);
        List<Long> photoIds = gridPhotoRelMapper.queryPhotoIds(po);
        List<GridOutDTO.Photo> resPhotoList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(photoIds)) {
            // 过滤当前单位且子单位的历史数据
            String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();
            // 获取所有历史照片
            List<GridHistoryPhotoPO> historyPhotos = gridMissionRecordMapper.getHistoryPhoto(photoIds, orgCodeFromAccount);
            // 添加标记照片
            if (!CollectionUtils.isEmpty(historyPhotos)) {
                List<GridOutDTO.Photo> photoList = historyPhotos.stream().map(r -> gridMissionConverter.convertPhotoPo(r)).collect(Collectors.toList());
                List<Long> photoIdsFromHistory = photoList.stream().map(GridOutDTO.Photo::getId).collect(Collectors.toList());
                List<GridCenterDetailIdOutPO> gridCenterDetailIdOutPOS = gridMissionRecordMapper.queryCenterDetailId(photoIdsFromHistory);
                if (!CollectionUtils.isEmpty(gridCenterDetailIdOutPOS)) {
                    HashMap<Long, String> photoIdTODetailIdMap = gridCenterDetailIdOutPOS.stream()
                            .collect(HashMap::new, (k, v) -> k.put(v.getPhotoId(), v.getCenterDetailId()), HashMap::putAll);
                    for (GridOutDTO.Photo photo : photoList) {
                        photo.setCenterDetailId(photoIdTODetailIdMap.get(photo.getId()));
                    }
                }
                resPhotoList.addAll(photoList);
            }
            // 照片集合统一处理
            if (!CollectionUtils.isEmpty(resPhotoList)) {
                List<Integer> missionIds = resPhotoList.stream().map(GridOutDTO.Photo::getMissionId).distinct().collect(Collectors.toList());
                List<GridMissionNamePO> namePOS = gridMissionRecordMapper.getMissionNameAndTaskName(missionIds);
                // 匹配任务名称和架次名称
                getMissionNameAndTaskNameToPhoto(resPhotoList, namePOS);
                resPhotoList.sort(Comparator.comparing(GridOutDTO.Photo::getCreateTime).reversed());
            }
        }

        return resPhotoList;
    }

    @Override
    public boolean saveMissionToGrid(GridMissionDTO gridMissionDTO) {
        Integer missionId = gridMissionDTO.getMissionId();
        String missionName = gridMissionDTO.getMissionName();
        String nestUuid = gridMissionDTO.getNestUuid();
        Integer missionRecordsId = gridMissionDTO.getMissionRecordsId();
        Integer taskId = gridMissionDTO.getTaskId();
        TaskEntity taskEntity = taskService.selectById(taskId);
        String orgCode = taskEntity.getOrgCode();
        String orgName = "";
        Result<OrgSimpleOutDO> orgDetails = orgServiceClient.getOrgDetails(orgCode);
        if (orgDetails.isOk()) {
            OrgSimpleOutDO data = orgDetails.getData();
            orgName = data.getOrgName();
        }
        // 获取管理网格信息
        String gridManageId = gridService.getGridManageByTaskId(taskId);
        LambdaQueryWrapper<GridManageEntity> wrapperGridManage = Wrappers.lambdaQuery(GridManageEntity.class)
                .eq(GridManageEntity::getGridManageId, gridManageId);
        Optional<GridManageEntity> optionalGridManage = Optional.ofNullable(gridManageMapper.selectOne(wrapperGridManage));
        Integer line = null;
        Integer col = null;
        Double east = null;
        Double west = null;
        Double north = null;
        Double south = null;

        if (optionalGridManage.isPresent()) {
            GridManageEntity gridManageEntity = optionalGridManage.get();
            line = gridManageEntity.getLine();
            col = gridManageEntity.getCol();
            east = gridManageEntity.getEast();
            west = gridManageEntity.getWest();
            north = gridManageEntity.getNorth();
            south = gridManageEntity.getSouth();
        }

        // 获取基站信息
        String baseNestName;
        String baseNestId;
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .eq(BaseNestEntity::getUuid, nestUuid);
        BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
        if (!Objects.nonNull(baseNestEntity)) {
            return false;
        }
        baseNestName = baseNestEntity.getName();
        baseNestId = baseNestEntity.getNestId();

        if (gridManageId != null && missionId != null && missionName != null && baseNestId != null && missionRecordsId != null) {
            GridMissionRecordEntity entity = new GridMissionRecordEntity();
            entity.setGridMissionId(BizIdUtils.snowflakeIdStr());
            entity.setGridManageId(gridManageId);
            entity.setMissionId(missionId);
            entity.setMissionName(missionName);
            entity.setBaseNestId(baseNestId);
            entity.setBaseNestName(baseNestName);
            entity.setTaskId(taskId);
            entity.setMissionRecordsId(missionRecordsId);
            entity.setSyncStatus(0);
            entity.setLine(line);
            entity.setCol(col);
            entity.setNorth(north);
            entity.setSouth(south);
            entity.setEast(east);
            entity.setWest(west);
            entity.setOrgCode(orgCode);
            entity.setOrgName(orgName);
            int res = gridMissionRecordMapper.insert(entity);
            return res != Fail;
        }
        return false;
    }

    @Override
    public boolean saveGridInspect(GridMissionDTO gridMissionDTO) {

        TaskEntity taskEntity = taskMapper.selectById(gridMissionDTO.getTaskId());
        String orgCode = taskEntity.getOrgCode();
        Integer missionId = gridMissionDTO.getMissionId();
        Integer missionRecordsId = gridMissionDTO.getMissionRecordsId();
        String gridInspectId = gridMissionDTO.getGridInspectId();

        // 补飞和替换操作
        if (StringUtils.hasText(gridInspectId)) {
            return supplementGridInspect(orgCode, gridInspectId, missionId, missionRecordsId);
        }
        // 与最新巡检记录比对做新增或者补充操作
        else {
            return addGridInspect(orgCode, taskEntity, missionId, missionRecordsId);
        }
    }


    @Override
    public PageResultInfo<GridMissionRecordPageOutDTO> pageGridMissionRecord(GridMissionRecordPageInDTO dto) {
        dto.setEndTime(addDay(dto.getEndTime()));
        // 判断是否包含#
        boolean isSpecialDeal = false;
        String missionNameSave = "";
        if (StringUtils.hasText(dto.getMissionName()) && dto.getMissionName().contains("#")) {
            missionNameSave = dto.getMissionName();
            isSpecialDeal = true;
            String[] split = dto.getMissionName().split("#");
            if (split.length > 2) {
                return null;
            }
            dto.setMissionName(split[0]);
        }
        GridMissionRecordCriteriaInPO po = buildGridMissionRecordPageCriteria(dto);
        long total = gridMissionRecordMapper.countByCondition(po);
        List<GridMissionRecordEntity> rows;
        if (total > 0) {
            rows = gridMissionRecordMapper.selectByCondition(po, PagingRestrictDo.getPagingRestrict(dto));
            if (!CollectionUtils.isEmpty(rows)) {
                List<GridMissionRecordPageOutDTO> convertRows = rows.stream().map(r -> gridMissionConverter.convert(r)).collect(Collectors.toList());
                List<Integer> missionRecordsIds = rows.stream().map(GridMissionRecordEntity::getMissionRecordsId).collect(Collectors.toList());
                List<GridDataStatusPO> gridDataStatusPOList = gridMissionRecordMapper.getMissionRecordsDataStatus(missionRecordsIds);
                // 设置同步状态与任务名称
                for (GridDataStatusPO gridDataStatusPO : gridDataStatusPOList) {
                    Integer missionRecordsId = gridDataStatusPO.getId();
                    Integer flyIndex = gridDataStatusPO.getFlyIndex();
                    Integer dataStatus = gridDataStatusPO.getDataStatus();
                    for (GridMissionRecordPageOutDTO convertRow : convertRows) {
                        if (Objects.equals(convertRow.getMissionRecordsId(), missionRecordsId)) {
                            String resMissionName = convertRow.getMissionName() + "#" + flyIndex;
                            convertRow.setMissionName(resMissionName);
                            convertRow.setSyncStatus(dataStatus);
                            break;
                        }
                    }
                }
                // 特殊处理
                if (isSpecialDeal) {
                    List<GridMissionRecordPageOutDTO> tmpList = Lists.newArrayList();
                    for (GridMissionRecordPageOutDTO convertRow : convertRows) {
                        if (missionNameSave.equals(convertRow.getMissionName())) {
                            tmpList.add(convertRow);
                        }
                    }
                    convertRows = tmpList;
                    total = convertRows.size();
                }

                //返回基站类型
                Map<String, Integer> nestIdToTypeMap = this.baseNestService.getNestTypeMap(rows.stream()
                        .map(GridMissionRecordEntity::getBaseNestId)
                        .collect(Collectors.toList()));
                convertRows.forEach(row ->
                        row.setBaseType(nestIdToTypeMap.get(row.getBaseNestId()))
                );

                return PageResultInfo.of(total, convertRows);
            }
        }
        return null;
    }

    @Override
    public List<GridOutDTO.GridManageHasDataDTO> gridManageHasData(List<String> gridManageIds, String startTime, String endTime) {
        if (StringUtils.hasText(endTime)) {
            endTime = addDay(endTime);
        }
        if (!CollectionUtils.isEmpty(gridManageIds)) {
            // 过滤除了本单位且子单位数据
            String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();
            LambdaQueryWrapper<GridInspectRecordEntity> con = Wrappers.lambdaQuery(GridInspectRecordEntity.class)
                    .in(GridInspectRecordEntity::getGridManageId, gridManageIds)
                    .likeRight(GridInspectRecordEntity::getOrgCode, orgCodeFromAccount);
            List<GridInspectRecordEntity> gridInspectRecordEntityList = gridInspectRecordMapper.selectList(con);
            List<String> gridManageIdsFromInspectRecord = gridInspectRecordEntityList.stream().map(GridInspectRecordEntity::getGridManageId).collect(Collectors.toList());


            List<GridManageHasDataPO> pos = gridPhotoRelMapper.gridManageHasData(gridManageIds, startTime, endTime);
            List<GridOutDTO.GridManageHasDataDTO> res = new ArrayList<>();
            for (GridManageHasDataPO po : pos) {
                GridOutDTO.GridManageHasDataDTO dto = new GridOutDTO.GridManageHasDataDTO();
                String gridManageId = po.getGridManageId();
                dto.setGridManageId(gridManageId);
                dto.setHasData(po.getNums() > 0 && gridManageIdsFromInspectRecord.contains(gridManageId));
                res.add(dto);
            }
            return res;

        }
        return null;
    }

    @Override
    public List<GridOutDTO.MissionStatusDTO> queryMissionStatus(String gridMangeId, String gridInspectId, String orgCode) {
        List<GridOutDTO.MissionStatusDTO> res = Lists.newArrayList();
        // 根据具体的巡检记录获取架次状态
        if (StringUtils.hasText(gridInspectId)) {
            LambdaQueryWrapper<GridInspectRecordEntity> con = Wrappers.lambdaQuery(GridInspectRecordEntity.class)
                    .eq(GridInspectRecordEntity::getGridManageId, gridMangeId)
                    .eq(GridInspectRecordEntity::getGridInspectId, gridInspectId);
            List<GridInspectRecordEntity> inspectRecordEntities = gridInspectRecordMapper.selectList(con);
            if (!CollectionUtils.isEmpty(inspectRecordEntities)) {
                for (GridInspectRecordEntity gridInspectRecordEntity : inspectRecordEntities) {
                    GridOutDTO.MissionStatusDTO dto = new GridOutDTO.MissionStatusDTO();
                    dto.setTaskId(gridInspectRecordEntity.getTaskId());
                    dto.setMissionId(gridInspectRecordEntity.getMissionId());
                    dto.setMissionName(gridInspectRecordEntity.getMissionName());
                    dto.setGridInspectId(gridInspectRecordEntity.getGridInspectId());
                    dto.setExecuteStatus(gridInspectRecordEntity.getExecuteStatus());
                    res.add(dto);
                }
            }
        }
        // 如果没有巡检记录Id则查看最新架次的状态
        else {
            LambdaQueryWrapper<GridInspectRecordEntity> con;
            if (StringUtils.hasText(orgCode)) {
                con = Wrappers.lambdaQuery(GridInspectRecordEntity.class)
                        .eq(GridInspectRecordEntity::getGridManageId, gridMangeId)
                        .eq(GridInspectRecordEntity::getIsNewest, 1)
                        .eq(GridInspectRecordEntity::getOrgCode, orgCode)
                        .orderByDesc(GridInspectRecordEntity::getCreatedTime);
            } else {
                con = Wrappers.lambdaQuery(GridInspectRecordEntity.class)
                        .eq(GridInspectRecordEntity::getGridManageId, gridMangeId)
                        .eq(GridInspectRecordEntity::getIsNewest, 1)
                        .orderByDesc(GridInspectRecordEntity::getCreatedTime);
            }

            List<GridInspectRecordEntity> inspectRecordEntities = gridInspectRecordMapper.selectList(con);
            if (!CollectionUtils.isEmpty(inspectRecordEntities)) {
                GridInspectRecordEntity entity = inspectRecordEntities.get(0);
                String newestGridInspectId = entity.getGridInspectId();
                List<GridInspectRecordEntity> tmpList = inspectRecordEntities.stream()
                        .filter(r -> Objects.equals(r.getGridInspectId(), newestGridInspectId))
                        .collect(Collectors.toList());
                int totalExuStatus = 0;
                for (GridInspectRecordEntity gridInspectRecordEntity : tmpList) {
                    GridOutDTO.MissionStatusDTO dto = new GridOutDTO.MissionStatusDTO();
                    dto.setMissionId(gridInspectRecordEntity.getMissionId());
                    dto.setMissionName(gridInspectRecordEntity.getMissionName());
                    dto.setGridInspectId(gridInspectRecordEntity.getGridInspectId());
                    dto.setTaskId(gridInspectRecordEntity.getTaskId());
                    Integer executeStatus = gridInspectRecordEntity.getExecuteStatus();
                    if (executeStatus == 1) {
                        totalExuStatus++;
                    }
                    dto.setExecuteStatus(executeStatus);
                    res.add(dto);
                }
                // 如果全执行了，返回都未执行
                if (totalExuStatus == tmpList.size()) {
                    res.forEach(r -> r.setExecuteStatus(0));
                }

            } else {
                Integer taskId = gridService.getTaskIdByGridManagerId(gridMangeId, orgCode);
                if (taskId != null) {
                    List<MissionEntity> missionEntities = missionService.listMissionByTaskId(taskId);
                    for (MissionEntity missionEntity : missionEntities) {
                        GridOutDTO.MissionStatusDTO dto = new GridOutDTO.MissionStatusDTO();
                        dto.setMissionId(missionEntity.getId());
                        dto.setMissionName(missionEntity.getName());
                        dto.setTaskId(taskId);
                        dto.setExecuteStatus(0);
                        res.add(dto);
                    }
                }
            }

        }
        return res;
    }

    @Override
    public void updateIsNewestInspect(List<Integer> taskIds) {
        if (!CollectionUtils.isEmpty(taskIds)) {
            gridInspectRecordMapper.batchUpdateIsNewest(taskIds);
        }
    }

    /**
     * 获取各个架次最新的照片
     */
    private List<GridOutDTO.PhotoDTO>  getLastestMissionPhoto(List<String> gridManageIds, String orgCode) {
        List<GridOutDTO.PhotoDTO> res = Lists.newArrayList();
        log.info("GridMissionServiceImpl:getLastestMissionPhoto==========>gridManageIds:" + gridManageIds);
        if (!CollectionUtils.isEmpty(gridManageIds)) {
            List<GridMissionRecordsPO> newestInpectRecords;
            // 如果有传单位，则查询指定单位对应的最新缩略图，如果没有单位则默认当前账户的单位及子单位下的最新缩略图
            if (!StringUtils.hasText(orgCode)) {
                orgCode = TrustedAccessTracerHolder.get().getOrgCode();
                // 判定管理网格最新的关联表，如果最新的关联没有航线架次，则过滤掉（每个网格只有一个最新的航线）
                List<GridOutDTO.OrgAndManageIdOutDTO> orgAndManageIdOutDTOS = filterGridManage(gridManageIds, orgCode);
                // 网格对应最新的单位
                Map<String, String> manageAndOrgMap = orgAndManageIdOutDTOS.stream().collect(Collectors.toMap(GridOutDTO.OrgAndManageIdOutDTO::getGridManageId, GridOutDTO.OrgAndManageIdOutDTO::getOrgCode));
                newestInpectRecords = gridMissionRecordMapper.selectGridMissionRecords(gridManageIds);
                Map<String, String> noteMap = Maps.newHashMap();
                if (!CollectionUtils.isEmpty(newestInpectRecords)) {
                    List<GridMissionRecordsPO> finalNewestInpectRecords = Lists.newArrayList();
                    for (GridMissionRecordsPO record : newestInpectRecords) {
                        String gridManageIdFromRecord = record.getGridManageId();
                        String orgCodeFromRecord = record.getOrgCode();
                        String orgCodeFromMap = manageAndOrgMap.get(gridManageIdFromRecord);
                        String gridInspectId = record.getGridInspectId();
                        String note = gridManageIdFromRecord + orgCodeFromRecord;
                        if (StringUtils.hasText(orgCodeFromMap) && orgCodeFromMap.equals(orgCodeFromRecord)) {
                            if (noteMap.containsKey(note)) {
                                String gridInspectIdFromMap = noteMap.get(note);
                                if (gridInspectIdFromMap.equals(gridInspectId)) {
                                    finalNewestInpectRecords.add(record);
                                }
                            } else {
                                noteMap.put(note, gridInspectId);
                                finalNewestInpectRecords.add(record);
                            }
                        }
                    }
                    newestInpectRecords = finalNewestInpectRecords;
                }

            }  else {
                newestInpectRecords = gridMissionRecordMapper.selectGridMissionRecordsWithOrgCode(gridManageIds, orgCode);
            }
            // 通过管理网格ID获取各个网格中的最新巡检记录
            log.info("GridMissionServiceImpl:getLastestMissionPhoto==========>newestInpectRecords:" + newestInpectRecords.size());
            for (GridMissionRecordsPO newestInpectRecord : newestInpectRecords) {
                Integer missionRecordsId = newestInpectRecord.getMissionRecordsId();
                if (!ObjectUtils.isEmpty(missionRecordsId)) {
                    List<GridOutDTO.Photo> photoDTOS = this.getPhotoBySimplyMissionRecordsId(missionRecordsId);
                    GridOutDTO.PhotoDTO dto = new GridOutDTO.PhotoDTO();
                    dto.setMissionRecordId(missionRecordsId);
                    dto.setMissionId(newestInpectRecord.getMissionId());
                    dto.setMissionName(newestInpectRecord.getMissionName());
                    dto.setTaskId(newestInpectRecord.getTaskId());
                    dto.setGridManageId(newestInpectRecord.getGridManageId());
                    dto.setPhotoList(photoDTOS);
                    dto.setTaskName(newestInpectRecord.getTaskName());
                    res.add(dto);

                }
            }
        }

        return res;
    }


    private GridPhotoRelQueryCriteriaInPO buildqueryHistoryCriteria(GridHistoryInDTO dto) {
        return GridPhotoRelQueryCriteriaInPO.builder()
                .gridDataId(dto.getGridDataId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();
    }

    /**
     * 架次->照片与网格关联表->网格
     */
    public List<GridOutDTO.Photo> getPhotoBySimplyMissionRecordsId(Integer missionRecordsId) {
        List<GridOutDTO.Photo> photoDTOS = Lists.newArrayList();
        LambdaQueryWrapper<GridPhotoRelEntity> condition = Wrappers.lambdaQuery(GridPhotoRelEntity.class)
                .eq(GridPhotoRelEntity::getMissionRecordsId, missionRecordsId);
        List<GridPhotoRelEntity> gridPhotoRelEntities = gridPhotoRelMapper.selectList(condition);
        if (!CollectionUtils.isEmpty(gridPhotoRelEntities)) {
            try {
                // 获取关联的照片
                List<Long> photoIds = gridPhotoRelEntities.stream().map(GridPhotoRelEntity::getPhotoId).collect(Collectors.toList());
                List<MissionPhotoEntity> photoEntities = missionPhotoService.selectBatchPhoto(photoIds);
                if (!CollectionUtils.isEmpty(photoEntities)) {
                    photoDTOS = photoEntities.stream().map(r -> gridMissionConverter.convert(r)).collect(Collectors.toList());
                    // 匹配数据网格
                    Map<Long, String> map = gridPhotoRelEntities.stream()
                            .filter(r -> r.getGridDataId() != null)
                            .collect(Collectors.toMap(GridPhotoRelEntity::getPhotoId, GridPhotoRelEntity::getGridDataId));
                    for (GridOutDTO.Photo photoDTO : photoDTOS) {
                        photoDTO.setGridDataId(map.get(photoDTO.getId()));
                        photoDTO.setPhotoUrl(URLEncoder.encode(photoDTO.getPhotoUrl(), "utf-8").replace("+", "%20"));
                        photoDTO.setThumbnailUrl(URLEncoder.encode(photoDTO.getThumbnailUrl(), "utf-8").replace("+", "%20"));
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        return photoDTOS;
    }

    /**
     * 偷个懒
     */
    public List<GridOutDTO.Photo> getAllPhotoByMissionRecordsIds(List<Integer> missionRecordsIds) {
        List<GridOutDTO.Photo> photoDTOS = Lists.newArrayList();
        LambdaQueryWrapper<GridPhotoRelEntity> condition = Wrappers.lambdaQuery(GridPhotoRelEntity.class)
                .in(GridPhotoRelEntity::getMissionRecordsId, missionRecordsIds);
        List<GridPhotoRelEntity> gridPhotoRelEntities = gridPhotoRelMapper.selectList(condition);
        if (!CollectionUtils.isEmpty(gridPhotoRelEntities)) {
            try {
                // 获取关联的照片
                List<Long> photoIds = gridPhotoRelEntities.stream().map(GridPhotoRelEntity::getPhotoId).collect(Collectors.toList());
                List<MissionPhotoEntity> photoEntities = missionPhotoService.selectBatchPhoto(photoIds);
                if (!CollectionUtils.isEmpty(photoEntities)) {
                    photoDTOS = photoEntities.stream().map(r -> gridMissionConverter.convert(r)).collect(Collectors.toList());
                    // 匹配数据网格
                    Map<Long, String> map = gridPhotoRelEntities.stream()
                            .filter(r -> r.getGridDataId() != null)
                            .collect(Collectors.toMap(GridPhotoRelEntity::getPhotoId, GridPhotoRelEntity::getGridDataId));
                    for (GridOutDTO.Photo photoDTO : photoDTOS) {
                        photoDTO.setGridDataId(map.get(photoDTO.getId()));
                        photoDTO.setPhotoUrl(URLEncoder.encode(photoDTO.getPhotoUrl(), "utf-8").replace("+", "%20"));
                        photoDTO.setThumbnailUrl(URLEncoder.encode(photoDTO.getThumbnailUrl(), "utf-8").replace("+", "%20"));
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        return photoDTOS;
    }

    private GridMissionRecordCriteriaInPO buildGridMissionRecordPageCriteria(GridMissionRecordPageInDTO dto) {
        return GridMissionRecordCriteriaInPO.builder()
                .gridManageId(dto.getGridManageId())
                .missionName(dto.getMissionName())
                .baseNestId(dto.getBaseNestId())
                .baseNestName(dto.getBaseNestName())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .orgCode(dto.getOrgCode())
                .build();
    }

    /**
     * 增加一天
     */
    private String addDay(String date) {
        if (StringUtils.isEmpty(date)) return "";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime localDateTime = LocalDate.parse(date, df).atStartOfDay().plusDays(1);
        return localDateTime.format(df);
    }


    /**
     * 获取任务名称和架次名称
     */
    private List<GridOutDTO.Photo> getMissionNameAndTaskNameToPhoto(List<GridOutDTO.Photo> pos, List<GridMissionNamePO> namePOS) {
        if (!CollectionUtils.isEmpty(pos) && !CollectionUtils.isEmpty(namePOS)) {
            Map<Integer, GridMissionNamePO> mapPOS = namePOS.stream().collect(Collectors.toMap(GridMissionNamePO::getMissionId, r -> r));
            for (GridOutDTO.Photo photo : pos) {
                GridMissionNamePO po = mapPOS.get(photo.getMissionId());
                if (!ObjectUtils.isEmpty(po)) {
                    photo.setMissionName(po.getMissionName());
                    photo.setTaskName(po.getTaskName());
                }
            }
        }
        return pos;
    }

    /**
     * 判断架次在最新的巡检记录中有没有执行
     */
    private boolean isMissionExecute(Integer missionId, List<GridInspectRecordEntity> newestInspectRecord) {
        for (GridInspectRecordEntity entity : newestInspectRecord) {
            if (Objects.equals(entity.getMissionId(), missionId) && entity.getExecuteStatus() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 新建巡检记录
     */
    private boolean saveGridInspectRecord(TaskEntity taskEntity, Integer missionRecordId, Integer inspectSeq, String gridManageId) {
        List<GridInspectRecordEntity> gridInspectRecordEntityList = Lists.newArrayList();
        String gridInspectId = BizIdUtils.snowflakeIdStr();
        List<MissionEntity> missionEntities = missionService.listMissionByTaskId(taskEntity.getId());
        MissionEntity mission = missionService.queryMissionByMissionRecordsId(missionRecordId);
        Integer missionId = mission.getId();
        for (MissionEntity missionEntity : missionEntities) {
            Integer missionIdFromTask = missionEntity.getId();
            String missionNameFromTask = missionEntity.getName();

            GridInspectRecordEntity entity = new GridInspectRecordEntity();
            entity.setGridInspectId(gridInspectId);
            entity.setGridManageId(gridManageId);
            entity.setOrgCode(taskEntity.getOrgCode());
            entity.setTaskId(taskEntity.getId());
            entity.setTaskName(taskEntity.getName());
            entity.setMissionId(missionIdFromTask);
            entity.setMissionName(missionNameFromTask);
            if (Objects.equals(missionIdFromTask, missionId)) {
                entity.setMissionRecordsId(missionRecordId);
                entity.setExecuteStatus(1);
            } else {
                entity.setExecuteStatus(0);
            }
            entity.setMissionSeq(missionEntity.getSeqId());
            entity.setInspectSeq(inspectSeq + 1);
            entity.setIsNewest(1);
            gridInspectRecordEntityList.add(entity);
        }
        return gridInspectRecordMapper.batchInsert(gridInspectRecordEntityList);
    }

    /**
     * 立即巡检记录
     */
    private boolean addGridInspect(String orgCode, TaskEntity taskEntity, Integer missionId, Integer missionRecordsId) {
        boolean isSuccess;
        String gridManageId = gridService.getGridManageByTaskId(taskEntity.getId());
        // 判断是否是最新的巡检记录
        LambdaQueryWrapper<GridInspectRecordEntity> con = Wrappers.lambdaQuery(GridInspectRecordEntity.class)
                .eq(GridInspectRecordEntity::getOrgCode, orgCode)
                .eq(GridInspectRecordEntity::getGridManageId, gridManageId)
                .eq(GridInspectRecordEntity::getIsNewest, 1)
                .orderByDesc(GridInspectRecordEntity::getCreatedTime);
        List<GridInspectRecordEntity> inspectRecordEntities = gridInspectRecordMapper.selectList(con);
        // 当前航线无巡检记录则新建巡检记录
        if (CollectionUtils.isEmpty(inspectRecordEntities)) {
            isSuccess = this.saveGridInspectRecord(taskEntity, missionRecordsId, 0, gridManageId);
        } else {
            // 获取最新的巡检记录
            GridInspectRecordEntity gridInspectRecordEntity = inspectRecordEntities.get(0);
            String gridInspectId = gridInspectRecordEntity.getGridInspectId();
            List<GridInspectRecordEntity> newestInspectRecord = inspectRecordEntities.stream().filter(r -> gridInspectId.equals(r.getGridInspectId())).collect(Collectors.toList());
            // 架次在最新的巡检记录中有执行
            if (isMissionExecute(missionId, newestInspectRecord)) {
                Integer inspectSeq = gridInspectRecordEntity.getInspectSeq();
                isSuccess = this.saveGridInspectRecord(taskEntity, missionRecordsId, inspectSeq, gridManageId);
            }
            // 架次在最新的巡检记录中没有执行
            else {
                List<GridInspectRecordEntity> collect = newestInspectRecord.stream().filter(r -> Objects.equals(r.getMissionId(), missionId)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(collect)) {
                    GridInspectRecordEntity entity = collect.get(0);
                    entity.setMissionRecordsId(missionRecordsId);
                    entity.setExecuteStatus(1);
                    int res = gridInspectRecordMapper.updateById(entity);
                    isSuccess = res != 0;
                } else {
                    isSuccess = false;
                }
            }
        }

        return isSuccess;
    }

    /**
     * 补飞操作
     */
    private boolean supplementGridInspect(String orgCode, String gridInspectId, Integer missionId, Integer missionRecordsId) {
        LambdaQueryWrapper<GridInspectRecordEntity> con = Wrappers.lambdaQuery(GridInspectRecordEntity.class)
                .eq(GridInspectRecordEntity::getOrgCode, orgCode)
                .eq(GridInspectRecordEntity::getGridInspectId, gridInspectId)
                .eq(GridInspectRecordEntity::getMissionId, missionId);
        GridInspectRecordEntity entity = gridInspectRecordMapper.selectOne(con);
        if (!ObjectUtils.isEmpty(entity)) {
            entity.setMissionRecordsId(missionRecordsId);
            entity.setExecuteStatus(1);
            int res = gridInspectRecordMapper.updateById(entity);
            return res != 0;
        }
        return false;
    }

    /**
     * 根据巡检记录获取任务照片
     */
    private List<GridOutDTO.PhotoDTO> getMissionPhotoBygridInspectId(String gridInspectId) {
        LambdaQueryWrapper<GridInspectRecordEntity> con = Wrappers.lambdaQuery(GridInspectRecordEntity.class).eq(GridInspectRecordEntity::getGridInspectId, gridInspectId);
        List<GridInspectRecordEntity> gridInspectRecordEntityList = gridInspectRecordMapper.selectList(con);
        if (CollectionUtils.isEmpty(gridInspectRecordEntityList)) {
            return null;
        }
        // 获取所有照片
        List<Integer> missionRecordIds = gridInspectRecordEntityList.stream().map(GridInspectRecordEntity::getMissionRecordsId).collect(Collectors.toList());
        List<GridOutDTO.Photo> photoDTOS = this.getAllPhotoByMissionRecordsIds(missionRecordIds);

        List<GridOutDTO.PhotoDTO> res = Lists.newArrayList();
        for (GridInspectRecordEntity entity : gridInspectRecordEntityList) {
            GridOutDTO.PhotoDTO dto = new GridOutDTO.PhotoDTO();
            Integer missionRecordsId = entity.getMissionRecordsId();
            dto.setMissionRecordId(missionRecordsId);
            dto.setMissionId(entity.getMissionId());
            dto.setMissionName(entity.getMissionName());
            dto.setTaskId(entity.getTaskId());
            dto.setGridManageId(entity.getGridManageId());
            dto.setTaskName(entity.getTaskName());
            List<GridOutDTO.Photo> tmp = photoDTOS.stream().filter(r -> Objects.equals(r.getMissionRecordsId(), missionRecordsId)).collect(Collectors.toList());
            dto.setPhotoList(tmp);
            GridDataInfoDTO gridDataInfoDTO = quryGridDataInfoDTO(entity.getMissionId());
            dto.setGridDataInfoDTO(gridDataInfoDTO);
            res.add(dto);
        }
        return res;
    }

    /**
     * 获取第一个数据网格信息
     */
    private GridDataInfoDTO quryGridDataInfoDTO(Integer missionId) {
        GridDataInfoDTO res = null;
        AirLineEntity airLineEntity = missionService.getAirLineByMissionId(missionId);
        if (!ObjectUtils.isEmpty(airLineEntity)) {
            String waypoints = airLineEntity.getWaypoints();
            UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(waypoints, UnifyAirLineFormatDto.class);
            Map<String, Object> lineConfigs = unifyAirLineFormatDto.getLineConfigs();
            if (!ObjectUtils.isEmpty(lineConfigs)) {
                Map<String, Object> grid = (Map<String, Object>) lineConfigs.get("GRID");
                if (!ObjectUtils.isEmpty(grid)) {
                    JSONArray gridDataArr = (JSONArray) grid.get("gridData");
                    if (!CollectionUtils.isEmpty(gridDataArr)) {
                        res = JSONObject.parseObject(gridDataArr.get(0).toString(), GridDataInfoDTO.class);
                    }
                }
            }
        }
        return res;
    }

    /**
     * 根据架次记录获取任务照片
     */
    private List<GridOutDTO.PhotoDTO> getMissionPhotoByMissionRecordId(String gridManageId, Integer missionRecordsId) {

        MissionEntity missionEntity = missionService.queryMissionByMissionRecordsId(missionRecordsId);
        if (Objects.isNull(missionEntity)) {
            return null;
        }

        List<GridOutDTO.Photo> photoDTOS = this.getPhotoBySimplyMissionRecordsId(missionRecordsId);

        String missionName = missionEntity.getName();
        Integer taskId = missionEntity.getTaskId();
        GridOutDTO.PhotoDTO dto = new GridOutDTO.PhotoDTO();
        List<GridOutDTO.PhotoDTO> res = new ArrayList<>();
        if (!CollectionUtils.isEmpty(photoDTOS)) {
            LambdaQueryWrapper<TaskEntity> con = Wrappers.lambdaQuery(TaskEntity.class).eq(TaskEntity::getId, taskId);
            TaskEntity taskEntity = taskMapper.selectOne(con);
            dto.setGridManageId(gridManageId);
            dto.setPhotoList(photoDTOS);
            dto.setMissionName(missionName);
            dto.setTaskName(taskEntity.getName());
            res.add(dto);
        }
        return res;
    }

    /**
     * 计算巡检记录次数
     */
    private int countInpectRecords(String gridManageId, List<GridInspectRecordEntity> entities) {
        if (!CollectionUtils.isEmpty(entities)) {
            List<GridInspectRecordEntity> collect = entities.stream().filter(r -> Objects.equals(r.getGridManageId(), gridManageId)).collect(Collectors.toList());
            return collect.size();
        }
        return 0;
    }

    /**
     * 时间转换
     */
    private String converteLocalDateTime(LocalDateTime data) {
        String dataString = data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return dataString;
    }

    /**
     * 获取照片类型
     */
    private int getPhotoType(List<MissionPhotoEntity> photoEntityList) {
        int count;
        // 用第二个航线做校验
        List<MissionPhotoEntity> collect = photoEntityList.stream().filter(r -> r.getWaypointIndex() == 1).collect(Collectors.toList());
        List<MissionPhotoEntity> collect2 = photoEntityList.stream().filter(r -> r.getWaypointIndex() == 2).collect(Collectors.toList());
        count = Math.max(collect.size(), collect2.size());
        return count;
    }

    /**
     * 照片根据经纬度匹配数据网格
     */
    private String matchGridData(Double latitude, Double longitude, List<GridOutDTO.GridDataOutDTO> gridDataList) {
        if (!CollectionUtils.isEmpty(gridDataList)) {
            for (GridOutDTO.GridDataOutDTO dto : gridDataList) {
                String gridDataId = dto.getGridDataId();
                Double east = dto.getEast();
                Double north = dto.getNorth();
                Double south = dto.getSouth();
                Double west = dto.getWest();
                if (west <= longitude && longitude < east && south <= latitude && latitude < north) {
                    return gridDataId;
                }
            }
        }
        return "";
    }

    /**
     * 每张照片添加数据网格ID
     */
    private List<GridOutDTO.Photo> addGridDataIdToPhoto(Map<String, MissionPhotoEntity> photoMap) {
        List<GridOutDTO.Photo> photoDTOS = Lists.newArrayList();
        for (Map.Entry<String, MissionPhotoEntity> entry : photoMap.entrySet()) {
            MissionPhotoEntity photo = entry.getValue();
            GridOutDTO.Photo photoed = gridMissionConverter.convert(photo);
            photoed.setGridDataId(entry.getKey());
            photoDTOS.add(photoed);
        }
        return photoDTOS;
    }

    /**
     * 网格与照片是否已关联
     */
    private List<Long> getRelPhotoListIds(List<Long> list1, List<Long> list2) {
        List<Long> tmp1 = Lists.newArrayList(list1);
        List<Long> tmp2 = Lists.newArrayList(list2);
        tmp1.removeAll(tmp2);
        return tmp1;
    }

    /**
     * 过滤区域网格
     */
    private List<GridOutDTO.OrgAndManageIdOutDTO> filterGridManage(List<String> gridManageIds, String orgCode) {
        LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                .in(GridManageOrgRelEntity::getGridManageId, gridManageIds)
                .likeRight(GridManageOrgRelEntity::getOrgCode, orgCode)
                .orderByDesc(GridManageOrgRelEntity::getModifiedTime);
        List<GridManageOrgRelEntity> gridManageOrgRelEntityList = gridManageOrgRelMapper.selectList(con);
        List<GridOutDTO.OrgAndManageIdOutDTO> res = Lists.newArrayList();
        List<String> noteList = Lists.newArrayList();
        for (GridManageOrgRelEntity entity : gridManageOrgRelEntityList) {
            String gridManageId = entity.getGridManageId();
            if (!noteList.contains(gridManageId)) {
                if (entity.getTaskId() != null) {
                    GridOutDTO.OrgAndManageIdOutDTO dto = new GridOutDTO.OrgAndManageIdOutDTO();
                    dto.setGridManageId(entity.getGridManageId());
                    dto.setOrgCode(entity.getOrgCode());
                    res.add(dto);
                }
                noteList.add(gridManageId);
            }
        }
        return res;
    }
}
