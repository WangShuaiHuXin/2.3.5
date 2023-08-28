package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.TaskLabelEntity;
import com.imapcloud.nest.mapper.TaskLabelMapper;
import com.imapcloud.nest.pojo.dto.TaskLabelDTO;
import com.imapcloud.nest.service.TaskLabelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 任务标签 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2021-05-20
 */
@Service
public class TaskLabelServiceImpl extends ServiceImpl<TaskLabelMapper, TaskLabelEntity> implements TaskLabelService {

    @Override
    public RestRes batchAddOrUpdateLabel(List<TaskLabelDTO> taskLabelList) {
        if (CollectionUtil.isNotEmpty(taskLabelList)) {
            List<TaskLabelEntity> list = new ArrayList<>(taskLabelList.size());
            for (TaskLabelDTO tld : taskLabelList) {
                TaskLabelEntity taskLabelEntity = new TaskLabelEntity();
                BeanUtils.copyProperties(tld, taskLabelEntity);
                list.add(taskLabelEntity);
            }

            boolean save = this.saveOrUpdateBatch(list);
            if (save) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_TASK_TAG_SAVE_MODIFICATION.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SAVE_TASK_LABEL.getContent()));
    }

    @Override
    public RestRes batchSoftDelete(List<Integer> labelIdList) {
        int i = baseMapper.batchUpdateDeleted(labelIdList);
        if (i > 0) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_BATCH_DELETE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_DELETE_FAILED.getContent()));
    }
}
