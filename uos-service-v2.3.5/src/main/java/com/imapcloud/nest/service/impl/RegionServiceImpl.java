package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.enums.RegionTypeEnum;
import com.imapcloud.nest.mapper.NestMapper;
import com.imapcloud.nest.mapper.RegionMapper;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.model.RegionEntity;
import com.imapcloud.nest.service.RegionService;
import com.imapcloud.nest.utils.RestRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 架次表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Service
@Slf4j
public class RegionServiceImpl extends ServiceImpl<RegionMapper, RegionEntity> implements RegionService {

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private NestMapper nestMapper;

    @Override
    public RegionEntity queryByName(String name) {
        return this.getOne(new QueryWrapper<RegionEntity>().eq("name", name).eq("deleted", false));
    }

    @Override
    public IPage<RegionEntity> listRegionByPages(Integer pageNo, Integer pageSize, String name) {
        if (pageNo != null && pageSize != null) {
            try {
                IPage<RegionEntity> regionEntityIPage = baseMapper.getRegionPage(new Page<>(pageNo, pageSize), name);
                if (regionEntityIPage != null) {
                    List<RegionEntity> records = regionEntityIPage.getRecords();
                    if (!CollectionUtils.isEmpty(records)){
                        List<RegionEntity> regionEntityList = regionMapper.listRegionCountByNest();
                        Map<Integer, Integer> regionMap = regionEntityList.stream().collect(
                                Collectors.toMap(RegionEntity::getId, RegionEntity::getNestCount));
                        records.forEach(it -> {
                            if (regionMap.get(it.getId()) != null) {
                                it.setNestCount(regionMap.get(it.getId()));
                            }
                        });
                    }
                }

                return regionEntityIPage;
            } catch (Exception e) {
                log.info("分页出现问题： {}", e.getMessage());
                return null;
            }
        }

        return null;
    }

    @Override
    public List<RegionEntity> getAllRegion() {
        return baseMapper.getAllRegion();
    }

    @Override
    public List<RegionEntity> listIdAndNameByIdList(List<Integer> idList) {
        if (CollectionUtil.isNotEmpty(idList)) {
            return baseMapper.listIdAndNameByIdList(idList);
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes batchDeleteRegions(List<Integer> regionIds) {
        try {
            QueryWrapper<NestEntity> nestRegionIdsQueryWrapper = new QueryWrapper<NestEntity>().in("region_id", regionIds);
            NestEntity updateNest = new NestEntity();
            RegionEntity defaultRegion = this.getOne(new QueryWrapper<RegionEntity>().eq("type", RegionTypeEnum.DEFAULT.getCode()));
            if (defaultRegion == null) {
                log.error("当前为创建默认分组的数据");
                throw new RuntimeException();
            } else {
                updateNest.setRegionId(defaultRegion.getId());
            }
            nestMapper.update(updateNest, nestRegionIdsQueryWrapper);

            QueryWrapper<RegionEntity> regionIdsQueryWrapper = new QueryWrapper<RegionEntity>().in("id", regionIds);
            RegionEntity updateRegion = new RegionEntity();
            updateRegion.setDeleted(true);

            this.update(updateRegion, regionIdsQueryWrapper);
            return RestRes.ok();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
