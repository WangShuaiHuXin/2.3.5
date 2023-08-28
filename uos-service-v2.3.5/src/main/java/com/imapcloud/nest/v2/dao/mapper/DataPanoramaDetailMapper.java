package com.imapcloud.nest.v2.dao.mapper;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.DataPanoramaDetailEntity;
import com.imapcloud.nest.v2.dao.po.in.DataPanoramaDetailCriteriaPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaDetailMapper.java
 * @Description DataPanoramaDetailMapper
 * @createTime 2022年07月13日 15:01:00
 */
@Mapper
public interface DataPanoramaDetailMapper extends BaseBatchMapper<DataPanoramaDetailEntity>, IPageMapper<DataPanoramaDetailEntity, DataPanoramaDetailCriteriaPO, PagingRestrictDo> {

    /**
     * 新增
     * @author double
     * @date 2022/09/29
     **/
    @Override
    int insert(DataPanoramaDetailEntity dataPanoramaDetail);

    /**
     * 刪除
     * @author double
     * @date 2022/09/29
     **/
    int deleteOne(int id);

    /**
     * 更新
     * @author double
     * @date 2022/09/29
     **/
    int updateDetail(DataPanoramaDetailEntity dataPanoramaDetail);
}
