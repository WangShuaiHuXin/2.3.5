package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.NestRtkEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zheng
 * @since 2021-09-15
 */
public interface NestRtkMapper extends BaseMapper<NestRtkEntity> {

    /**
     * 获取num天到期的rtk
     * @param num
     * @param nestIdList
     * @return
     */
    List<NestRtkEntity> getExpireRtkList(@Param("num") Integer num, @Param("nestIdList") List<String> nestIdList);

    /**
     * 更新
     *
     * @param nestRtkEntity 巢rtk实体
     * @return int
     */
    int updateByNestId(NestRtkEntity nestRtkEntity);

    /**
     * 删除
     *
     * @param nestId 巢id
     * @return int
     */
    int deleteByNestId(@Param("nestId") String nestId);
}
