package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerWaypointLedgerInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PowerWaypointLedgerInfoMapper extends BaseMapper<PowerWaypointLedgerInfoEntity> {
    void updateToDeleteByOrg(String orgCode);

    void saveList(@Param("list") List<PowerWaypointLedgerInfoEntity> ledgerInfoEntities);
}
