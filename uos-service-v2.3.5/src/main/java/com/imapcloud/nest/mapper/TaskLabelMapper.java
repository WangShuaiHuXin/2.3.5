package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.TaskLabelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 任务标签 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2021-05-20
 */
public interface TaskLabelMapper extends BaseMapper<TaskLabelEntity> {

    /**
     * 批量软删除
     * @param labelIdList
     * @return
     */
    int batchUpdateDeleted(@Param("labelIdList") List<Integer> labelIdList);

}
