package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.DataMultispectralEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 正射数据表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
public interface DataMultispectralMapper extends BaseMapper<DataMultispectralEntity> {

    List<MissionRecordsEntity> getMissionRecords(@Param("startTime") String startTime, @Param("endTime") String endTime);

}
