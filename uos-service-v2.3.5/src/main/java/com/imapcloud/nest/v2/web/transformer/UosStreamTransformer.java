package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.out.StreamOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.StreamRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosBatteryEnableTransformer.java
 * @Description UosBatteryEnableTransformer
 * @createTime 2022年08月19日 14:07:00
 */
@Mapper(componentModel = "spring")
public interface UosStreamTransformer {

    UosStreamTransformer INSTANCES = Mappers.getMapper(UosStreamTransformer.class);

    /**
     * 转换出口
     *
     * @param reqVO
     * @return
     */
    StreamRespVO transform(StreamOutDTO reqVO);


}
