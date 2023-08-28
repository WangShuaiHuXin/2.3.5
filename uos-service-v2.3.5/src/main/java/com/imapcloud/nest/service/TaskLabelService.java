package com.imapcloud.nest.service;

import com.imapcloud.nest.model.TaskLabelEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.pojo.dto.TaskLabelDTO;
import com.imapcloud.nest.utils.RestRes;

import java.util.List;

/**
 * <p>
 * 任务标签 服务类
 * </p>
 *
 * @author wmin
 * @since 2021-05-20
 */
public interface TaskLabelService extends IService<TaskLabelEntity> {

    /**
     * 批量添加或者修改标签
     *
     * @param taskLabelList
     * @return
     */
    RestRes batchAddOrUpdateLabel(List<TaskLabelDTO> taskLabelList);

    /**
     * 批量软删除
     *
     * @param labelIdList
     * @return
     */
    RestRes batchSoftDelete(List<Integer> labelIdList);
}
