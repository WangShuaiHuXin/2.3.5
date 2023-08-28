package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.MissionParamEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 架次参数表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface MissionParamMapper extends BaseMapper<MissionParamEntity> {

    /**
     * 批量软删除
     *
     * @param idList
     * @return
     */
    int batchSoftDeleteByIds(@Param("idList") List<Integer> idList);

}
