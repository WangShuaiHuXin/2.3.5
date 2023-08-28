package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.out.BatteryOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.BatteryRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosBatteryTransformer.java
 * @Description UosBatteryTransformer
 * @createTime 2022年08月19日 14:07:00
 */
@Mapper(componentModel = "spring")
public interface UosBatteryTransformer {

    UosBatteryTransformer INSTANCES = Mappers.getMapper(UosBatteryTransformer.class);

    /**
     * 转换出口
     *
     * @param reqVO
     * @return
     */
    BatteryRespVO transform(BatteryOutDTO reqVO);


}
