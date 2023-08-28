package com.imapcloud.nest.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.TaskLabelEntity;
import com.imapcloud.nest.pojo.dto.TaskLabelDTO;
import com.imapcloud.nest.service.TaskLabelService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务标签 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2021-05-20
 */
@RestController
@RequestMapping("/task/label")
public class TaskLabelController {

    @Autowired
    private TaskLabelService taskLabelService;

    @PostMapping("/batch/add/update/label")
    public RestRes batchAddOrUpdateLabel(@RequestBody @Valid List<TaskLabelDTO> taskLabelList, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()));
        }
        return taskLabelService.batchAddOrUpdateLabel(taskLabelList);
    }


    @PostMapping("/update/label/remarks")
    public RestRes updateLabelRemarks(@RequestBody Map<String, Object> param) {
        Integer labelId = (Integer) param.get("labelId");
        String remarks = (String) param.get("remarks");
        if (labelId != null && StrUtil.isNotBlank(remarks)) {
            boolean update = taskLabelService.update(new UpdateWrapper<TaskLabelEntity>()
                    .lambda()
                    .set(TaskLabelEntity::getRemarks, remarks)
                    .eq(TaskLabelEntity::getId, labelId));
            if (update) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TAG_NOTE_UPDATE_SUCCESS.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @PostMapping("/batch/delete/label")
    public RestRes batchDeleteLabel(@RequestBody List<Integer> labelIdList) {
        if (CollectionUtil.isNotEmpty(labelIdList)) {
            return taskLabelService.batchSoftDelete(labelIdList);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }


    @GetMapping("/list/label/{taskId}")
    public RestRes listLabelByTaskId(@PathVariable Integer taskId) {
        if (taskId != null) {
            List<TaskLabelEntity> labelList = taskLabelService.list(new QueryWrapper<TaskLabelEntity>()
                    .lambda()
                    .eq(TaskLabelEntity::getDeleted, false)
                    .eq(TaskLabelEntity::getTaskId, taskId));

            Map<String, Object> map = new HashMap<>(2);
            map.put("labelList", labelList);
            return RestRes.ok(map);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }
}

