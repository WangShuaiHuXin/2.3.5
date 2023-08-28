package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.DataPointCloudEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 点云数据表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
public interface DataPointCloudMapper extends BaseMapper<DataPointCloudEntity> {

    List<MissionRecordsEntity> getMissionRecords(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据id获取关键文件路径
     * @param id
     * @return
     */
    @Select("SELECT las_url FROM data_point_cloud WHERE id = #{id} ")
    String getLasUrlById(Integer id);

    /**
     * 根据id修改state为0
     * @param beforeId
     * @param afterId
     * @param state
     */
    @Update("UPDATE data_point_cloud SET state = #{state} WHERE id = #{beforeId} OR id = #{afterId} ")
    void updateStateById(@Param("beforeId") Integer beforeId, @Param("afterId") Integer afterId, @Param("state") Integer state);
}
