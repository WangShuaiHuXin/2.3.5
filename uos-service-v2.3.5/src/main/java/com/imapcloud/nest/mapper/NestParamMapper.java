package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.NestParamEntity;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 机巢参数表
 *
 * @author: zhengxd
 * @create: 2020/9/25
 **/
public interface NestParamMapper extends BaseMapper<NestParamEntity> {

    @Update("UPDATE nest_param SET deleted = 1 WHERE nest_id = #{nestId}")
    void deleteByNestId(Integer nestId);

    @Select("SELECT start_stop_point_altitude FROM nest_param WHERE nest_id = #{nestId} AND deleted = 0")
    Double selectStartStopAltitude();

    /**
     * 逻辑删除
     *
     * @param nestId    巢id
     * @return int
     */
    @Update("UPDATE nest_param SET deleted = 1 WHERE base_nest_id = #{nestId} and deleted = 0")
    int deleteByBaseNestId(String nestId);
}
