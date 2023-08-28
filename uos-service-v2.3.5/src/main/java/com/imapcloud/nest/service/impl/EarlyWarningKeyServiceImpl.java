package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.mapper.EarlyWarningKeyMapper;
import com.imapcloud.nest.model.EarlyWarningKeyEntity;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.dto.EarlyWarningKeyDto;
import com.imapcloud.nest.pojo.dto.NestDto;
import com.imapcloud.nest.service.EarlyWarningKeyService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("earlyWarningKey")
public class EarlyWarningKeyServiceImpl extends ServiceImpl<EarlyWarningKeyMapper, EarlyWarningKeyEntity>
        implements EarlyWarningKeyService {

    @Resource
    private NestService nestService;

    @Resource
    private UosOrgManager uosOrgManager;

    @Override
    @Transactional
    public boolean saveAll(EarlyWarningKeyDto entity){
        if (entity == null){
            return false;
        }
        List<OrgSimpleOutDO> orgInfos = uosOrgManager.listAllOrgInfos();
        for (OrgSimpleOutDO unit : orgInfos){
            entity.setUnitId(unit.getOrgCode());
            saveByUnitId(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean saveByUnitId(EarlyWarningKeyDto entity){
        List<NestDto> nestEntities;
        if (entity == null){
            return false;
        }
        nestEntities = nestService.listNestByOrgCode(entity.getUnitId());
        if (nestEntities == null || nestEntities.size() == 0) return false;
        for (NestEntity nest: nestEntities){
            EarlyWarningKeyEntity newEntity = new EarlyWarningKeyEntity();
            if (entity.getName() == null){
                newEntity.setName(nest.getName() + "天气预警秘钥");
            }
            saveByNest(nest, newEntity);
        }
        return true;
    }

    @Override
    public boolean saveByNestId(EarlyWarningKeyEntity entity){
        if (entity == null){
            return false;
        }
        NestEntity nest = nestService.getById(entity.getNestId());
        saveByNest(nest, entity);
        return true;
    }

    private void saveByNest(NestEntity nest, EarlyWarningKeyEntity entity){
        Assert.isNull(nest, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_PARAMETER_CANNOT_BE_EMPTY.getContent()));
        entity.setNestId(nest.getId());
        getBaseMapper().saveEntity(entity);
    }

}
