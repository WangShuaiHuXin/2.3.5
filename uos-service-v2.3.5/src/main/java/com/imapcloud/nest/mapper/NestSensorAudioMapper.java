package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.NestSensorAudioEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 机巢喊话器的音频表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2021-04-06
 */
public interface NestSensorAudioMapper extends BaseMapper<NestSensorAudioEntity> {

    /**
     * 分页列表
     * @param page
     * @param nestId
     * @param audioName
     * @return
     */
    IPage<NestSensorAudioEntity> getListPage(IPage<NestSensorAudioEntity> page, @Param("nestId") String nestId, @Param("audioName") String audioName);

    /**
     * 获取修改的名称是否有重命的
     * @param id
     * @param audioName
     * @return
     */
    @Select("SELECT * FROM nest_sensor_audio WHERE nest_id = #{nestId} AND id != #{id} AND audio_name = #{audioName}  AND deleted = 0")
    NestSensorAudioEntity getSameName(@Param("nestId")Integer nestId, @Param("id") Integer id, @Param("audioName") String audioName);

    /**
     * 根据id获取机巢id
     * @param id
     * @return
     */
    @Select("SELECT nest_id FROM nest_sensor_audio WHERE id = #{id}")
    Integer getNestIdById(Integer id);

    /**
     * 根据机巢id和index修改名称
     * @param nestId
     * @param index
     * @param name
     * @return
     */
    @Update("UPDATE nest_sensor_audio SET audio_name = #{name} WHERE nest_id = #{nestId}  AND audio_index = #{index}  AND deleted = 0")
    Integer updateNameByNestIdAndIndex(@Param("nestId") Integer nestId, @Param("index") Integer index, @Param("name") String name);
}
