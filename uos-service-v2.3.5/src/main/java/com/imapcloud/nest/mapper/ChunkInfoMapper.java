package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.ChunkInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件块mapper
 *
 * @author: zhengxd
 * @create: 2020/12/15
 **/
public interface ChunkInfoMapper extends BaseMapper<ChunkInfoEntity> {

    /**
     * 查询文件块号
     * @param identifier
     * @return
     */
    @Select("select chunk_number from chunk_info where identifier = #{identifier} AND deleted = 0")
    List<Integer> selectChunkNumbers(@Param("identifier") String identifier);

    /**
     * 获取3天前的分片数据
     * @return
     */
    @Select("SELECT id, chunk_url FROM chunk_info WHERE deleted = 0 AND create_time < DATE_SUB(NOW(),INTERVAL 3 DAY)")
    List<ChunkInfoEntity> getOldChunkInfo();

}
