package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.MediaDeviceEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 媒体设备表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 * @deprecated 2.3.2，将在后续版本删除该类
 */
@Deprecated
public interface MediaDeviceMapper extends BaseMapper<MediaDeviceEntity> {

    /**
     * 批量插入
     *
     * @param mediaDeviceEntityList 媒体设备实体列表
     * @return int
     */
    int batchInsert(@Param("entityList")List<MediaDeviceEntity> mediaDeviceEntityList);

    /**
     * 更新设备
     *
     * @param mediaDeviceEntity
     * @return int
     */
    int updateByDeviceId(MediaDeviceEntity mediaDeviceEntity);

    /**
     * 删除
     *
     * @param deviceIdList 设备id列表
     * @param accountId    帐户id
     * @return int
     */
    int deleteByDeviceIdList(@Param("deviceIdList") List<String> deviceIdList, @Param("accountId") String accountId);
}
