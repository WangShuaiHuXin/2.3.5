package com.imapcloud.nest.pojo.dto;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.model.MapPlottingEntity;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import lombok.Data;

import java.util.List;

@Data
public class MapPlottingDto {

    private Integer id;

    private List<String> pointList;

    private String name;

    private Byte type;

    public MapPlottingEntity getMapPlottingEntity(){
        MapPlottingEntity mapPlottingEntity = new MapPlottingEntity();
        Assert.isNull(name, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NAME_PARAMETER_CANNOT_BE_EMPTY.getContent()));
        Assert.isNull(type, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TYPE_PARAMETER_CANNOT_BE_EMPTY.getContent()));
        mapPlottingEntity.setName(name);
        mapPlottingEntity.setType(type);
        mapPlottingEntity.setId(id);
        StringBuilder stringBuilder = new StringBuilder();
        Assert.isNull(pointList, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POINTLIST_PARAMETER_CANNOT_BE_EMPTY.getContent()));
        for (int i = 0; i < pointList.size(); i++) {
            String point = pointList.get(i);
            stringBuilder.append(point);
            if (i != pointList.size() - 1){
                stringBuilder.append(";");
            }
        }
        mapPlottingEntity.setPointList(stringBuilder.toString());
        return mapPlottingEntity;
    }

}
