package com.imapcloud.nest.v2.manager.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultGroupEntity;
import com.imapcloud.nest.v2.dao.entity.GridManageEntity;
import com.imapcloud.nest.v2.dao.entity.GridRegionEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultGroupMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultMapper;
import com.imapcloud.nest.v2.dao.mapper.GridManageMapper;
import com.imapcloud.nest.v2.dao.mapper.GridRegionMapper;
import com.imapcloud.nest.v2.manager.dataobj.GridManageDO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname GridProblemRelProcessor
 * @Description 网格与发现问题关联
 * @Date 2022/12/30 18:12
 * @Author Carnival
 */
@Component
public class GridProblemRelProcessor implements BasicProcessor {

    @Resource
    private GridRegionMapper gridRegionMapper;
    @Resource
    private GridManageMapper gridManageMapper;
    @Resource
    private DataAnalysisResultGroupMapper dataAnalysisResultGroupMapper;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        OmsLogger omsLogger = context.getOmsLogger();
        omsLogger.info("GridProblemRelProcessor start to process, current JobParams is {}.", context.getJobParams());

        // 根据标准获取需要同步的区域网格
        LambdaQueryWrapper<GridRegionEntity> conRegion = Wrappers.lambdaQuery(GridRegionEntity.class).eq(GridRegionEntity::getIsSync, 1);
        List<GridRegionEntity> gridRegionEntityList = gridRegionMapper.selectList(conRegion);
        if (!CollectionUtils.isEmpty(gridRegionEntityList)) {
            for (GridRegionEntity gridRegionEntity : gridRegionEntityList) {
                String gridRegionId = gridRegionEntity.getGridRegionId();
                String orgCode = gridRegionEntity.getOrgCode();
                LambdaQueryWrapper<GridManageEntity> conManage = Wrappers.lambdaQuery(GridManageEntity.class).eq(GridManageEntity::getGridRegionId, gridRegionId);
                // 获取需要同步的管理网格
                List<GridManageEntity> gridManageEntityList = gridManageMapper.selectList(conManage);
                // 收集网格经纬度
                List<GridManageDO> gridManageList = gridManageEntityList.stream().map(r -> {
                    GridManageDO gridManageDO = new GridManageDO();
                    gridManageDO.setGridManageId(r.getGridManageId());
                    gridManageDO.setWest(r.getWest());
                    gridManageDO.setEast(r.getEast());
                    gridManageDO.setNorth(r.getNorth());
                    gridManageDO.setSouth(r.getSouth());
                    return gridManageDO;
                }).collect(Collectors.toList());

                // 获取需要统计的问题
                LambdaQueryWrapper<DataAnalysisResultGroupEntity> conResult = Wrappers.lambdaQuery(DataAnalysisResultGroupEntity.class).eq(DataAnalysisResultGroupEntity::getOrgCode, orgCode);
                List<DataAnalysisResultGroupEntity> resultEntityList = dataAnalysisResultGroupMapper.selectList(conResult);
                // 问题根据经纬度匹配网格
                resultEntityList.forEach(r -> {
                    Double longitude = r.getLongitude();
                    Double latitude = r.getLatitude();
                    Double east, north, south, west;
                    r.setGridManageId(null);
                    for (GridManageDO gridManageDO : gridManageList) {
                        String gridManageId = gridManageDO.getGridManageId();
                        east = gridManageDO.getEast();
                        north = gridManageDO.getNorth();
                        south = gridManageDO.getSouth();
                        west = gridManageDO.getWest();
                        if (west <= longitude && longitude < east && south <= latitude && latitude < north) {
                            r.setGridManageId(gridManageId);
                            break;
                        }
                    }
                });
                if (!CollectionUtils.isEmpty(resultEntityList)) {
                    dataAnalysisResultGroupMapper.batchUpdateGridManage(resultEntityList);
                }
            }
        }
        // 消除标记
        gridRegionEntityList.forEach(r -> {
            r.setIsSync(0);
            gridRegionMapper.updateById(r);
        });


        return new ProcessResult(true, "问题配置网格成功");
    }
}
