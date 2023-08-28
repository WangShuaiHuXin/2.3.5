package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.enums.GisDataFileRouteCheckStatusEnum;
import com.imapcloud.nest.enums.GisDataFileRouteDataEnum;
import com.imapcloud.nest.enums.GisDataFileRouteSourceEnum;
import com.imapcloud.nest.mapper.GisdataFileRouteMapper;
import com.imapcloud.nest.model.GisdataFileRouteEntity;
import com.imapcloud.nest.service.GisdataFileRouteService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 地理信息文件路径表 服务实现类
 * </p>
 *
 * @author root
 * @since 2020-09-23
 */
@Service
public class GisdataFileRouteServiceImpl extends ServiceImpl<GisdataFileRouteMapper, GisdataFileRouteEntity> implements GisdataFileRouteService {

    @Override
    public List<GisdataFileRouteEntity> listFileRouteBy(GisdataFileRouteEntity gisdataFileRouteEntity) {
        Integer type = gisdataFileRouteEntity.getType();
        Integer checkStatus = null;
        String orgCode = gisdataFileRouteEntity.getOrgCode();

        // source=0为地图请求，只展示图层管理中勾选展示的数据
        if (GisDataFileRouteSourceEnum.DDZT.getCode().equals(gisdataFileRouteEntity.getSource())) {
            checkStatus = GisDataFileRouteCheckStatusEnum.LOAD_DEFAULT.getCode();
        }
        // 需要过滤权限
        String visibleOrgCode = TrustedAccessTracerHolder.get().getOrgCode();
        List<GisdataFileRouteEntity> gisdataFileRouteEntityList = baseMapper.getGisDataFileRouteList(visibleOrgCode, type, orgCode, checkStatus, gisdataFileRouteEntity.getName());

        if (!CollectionUtils.isEmpty(gisdataFileRouteEntityList)) {
            //为了适应调度的图层展示，需要调一下，当type为null，就是在做调度中台的刷新
            if (type == null) {
                List<GisdataFileRouteEntity> routeEntities = gisdataFileRouteEntityList.stream().filter(it -> !GisDataFileRouteDataEnum.AILLINE.getCode().equals(it.getType())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(routeEntities)) {
                    return null;
                }
                gisdataFileRouteEntityList = routeEntities;
            }
        }
        return gisdataFileRouteEntityList;
    }
}
