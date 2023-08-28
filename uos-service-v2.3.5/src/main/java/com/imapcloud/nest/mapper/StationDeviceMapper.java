package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.StationDeviceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.pojo.dto.StationDevicePhotoDTO;
import com.imapcloud.nest.pojo.dto.StationIdentifyPhotoDTO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author daolin
 * @since 2020-11-02
 */
public interface StationDeviceMapper extends BaseMapper<StationDeviceEntity> {
    @Delete("delete from station_device where base_nest_id = #{nestId}")
    void deleteByNestId(String nestId);

    @Select("select * from station_device where base_nest_id = #{nestId}")
    List<StationDeviceEntity> selectBynestId(String nestId);

    @Select("SELECT name FROM station_device WHERE uuid = #{uuid} ")
    String getDeviceName(String uuid);
}
