package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.DefectInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 缺陷的详细信息表 Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2021-02-23
 */
public interface DefectInfoMapper extends BaseMapper<DefectInfoEntity> {
    /**
     * 通过图片id获取缺陷信息
     * @param photoId
     * @return
     */
    @Select("SELECT * FROM defect_info WHERE deleted = 0 AND photo_id = #{photoId} ")
    List<DefectInfoEntity> getByPhotoId(Long photoId);

    /**
     * 通过图片id获取问题信息和问题code
     * @param photoId
     * @return
     */
    List<DefectInfoEntity> getDefectInfoList(@Param("photoId") Long photoId, @Param("typeIdList") List<Integer> typeIdList);
}
