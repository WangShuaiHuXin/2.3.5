package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.DjiBuildPushUrlInDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestOutDTO;
import com.imapcloud.nest.v2.web.vo.req.AdminNestReqVO;
import com.imapcloud.nest.v2.web.vo.resp.AdminNestRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName AdminNestDetailTransformer.java
 * @Description AdminNestDetailTransformer
 * @createTime 2022年08月19日 14:07:00
 */
@Mapper(componentModel = "spring")
public interface AdminNestDetailTransformer {

    AdminNestDetailTransformer INSTANCES = Mappers.getMapper(AdminNestDetailTransformer.class);

    /**
     * 转换出口
     *
     * @param reqVO
     * @return
     */
    AdminNestRespVO.NestDetailRespVO transform(BaseNestOutDTO.NestDetailOutDTO reqVO);

    DjiBuildPushUrlInDTO transform(AdminNestReqVO.DjiUavPushUrlReqVO reqVO);
}
