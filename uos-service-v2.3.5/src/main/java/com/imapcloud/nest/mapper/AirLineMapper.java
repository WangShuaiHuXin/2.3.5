package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.AirLineEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 航线表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface AirLineMapper extends BaseMapper<AirLineEntity> {

    /**
     * 查询所有的航线id、name
     *
     * @return
     */
    @Select("select id,name from air_line where deleted = 0")
    List<AirLineEntity> selectAllIdAndName();

    /**
     * 通过IdList查询航线的飞行距离和航点数
     *
     * @param idList
     * @return
     */
    List<AirLineEntity> batchSelectEstimateMilesAndMergeCountByIdList(@Param("idList") List<Integer> idList);


    /**
     * 查询部分值，mergeCount，predicMiles，predicTime，photoCount
     *
     * @param idList
     * @return
     */
    List<AirLineEntity> batchSelectPart5ByIdList(@Param("idList") List<Integer> idList);

    /**
     * 批量软删除
     *
     * @param idList
     * @return
     */
    int batchSoftDeleteByIds(@Param("idList") List<Integer> idList);

    @Select("SELECT type FROM air_line WHERE id = #{id} AND deleted = 0")
    Integer selectTypeById(Integer id);

    @Select("SELECT photo_count,video_count,video_length FROM air_line a LEFT JOIN mission m ON a.id = m.air_line_id WHERE m.id = #{missionId}")
    AirLineEntity selectPhotoCountAndVideoCountAndLen(Integer missionId);

}
