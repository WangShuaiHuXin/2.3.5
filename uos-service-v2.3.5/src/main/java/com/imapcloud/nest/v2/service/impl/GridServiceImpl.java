package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.GridData;
import com.imapcloud.nest.service.MissionService;
import com.imapcloud.nest.service.TaskService;
import com.imapcloud.nest.utils.ParseVectorFileUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.dao.entity.GridDataEntity;
import com.imapcloud.nest.v2.dao.entity.GridManageEntity;
import com.imapcloud.nest.v2.dao.entity.GridManageOrgRelEntity;
import com.imapcloud.nest.v2.dao.entity.GridRegionEntity;
import com.imapcloud.nest.v2.dao.mapper.*;
import com.imapcloud.nest.v2.dao.po.out.GridManageOrgCodeOutPO;
import com.imapcloud.nest.v2.dao.po.out.GridMissionRecordsPO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.GridMissionService;
import com.imapcloud.nest.v2.service.GridService;
import com.imapcloud.nest.v2.service.converter.GridRegionConverter;
import com.imapcloud.nest.v2.service.dto.in.GridInDTO;
import com.imapcloud.nest.v2.service.dto.in.GridManageInDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname GridRegionServiceImpl
 * @Description 网格区域实现类
 * @Date 2022/12/7 15:30
 * @Author Carnival
 */
@Slf4j
@Service
public class GridServiceImpl implements GridService {

    @Resource
    private GridRegionMapper gridRegionMapper;

    @Resource
    private GridManageMapper gridManageMapper;

    @Resource
    private GridDataMapper gridDataMapper;

    @Resource
    private GridRegionConverter gridRegionConverter;

    @Resource
    private GridMissionService gridMissionService;

    @Resource
    private TaskService taskService;

    @Resource
    private MissionService missionService;

    @Resource
    private GridManageOrgRelMapper gridManageOrgRelMapper;

    @Resource
    private GridMissionRecordMapper gridMissionRecordMapper;

    @Resource
    private UosOrgManager uosOrgManager;

    private static final Integer Fail = 0;


