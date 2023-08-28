package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.MissionAirEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-04-21
 */
public interface MissionAirMapper extends BaseMapper<MissionAirEntity> {

    List<MissionRecordsEntity> getMissionRecords(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
