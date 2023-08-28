package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.model.NestMaintenanceProjectEntity;
import com.imapcloud.nest.mapper.NestMaintenanceProjectMapper;
import com.imapcloud.nest.pojo.dto.SaveNestMaintenanceProjectDto;
import com.imapcloud.nest.service.NestMaintenanceProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 维护项目 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-05-14
 */
@Service
public class NestMaintenanceProjectServiceImpl extends ServiceImpl<NestMaintenanceProjectMapper, NestMaintenanceProjectEntity> implements NestMaintenanceProjectService {

    @Resource
    private NestMaintenanceProjectService nestMaintenanceProjectService;

    @Override
    public List<NestMaintenanceProjectEntity> listByType(Integer type) {
        return this.list(new QueryWrapper<NestMaintenanceProjectEntity>()
                .lambda()
                .eq(NestMaintenanceProjectEntity::getType, type)
                .eq(NestMaintenanceProjectEntity::getDeleted, false));
    }

    @Transactional
    @Override
    public RestRes saveProject(SaveNestMaintenanceProjectDto dto){
        Assert.isNull(dto, "参数为空");
        Assert.isNull(dto.getType(), "维保类别为空");
        List<NestMaintenanceProjectEntity> entities = dto.getEntities();
//
        if (entities != null && entities.size() > 0){
            int size = entities.size();
            //去除重复元素,添加类别字段
            entities = entities.stream().peek(entity -> entity.setType(dto.getType())).distinct().collect(Collectors.toList());
            if (entities.size() < size){
                Assert.failure(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_BE_TWO_MAINTENANCE_PROJECTS_WITH_THE_SAME_NAME.getContent()));
            }

            List<String> nameList = new ArrayList();
            for (NestMaintenanceProjectEntity nestMaintenanceProjectEntity : entities){
                nameList.add(nestMaintenanceProjectEntity.getName());
            }
            long count = nameList.stream().distinct().count();
            // 如果count小于List的尺寸，则表明有重复的值
            if (count < nameList.size()) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_BE_TWO_MAINTENANCE_PROJECTS_WITH_THE_SAME_NAME.getContent()));
            }

            List<Integer> noDeleteIds = entities.stream().map(NestMaintenanceProjectEntity::getId).collect(Collectors.toList());
            remove(new QueryWrapper<NestMaintenanceProjectEntity>().eq("type", dto.getType()).notIn("id", noDeleteIds));
            saveOrUpdateBatch(entities);
        }else {
            remove(new QueryWrapper<NestMaintenanceProjectEntity>().eq("type", dto.getType()));
        }
        return RestRes.ok();
    }

}
