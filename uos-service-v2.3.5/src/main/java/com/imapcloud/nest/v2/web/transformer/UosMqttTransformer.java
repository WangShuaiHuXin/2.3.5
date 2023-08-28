package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.UosMqttCreationInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosMqttModifyInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosMqttPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosMqttQueryOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosMqttSimpleOutDTO;
import com.imapcloud.nest.v2.web.vo.req.UosMqttCreationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosMqttModifyReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosMqttPageReqVO;
import com.imapcloud.nest.v2.web.vo.resp.UosMqttQueryRespVO;
import com.imapcloud.nest.v2.web.vo.resp.UosMqttSimpleRespVO;
import org.mapstruct.Mapper;

/**
 * @Classname UosMqttTransformer
 * @Description Mqtt代理地址转换器
 * @Date 2022/8/16 15:13
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface UosMqttTransformer {

    UosMqttCreationInDTO transform(UosMqttCreationReqVO req);

    UosMqttModifyInDTO transform(UosMqttModifyReqVO req);

    UosMqttPageInDTO transform(UosMqttPageReqVO req);

    UosMqttQueryRespVO transform(UosMqttQueryOutDTO dto);

    UosMqttSimpleRespVO transform(UosMqttSimpleOutDTO in);
}
