package com.imapcloud.nest.service.impl;

import cn.hutool.core.stream.CollectorUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.mapper.MapManageMapper;
import com.imapcloud.nest.model.MapManageEntity;
import com.imapcloud.nest.service.MapManageService;
import com.imapcloud.nest.utils.Query;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.OrgAccountService;
import org.apache.commons.collections4.bag.TreeBag;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 地图管理 业务层实现
 */
@Service("mapManageService")
public class MapManageServiceImpl extends ServiceImpl<MapManageMapper, MapManageEntity> implements MapManageService {

    @Resource
    private OrgAccountService orgAccountService;

    @Override
    public IPage<MapManageEntity> queryPage(Map<String, Object> params){
        // 查询用户单位信息
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        params.put("orgCode", orgCode);
        return getBaseMapper().queryPage(new Query<MapManageEntity>().getPage(params), params);
    }

    @Override
    public List<MapManageEntity> listAll(Map<String, Object> params){
        String orgCode = (String) params.get("orgCode");
        if (!StringUtils.hasText(orgCode)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_UNIT_CODE_CANNOT_BE_EMPTY.getContent()));
        }
        List<MapManageEntity> mapManageEntities = getBaseMapper().listAll(params);
        if (CollectionUtils.isEmpty(mapManageEntities)) {
            this.saveUnitAddMap(orgCode);
        }

        List<MapManageEntity> map = getBaseMapper().listAll(params);
        for (MapManageEntity entity : map) {
            String mapKey = entity.getMapKey();
            if (!StringUtils.isEmpty(mapKey)) {
                entity.setName(MessageUtils.getMessage(entity.getMapKey()));
            }
        }

        return map;
    }

    @Override
    public MapManageEntity byId(Integer id){
        return getBaseMapper().byId(id);
    }

    @Override
    public boolean saveMap(MapManageEntity entity){
        return save(entity);
    }

    @Override
    public void saveUnitAddMap(String orgCode){
        MapManageEntity mapManage = new MapManageEntity();
        mapManage.setNoManage((byte) 0);
        mapManage.setChecked((byte) 1);
        mapManage.setDefaultChecked((byte) 0);
        mapManage.setEditable((byte) 0);
        mapManage.setName("天地图");
        mapManage.setMapKey("geoai_uos_map_sky");
        mapManage.setUrl("https://t0.tianditu.gov.cn/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&" +
                "LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&" +
                "TILECOL={x}&tk=c2fa9f14e6e0ce6254c65ad064e3dc9c");
        mapManage.setMaximumLevel(17);
        mapManage.setOrgCode(orgCode);
        save(mapManage);
        mapManage.setId(null);
        mapManage.setChecked((byte) 0);
        mapManage.setName("openStreet地图");
        mapManage.setMapKey("geoai_uos_map_openstreet");
        mapManage.setUrl("https://c.tile.thunderforest.com/cycle/{z}/{x}/{y}.png?apikey=6170aad10dfd42a38d4d8c709a536f38");
        save(mapManage);
        mapManage.setId(null);
        mapManage.setName("夜间地图");
        mapManage.setMapKey("geoai_uos_map_nighttime");
        mapManage.setUrl("https://map.geoq.cn/ArcGIS/rest/services/ChinaOnlineStreetPurplishBlue/MapServer/tile/{z}/{y}/{x}");
        save(mapManage);
    }




}
