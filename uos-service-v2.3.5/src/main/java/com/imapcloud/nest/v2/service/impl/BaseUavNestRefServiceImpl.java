package com.imapcloud.nest.v2.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.BaseUavNestRefEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseUavNestRefMapper;
import com.imapcloud.nest.v2.service.BaseUavNestRefService;
import com.imapcloud.nest.v2.service.converter.BaseUavNestConverter;
import com.imapcloud.nest.v2.service.dto.out.BaseNestUavOutDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 基站与无人机关系表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Service
public class BaseUavNestRefServiceImpl implements BaseUavNestRefService {

    @Resource
    private BaseUavNestRefMapper baseUavNestRefMapper;

    @Override
    public List<BaseNestUavOutDTO> listNestUavIds(List<String> nestIdList) {
        LambdaQueryWrapper<BaseUavNestRefEntity> wrapper = Wrappers.lambdaQuery(BaseUavNestRefEntity.class)
                .in(BaseUavNestRefEntity::getNestId, nestIdList)
                .select(BaseUavNestRefEntity::getNestId, BaseUavNestRefEntity::getUavId);

        List<BaseUavNestRefEntity> baseUavNestRefEntities = baseUavNestRefMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(baseUavNestRefEntities)) {
            return Collections.emptyList();
        }
        List<BaseNestUavOutDTO> collect = baseUavNestRefEntities.stream().map(BaseUavNestConverter.INSTANCES::convert).collect(Collectors.toList());
        return collect;
    }

    /**
     * 根据uavSn查询基站Sn
     *
     * @param uavSn
     * @return
     */
    @Override
    public String getNestUUIDByUavSN(String uavSn) {


        return null;
    }
}
