package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.GridManageEntity;
import com.imapcloud.nest.v2.dao.po.in.GridManageQueryCriteriaInPO;
import com.imapcloud.nest.v2.dao.po.out.GridManageOrgCodeOutPO;
import com.imapcloud.nest.v2.manager.dataobj.GridManageDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname GridManageMapper
 * @Description 管理网格 Mapper 接口
 * @Date 2022/12/9 15:25
 * @Author Carnival
 */
public interface GridManageMapper extends BaseMapper<GridManageEntity>,
        IPageMapper<GridManageEntity, GridManageQueryCriteriaInPO, PagingRestrictDo> {

    int batchInsert(@Param("entityList") List<GridManageEntity> entities);

    int batchDeleteTask(@Param("idList") List<Integer> idList);


    List<String> selectByLngAndLat(@Param("lng") Double lng,@Param("lat") Double lat, @Param("orgCode") String orgCode);

    String selectByTask(@Param("taskId") Integer taskId);

    List<GridManageDO> selectGridManage(@Param("orgCodeList") List<String> orgCodeList);

    void batchUpdateIsRest(@Param("list")List<String> gridManageIds, @Param("isReset")Integer isReset);

    List<GridManageOrgCodeOutPO> selectOrgByManage(@Param("list") List<String> gridManageIds);

}
