package com.imapcloud.nest.v2.dao.mapper;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.DataPanoramaPointEntity;
import com.imapcloud.nest.v2.dao.po.in.DataPanoramaPointCriteriaPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointMapper.java
 * @Description DataPanoramaPointMapper
 * @createTime 2022年07月13日 15:01:00
 */
@Mapper
public interface DataPanoramaPointMapper extends BaseBatchMapper<DataPanoramaPointEntity>, IPageMapper<DataPanoramaPointEntity, DataPanoramaPointCriteriaPO, PagingRestrictDo> {

    /**
     * 新增
     * @author double
     * @date 2022/09/29
     **/
    @Override
    int insert(DataPanoramaPointEntity dataPanoramaPoint);

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
    int updateByPointId(DataPanoramaPointEntity dataPanoramaPoint);

}
