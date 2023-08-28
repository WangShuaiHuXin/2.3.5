package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.FixAirLineEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 固定航线表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-10-10
 */
public interface FixAirLineMapper extends BaseMapper<FixAirLineEntity> {

    /**
     * 批量查询id和name
     *
     * @param nestId
     * @return
     */
    @Select("SELECT id,name,modify_time FROM fix_air_line WHERE nest_id = #{nestId} AND deleted = 0 ORDER BY modify_time DESC")
    List<FixAirLineEntity> batchSelectIdAndNameByNestId(Integer nestId);

    @Update("UPDATE fix_air_line SET deleted = #{deleted} WHERE id = #{id} ")
    Integer updateDeletedById(Integer id, Integer deleted);

    /**
     *
     * @param idList
     * @return
     */
    Integer batchUpdateDeletedByIdList(@Param("idList") List<Integer> idList);
}
