package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.mapper.MapPlottingMapper;
import com.imapcloud.nest.model.MapPlottingEntity;
import com.imapcloud.nest.pojo.dto.MapPlottingDto;
import com.imapcloud.nest.pojo.vo.MapPlottingVO;
import com.imapcloud.nest.service.MapPlottingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 地图标绘 业务层实现
 */
@Service("mapPlottingService")
public class MapPlottingServiceImpl extends ServiceImpl<MapPlottingMapper, MapPlottingEntity> implements MapPlottingService {

    private void setPointList(IPage<MapPlottingVO> iPage){
        List<MapPlottingVO> records = iPage.getRecords();
        setPointList(records);
    }

    private void setPointList(List<MapPlottingVO> records){
        if (records != null && records.size() != 0){
            records.forEach(this::setPointList);
        }
    }

    private void setPointList(MapPlottingVO e){
        String pointStr = e.getPointStr();
        if (StringUtils.isNotBlank(pointStr)){
            String[] pointAry = pointStr.split(";");
            e.setPointList(new ArrayList<>(Arrays.asList(pointAry)));
            e.setPointStr(null);
        }
    }

    @Override
    public IPage<MapPlottingVO> queryPage(Map<String, Object> params) {
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        String visitorName = TrustedAccessTracerHolder.get().getUsername();
        params.put("userId", Long.valueOf(visitorId));

        IPage<MapPlottingVO> iPage = new Page<>();
        List<MapPlottingVO> list = baseMapper.getList(params);
        setPointList(list);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(e -> e.setUserName(visitorName));
        }
        iPage.setRecords(list);
        return iPage;
    }

    @Override
    public MapPlottingVO info(Integer id) {
        return getBaseMapper().byId(id, Long.valueOf(TrustedAccessTracerHolder.get().getAccountId()));
    }

    @Override
    public List<MapPlottingVO> listByUser(){
        List<MapPlottingVO> records = getBaseMapper().listByUser(Long.valueOf(TrustedAccessTracerHolder.get().getAccountId()));
        setPointList(records);
        if(!CollectionUtils.isEmpty(records)){
            records.forEach(e -> e.setUserName(TrustedAccessTracerHolder.get().getUsername()));
        }
        return records;
    }

    @Override
    public boolean savePlotting(MapPlottingDto dto){
        dto.setId(null);
        MapPlottingEntity entity = dto.getMapPlottingEntity();
        entity.setCreatorId(Long.valueOf(TrustedAccessTracerHolder.get().getAccountId()));
        return save(entity);
    }

    @Override
    public boolean updatePlotting(MapPlottingDto dto){
        Assert.isNull(dto.getId(), "id参数不能为空");
        MapPlottingEntity entity = getBaseMapper().selectById(dto.getId());
        Assert.isNull(entity, "未找到id=" + dto.getId() + "的对象");
        Long creatorId = Long.valueOf(TrustedAccessTracerHolder.get().getAccountId());
        if (Objects.equals(entity.getCreatorId(), creatorId)){//判断是否为合法用户在修改地图标绘
            entity = dto.getMapPlottingEntity();
            entity.setCreatorId(creatorId);
            getBaseMapper().updateById(entity);
            return true;
        }else {
            return false;
        }
    }

}
