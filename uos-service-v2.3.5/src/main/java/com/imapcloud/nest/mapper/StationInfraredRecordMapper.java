package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.StationInfraredRecordEntity;
import com.imapcloud.nest.pojo.dto.StationInfraredRecordDto;
import com.imapcloud.nest.utils.RestRes;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 变电站的设备出现的缺陷记录 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2020-12-29
 */
public interface StationInfraredRecordMapper extends BaseMapper<StationInfraredRecordEntity> {

    List<Map> queryRecordByTagId(Integer tagId);

    IPage<StationInfraredRecordDto> queryPhotosByPhotoName(@Param("page") IPage<StationInfraredRecordDto> page, @Param("params") Map<String, Object> params);

    List<Map> queryMaxPicByMonth(@Param("tagId") Integer tagId,@Param("photoName")String photoName, @Param("startTime") String startTime,@Param("endTime")String endTime);

    List<Map> queryMinPicByMonth(@Param("tagId") Integer tagId,@Param("photoName")String photoName, @Param("startTime") String startTime,@Param("endTime")String endTime);

    List<Map> queryAvgPicByMonth(@Param("tagId") Integer tagId,@Param("photoName")String photoName, @Param("startTime") String startTime,@Param("endTime")String endTime);

    List<StationInfraredRecordDto> queryPhotos(@Param("tagId") String tagId);

    @Select("SELECT `value` FROM `station_infrared_threshold` where `name` = '红外阈值'")
    Double getThreshold();

    @Update("update `station_infrared_threshold` set `value` = #{value} where `name` = '红外阈值'")
    void setThreshold(String value);
}
