package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.mapper.IllegalVectorMapper;
import com.imapcloud.nest.model.IllegalVectorEntity;
import com.imapcloud.nest.service.IllegalVectorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-07-02
 */
@Service
public class IllegalVectorServiceImpl extends ServiceImpl<IllegalVectorMapper, IllegalVectorEntity> implements IllegalVectorService {

    @Override
    public List<IllegalVectorEntity> getVectorList(Integer dataType) {
            // 查询单位ID
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        LambdaQueryWrapper<IllegalVectorEntity> condition = Wrappers.lambdaQuery(IllegalVectorEntity.class)
                .eq(IllegalVectorEntity::getDataType, dataType)
                .eq(IllegalVectorEntity::getDeleted, false)
                .likeRight(IllegalVectorEntity::getOrgCode, orgCode);
        return this.list(condition);
    }

    @Override
    public void rename(Integer id, String name) {
        this.lambdaUpdate().set(IllegalVectorEntity::getVectorName, name).eq(IllegalVectorEntity::getId, id).update();
    }

    @Override
    public void delete(List idList) {
        this.lambdaUpdate().set(IllegalVectorEntity::getDeleted, 1).in(IllegalVectorEntity::getId, idList).update();
    }

    @Override
    public void upload(String name, String unitId, Integer dataType, String filePath) {
        IllegalVectorEntity illegalVectorEntity = new IllegalVectorEntity();
        illegalVectorEntity.setVectorName(name);
        illegalVectorEntity.setOrgCode(unitId);
        illegalVectorEntity.setVectorUrl(filePath);
        illegalVectorEntity.setDataType(dataType);
        this.save(illegalVectorEntity);
    }
}
