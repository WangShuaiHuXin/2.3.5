package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.StationCheckpointEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;

/**
 * <p>
 * 巡检点，对应拍照点坐标、角度等信息 Mapper 接口
 * </p>
 *
 * @author daolin
 * @since 2020-11-02
 */
public interface StationCheckpointMapper extends BaseMapper<StationCheckpointEntity> {
    @Delete("delete from station_checkpoint where base_nest_id = #{nestId}")
    void deleteByNestId(String nestId);
}
