package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.MediaStreamEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 流媒体信息表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface MediaStreamMapper extends BaseMapper<MediaStreamEntity> {

    /**
     * 更新拉流url
     *
     * @param streamId      流id
     * @param streamPullUrl 拉流url
     * @param modifierId 操作人
     * @return int
     */
    int updatePullStreamUrl(@Param("streamId") String streamId, @Param("streamPullUrl") String streamPullUrl, @Param("modifierId") String modifierId);

    /**
     * 批量插入
     *
     * @param mediaStreamEntityList
     * @return int
     */
    int batchInsert(@Param("entityList") List<MediaStreamEntity> mediaStreamEntityList);

    /**
     * 通过业务ID更新
     *
     * @param mediaStreamEntity 媒体流实体
     * @return int
     */
    int updateByStreamId(MediaStreamEntity mediaStreamEntity);

    /**
     * 删除
     *
     * @param streamIdList 流id列表
     * @param accountId    帐户id
     * @return int
     */
    int deleteByStreamIdList(@Param("streamIdList") List<String> streamIdList, @Param("accountId") String accountId);

    @Update("UPDATE media_stream SET deleted = #{deleted} WHERE stream_id = #{streamId}")
    int updateDeletedByStreamIdInt(@Param("streamId") String streamId,@Param("deleted") Integer deleted);
}
