package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.EarlyWarningUnitMapper;
import com.imapcloud.nest.model.EarlyWarningUnitEntity;
import com.imapcloud.nest.service.EarlyWarningUnitService;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.dto.UnitEntityDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service("earlyWarningUnitService")
public class EarlyWarningUnitServiceImpl extends ServiceImpl<EarlyWarningUnitMapper, EarlyWarningUnitEntity> implements EarlyWarningUnitService {

    @Resource
    private UosOrgManager uosOrgManager;

    @Override
    public List<UnitEntityDTO> getUnitByEarlyWarningId(Integer earlyWarningId){
        EarlyWarningUnitMapper baseMapper = getBaseMapper();
        LambdaQueryWrapper<EarlyWarningUnitEntity> condition = Wrappers.lambdaQuery(EarlyWarningUnitEntity.class)
                .eq(EarlyWarningUnitEntity::getEarlyWarningId, earlyWarningId);
        List<EarlyWarningUnitEntity> entities = baseMapper.selectList(condition);
        if(!CollectionUtils.isEmpty(entities)){
            List<String> orgCodes = entities.stream().map(EarlyWarningUnitEntity::getOrgCode).distinct().collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(orgCodes)){
                List<OrgSimpleOutDO> orgInfos = uosOrgManager.listOrgInfos(orgCodes);
                if (!CollectionUtils.isEmpty(orgInfos)) {
                    return orgInfos.stream()
                            .map(r -> {
                                UnitEntityDTO ue = new UnitEntityDTO();
                                ue.setId(r.getOrgCode());
                                ue.setName(r.getOrgName());
                                return ue;
                            })
                            .collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }

}
