package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import org.springframework.stereotype.Component;

public interface BaseNestAccountManager {
    BaseNestAccountOutDO selectByUserId(String accountId);
}
