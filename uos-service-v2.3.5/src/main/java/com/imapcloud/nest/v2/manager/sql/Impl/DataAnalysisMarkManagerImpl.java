package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisDetailMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisMarkMergeOutDO;
import com.imapcloud.nest.v2.manager.sql.DataAnalysisMarkManager;
import com.imapcloud.nest.v2.manager.sql.DataAnalysisMarkMergeManager;
import com.imapcloud.nest.v2.service.DataAnalysisMarkService;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataAnalysisMarkManagerImpl implements DataAnalysisMarkManager {

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private DataAnalysisResultMapper dataAnalysisResultMapper;

    @Resource
    private DataAnalysisMarkMergeManager dataAnalysisMarkMergeManager;

    @Resource
    private DataAnalysisDetailMapper dataAnalysisDetailMapper;

    @Override
    public void updateMarkIdByIds(List<Long> markIds) {
        LambdaUpdateWrapper<DataAnalysisMarkEntity> updateWrapper = Wrappers.<DataAnalysisMarkEntity>lambdaUpdate()
                .in(DataAnalysisMarkEntity::getMarkId, markIds)
                .eq(DataAnalysisMarkEntity::getDeleted, false)
                .set(DataAnalysisMarkEntity::getAddr, null)
                .set(DataAnalysisMarkEntity::getAddrImagePath, null);
        dataAnalysisMarkMapper.update(null, updateWrapper);
    }

    @Override
    public void updateMarkIdByGroupIds(List<String> groupIdList) {
        //执行删除的问题组，需要将和问题组关联的mark的add 及addrImagepath置为null  且更新最后操作时间
        List<DataAnalysisMarkMergeOutDO> dataAnalysisMarkMergeOutDOList = dataAnalysisMarkMergeManager.selectAllListByGroupIdList(groupIdList);
        //查询对应删除的分组内的result结果对应的mark_id (已核实的不删除addr信息)
        List<DataAnalysisResultEntity> dataAnalysisResultEntities = dataAnalysisResultMapper.selectAllByResultGroupId(groupIdList);
        Set<Long> collect = dataAnalysisResultEntities.stream().map(DataAnalysisResultEntity::getMarkId).collect(Collectors.toSet());

        if (CollectionUtil.isNotEmpty(dataAnalysisMarkMergeOutDOList)) {
            List<Long> markIds = dataAnalysisMarkMergeOutDOList.stream().map(DataAnalysisMarkMergeOutDO::getMarkId).collect(Collectors.toList());
            LambdaQueryWrapper<DataAnalysisMarkEntity> queryWrapper = Wrappers.<DataAnalysisMarkEntity>lambdaQuery()
                    .in(DataAnalysisMarkEntity::getMarkId, markIds)
                    .notIn(DataAnalysisMarkEntity::getMarkId, collect)
                    .eq(DataAnalysisMarkEntity::getDeleted, false);
            //需要更新重置的markId
            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = dataAnalysisMarkMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(dataAnalysisMarkEntities)) {
                Set<Long> photoSet = dataAnalysisMarkEntities.stream().map(DataAnalysisMarkEntity::getPhotoId).collect(Collectors.toSet());
                //根据photoSet查找原有图片数据
                List<DataAnalysisCenterDetailEntity> detailEntities = dataAnalysisDetailMapper.selectList(Wrappers.<DataAnalysisCenterDetailEntity>lambdaQuery()
                        .in(DataAnalysisCenterDetailEntity::getPhotoId, photoSet));
                Map<Long, DataAnalysisCenterDetailEntity> photoMap = detailEntities.stream().collect(Collectors.toMap(DataAnalysisCenterDetailEntity::getPhotoId, q -> q));
                for (DataAnalysisMarkEntity dataAnalysisMarkEntity : dataAnalysisMarkEntities) {
                    LambdaUpdateWrapper<DataAnalysisMarkEntity> updateWrapper = Wrappers.<DataAnalysisMarkEntity>lambdaUpdate()
                            .eq(DataAnalysisMarkEntity::getMarkId, dataAnalysisMarkEntity.getMarkId())
                            .eq(DataAnalysisMarkEntity::getDeleted, false)
                            .set(DataAnalysisMarkEntity::getAddr, null)
                            .set(DataAnalysisMarkEntity::getAddrImagePath, null);
                    if (photoMap.get(dataAnalysisMarkEntity.getPhotoId()) != null) {
                        //根据result的photoID查找到原来图片的经纬度
                        updateWrapper.set(DataAnalysisMarkEntity::getLongitude, photoMap.get(dataAnalysisMarkEntity.getPhotoId()).getLongitude());
                        updateWrapper.set(DataAnalysisMarkEntity::getLatitude, photoMap.get(dataAnalysisMarkEntity.getPhotoId()).getLatitude());
                    }
                    dataAnalysisMarkMapper.update(null, updateWrapper);
                }
            }
        }
    }
}
