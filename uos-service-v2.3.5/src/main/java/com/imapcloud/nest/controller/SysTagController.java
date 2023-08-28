package com.imapcloud.nest.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.SysTagEntity;
import com.imapcloud.nest.model.SysTaskTagEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.SysTagDto;
import com.imapcloud.nest.pojo.dto.reqDto.SysTagReqDto;
import com.imapcloud.nest.service.SysTagService;
import com.imapcloud.nest.service.TaskService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.NestOrgRefService;
import com.imapcloud.nest.v2.service.dto.out.NestOrgRefOutDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统标签表 前端控制器
 * </p>
 *
 * @author kings
 * @since 2020-12-03
 */
@RestController
@RequestMapping("/sysTag")
public class SysTagController {

    @Autowired
    private SysTagService sysTagService;
    @Autowired
    private TaskService taskService;

    @Resource
    private NestOrgRefService nestOrgRefService;

    /*添加区域*/
    @PostMapping("/saveSysTag")
    public RestRes addSysTag(@RequestBody @Valid SysTagDto sysTagDto) {
        String unitId = sysTagDto.getOrgCode();
        if (unitId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_SELECT_THE_USER_UNIT.getContent()));
        }

        return sysTagService.addSysTag(sysTagDto);
    }

    @PostMapping("/listSysTagBy")
    public RestRes listSysTagBy(@RequestBody @Valid SysTagReqDto sysTagReqDto) {
        String unitId = sysTagReqDto.getUnitId();
        String nestId = sysTagReqDto.getNestId();
        Integer dataType = sysTagReqDto.getDataType();
        List<SysTagEntity> sysTagEntityList = null;
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        if (StringUtils.hasText(unitId)) {
            sysTagEntityList = sysTagService.list(new QueryWrapper<SysTagEntity>().eq("org_code", unitId));
        } else {
            if (StringUtils.hasText(nestId)) {
                List<NestOrgRefOutDTO> noRefs = nestOrgRefService.listNestOrgRefs(Collections.singletonList(nestId), false);
                LambdaQueryWrapper<SysTagEntity> condition = Wrappers.lambdaQuery(SysTagEntity.class)
                        .likeRight(SysTagEntity::getOrgCode, orgCode);
                if (!CollectionUtils.isEmpty(noRefs)) {
                    Set<String> orgCodes = noRefs.stream().map(NestOrgRefOutDTO::getOrgCode).collect(Collectors.toSet());
                    condition = condition.in(SysTagEntity::getOrgCode, orgCodes);
                }
                sysTagEntityList = sysTagService.list(condition);
            }
        }
        if (CollectionUtil.isEmpty(sysTagEntityList)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_TAG_HAS_BEEN_CREATED_FOR_THE_CURRENT_UNIT.getContent()));
        }
        List<Integer> tagMissionDtos = new ArrayList<>();
        if(dataType!=null) {
            tagMissionDtos = sysTagService.selectMissionByType(dataType);
        }

        List<SysTaskTagEntity> taskTagEntities = sysTagService.getListByTagId(null, nestId, orgCode);
        List<TaskEntity> allTaskEntityList = taskService.getBaseMapper().selectList(new QueryWrapper<TaskEntity>().lambda().eq(TaskEntity::getNestId, nestId).eq(TaskEntity::getDeleted, false));
        for(SysTagEntity sysTagEntity:sysTagEntityList) {
            if(taskTagEntities.size()>0) {
                List<SysTaskTagEntity> sysTaskTagEntities = taskTagEntities.stream().filter(e -> e.getTagId() == sysTagEntity.getId().intValue()).collect(Collectors.toList());
                if (sysTaskTagEntities.size() > 0) {
                    List<Integer> taskIds = sysTaskTagEntities.stream().map(SysTaskTagEntity::getTaskId).collect(Collectors.toList());
                    List<TaskEntity> taskEntityList = allTaskEntityList.stream().filter(e -> taskIds.contains(e.getId().intValue())).collect(Collectors.toList());
                    if (taskEntityList.size() > 0) {
                        sysTagEntity.setIsTask(1);
                    } else {
                        sysTagEntity.setIsTask(0);
                    }
                } else {
                    sysTagEntity.setIsTask(0);
                }
            }else {
                sysTagEntity.setIsTask(0);
            }
            if(tagMissionDtos.size()>0&&tagMissionDtos.contains(sysTagEntity.getId().intValue())){
                sysTagEntity.setIsMission(1);
            }else {
                sysTagEntity.setIsMission(0);
            }
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("sysTagEntityList", sysTagEntityList);
        return RestRes.ok(map);
    }

    /**
     * 旧的分析应用使用的标签接口(包含变电站，红外，表计)
     * @param defectStatus
     * @param startTime
     * @param endTime
     * @param type
     * @param flag
     * @return
     */
    @GetMapping("/all/tag/list")
    public RestRes getAllTagList(@RequestParam(required = false) Integer defectStatus,
                                 @RequestParam(required = false) String startTime,
                                 @RequestParam(required = false) String endTime,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) Integer flag) {
        List<Integer> typeList = null;
        if (type != null) {
            typeList = Arrays.asList(type.split(",")).stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
        }
        return sysTagService.getAllTagList(defectStatus, startTime, endTime, typeList, flag);
    }

    /**
     * 新的分析应用、分析中台使用的标签接口
     * 查询标签下有没有分析中台的数据，有几个问题
     *
     * @param problemStatus
     * @param problemSourceStr
     * @param flag
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/data/tag/list")
    public RestRes getAllTagList(@RequestParam(required = false) Integer problemStatus,
                                 @RequestParam(required = false) String problemSourceStr,
                                 @RequestParam(required = false) Integer flag,
                                 @RequestParam(required = false) String startTime,
                                 @RequestParam(required = false) String endTime) {
        List<Integer> problemSourceList = null;
        if (problemSourceStr != null) {
            problemSourceList = Arrays.asList(problemSourceStr.split(",")).stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
        }
        return sysTagService.getAllDataTagList(problemStatus, startTime, endTime, problemSourceList, flag);
        //return sysTagService.getAllTagList(null, null, startTime, endTime, problemSourceList, flag);
    }

    @GetMapping("/get/all/tag/list")
    public RestRes getAllTagList() {
        List<SysTagEntity> allTagList = sysTagService.getAllTagByUser();
        Map map = new HashMap(2);
        map.put("list", allTagList);
        return RestRes.ok(map);
    }
}

