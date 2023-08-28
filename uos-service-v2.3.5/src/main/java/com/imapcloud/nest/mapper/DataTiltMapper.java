package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.DataTiltEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 倾斜数据表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
public interface DataTiltMapper extends BaseMapper<DataTiltEntity> {

    List<MissionRecordsEntity> getMissionRecords(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
