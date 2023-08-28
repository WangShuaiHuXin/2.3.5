package com.imapcloud.nest.v2.dao.mapper;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisMarkQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisMarkUpdateInPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkOutPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisMarkMapper.java
 * @Description DataAnalysisMarkMapper
 * @createTime 2022年07月13日 15:02:00
 */
@Mapper
public interface DataAnalysisMarkMapper extends BaseBatchMapper<DataAnalysisMarkEntity>, IPageMapper<DataAnalysisMarkEntity, DataAnalysisMarkQueryCriteriaPO, PagingRestrictDo> {

    /**
     * 根据MarkId数据查询已删除内容
     * @param markIds
     * @return
     */
    public List<DataAnalysisMarkEntity> queryDeleteDataById(@Param("markIds") List<Long> markIds);

    /**
     * 根据MarkId数据查询已删除内容
     * @param details
     * @return
     */
    public List<DataAnalysisMarkEntity> queryDeleteDataByDetailId(@Param("details") List<Long> details);

    /**
     * 根据detailIds查询对应的mark跟detail部分数据
     * @param detailsIds
     * @return
     */
    public List<DataAnalysisDetailMarkOutPO> queryDetailMark(@Param("detailsIds") List<Long> detailsIds);

    /**
     * 根据detailIds查询对应的mark跟detail部分数据
     * @param markQueryCriteriaPO
     * @return
     */
    public List<DataAnalysisDetailMarkOutPO> queryMarks(@Param("markQueryCriteriaPO") DataAnalysisMarkQueryCriteriaPO markQueryCriteriaPO);

    /**
     * 更新
     * @author double
     * @date 2022/07/23
     **/
    int updateEntity(DataAnalysisMarkEntity dataAnalysisMark);


    /**
     * 查询mark没有生成mark数据
     * @return
     */
    List<DataAnalysisDetailMarkOutPO> queryDetailMarkForSinglePic();

    /**
     * 查询没有生成result的数据
     * @return
     */
    List<DataAnalysisDetailMarkOutPO> queryDetailMarkForResult();

    /**
     * 更新
     */
    int updateMarkByMarkId(DataAnalysisMarkUpdateInPO inPo);
}
