package com.imapcloud.nest.v2.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.v2.dao.entity.BaseUavAppRefEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseUavAppRefMapper;
import com.imapcloud.nest.v2.service.BaseUavAppRefService;
import com.imapcloud.nest.v2.service.dto.in.SaveUavAppInDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 移动终端无人机关联表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Service
public class BaseUavAppRefServiceImpl implements BaseUavAppRefService {

    @Resource
    private BaseUavAppRefMapper baseUavAppRefMapper;

    @Override
    public Boolean saveUavAppRef(SaveUavAppInDTO saveUavAppInDTO) {
        if (Objects.nonNull(saveUavAppInDTO)) {
            ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
            BaseUavAppRefEntity ent = new BaseUavAppRefEntity();
            ent.setUavId(saveUavAppInDTO.getUavId());
            ent.setAppId(saveUavAppInDTO.getAppId());
            ent.setCreatorId(trustedAccessTracer.getAccountId());
            ent.setModifierId(trustedAccessTracer.getAccountId());
            int insert = baseUavAppRefMapper.insert(ent);
            return insert > 0;
        }
        return false;
    }

    @Override
    public Boolean softDeleteRef(String appId, String uavId) {
        if (Objects.nonNull(appId) && Objects.nonNull(uavId)) {
            int i = baseUavAppRefMapper.updateDeletedByAppIdAndUavId(appId, uavId, 1);
            return i > 0;
        }
        return false;
    }
}
