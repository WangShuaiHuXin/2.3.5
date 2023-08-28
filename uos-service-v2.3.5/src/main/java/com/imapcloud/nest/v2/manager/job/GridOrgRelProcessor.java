package com.imapcloud.nest.v2.manager.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.GridDataEntity;
import com.imapcloud.nest.v2.dao.entity.GridManageEntity;
import com.imapcloud.nest.v2.dao.entity.GridManageOrgRelEntity;
import com.imapcloud.nest.v2.dao.entity.GridRegionEntity;
import com.imapcloud.nest.v2.dao.mapper.GridDataMapper;
import com.imapcloud.nest.v2.dao.mapper.GridManageMapper;
import com.imapcloud.nest.v2.dao.mapper.GridManageOrgRelMapper;
import com.imapcloud.nest.v2.dao.mapper.GridRegionMapper;
import com.imapcloud.nest.v2.service.GridService;
import com.imapcloud.nest.v2.service.dto.in.GridInDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Classname GridOrgRelProcessor
 * @Description 管理网格与单位关联调度脚本
 * @Date 2023/4/21 17:57
 * @Author Carnival
 */
@Component
public class GridOrgRelProcessor implements BasicProcessor {

    @Resource
    private GridRegionMapper gridRegionMapper;
    
    @Resource
    private GridManageMapper gridManageMapper;

    @Resource
    private GridManageOrgRelMapper gridManageOrgRelMapper;

    @Resource
    private GridService gridService;

    @Resource
    private GridDataMapper gridDataMapper;

    @Override
    @Transactional
    public ProcessResult process(TaskContext context) throws Exception {

        OmsLogger omsLogger = context.getOmsLogger();
        omsLogger.info("网格单位关联调度任务启动： {}.", context.getJobParams());
        // ----------------------- 单位与网格关联关系 -----------------------
        saveOrgAndManage();

        // ----------------------- 刷Task到关联表 -----------------------
        saveTaskToOrgAndManage();

        // ----------------------- 刷数据网格单位 -----------------------
        saveGridData();


        return new ProcessResult(true, "网格单位关联成功");
    }

    /**
     * 单位与网格关联关系
     */
    public void saveOrgAndManage() {
        // 获取区域网格
        LambdaQueryWrapper<GridRegionEntity> conRegion = Wrappers.lambdaQuery(GridRegionEntity.class);
        List<GridRegionEntity> gridRegionEntityList = gridRegionMapper.selectList(conRegion);
        List<String> orgCodeFromGirdRegion = gridRegionEntityList.stream()
                .map(GridRegionEntity::getOrgCode)
                .distinct()
                .collect(Collectors.toList());
        // 获取网格与单位关联关系
        LambdaQueryWrapper<GridManageOrgRelEntity> conGMOR = Wrappers.lambdaQuery(GridManageOrgRelEntity.class);
        List<GridManageOrgRelEntity> gridManageOrgRelEntityList = gridManageOrgRelMapper.selectList(conGMOR);
        List<String> orgCodeFromGMOR = gridManageOrgRelEntityList.stream()
                .map(GridManageOrgRelEntity::getOrgCode)
                .distinct()
                .collect(Collectors.toList());
        // 去除已初始化的网格
        orgCodeFromGirdRegion.removeAll(orgCodeFromGMOR);
        // 建立初始化关联
        List<GridRegionEntity> resGridRegion = gridRegionEntityList.stream().filter(r -> orgCodeFromGirdRegion.contains(r.getOrgCode())).collect(Collectors.toList());
        for (GridRegionEntity gridRegionEntity : resGridRegion) {
            String orgCode = gridRegionEntity.getOrgCode();
            List<String> gridManageIds = gridService.queryGridManageIdsByGridRegionId(gridRegionEntity.getGridRegionId());
            List<GridManageOrgRelEntity> list = Lists.newArrayList();
            for (String gridManageId : gridManageIds) {
                GridManageOrgRelEntity dto = new GridManageOrgRelEntity();
                dto.setGridManageId(gridManageId);
                dto.setOrgCode(orgCode);
                list.add(dto);
            }
            if (!CollectionUtils.isEmpty(list)) {
                gridManageOrgRelMapper.batchInsert(list);
            }
        }
    }

    /**
     * 刷Task到关联表
     */
    private void saveTaskToOrgAndManage() {
        LambdaQueryWrapper<GridManageEntity> conManage = Wrappers.lambdaQuery(GridManageEntity.class)
                .isNotNull(GridManageEntity::getTaskId);
        List<GridManageEntity> gridManageEntityList = gridManageMapper.selectList(conManage);
        List<String> gridManageIds = gridManageEntityList.stream().map(GridManageEntity::getGridManageId).collect(Collectors.toList());
        HashMap<String, Integer> manageAndTaskMap = gridManageEntityList.stream().collect(HashMap::new, (k, v) -> k.put(v.getGridManageId(), v.getTaskId()), HashMap::putAll);
        LambdaQueryWrapper<GridManageOrgRelEntity> conGMOR = Wrappers.lambdaQuery(GridManageOrgRelEntity.class)
                .in(GridManageOrgRelEntity::getGridManageId, gridManageIds);
        List<GridManageOrgRelEntity> gridManageOrgRelEntityList = gridManageOrgRelMapper.selectList(conGMOR);
        for (GridManageOrgRelEntity entity : gridManageOrgRelEntityList) {
            entity.setTaskId(manageAndTaskMap.get(entity.getGridManageId()));
        }
        if (!CollectionUtils.isEmpty(gridManageOrgRelEntityList)) {
            gridManageOrgRelMapper.batchUpdateTaskId(gridManageOrgRelEntityList);
        }
    }

    /**
     * 刷数据网格单位
     */
    private void saveGridData() {
        LambdaQueryWrapper<GridDataEntity> conData = Wrappers.lambdaQuery(GridDataEntity.class);
        List<GridDataEntity> gridDataEntityList = gridDataMapper.selectList(conData);
        if (!CollectionUtils.isEmpty(gridDataEntityList)) {
            List<String> gridManageIds = gridDataEntityList.stream().map(GridDataEntity::getGridManageId).collect(Collectors.toList());
            List<GridInDTO.GridManageOrgCodeDTO> orgAndManages = gridService.findOrgAndManageByManageIds(gridManageIds);
            Map<String, String> manageAndOrgMap = orgAndManages.stream()
                    .collect(HashMap::new, (k, v) -> k.put(v.getGridManageId(), v.getOrgCode()),HashMap::putAll);
            for (GridDataEntity entity : gridDataEntityList) {
                entity.setOrgCode(manageAndOrgMap.get(entity.getGridManageId()));
            }
        }
        if (!CollectionUtils.isEmpty(gridDataEntityList)) {
            gridDataMapper.batchUpdateOrgCode(gridDataEntityList);
        }
    }
}
