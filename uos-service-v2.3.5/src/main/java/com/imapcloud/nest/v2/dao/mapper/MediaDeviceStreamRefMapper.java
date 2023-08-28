package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.MediaDeviceStreamRefEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 媒体设备与媒体流关系表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface MediaDeviceStreamRefMapper extends BaseMapper<MediaDeviceStreamRefEntity> {

    /**
     * 批量插入
     *
     * @param mediaDeviceStreamRefEntityList 流媒体设备参考实体列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<MediaDeviceStreamRefEntity> mediaDeviceStreamRefEntityList);

    /**
     * 删除
     *
     * @param deviceIdList 设备id列表
     * @param accountId    帐户id
     * @return int
     */
    int deleteByDeviceIdList(@Param("deviceIdList") List<String> deviceIdList, @Param("accountId") String accountId);
}
