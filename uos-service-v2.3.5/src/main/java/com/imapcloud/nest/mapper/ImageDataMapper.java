package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.ImageDataEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.pojo.dto.reqDto.ImageDataReqDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author daolin
 * @since 2020-11-16
 */
public interface ImageDataMapper extends BaseMapper<ImageDataEntity> {

    /**
     * 获取缩略图url分页列表
     * @param page
     * @param name
     * @param unitId
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<ImageDataReqDto> getThumbnailPage(@Param("page") IPage<ImageDataReqDto> page, @Param("name") String name,
                                         @Param("orgCode") String orgCode, @Param("startTime")String startTime,
                                         @Param("endTime")String endTime, @Param("region")String region);

    /**
     * 获取详情
     * @param id
     * @return
     */
    ImageDataEntity getInfoById(Integer id);

    /**
     * 软删除图像数据
     * @param idList
     * @return
     */
    int deleteByIdList(@Param("idList") List idList);

    /**
     * 通过id获取zip包名称和url
     * @param id
     * @return
     */
    @Select(" SELECT zip_name AS zipName, zip_url AS zipUrl FROM image_data WHERE id = #{id}")
    Map<String, String> getZipById(@Param("id") Integer id);
}
