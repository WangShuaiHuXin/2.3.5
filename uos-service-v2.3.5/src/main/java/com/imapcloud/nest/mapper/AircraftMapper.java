package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.AircraftEntity;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 飞机信息表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface AircraftMapper extends BaseMapper<AircraftEntity> {

    /**
     * 软删除
     *
     * @param nestId
     * @return
     */
    @Update("update aircraft set deleted = 1 where nest_id = #{nestId} ")
    int softDelete(Integer nestId);

    /**
     * 通过机巢id查询无人机id
     *
     * @param nestId
     * @return
     */
    @Select("select id from aircraft where deleted = 0 and nest_id = #{nestId} ")
    Integer getIdByNestId(Integer nestId);

    @Update("UPDATE aircraft SET deleted = 1 WHERE nest_id = #{nestId}")
    void deleteByNestId(Integer nestId);

    /**
     * 查询相机名称
     *
     * @param nestId
     * @return
     */
    @Select("SELECT camera_name FROM aircraft WHERE nest_id = #{nestId} AND deleted = 0")
    String selectCameraNameByNestId(Integer nestId);


    /**
     * 查询code通过NestId
     *
     * @param nestId
     * @return
     */
    @Select("SELECT code FROM aircraft WHERE  nest_id = #{nestId} AND deleted = 0")
    String selectCodeByNestId(Integer nestId);

    @Select("SELECT code FROM aircraft a,sys_app s WHERE a.app_id = s.id AND s.device_id = #{deviceId} AND s.deleted = 0 AND a.deleted = 0")
    String selectAirCodeByDeviceId(String deviceId);
}
