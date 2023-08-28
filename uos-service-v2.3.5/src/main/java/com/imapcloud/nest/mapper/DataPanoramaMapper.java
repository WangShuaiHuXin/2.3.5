package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.DataPanoramaEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 全景数据表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
public interface DataPanoramaMapper extends BaseMapper<DataPanoramaEntity> {

    List<MissionRecordsEntity> getMissionRecords(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
