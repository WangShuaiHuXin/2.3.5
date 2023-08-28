package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.mapper.NestRtkMapper;
import com.imapcloud.nest.model.NestRtkEntity;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.dataobj.in.NestRtkInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.NestRtkOutDO;
import com.imapcloud.nest.v2.manager.sql.NestRtkManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * RTK
 *
 * @author boluo
 * @date 2022-08-25
 */
@Component
public class NestRtkManagerImpl implements NestRtkManager {

    @Resource
    private NestRtkMapper nestRtkMapper;

    private NestRtkEntity toNestRtkEntity(NestRtkInDO.NestRtkEntityInDO nestRtkEntityInDO) {
        NestRtkEntity nestRtkEntity = new NestRtkEntity();
        nestRtkEntity.setEnable(nestRtkEntityInDO.getEnable());
        nestRtkEntity.setExpireTime(nestRtkEntityInDO.getExpireTime());
        nestRtkEntity.setBaseNestId(nestRtkEntityInDO.getBaseNestId());
        return nestRtkEntity;
    }

    private NestRtkOutDO.NestRtkEntityOutDO toNestRtkEntityOutDO(NestRtkEntity nestRtkEntity) {
        NestRtkOutDO.NestRtkEntityOutDO nestRtkEntityOutDO = new NestRtkOutDO.NestRtkEntityOutDO();
        nestRtkEntityOutDO.setEnable(nestRtkEntity.getEnable());
        nestRtkEntityOutDO.setExpireTime(nestRtkEntity.getExpireTime());
        nestRtkEntityOutDO.setBaseNestId(nestRtkEntity.getBaseNestId());
        return nestRtkEntityOutDO;
    }

    @Override
    public int insert(NestRtkInDO.NestRtkEntityInDO nestRtkEntityInDO) {

        NestRtkEntity nestRtkEntity = toNestRtkEntity(nestRtkEntityInDO);
        LocalDateTime now = LocalDateTime.now();
        nestRtkEntity.setCreateTime(now);
        nestRtkEntity.setModifyTime(now);
        return nestRtkMapper.insert(nestRtkEntity);
    }

    @Override
    public NestRtkOutDO.NestRtkEntityOutDO selectByNestId(String nestId) {

        LambdaQueryWrapper<NestRtkEntity> rtkEntityLambdaQueryWrapper = Wrappers.lambdaQuery(NestRtkEntity.class)
                .eq(NestRtkEntity::getBaseNestId, nestId).eq(NestRtkEntity::getDeleted, 0);
        List<NestRtkEntity> nestRtkEntityList = nestRtkMapper.selectList(rtkEntityLambdaQueryWrapper);
        if (CollUtil.isNotEmpty(nestRtkEntityList)) {
            NestRtkEntity nestRtkEntity = nestRtkEntityList.get(0);
            NestRtkOutDO.NestRtkEntityOutDO nestRtkEntityOutDO = toNestRtkEntityOutDO(nestRtkEntity);
            return nestRtkEntityOutDO;
        }
        return null;
    }

    @Override
    public int updateByNestId(NestRtkInDO.NestRtkEntityInDO nestRtkEntityInDO) {

        if (StringUtils.isBlank(nestRtkEntityInDO.getBaseNestId())) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IS_EXIST_NESTID.getContent()));
        }

        NestRtkEntity nestRtkEntity = toNestRtkEntity(nestRtkEntityInDO);
        nestRtkEntity.setModifyTime(LocalDateTime.now());
        return nestRtkMapper.updateByNestId(nestRtkEntity);
    }

    @Override
    public int deleteByNestId(String nestId) {
        return nestRtkMapper.deleteByNestId(nestId);
    }
}
