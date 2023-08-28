package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.out.TaskOutDO;

import java.util.Collection;
import java.util.List;

/**
 * 航线任务
 *
 * @author boluo
 * @date 2022-11-30
 */
public interface TaskManager {

    /**
     * 选择任务id列表
     *
     * @param taskIdList 任务id列表
     * @return {@link List}<{@link TaskOutDO}>
     */
    List<TaskOutDO> selectByTaskIdList(Collection<Long> taskIdList);
}
