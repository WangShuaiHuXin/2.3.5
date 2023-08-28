package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultGroupEntity;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisResultGroupInPO;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisTraceSpacetimeInPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisTraceSpacetimeOutPO;
import com.imapcloud.nest.v2.manager.dataobj.in.DataAnalysisHisResultInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisResultGroupOutDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * 问题组
 *
 * @author boluo
 * @date 2022-10-11
 */
@Mapper
public interface DataAnalysisResultGroupMapper extends BaseMapper<DataAnalysisResultGroupEntity>
        , IPageMapper<DataAnalysisResultGroupEntity, DataAnalysisResultGroupInPO, PagingRestrictDo> {

    /**
     * 获取标注图片
     */
    List<HashMap<String, String>> getResultImages(String resultGroupId);

    /**
     * 计算发现次数
     */
    Integer countFoundNums(String resultGroupId);

    /**
     * 获取相同航线架次的照片数据
     */
    List<DataAnalysisCenterDetailEntity> getSameMissonPhoto(@Param("criteria") DataAnalysisTraceSpacetimeInPO po);


    void deleteGroupByIds(@Param("ids") List<String> ids);

    void saveBatch(@Param("list") List<DataAnalysisResultGroupEntity> dataAnalysisResultGroupEntities);

    List<DataAnalysisResultGroupOutDO> queryHisResultByCondition(DataAnalysisHisResultInDO condition);

    List<DataAnalysisResultGroupEntity> selectAllByCondition(@Param("criteria") DataAnalysisResultGroupInPO po);

    /**
     * 根据问题组获取全部的问题照片ID
     */
    List<DataAnalysisTraceSpacetimeOutPO> selectPhotoByResultGroupId(@Param("criteria") DataAnalysisTraceSpacetimeInPO po);

    /**
     * 获取同类问题的问题照片
     */
    List<DataAnalysisTraceSpacetimeOutPO> selectPhotoByTopicProblemId(@Param("criteria") DataAnalysisTraceSpacetimeInPO po);

    /**
     * 批量更新
     *
     * @param dataAnalysisResultGroupEntityList 数据分析结果组实体列表
     * @return int
     */
    int batchUpdate(@Param("entityList") List<DataAnalysisResultGroupEntity> dataAnalysisResultGroupEntityList);

    List<DataAnalysisCenterDetailEntity> selectAllPhoto(@Param("ids") List<Long> ids);

    List<DataAnalysisResultGroupEntity> selectExportList(@Param("ids") List<String> ids);

    void batchUpdateGridManage(@Param("entityList") List<DataAnalysisResultGroupEntity> entityList);
}
