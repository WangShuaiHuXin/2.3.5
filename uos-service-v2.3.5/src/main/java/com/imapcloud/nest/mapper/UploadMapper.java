package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.UploadEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-11-30
 */
public interface UploadMapper extends BaseMapper<UploadEntity> {

    /**
     * 物理删除
     *
     * @param filePath 文件路径
     */
    void deleteByFilePath(@Param("filePath") String filePath);
}
