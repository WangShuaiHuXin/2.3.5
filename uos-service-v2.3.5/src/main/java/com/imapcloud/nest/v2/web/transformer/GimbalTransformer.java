package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.GimbalInDTO;
import com.imapcloud.nest.v2.web.vo.req.GimbalReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName AudioTransformer.java
 * @Description AudioTransformer
 * @createTime 2022年08月17日 18:14:00
 */
@Mapper(componentModel = "spring")
public interface GimbalTransformer {

    GimbalTransformer INSTANCES = Mappers.getMapper(GimbalTransformer.class);

    /**
     * 转换入口
     *
     * @param reqVO
     * @return
     */
    GimbalInDTO transform(GimbalReqVO reqVO);


}
