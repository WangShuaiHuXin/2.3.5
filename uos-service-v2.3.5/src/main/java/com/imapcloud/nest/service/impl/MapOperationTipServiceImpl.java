package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.mapper.MapOperationTipMapper;
import com.imapcloud.nest.model.MapOperationTipEntity;
import com.imapcloud.nest.service.MapOperationTipService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 航线编辑地图操作提示 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-05-24
 */

@Service
public class MapOperationTipServiceImpl extends ServiceImpl<MapOperationTipMapper, MapOperationTipEntity> implements MapOperationTipService {

    private static final String GRID_TYPE = "Grid";


    @Override
    public RestRes switchMapOperationTip(Integer enable) {
        Long id = Long.valueOf(TrustedAccessTracerHolder.get().getAccountId());
        MapOperationTipEntity entity = new MapOperationTipEntity();
        entity.setTip(Objects.equals(enable, 1));
        entity.setCreatorId(id);
        boolean update = this.saveOrUpdate(entity, new UpdateWrapper<MapOperationTipEntity>().lambda().eq(MapOperationTipEntity::getCreatorId, id).isNull(MapOperationTipEntity::getType));
        String str = enable == 1 ? MessageUtils.getMessage(MessageEnum.GROAI_UOS_OPEN.getContent()):MessageUtils.getMessage(MessageEnum.GROAI_UOS_CLOSE.getContent());
        if (update) {
            return RestRes.ok(str + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MAP_OPERATION_SUCCESSFUL.getContent()));
        }
        return RestRes.err(str +MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MAP_OPERATION_FAILED.getContent()));
    }


    @Override
    public RestRes getMapOperationTip() {
        Long id = Long.valueOf(TrustedAccessTracerHolder.get().getAccountId());
        MapOperationTipEntity entity = this.lambdaQuery()
                .eq(MapOperationTipEntity::getCreatorId, id)
                .isNull(MapOperationTipEntity::getType)
                .select(MapOperationTipEntity::getTip)
                .one();

        if (entity != null) {
            Boolean tip = entity.getTip();
            return RestRes.ok("enable", tip ? 1 : 0);
        }

        return RestRes.ok("enable", 1);
    }


    @Override
    public RestRes switchGridOperationTip(Integer enable) {
        Long id = Long.valueOf(TrustedAccessTracerHolder.get().getAccountId());
        MapOperationTipEntity entity = new MapOperationTipEntity();
        entity.setTip(Objects.equals(enable, 1));
        entity.setCreatorId(id);
        entity.setType(GRID_TYPE);
        boolean update = this.saveOrUpdate(entity, new UpdateWrapper<MapOperationTipEntity>().lambda().eq(MapOperationTipEntity::getCreatorId, id).eq(MapOperationTipEntity::getType, GRID_TYPE));
        String str = enable == 1 ? MessageUtils.getMessage(MessageEnum.GROAI_UOS_OPEN.getContent()):MessageUtils.getMessage(MessageEnum.GROAI_UOS_CLOSE.getContent());
        if (update) {
            return RestRes.ok(str + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MAP_OPERATION_SUCCESSFUL.getContent()));
        }
        return RestRes.err(str +MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MAP_OPERATION_FAILED.getContent()));
    }


    @Override
    public RestRes getGridOperationTip() {
        Long id = Long.valueOf(TrustedAccessTracerHolder.get().getAccountId());
        MapOperationTipEntity entity = this.lambdaQuery()
                .eq(MapOperationTipEntity::getCreatorId, id)
                .eq(MapOperationTipEntity::getType, GRID_TYPE)
                .select(MapOperationTipEntity::getTip)
                .one();

        if (entity != null) {
            Boolean tip = entity.getTip();
            return RestRes.ok("enable", tip ? 1 : 0);
        }

        return RestRes.ok("enable", 1);
    }
}
