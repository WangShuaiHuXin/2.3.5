package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.UosRegionCreationInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosRegionModificationInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosRegionPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionQueryInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRegionSimpleOutDTO;
import com.imapcloud.nest.v2.web.vo.req.UosRegionCreationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosRegionModificationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosRegionPageReqVO;
import com.imapcloud.nest.v2.web.vo.resp.UosRegionQueryRespVO;
import com.imapcloud.nest.v2.web.vo.resp.UosRegionSimpleRespVO;
import org.mapstruct.Mapper;

/**
 * @Classname UosRegionTransformer
 * @Description 区域管理转换器
 * @DATE 2022/8/11 14:11
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface UosRegionTransformer {

    UosRegionCreationInDTO transform(UosRegionCreationReqVO req);

    UosRegionModificationInDTO transform(UosRegionModificationReqVO req);

    UosRegionPageInDTO transform(UosRegionPageReqVO req);

    UosRegionQueryRespVO transform(UosRegionQueryInfoOutDTO in);

    UosRegionSimpleRespVO transform(UosRegionSimpleOutDTO in);


}
