package com.imapcloud.nest.v2.dao.mapper;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisDetailQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisDetailSumInPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkNumOutPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisStateSumOutPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisCenterDetailMapper.java
 * @Description DataAnalysisDetailMapper
 * @createTime 2022年07月13日 15:02:00
 */
@Mapper
public interface DataAnalysisDetailMapper extends BaseBatchMapper<DataAnalysisCenterDetailEntity>, IPageMapper<DataAnalysisCenterDetailEntity, DataAnalysisDetailQueryCriteriaPO, PagingRestrictDo> {

    /**
     *  countStateSum
     * @param dataAnalysisDetailSumInPO
     * @return
     */
    public DataAnalysisStateSumOutPO countStateSum(DataAnalysisDetailSumInPO dataAnalysisDetailSumInPO);

    /**
     *  countStateSumByDate
     * @param dataAnalysisDetailSumInPO
     * @return
     */
    public DataAnalysisStateSumOutPO countStateSumByDate(DataAnalysisDetailSumInPO dataAnalysisDetailSumInPO);

    /**
     *  queryByDate
     * @param dataAnalysisDetailSumInPO
     * @return
     */
    public List<DataAnalysisCenterDetailEntity> queryByCondition(@Param("detailPO") DataAnalysisDetailSumInPO dataAnalysisDetailSumInPO);


    /**
     * 根据明细数据查询已删除内容
     * @param detailIds
     * @return
     */
    public List<DataAnalysisCenterDetailEntity> queryDeleteDataById(@Param("detailIds") List<Long> detailIds);

    /**
     *  查询历史照片
     * @param taskId
     * @param upDistinct
     * @param downDistinct
     * @param leftDistinct
     * @param rightDistinct
     * @return
     */
    public List<DataAnalysisCenterDetailEntity> queryHistoryPic(@Param("taskId") Long taskId
            , @Param("upDistinct") Integer upDistinct
            ,@Param("downDistinct") Integer downDistinct
            ,@Param("leftDistinct") Integer leftDistinct
            ,@Param("rightDistinct") Integer rightDistinct);

    /**
     * 查询Detail下是否包含mark记录
     * @param markIds
     * @return
     */
    public List<DataAnalysisDetailMarkNumOutPO> queryDetailMarkNum(@Param("markIds") List<Long> markIds);

    /**
     * 查询范围内的所有架次记录信息
     *
     * @param queryCriteriaPO 查询条件阿宝
     * @return {@link List}<{@link DataAnalysisCenterDetailEntity}>
     */
    List<DataAnalysisCenterDetailEntity> queryMissionRecordInfo(@Param("criteria") DataAnalysisDetailQueryCriteriaPO queryCriteriaPO);
}
