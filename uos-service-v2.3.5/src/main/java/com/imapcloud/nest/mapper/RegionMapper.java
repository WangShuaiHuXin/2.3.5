package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.RegionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 架次表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
public interface RegionMapper extends BaseMapper<RegionEntity> {
    /**
     * 查询所以的区域
     *
     * @return
     */
    @Select("SELECT * FROM region WHERE deleted = 0 and (type <> 0 or type is null)")
    List<RegionEntity> getAllRegion();

    List<RegionEntity> listRegionCountByNest();

    /**
     * 通过区域id,批量查询id
     *
     * @return
     */
    List<RegionEntity> listIdAndNameByIdList(@Param("idList") List<Integer> idList);


    IPage<RegionEntity> getRegionPage(@Param("page") IPage<RegionEntity> page, @Param("name") String name);

}