    @Override
    public String uploadGridRegion(MultipartFile file) {
        if (file == null) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_01.getContent()));
        }
        if (!(Objects.requireNonNull(file.getOriginalFilename()).endsWith(".json") || file.getOriginalFilename().endsWith(".kml"))) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_02.getContent()));
        }
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String result = "";
        try {
            // kml解析
            if (".kml".equals(suffixName)) {
                result = ParseVectorFileUtil.parseKmlFile(file);
            }
            // json解析
            if (".json".equals(suffixName) || ".geojson".equals(suffixName)) {
                result = ParseVectorFileUtil.parseJsonFile(file);
            }
        } catch (Exception e) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_04.getContent()));
        }

        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean saveGridRegion(GridInDTO.RegionInDTO regionInDTO) {
        // 保存区域网格
        GridRegionEntity gridRegionEntity = new GridRegionEntity();
        gridRegionEntity.setGridRegionId(BizIdUtils.snowflakeIdStr());
        gridRegionEntity.setGridRegion(regionInDTO.getGridRegion());
        gridRegionEntity.setGridRegionCoor(regionInDTO.getGridRegionCoor());
        gridRegionEntity.setSideLen(regionInDTO.getSideLen());
        gridRegionEntity.setOrgCode(regionInDTO.getOrgCode());
        gridRegionEntity.setIsSync(1);
        int resGridRegion = gridRegionMapper.insert(gridRegionEntity);

        // 保存管理网格
        List<GridInDTO.GridManageInDTO> gridManageInDTOList = regionInDTO.getGridManageList();
        List<GridManageEntity> gridManageEntityList = gridManageInDTOList.stream().map(r -> {
            GridManageEntity entity = gridRegionConverter.convert(r);
            entity.setGridManageId(BizIdUtils.snowflakeIdStr());
            entity.setGridRegionId(gridRegionEntity.getGridRegionId());
            return entity;
        }).collect(Collectors.toList());
        int resGridManager = gridManageMapper.batchInsert(gridManageEntityList);

        // 初始化管理网格和单位
        boolean bindOrgRes = bindGridManageAndOrg(gridManageEntityList, regionInDTO.getOrgCode());

        return resGridRegion != Fail && resGridManager != Fail && bindOrgRes;
    }

    @Override
    public Boolean updateGridRegion(GridInDTO.RegionInDTO regionInDTO, String gridRegionId) {
        Optional<GridRegionEntity> gridRegionById = findGridRegionById(gridRegionId);
        if (!gridRegionById.isPresent()) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_SERVICE_06.getContent()));
        }
        GridRegionEntity gridRegionEntity = gridRegionById.get();
        gridRegionEntity.setGridRegion(regionInDTO.getGridRegion());
        gridRegionEntity.setGridRegionCoor(regionInDTO.getGridRegionCoor());
        gridRegionEntity.setSideLen(regionInDTO.getSideLen());
        gridRegionEntity.setOrgCode(regionInDTO.getOrgCode());
        gridRegionEntity.setIsSync(1);
        int resGridRegion = gridRegionMapper.updateById(gridRegionEntity);

        // 删除之前管理网格
        Wrapper<GridManageEntity> conManage = Wrappers.lambdaQuery(GridManageEntity.class)
                .eq(GridManageEntity::getGridRegionId, gridRegionId);
        List<GridManageEntity> gridManageEntities = gridManageMapper.selectList(conManage);
        List<Integer> taskIds = gridManageEntities.stream().map(GridManageEntity::getTaskId).collect(Collectors.toList());
        List<Long> Ids = gridManageEntities.stream().map(GridManageEntity::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(Ids)) {
            gridManageMapper.deleteBatchIds(Ids);
        }

        // 删除之前的航线
        if (!CollectionUtils.isEmpty(taskIds)) {
            taskService.batchDeleteTask(taskIds);
        }

        // 删除管理网格与单位的绑定关系
        List<String> gridManageIds = gridManageEntities.stream().map(GridManageEntity::getGridManageId).collect(Collectors.toList());
        delGridManangeOrgRel(gridManageIds);

        // 保存新的管理网格
        List<GridInDTO.GridManageInDTO> gridManageInDTOList = regionInDTO.getGridManageList();
        List<GridManageEntity> gridManageEntityList = gridManageInDTOList.stream().map(r -> {
            GridManageEntity entity = gridRegionConverter.convert(r);
            entity.setGridManageId(BizIdUtils.snowflakeIdStr());
            entity.setGridRegionId(gridRegionEntity.getGridRegionId());
            return entity;
        }).collect(Collectors.toList());
        int resGridManager = gridManageMapper.batchInsert(gridManageEntityList);

        // 初始化管理网格和单位关系
        boolean bindOrgRes = bindGridManageAndOrg(gridManageEntityList, regionInDTO.getOrgCode());

        return resGridRegion != Fail && resGridManager != Fail && bindOrgRes;
    }

    @Override
    public List<GridOutDTO.RegionOutDTO> queryGridRegion(String _orgCode) {
        // 判断选择单位是否在账号单位下
        String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();
        if (!cheakPermisson(_orgCode, orgCodeFromAccount)) {
            return null;
        }
        List<GridOutDTO.RegionOutDTO> res = Lists.newArrayList();
        // 从关联表中获取该单位及其子单位的管理网格
        List<String> gridManages = gridManageOrgRelMapper.queryGridManage(_orgCode);
        if (CollectionUtils.isEmpty(gridManages)) {
            return null;
        }
        // 反推区域网格
        List<GridRegionEntity> gridRegionEntityList = gridRegionMapper.selectGridRegionByGridManageIds(gridManages);

        // 有区域网格的情况，按区域网格返回
        if (!CollectionUtils.isEmpty(gridRegionEntityList)) {
            // 获取管理网格的所属单位
            Map<String, List<String>> gridManageIdTOOrgCodeMap = getOrgCodeByGridManageIds(gridManages);
            // 获取管理网格的所属航线任务
            Map<String, List<Integer>> gridManageIdTOTaskMap = getTaskIdsByGridManageIds(gridManages, _orgCode);
            // 获取管理网格
            List<GridManageEntity> gridManageList = findGridManageByOrgCode(gridManages);

            for (GridRegionEntity gridRegionEntity : gridRegionEntityList) {
                GridOutDTO.RegionOutDTO dto = gridRegionConverter.convert(gridRegionEntity);
                String gridRegionId = dto.getGridRegionId();
                List<GridManageEntity> filterGridManageList = gridManageList.stream().filter(r -> r.getGridRegionId().equals(gridRegionId)).collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(filterGridManageList)) {
                    List<GridOutDTO.GridManageOutDTO> gridManageOutDTOList = filterGridManageList.stream()
                            .map(r -> {
                                String gridManageId = r.getGridManageId();
                                List<String> orgCodeFromGridManage = gridManageIdTOOrgCodeMap.get(gridManageId);
                                GridOutDTO.GridManageOutDTO convert = gridRegionConverter.convert(r);
                                // 匹配所属单位
                                convert.setOrgCodes(orgCodeFromGridManage);
                                // 设置是否有航线
                                List<Integer> taskIds = gridManageIdTOTaskMap.get(gridManageId);
                                if (!CollectionUtils.isEmpty(taskIds)) {
                                    convert.setHasRoute(true);
                                }
                                return convert;
                            }).collect(Collectors.toList());
                    dto.setGridManageList(gridManageOutDTOList);
                    res.add(dto);
                }
            }
        }
        // 如果没有区域网格的情况 (目前应该没有这种情况但代码仍保留)
        else {
            GridOutDTO.RegionOutDTO dto = new GridOutDTO.RegionOutDTO();
            dto.setOrgCode(_orgCode);
            List<GridManageOrgRelEntity> gridManageOrgRelEntityList = findGridManageIdsByOrgCode(Collections.singletonList(_orgCode));
            if (!CollectionUtils.isEmpty(gridManageOrgRelEntityList)) {
                List<String> gridManageIds = gridManageOrgRelEntityList.stream().map(GridManageOrgRelEntity::getGridManageId).collect(Collectors.toList());
                List<GridManageEntity> gridManageList = findGridManageByIds(gridManageIds);
                if (!CollectionUtils.isEmpty(gridManageList)) {
                    // 获取边界长度
                    dto.setSideLen(getSideLen(gridManageList.get(0)));
                    // 获取管理网格的所属单位
                    Map<String, List<String>> gridManageIdTOOrgCodeMap = getOrgCodeByGridManageIds(gridManageIds);
                    // 获取管理网格的所属航线任务
                    Map<String, List<Integer>> gridManageIdTOTaskMap = getTaskIdsByGridManageIds(gridManageIds, _orgCode);

                    List<GridOutDTO.GridManageOutDTO> gridManageOutDTOList = gridManageList.stream()
                            .map(r -> {
                                GridOutDTO.GridManageOutDTO convert = gridRegionConverter.convert(r);
                                String gridManageId = convert.getGridManageId();
                                List<Integer> taskIds = gridManageIdTOTaskMap.get(gridManageId);
                                // 匹配所属单位
                                convert.setOrgCodes(gridManageIdTOOrgCodeMap.get(gridManageId));
                                // 设置是否有航线
                                if (!CollectionUtils.isEmpty(taskIds)) {
                                    convert.setHasRoute(true);
                                }
                                return convert;
                            }).collect(Collectors.toList());
                    dto.setGridManageList(gridManageOutDTOList);
                }
                res.add(dto);
            }
        }
        // 匹配单位名称
        if (!CollectionUtils.isEmpty(res)) {
            List<String> orgCodes = res.stream().map(GridOutDTO.RegionOutDTO::getOrgCode).collect(Collectors.toList());
            List<OrgSimpleOutDO> orgSimpleOutDOS = uosOrgManager.listOrgInfos(orgCodes);
            if (!CollectionUtils.isEmpty(orgSimpleOutDOS)) {
                Map<String, String> map = orgSimpleOutDOS.stream()
                        .filter(r -> r.getOrgName() != null)
                        .collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
                for (GridOutDTO.RegionOutDTO dto : res) {
                    dto.setOrgName(map.get(dto.getOrgCode()));
                }
            }
        }
        return res;
    }


    @Override
    public List<GridOutDTO.GridDataBatchDTO> queryGridData(List<String> gridManageIds, String orgCode) {
        // 如果不传单位则按最新的数据网格返回
        List<GridOutDTO.GridDataBatchDTO> res;
        // 如果有传单位，则查询指定单位对应的最新数据网格，如果没有单位则默认当前账户的单位及子单位下的最新数据网格
        if (StringUtils.hasText(orgCode)) {
            res = queryGridDataWithOrgCode(gridManageIds, orgCode);
        } else {
            gridManageIds = filterGridManage(gridManageIds);
            res = queryGridDataWithoutOrgCode(gridManageIds);
        }
        return res;
    }

    private List<GridOutDTO.GridDataBatchDTO> queryGridDataWithOrgCode(List<String> gridManageIds, String orgCode) {
        LambdaQueryWrapper<GridDataEntity> conData = Wrappers.lambdaQuery(GridDataEntity.class)
                .in(GridDataEntity::getGridManageId, gridManageIds)
                .eq(GridDataEntity::getOrgCode, orgCode);
        List<GridDataEntity> gridDataEntities = gridDataMapper.selectList(conData);
        if (!CollectionUtils.isEmpty(gridDataEntities)) {
            List<GridOutDTO.GridDataOutDTO> gridDataOutDTOS = gridDataEntities.stream().map(r -> gridRegionConverter.convert(r)).collect(Collectors.toList());
            List<GridOutDTO.GridDataBatchDTO> batchOutDTOS = Lists.newArrayList();
            List<String> noteGridManageList = Lists.newArrayList();
            for (GridOutDTO.GridDataOutDTO dto : gridDataOutDTOS) {
                String gridManageId = dto.getGridManageId();
                // 在管理网格中添加数据网格
                if (noteGridManageList.contains(gridManageId)) {
                    for (GridOutDTO.GridDataBatchDTO batchOutDTO : batchOutDTOS) {
                        if (gridManageId.equals(batchOutDTO.getGridManageId())) {
                            List<GridOutDTO.GridDataOutDTO> tmp = batchOutDTO.getGridDataOutDTOS();
                            tmp.add(dto);
                            break;
                        }
                    }
                }
                // 新增管理网格
                else {
                    noteGridManageList.add(gridManageId);
                    GridOutDTO.GridDataBatchDTO batchOutDTO = new GridOutDTO.GridDataBatchDTO();
                    batchOutDTO.setGridManageId(gridManageId);
                    List<GridOutDTO.GridDataOutDTO> tmp = Lists.newArrayList();
                    tmp.add(dto);
                    batchOutDTO.setGridDataOutDTOS(tmp);
                    batchOutDTOS.add(batchOutDTO);
                }
            }
            return batchOutDTOS;
        }
        return null;
    }

    private List<GridOutDTO.GridDataBatchDTO> queryGridDataWithoutOrgCode(List<String> gridManageIds) {
        if (CollectionUtils.isEmpty(gridManageIds)) {
            return null;
        }
        String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();
        LambdaQueryWrapper<GridDataEntity> conData = Wrappers.lambdaQuery(GridDataEntity.class)
                .in(GridDataEntity::getGridManageId, gridManageIds)
                .likeRight(GridDataEntity::getOrgCode, orgCodeFromAccount)
                .orderByDesc(GridDataEntity::getCreatedTime);
        List<GridDataEntity> gridDataEntities = gridDataMapper.selectList(conData);
        if (!CollectionUtils.isEmpty(gridDataEntities)) {
            // 获取每个管理网格里最新数据网格
            List<GridOutDTO.GridDataOutDTO> gridDataOutDTOS = gridDataEntities.stream().map(r -> gridRegionConverter.convert(r)).collect(Collectors.toList());
            List<GridOutDTO.GridDataBatchDTO> batchOutDTOS = Lists.newArrayList();
            Map<String, String> noteMap = new HashMap<>();
            for (GridOutDTO.GridDataOutDTO dto : gridDataOutDTOS) {
                String gridManageId = dto.getGridManageId();
                String orgCodeFromGridData = dto.getOrgCode();
                // 在管理网格中添加数据网格
                if (noteMap.containsKey(gridManageId)) {
                    // 一个管理网格只对应一个单位，返回最新的单位的管理网格
                    if (orgCodeFromGridData.equals(noteMap.get(gridManageId))) {
                        for (GridOutDTO.GridDataBatchDTO batchOutDTO : batchOutDTOS) {
                            if (gridManageId.equals(batchOutDTO.getGridManageId()) && orgCodeFromGridData.equals(batchOutDTO.getOrgCode())) {
                                List<GridOutDTO.GridDataOutDTO> tmp = batchOutDTO.getGridDataOutDTOS();
                                tmp.add(dto);
                                break;
                            }
                        }
                    }
                }
                // 新增管理网格
                else {
                    noteMap.put(gridManageId, orgCodeFromGridData);
                    GridOutDTO.GridDataBatchDTO batchOutDTO = new GridOutDTO.GridDataBatchDTO();
                    batchOutDTO.setGridManageId(gridManageId);
                    batchOutDTO.setOrgCode(orgCodeFromGridData);
                    List<GridOutDTO.GridDataOutDTO> tmp = Lists.newArrayList();
                    tmp.add(dto);
                    batchOutDTO.setGridDataOutDTOS(tmp);
                    batchOutDTOS.add(batchOutDTO);
                }
            }
            return batchOutDTOS;
        }
        return null;
    }

    @Override
    public Boolean saveGridData(List<GridData> gridData, String girdManageId, String orgCode) {

        // 删除当前单位的管理网格中的数据网格
        Wrapper<GridDataEntity> conData = Wrappers.lambdaQuery(GridDataEntity.class)
                .eq(GridDataEntity::getGridManageId, girdManageId)
                .eq(GridDataEntity::getOrgCode, orgCode);
        gridDataMapper.delete(conData);
        log.info("#GridServiceImpl.saveGridData# orgCode:{}", orgCode);
        List<GridDataEntity> gridDataEntities = new ArrayList<>();
        int i = 0;
        for (GridData data : gridData) {
            GridDataEntity entity = new GridDataEntity();
            entity.setGridManageId(girdManageId);
            entity.setGridDataId(BizIdUtils.snowflakeIdStr());
            entity.setEast(data.getEast());
            entity.setWest(data.getWest());
            entity.setNorth(data.getNorth());
            entity.setSouth(data.getSouth());
            entity.setLine(data.getRow());
            entity.setCol(data.getColumn());
            entity.setSeq(i);
            entity.setOrgCode(orgCode);
            i++;
            gridDataEntities.add(entity);
        }
        int res = gridDataMapper.batchInsert(gridDataEntities);

        return res != Fail;
    }

    @Override
    public void setGridManage(GridManageInDTO gridManageInDTO) {
        LambdaQueryWrapper<GridManageEntity> con = Wrappers.lambdaQuery(GridManageEntity.class)
                .eq(GridManageEntity::getGridManageId, gridManageInDTO.getGridManageId());
        Optional<GridManageEntity> optional = Optional.ofNullable(gridManageMapper.selectOne(con));
        if (optional.isPresent()) {
            // 在网格——单位表更新航线信息
            LambdaQueryWrapper<GridManageOrgRelEntity> conRel = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                    .eq(GridManageOrgRelEntity::getGridManageId, gridManageInDTO.getGridManageId())
                    .eq(GridManageOrgRelEntity::getOrgCode, gridManageInDTO.getOrgCode());
            GridManageOrgRelEntity gridManageOrgRelEntity = gridManageOrgRelMapper.selectOne(conRel);
            if (!ObjectUtils.isEmpty(gridManageOrgRelEntity)) {
                gridManageOrgRelEntity.setTaskId(gridManageInDTO.getTaskId());
                gridManageOrgRelEntity.setModifiedTime(LocalDateTime.now());
                gridManageOrgRelMapper.updateById(gridManageOrgRelEntity);
            }
        }
    }

    @Override
    public String getGridBoundsByTaskId(Integer taskId) {
        LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                .eq(GridManageOrgRelEntity::getTaskId, taskId);
        Optional<GridManageOrgRelEntity> optional = Optional.ofNullable(gridManageOrgRelMapper.selectOne(con));
        if (optional.isPresent()) {
            GridManageOrgRelEntity gridManageOrgRelEntity = optional.get();
            String gridManageId = gridManageOrgRelEntity.getGridManageId();
            List<GridManageEntity> gridManageEntityList = findGridManageByIds(Collections.singletonList(gridManageId));
            if (!CollectionUtils.isEmpty(gridManageEntityList)) {
                return gridManageEntityList.get(0).getGridBounds();
            }

        }
        return null;
    }

    @Override
    public String getGridManageByTaskId(Integer taskId) {
        LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                .eq(GridManageOrgRelEntity::getTaskId, taskId);
        Optional<GridManageOrgRelEntity> optional = Optional.ofNullable(gridManageOrgRelMapper.selectOne(con));
        if (optional.isPresent()) {
            GridManageOrgRelEntity gridManageOrgRelEntity = optional.get();
            String gridManageId = gridManageOrgRelEntity.getGridManageId();
            List<GridManageEntity> gridManageEntityList = findGridManageByIds(Collections.singletonList(gridManageId));
            if (!CollectionUtils.isEmpty(gridManageEntityList)) {
                return gridManageEntityList.get(0).getGridManageId();
            }

        }
        return null;
    }

    @Override
    public void delGridManageTask(List<Integer> taskIdList) {
        if (!CollectionUtils.isEmpty(taskIdList)) {

            LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                    .in(GridManageOrgRelEntity::getTaskId, taskIdList);
            List<GridManageOrgRelEntity> gridManageOrgRelEntityList = gridManageOrgRelMapper.selectList(con);
            if (!CollectionUtils.isEmpty(gridManageOrgRelEntityList)) {
                List<GridInDTO.GridManageOrgCodeDTO> gridManageOrgCodeList = Lists.newArrayList();
                for (GridManageOrgRelEntity gridManageOrgRelEntity : gridManageOrgRelEntityList) {
                    GridInDTO.GridManageOrgCodeDTO dto = new GridInDTO.GridManageOrgCodeDTO();
                    dto.setGridManageId(gridManageOrgRelEntity.getGridManageId());
                    dto.setOrgCode(gridManageOrgRelEntity.getOrgCode());
                    gridManageOrgCodeList.add(dto);
                }

                // 删除数据网格
                delGridData(gridManageOrgCodeList);
                // 更新巡检记录
                gridMissionService.updateIsNewestInspect(taskIdList);
                // gridManageOrgRel表中删除taskId
                gridManageOrgRelMapper.batchDeleteTask(taskIdList);
            }
        }
    }

    @Override
    public String findGridIdByLngAndLat(BigDecimal lng, BigDecimal lat, Long missionRecordsId) {
        if (Objects.isNull(lng) || Objects.isNull(lat) || Objects.isNull(missionRecordsId)) {
            return null;
        }
        log.info("findGridIdByLngAndLat.lng:{}", lng);
        log.info("findGridIdByLngAndLat.lat:{}", lat);
        log.info("findGridIdByLngAndLat.missionRecordsId:{}", missionRecordsId);
        MissionEntity missionEntity = missionService.queryMissionByMissionRecordsId(missionRecordsId.intValue());
        if (Objects.isNull(missionEntity) || Objects.isNull(missionEntity.getOrgCode())) {
            return null;
        }
        String gridManageId = null;
        log.info("findGridIdByLngAndLat.orgcode:{}", missionEntity.getOrgCode());
        // 先从网格巡检记录中获取管理网格
        gridManageId = gridManageMapper.selectByTask(missionEntity.getTaskId());
        // 如果没有管理网格，有可能是其他航线产生的问题，则用经纬度匹配
        if(!StringUtils.hasText(gridManageId)) {
            List<String> gridManageIds = gridManageMapper.selectByLngAndLat(lng.doubleValue(), lat.doubleValue(), missionEntity.getOrgCode());
            if (!CollectionUtils.isEmpty(gridManageIds)) {
                return gridManageIds.get(0);
            }
        }
        return gridManageId;
    }

    @Override
    public Integer getTaskIdByGridManagerId(String gridManageId, String orgCode) {
        if (!StringUtils.hasText(orgCode)) {
            return null;
        }
        LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                .eq(GridManageOrgRelEntity::getGridManageId, gridManageId)
                .eq(GridManageOrgRelEntity::getOrgCode, orgCode);
        GridManageOrgRelEntity gridManageOrgRelEntity = gridManageOrgRelMapper.selectOne(con);
        if (gridManageOrgRelEntity == null) {
            return null;
        }
        return gridManageOrgRelEntity.getTaskId();
    }

    @Override
    public String setGridManageOrgCode(List<GridInDTO.GridManageOrgCodeDTO> list) {
        int res0 = 0, res1 = 0;
        if (CollectionUtils.isEmpty(list)) return MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_PERMISSION_03.getContent());
        // 不知道为何前端会传多个相同的对象，所以做个过滤
        List<GridInDTO.GridManageOrgCodeDTO> distList = distinctSingleOrgAndManagId(list);
        if (CollectionUtils.isEmpty(distList)) return MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_PERMISSION_03.getContent());
        // 处理子单位网格分配父单位权限的问题
        List<GridInDTO.GridManageOrgCodeDTO> finalList = distIllegalGridManage(distList);
        if (CollectionUtils.isEmpty(finalList)) return MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_PERMISSION_02.getContent());
        boolean isAllDistribute = distList.size() == finalList.size();

        // 处理已分配的网格
        List<String> gridManageIds = finalList.stream().map(GridInDTO.GridManageOrgCodeDTO::getGridManageId).collect(Collectors.toList());

        LambdaQueryWrapper<GridManageEntity> con = Wrappers.lambdaQuery(GridManageEntity.class)
                .eq(GridManageEntity::getIsReset, 1)
                .in(GridManageEntity::getGridManageId, gridManageIds);
        // 处理reset=1的网格
        List<GridManageEntity> resetGridManage = gridManageMapper.selectList(con);
        List<String> resetGridMangeIds = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(resetGridManage)) {
            resetGridMangeIds = resetGridManage.stream().map(GridManageEntity::getGridManageId).collect(Collectors.toList());
            // 在关联表找对应的数据
            List<GridManageOrgRelEntity> gridManageAndOrg = findGridManageAndOrgByIds(resetGridMangeIds);
            // 相同管理网格和单位去重，同个网格同个单位不允许多次分配的过滤
            List<GridManageOrgRelEntity> finalResetGridMangeIds = distinctOrgAndManagId(finalList, gridManageAndOrg);
            if (!CollectionUtils.isEmpty(finalResetGridMangeIds)) {
                res0 = gridManageOrgRelMapper.batchInsert(finalResetGridMangeIds);
            }
        }
        gridManageIds.removeAll(resetGridMangeIds);
        // 处理reset=0的网格
        if (!CollectionUtils.isEmpty(gridManageIds)) {
            // 设置重分配标识
            gridManageMapper.batchUpdateIsRest(gridManageIds, 1);
            // 删除原有的航线、数据网格、单位关联关系
            doDelGridManage(gridManageIds);
            // 插入新的单位信息
            List<GridManageOrgRelEntity> gridManageOrgCode = finalList.stream()
                    .filter(r -> gridManageIds.contains(r.getGridManageId()))
                    .map(r -> gridRegionConverter.convert(r))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(gridManageOrgCode)) {
                res1 = gridManageOrgRelMapper.batchInsert(gridManageOrgCode);
            }
        }
        // 返回提示语
        if (res0 != Fail || res1 != Fail) {
            if (isAllDistribute) {
                return MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_PERMISSION_01.getContent());
            } else {
                return MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_PERMISSION_02.getContent());
            }
        } else {
            return MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GRID_PERMISSION_03.getContent());
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cancelGridManageOrgCode(List<String> gridManageIds) {
        // 先找出需要重置的管理网格
        LambdaQueryWrapper<GridManageEntity> con = Wrappers.lambdaQuery(GridManageEntity.class)
                .in(GridManageEntity::getGridManageId, gridManageIds);
        List<GridManageEntity> gridManageEntityList = gridManageMapper.selectList(con);
        if (!CollectionUtils.isEmpty(gridManageEntityList)) {
            gridManageIds = gridManageEntityList.stream().map(GridManageEntity::getGridManageId).collect(Collectors.toList());
            // 删除管理网格信息
            doDelGridManage(gridManageIds);
            // 重新绑定初始单位
            gridManageMapper.batchUpdateIsRest(gridManageIds, 0);
            List<GridInDTO.GridManageOrgCodeDTO> orgByManage = findOrgAndManageByManageIds(gridManageIds);
            setGridManageOrgCode(orgByManage);
            // 重置重置标识
            gridManageMapper.batchUpdateIsRest(gridManageIds, 0);
        }
    }

    @Override
    public void delGridManage(List<String> gridManageIds) {
        LambdaQueryWrapper<GridManageEntity> con = Wrappers.lambdaQuery(GridManageEntity.class)
                .in(GridManageEntity::getGridManageId, gridManageIds);
        List<GridManageEntity> gridManageEntities = gridManageMapper.selectList(con);
        if (!CollectionUtils.isEmpty(gridManageEntities)) {
            List<Long> Ids = gridManageEntities.stream().map(GridManageEntity::getId).collect(Collectors.toList());
            List<String> gridManageIdsFromEntity = gridManageEntities.stream().map(GridManageEntity::getGridManageId).collect(Collectors.toList());
            List<String> regionList = gridManageEntities.stream().map(GridManageEntity::getGridRegionId).distinct().collect(Collectors.toList());
            // 删除网格关系信息、航线、数据网格、单位关联关系
            if (!CollectionUtils.isEmpty(gridManageIdsFromEntity)) {
                doDelGridManage(gridManageIdsFromEntity);
            }
            // 删除管理网格
            if (!CollectionUtils.isEmpty(Ids)) {
                gridManageMapper.deleteBatchIds(Ids);
            }
            // 当区域类的数据网格全部删除后，则删除区域网格
            delGridRegion(regionList);

        }
    }


    @Override
    public List<String> queryGridManageIdsByGridRegionId(String gridRegionId) {
        LambdaQueryWrapper<GridManageEntity> con = Wrappers.lambdaQuery(GridManageEntity.class)
                .eq(GridManageEntity::getGridRegionId, gridRegionId);
        List<GridManageEntity> gridManageEntityList = gridManageMapper.selectList(con);
        if (!CollectionUtils.isEmpty(gridManageEntityList)) {
            return gridManageEntityList.stream().map(GridManageEntity::getGridManageId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<GridOutDTO.OrgAndTaskOutDTO> queryOrgCodeAndTask(String gridManageId) {
        Map<String, List<String>> map = getOrgCodeByGridManageIds(Collections.singletonList(gridManageId));
        if (map.containsKey(gridManageId)) {
            List<String> orgCodes = map.get(gridManageId);
            String orgCodeFromAccount = TrustedAccessTracerHolder.get().getOrgCode();
            orgCodes = filterOrgCode(orgCodes, orgCodeFromAccount);
            if (!CollectionUtils.isEmpty(orgCodes)) {
                List<OrgSimpleOutDO> orgSimpleOutDOS = uosOrgManager.listOrgInfos(orgCodes);
                if (!CollectionUtils.isEmpty(orgSimpleOutDOS)) {
                    // 获取单位名称Map
                    Map<String, String> orgCodeAndNameMap = orgSimpleOutDOS.stream()
                            .collect(HashMap::new, (k, v) -> k.put(v.getOrgCode(), v.getOrgName()), HashMap::putAll);

                    List<GridManageOrgRelEntity> taskIdByOrgCode = findTaskIdByOrgCode(gridManageId, orgCodes);
                    // 获取基站Map
                    Map<Integer, String> taskIdAndNestIdMap = new HashMap<>();
                    if (!CollectionUtils.isEmpty(taskIdByOrgCode)) {
                        List<Integer> taskIds = taskIdByOrgCode.stream()
                                .filter(Objects::nonNull)
                                .map(GridManageOrgRelEntity::getTaskId)
                                .collect(Collectors.toList());
                        List<TaskEntity> taskEntityList = taskService.queryBatchTaskEntities(taskIds);
                        if (!CollectionUtils.isEmpty(taskEntityList)) {
                            taskIdAndNestIdMap = taskEntityList.stream().collect(Collectors.toMap(TaskEntity::getId, TaskEntity::getBaseNestId));
                        }
                    }

                    // 获取航线任务Map
                    Map<String, Integer> orgCodeAndTaskIdMap = new HashMap<>();
                    if (!CollectionUtils.isEmpty(taskIdByOrgCode)) {
                        orgCodeAndTaskIdMap = taskIdByOrgCode.stream()
                                .filter(r -> r.getTaskId() != null)
                                .collect(Collectors.toMap(GridManageOrgRelEntity::getOrgCode, GridManageOrgRelEntity::getTaskId));
                    }

                    List<GridOutDTO.OrgAndTaskOutDTO> res = Lists.newArrayList();
                    for (String orgCode : orgCodes) {
                        GridOutDTO.OrgAndTaskOutDTO dto = new GridOutDTO.OrgAndTaskOutDTO();
                        dto.setOrgCode(orgCode);
                        dto.setOrgName(orgCodeAndNameMap.get(orgCode));
                        Integer taskId = orgCodeAndTaskIdMap.get(orgCode);
                        if (!ObjectUtils.isEmpty(taskId)) {
                            dto.setTaskId(taskId);
                            dto.setNestId(taskIdAndNestIdMap.get(taskId));
                        }
                        res.add(dto);
                    }
                    return res;
                }
            }
        }
        return null;
    }


    /**
     * 根据ID查询区域网格
     */
    private Optional<GridRegionEntity> findGridRegionById(String gridRegionId) {
        if (StringUtils.hasText(gridRegionId)) {
            LambdaQueryWrapper<GridRegionEntity> con = Wrappers.lambdaQuery(GridRegionEntity.class)
                    .eq(GridRegionEntity::getGridRegionId, gridRegionId);
            return Optional.ofNullable(gridRegionMapper.selectOne(con));
        }
        return Optional.empty();
    }

    /**
     * 查找管理网格
     */
    private List<GridManageEntity> findGridManageByOrgCode(List<String> gridMangeIdsByOrgRel) {
        LambdaQueryWrapper<GridManageEntity> con = Wrappers.lambdaQuery(GridManageEntity.class)
                .in(GridManageEntity::getGridManageId, gridMangeIdsByOrgRel);
        return gridManageMapper.selectList(con);
    }

    /**
     * 查找管理网格
     */
    private List<GridManageEntity> findGridManageByIds(List<String> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            LambdaQueryWrapper<GridManageEntity> con = Wrappers.lambdaQuery(GridManageEntity.class)
                    .in(GridManageEntity::getGridManageId, ids);
            return gridManageMapper.selectList(con);
        }
        return Collections.emptyList();
    }

    /**
     * 查找管理网格——单位关联
     */
    private List<GridManageOrgRelEntity> findGridManageAndOrgByIds(List<String> gridManageIds) {
        if (!CollectionUtils.isEmpty(gridManageIds)) {
            LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                    .in(GridManageOrgRelEntity::getGridManageId, gridManageIds);
            return gridManageOrgRelMapper.selectList(con);
        }
        return Collections.emptyList();
    }

    /**
     * 在网格单位关联表查找管理网格
     */
    private List<GridManageOrgRelEntity> findGridManageIdsByOrgCode(List<String> orgCodes) {
        List<GridManageOrgRelEntity> res = Lists.newArrayList();
        for (String orgCode : orgCodes) {
            LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                    .likeRight(GridManageOrgRelEntity::getOrgCode, orgCode);
            List<GridManageOrgRelEntity> entityList = gridManageOrgRelMapper.selectList(con);
            if (!CollectionUtils.isEmpty(entityList)) {
                res.addAll(entityList);
            }
        }
        return res;
    }

    /**
     * 删除数据网格
     */
    private void delGridData(List<GridInDTO.GridManageOrgCodeDTO> gridManageOrgCodeList) {
        if (!CollectionUtils.isEmpty(gridManageOrgCodeList)) {
            gridDataMapper.batchDelete(gridManageOrgCodeList);
        }
    }

    /**
     * 判断所选单位是否属于账号单位下
     */
    private boolean cheakPermisson(String orgCode, String orgCodeFromAccount) {
        /* orgCode是否以orgCodeFromAccount开头 */
        return StringUtils.startsWithIgnoreCase(orgCode, orgCodeFromAccount);
    }

    /**
     * 初始化管理网格与单位的关系
     */
    private boolean bindGridManageAndOrg(List<GridManageEntity> gridManageEntityList, String orgCode) {
        List<GridManageOrgRelEntity> entityList = Lists.newArrayList();
        for (GridManageEntity gridManageEntity : gridManageEntityList) {
            GridManageOrgRelEntity entity = new GridManageOrgRelEntity();
            entity.setGridManageId(gridManageEntity.getGridManageId());
            entity.setOrgCode(orgCode);
            entityList.add(entity);
        }
        return gridManageOrgRelMapper.batchInsert(entityList) != Fail;
    }

    /**
     * 删除管理网格与单位的关系
     */
    private boolean delGridManangeOrgRel(List<String> gridManageIds) {
        LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                .in(GridManageOrgRelEntity::getGridManageId, gridManageIds);
        int res = gridManageOrgRelMapper.delete(con);
        return res != Fail;
    }

    /**
     * 获取管理网格的所属航线任务
     */
    private Map<String, List<Integer>> getTaskIdsByGridManageIds(List<String> gridManageIds, String orgCode) {
        Map<String, List<Integer>> res = new HashMap<>();
        if (!CollectionUtils.isEmpty(gridManageIds)) {
            LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                    .in(GridManageOrgRelEntity::getGridManageId, gridManageIds)
                    .likeRight(GridManageOrgRelEntity::getOrgCode, orgCode)
                    .isNotNull(GridManageOrgRelEntity::getTaskId);
            List<GridManageOrgRelEntity> gridManageOrgRelEntityList = gridManageOrgRelMapper.selectList(con);

            for (GridManageOrgRelEntity entity : gridManageOrgRelEntityList) {
                String gridManageId = entity.getGridManageId();
                Integer taskId = entity.getTaskId();
                if (res.containsKey(gridManageId)) {
                    List<Integer> taskIds = res.get(gridManageId);
                    if (!taskIds.contains(taskId)) {
                        taskIds.add(taskId);
                        res.put(gridManageId, taskIds);
                    }
                } else if (!ObjectUtils.isEmpty(taskId)) {
                    List<Integer> taskIds = Lists.newArrayList();
                    taskIds.add(taskId);
                    res.put(gridManageId, taskIds);
                }
            }
        }
        return res;
    }

    /**
     * 获取管理网格的所属单位
     */
    private Map<String, List<String>> getOrgCodeByGridManageIds(List<String> gridManageIds) {
        Map<String, List<String>> res = new HashMap<>();
        if (!CollectionUtils.isEmpty(gridManageIds)) {
            LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                    .in(GridManageOrgRelEntity::getGridManageId, gridManageIds);
            List<GridManageOrgRelEntity> gridManageOrgRelEntityList = gridManageOrgRelMapper.selectList(con);

            for (GridManageOrgRelEntity entity : gridManageOrgRelEntityList) {
                String gridManageId = entity.getGridManageId();
                String orgCode = entity.getOrgCode();
                if (res.containsKey(gridManageId)) {
                    List<String> orgCodes = res.get(gridManageId);
                    if (!orgCodes.contains(orgCode)) {
                        orgCodes.add(orgCode);
                        res.put(gridManageId, orgCodes);
                    }
                } else {
                    List<String> orgCodes = Lists.newArrayList();
                    orgCodes.add(orgCode);
                    res.put(gridManageId, orgCodes);
                }
            }
        }
        return res;
    }


    /**
     * 根据管理网格查找所属区域网格
     */
    @Override
    public List<GridInDTO.GridManageOrgCodeDTO> findOrgAndManageByManageIds(List<String> gridManageIds) {
        List<GridManageOrgCodeOutPO> gridManageOrgCodeOutPOList = gridManageMapper.selectOrgByManage(gridManageIds);
        if (!CollectionUtils.isEmpty(gridManageOrgCodeOutPOList)) {
            return gridManageOrgCodeOutPOList.stream().map(r -> gridRegionConverter.convert(r)).collect(Collectors.toList());

        }
        return Collections.emptyList();
    }

    /**
     * 删除航线、数据网格、单位关联关系
     */
    private void doDelGridManage(List<String> gridManageIds) {
        LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                .in(GridManageOrgRelEntity::getGridManageId, gridManageIds);

        List<GridManageOrgRelEntity> gridManageEntities = gridManageOrgRelMapper.selectList(con);
        if (!CollectionUtils.isEmpty(gridManageEntities)) {
            HashMap<Integer, String> taskAndGridManageMap = gridManageEntities.stream().
                    collect(HashMap::new, (k, v) -> k.put(v.getTaskId(), v.getGridManageId()), HashMap::putAll);
            List<Integer> taskIds = gridManageEntities.stream()
                    .map(GridManageOrgRelEntity::getTaskId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            // 删除之前的航线
            if (!CollectionUtils.isEmpty(taskIds)) {
                List<TaskEntity> taskEntities = taskService.queryBatchTaskEntities(taskIds);
                List<GridInDTO.GridManageOrgCodeDTO> gridData = Lists.newArrayList();
                for (TaskEntity taskEntity : taskEntities) {
                    GridInDTO.GridManageOrgCodeDTO dto = new GridInDTO.GridManageOrgCodeDTO();
                    dto.setGridManageId(taskAndGridManageMap.get(taskEntity.getId()));
                    dto.setOrgCode(taskEntity.getOrgCode());
                    gridData.add(dto);
                }
                // 删除数据网格
                delGridData(gridData);
                // 更新巡检记录
                gridMissionService.updateIsNewestInspect(taskIds);
                // 删除航线任务
                taskService.batchDeleteTask(taskIds);
            }

            // 删除单位关联关系
            delGridManangeOrgRel(gridManageIds);
        }
    }

    /**
     * 在网格——单位关联表上获取航线
     */
    private List<GridManageOrgRelEntity> findTaskIdByOrgCode(String gridManageId, List<String> orgCodes) {
        if (!CollectionUtils.isEmpty(orgCodes)) {
            LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                    .in(GridManageOrgRelEntity::getOrgCode, orgCodes)
                    .eq(GridManageOrgRelEntity::getGridManageId, gridManageId);
            List<GridManageOrgRelEntity> gridManageOrgRelEntityList = gridManageOrgRelMapper.selectList(con);
            if (!CollectionUtils.isEmpty(gridManageOrgRelEntityList)) {
                return gridManageOrgRelEntityList;
            }
        }
        return Collections.emptyList();
    }

    /**
     * 根据管理网格获取网格边长
     */
    private Integer getSideLen(GridManageEntity gridManageEntity) {
        String gridRegionId = gridManageEntity.getGridRegionId();
        LambdaQueryWrapper<GridRegionEntity> con = Wrappers.lambdaQuery(GridRegionEntity.class)
                .eq(GridRegionEntity::getGridRegionId, gridRegionId);
        GridRegionEntity gridRegionEntity = gridRegionMapper.selectOne(con);
        if (!ObjectUtils.isEmpty(gridRegionEntity)) {
            return gridRegionEntity.getSideLen();
        }
        return 0;
    }

    /**
     * 当区域类的数据网格全部删除后，则删除区域网格
     */
    private void delGridRegion(List<String> regionList) {
        List<String> delGridRegionIds = Lists.newArrayList();
        for (String gridRegionId : regionList) {
            LambdaQueryWrapper<GridManageEntity> con = Wrappers.lambdaQuery(GridManageEntity.class)
                    .eq(GridManageEntity::getGridRegionId, gridRegionId);
            List<GridManageEntity> gridManageEntityList = gridManageMapper.selectList(con);
            if (CollectionUtils.isEmpty(gridManageEntityList)) {
                delGridRegionIds.add(gridRegionId);
            }
        }
        if (!CollectionUtils.isEmpty(delGridRegionIds)) {
            LambdaQueryWrapper<GridRegionEntity> con = Wrappers.lambdaQuery(GridRegionEntity.class)
                    .in(GridRegionEntity::getGridRegionId, delGridRegionIds);
            gridRegionMapper.delete(con);
        }
    }

    /**
     * TODO 后续优化
     * 相同管理网格和单位去重
     */
    private List<GridManageOrgRelEntity> distinctOrgAndManagId(List<GridInDTO.GridManageOrgCodeDTO> list, List<GridManageOrgRelEntity> gridManageAndOrg) {
        List<GridManageOrgRelEntity> finalResetGridMangeIds = Lists.newArrayList();
        for (GridInDTO.GridManageOrgCodeDTO dto : list) {
            boolean isAdd = true;
            String gridManageId = dto.getGridManageId();
            String orgCode = dto.getOrgCode();
            for (GridManageOrgRelEntity entity : gridManageAndOrg) {
                if (gridManageId.equals(entity.getGridManageId()) && orgCode.equals(entity.getOrgCode())) {
                    isAdd = false;
                    break;
                }
            }
            if (isAdd) {
                finalResetGridMangeIds.add(gridRegionConverter.convert(dto));
            }
        }
        return finalResetGridMangeIds;
    }

    /**
     * TODO 后续优化
     * 每个管理网格只返回一个数据网格
     */
    private List<GridDataEntity> distinctGridData(List<GridDataEntity> gridDataEntities, List<GridMissionRecordsPO> newestInpectRecords) {
        if (CollectionUtils.isEmpty(newestInpectRecords)) {
            return gridDataEntities;
        }
        List<GridDataEntity> res = Lists.newArrayList();
        for (GridMissionRecordsPO newestInpectRecord : newestInpectRecords) {
            String gridManageId = newestInpectRecord.getGridManageId();
            String orgCode = newestInpectRecord.getOrgCode();
            for (GridDataEntity gridDataEntity : gridDataEntities) {
                if (Objects.equals(gridDataEntity.getGridManageId(), gridManageId) && !Objects.equals(gridDataEntity.getOrgCode(), orgCode)) {
                    continue;
                }
                res.add(gridDataEntity);
            }
        }
        return res;
    }

    /**
     * 去重
     */
    private List<GridInDTO.GridManageOrgCodeDTO> distinctSingleOrgAndManagId(List<GridInDTO.GridManageOrgCodeDTO> list) {
        List<GridInDTO.GridManageOrgCodeDTO> finalResetGridMangeIds = Lists.newArrayList();
        for (GridInDTO.GridManageOrgCodeDTO dto : list) {
            boolean isAdd = true;
            String gridManageId = dto.getGridManageId();
            String orgCode = dto.getOrgCode();
            for (GridInDTO.GridManageOrgCodeDTO entity : finalResetGridMangeIds) {
                if (gridManageId.equals(entity.getGridManageId()) && orgCode.equals(entity.getOrgCode())) {
                    isAdd = false;
                    break;
                }
            }
            if (isAdd) {
                finalResetGridMangeIds.add(dto);
            }
        }
        return finalResetGridMangeIds;
    }

    /**
     * 过滤父级单位
     */
    private List<String> filterOrgCode(List<String> orgCodes, String orgCodeFromAccount) {
        List<String> res = Lists.newArrayList();
        for (String orgCode : orgCodes) {
            if (cheakPermisson(orgCode, orgCodeFromAccount)) {
                res.add(orgCode);
            }
        }
        return res;
    }

    /**
     * 过滤区域网格
     */
    private List<String> filterGridManage(List<String> gridManageIds) {
        LambdaQueryWrapper<GridManageOrgRelEntity> con = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                .in(GridManageOrgRelEntity::getGridManageId, gridManageIds)
                .orderByDesc(GridManageOrgRelEntity::getModifiedTime);
        List<GridManageOrgRelEntity> gridManageOrgRelEntityList = gridManageOrgRelMapper.selectList(con);
        List<String> res = Lists.newArrayList();
        List<String> noteList = Lists.newArrayList();
        for (GridManageOrgRelEntity entity : gridManageOrgRelEntityList) {
            String gridManageId = entity.getGridManageId();
            if (!noteList.contains(gridManageId)) {
                if (entity.getTaskId() != null) {
                    res.add(gridManageId);
                }
                noteList.add(gridManageId);
            }
        }
        return res;
    }

    /**
     * 子级单位不允许赋予父级单位权限过滤
     */
    private List<GridInDTO.GridManageOrgCodeDTO> distIllegalGridManage(List<GridInDTO.GridManageOrgCodeDTO> list) {
        List<String> griManageIds = list.stream().map(GridInDTO.GridManageOrgCodeDTO::getGridManageId).collect(Collectors.toList());
        List<GridInDTO.GridManageOrgCodeDTO> orgAndManageByManageIds = findOrgAndManageByManageIds(griManageIds);
        HashMap<String, String> manageIdTOOrgMap = orgAndManageByManageIds.stream()
                .collect(HashMap::new, (k, v) -> k.put(v.getGridManageId(), v.getOrgCode()), HashMap::putAll);
        HashMap<String, String> distributeMap = list.stream()
                .collect(HashMap::new, (k, v) -> k.put(v.getGridManageId(), v.getOrgCode()), HashMap::putAll);
        List<GridInDTO.GridManageOrgCodeDTO> res = Lists.newArrayList();
        for (GridInDTO.GridManageOrgCodeDTO dto : list) {
            String gridManageId = dto.getGridManageId();
            String originOrgCode = manageIdTOOrgMap.get(gridManageId);
            String distributeMapOrgCode = distributeMap.get(gridManageId);
            if(cheakPermisson(distributeMapOrgCode, originOrgCode)) {
                res.add(dto);
            }
        }
        return res;
    }

}
