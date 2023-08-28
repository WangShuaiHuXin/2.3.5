package com.imapcloud.nest.controller;


import com.imapcloud.nest.model.*;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.dto.UnitEntityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 任务标签关系表 前端控制器
 * </p>
 *
 * @author kings
 * @since 2020-12-03
 */
@RestController
@RequestMapping("/sysTaskTagEntity")
public class SysTaskTagController {

    @Autowired
    private SysTaskTagService sysTaskTagService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SysUnitService sysUnitService;
    @Autowired
    private SysTagService sysTagService;
    @Autowired
    private NestService nestService;

   /* @GetMapping("/taskTagDataProcessing")
    public RestRes getAllTagList() {
        List<SysTaskTagEntity> list = sysTaskTagService.list();
        list.forEach(e->{
            if (e.getTagId()==null||e.getTagId()<0){
                TaskEntity taskEntity = taskService.getById(e.getTaskId());
                if (taskEntity!=null&&taskEntity.getNestId()!=null){
                    NestEntity nestEntity = nestService.getById(taskEntity.getNestId());

                    if (nestEntity!=null&&nestEntity.getUnitId()!=null){
                        SysUnitEntity sysUnitEntity = sysUnitService.getById(nestEntity.getUnitId());
                        if (sysUnitEntity!=null){
                            e.setTagId(setUnitTag(sysUnitEntity.getId()));
                            sysTaskTagService.updateById(e);
                        }
                    }
                }
            }
        });
        return RestRes.ok();
    }*/

}

