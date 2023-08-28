package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.BaseUavNestRefEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 基站与无人机关系表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseUavNestRefMapper extends BaseMapper<BaseUavNestRefEntity> {

    /**
     * 删除
     *
     * @param nestId    巢id
     * @param accountId 帐户id
     * @return int
     */
    int deleteByNestId(@Param("nestId") String nestId, @Param("accountId") String accountId);
}
