package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.manager.dataobj.in.AccountInfoModificationInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PasswordModificationInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.SignInInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.SignInOutDO;
import com.imapcloud.nest.v2.service.dto.in.AccountInfoModificationInDTO;
import com.imapcloud.nest.v2.service.dto.in.PasswordModificationInDTO;
import com.imapcloud.nest.v2.service.dto.in.SignInInDTO;
import com.imapcloud.nest.v2.service.dto.out.AccountDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.AccountInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.SignInOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 账号信息转换器
 * @author Vastfy
 * @date 2022/4/25 16:09
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface AccountConverter {

    SignInInDO convert(SignInInDTO in);

    SignInOutDTO convert(SignInOutDO in);

    @Mapping(source = "accountId", target = "id")
    AccountInfoOutDTO convert(AccountOutDO in);

    AccountDetailOutDTO convertDetail(AccountOutDO in);

    @Mapping(source = "realName", target = "name")
    AccountInfoModificationInDO convert(AccountInfoModificationInDTO in);

    PasswordModificationInDO convert(PasswordModificationInDTO in);

}
