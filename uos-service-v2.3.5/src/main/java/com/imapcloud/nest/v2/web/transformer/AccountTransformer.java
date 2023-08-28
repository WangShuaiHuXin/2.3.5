package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.web.vo.req.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * 账号信息转换器
 * @author Vastfy
 * @date 2022/4/25 16:09
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface AccountTransformer {

    AccountInfoModificationInDTO transform(AccountInfoModificationReqVO signUpReq);

    AccountInfoInDTO transform(AccountInfoReqVO signUpReq);

    AccountCreationInDTO transform(AccountCreationReqVO in);

    AccountModificationInDTO transform(AccountModificationReqVO in);

    @Mappings({
            @Mapping(source = "originPassword", target = "oldPassword"),
            @Mapping(source = "destPassword", target = "newPassword")
    })
    PasswordModificationInDTO transform(PasswordModificationReqVO signUpReq);

}
