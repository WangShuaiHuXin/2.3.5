package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.UosRoleQueryDTO;
import com.imapcloud.nest.v2.service.dto.out.UosPageResourceNodeOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleCreationInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleModificationInDTO;
import com.imapcloud.nest.v2.web.vo.req.UosRoleBasicRespVO;
import com.imapcloud.nest.v2.web.vo.req.UosRoleCreationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosRoleModificationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosRoleQueryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.UosPageResourceNodeRespVO;
import org.mapstruct.Mapper;

/**
 * 转化器
 * @author boluo
 * @date 2022-05-19
 */
@Mapper(componentModel = "spring")
public interface UosRoleTransformer {

    UosRoleQueryDTO transform(UosRoleQueryReqVO in);

    UosRoleCreationInDTO transform(UosRoleCreationReqVO in);

    UosRoleModificationInDTO transform(UosRoleModificationReqVO in);

    UosRoleBasicRespVO transform(UosRoleInfoOutDTO in);

    UosPageResourceNodeRespVO transform(UosPageResourceNodeOutDTO in);

}
