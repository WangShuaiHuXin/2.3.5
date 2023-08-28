package com.imapcloud.nest.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.model.RegionEntity;
import com.imapcloud.nest.pojo.dto.RegionDto;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.service.RegionService;
import com.imapcloud.nest.service.SysUnitService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 架次表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@RestController
@RequestMapping("/region")
public class RegionController {

    @Autowired
    private RegionService regionService;
    @Autowired
    private SysUnitService sysUnitService;
    @Autowired
    private NestService nestService;
    @Autowired
    private RedisService redisService;


    /*添加区域*/
    @Deprecated
    @PostMapping("/addRegion")
    public RestRes addRegion(@RequestBody @Valid RegionDto regionDto) {
        String name = regionDto.getName();
        RegionEntity regionEntity = regionService.queryByName(name);
        if (regionEntity != null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_REGION.getContent()));
        }
        regionDto.setCreateUserId(Long.valueOf(TrustedAccessTracerHolder.get().getAccountId()));
        RegionEntity newRegionEntity = new RegionEntity();
        BeanUtils.copyProperties(regionDto, newRegionEntity);
        newRegionEntity.setCreateTime(LocalDateTime.now());
        newRegionEntity.setModifyTime(LocalDateTime.now());

        return regionService.save(newRegionEntity) ? RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AREA_ADDED_SUCCESSFULLY.getContent())) : RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AREA_ADDITION_FAILED.getContent())
        );
    }

    /*更改区域*/
    @Deprecated
    @PostMapping("/updateRegion")
    public RestRes updateRegion(@RequestBody @Valid RegionDto regionDto) {
        String name = regionDto.getName();
        Integer id = regionDto.getId();
        String description = regionDto.getDescription();
        if (id == null) {
            return RestRes.errorParam();
        }

        RegionEntity regionEntity = regionService.getById(id);
        if (regionEntity == null) {
            return RestRes.errorParam();
        }

        RegionEntity regionEntityByName = regionService.queryByName(name);
        if (regionEntityByName != null && !id.equals(regionEntityByName.getId())) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_REGION.getContent()));
        }
        regionEntity.setName(name);
        regionEntity.setModifyTime(LocalDateTime.now());
        regionEntity.setDescription(description);
        boolean res = regionService.updateById(regionEntity);
        if (res) {
            //清空机巢列表的区域缓存
            redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_REGION_LIST_KEY, TrustedAccessTracerHolder.get().getUsername()));
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AREA_MODIFICATION_SUCCESSFUL.getContent()));
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AREA_MODIFICATION_FAILED.getContent()));
        }
    }

    /*获取区域信息*/
    @Deprecated
    @PostMapping("/getRegion")
    public RestRes getRegion(@RequestBody RegionEntity regionDto) {
        Integer id = regionDto.getId();
        if (id == null) {
            return RestRes.errorParam();
        }
        RegionEntity regionEntity = regionService.getById(id);

        Map<String, Object> map = new HashMap<>(2);
        map.put("regionEntity", regionEntity);
        return regionEntity == null ? RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_REGION_DOES_NOT_EXIST.getContent())) : RestRes.ok(map);
    }


    /*获取区域信息*/
    @Deprecated
    @PostMapping("/listRegionBy")
    public RestRes listRegionByPage(@RequestBody RegionDto regionDto) {
        String name = regionDto.getName();
        Integer pageNo = regionDto.getCurrentPageNo();
        Integer pageSize = regionDto.getCurrentPageSize();
        IPage<RegionEntity> regionEntityIPage = regionService.listRegionByPages(pageNo, pageSize, name);
        return RestRes.ok("regionEntityIPage", regionEntityIPage);
    }

    /*删除区域*/
    @Deprecated
    @PostMapping("/deleteRegion")
    public RestRes deleteRegion(@RequestBody @Valid RegionEntity regionEntity) {
        Integer id = regionEntity.getId();
        if (id == null) {
            return RestRes.errorParam();
        }
        List<NestEntity> nestEntities = nestService.list(new QueryWrapper<NestEntity>().eq("region_id", id));
        if (!CollectionUtils.isEmpty(nestEntities)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NESTS_UNDER_THE_CURRENT_REGION_DELETE_THE_REGION.getContent()));
        }

        return regionService.batchDeleteRegions(Collections.singletonList(id));
    }

    /*批量删除区域*/
    @Deprecated
    @PostMapping("/batchDeleteRegions")
    public RestRes batchDeleteRegion(@RequestBody RegionDto regionDto) {
        List<Integer> ids = regionDto.getRegionIds();
        if (CollectionUtils.isEmpty(ids)) {
            return RestRes.errorParam();
        }

        return regionService.batchDeleteRegions(ids);
    }

    @Deprecated
    @GetMapping("/getAllRegion")
    public RestRes getAllRegion() {
        List regionEntityList = regionService.getAllRegion();
        Map<String, Object> map = new HashMap<>(2);
        map.put("regionEntityList", regionEntityList);
        return RestRes.ok(map);
    }
}

