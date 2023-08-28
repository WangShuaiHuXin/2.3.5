package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.BaseNestAccountEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseNestAccountMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BaseNestAccountManagerImpl implements BaseNestAccountManager {

    @Resource
    private BaseNestAccountMapper baseNestAccountMapper;

    @Override
    public BaseNestAccountOutDO selectByUserId(String accountId) {
        List<BaseNestAccountEntity> baseNestAccountEntities = baseNestAccountMapper.selectList(Wrappers.<BaseNestAccountEntity>lambdaQuery().eq(BaseNestAccountEntity::getAccountId, accountId)
        );
        BaseNestAccountOutDO outDO=new BaseNestAccountOutDO();
        if (CollectionUtil.isEmpty(baseNestAccountEntities)) {
            return null;
        }
        List<String> baseNestIds = baseNestAccountEntities.stream().map(e -> e.getBaseNestId()).collect(Collectors.toList());
        List<String> nestIds = baseNestAccountEntities.stream().map(e -> e.getNestId()).collect(Collectors.toList());
        outDO.setAccountId(accountId);
        outDO.setBaseNestId(baseNestIds);
        return outDO;
    }
}
