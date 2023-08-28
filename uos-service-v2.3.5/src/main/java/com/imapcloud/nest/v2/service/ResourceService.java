package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.ResourceAccountPageOutDTO;

/**
 * 页面资源服务
 *
 * @author boluo
 * @date 2022-05-23
 */
public interface ResourceService {
    /**
     * 账户列表页面
     * 账户的页面资源信息
     * @param accountId 帐户id
     * @return {@link ResourceAccountPageOutDTO}
     */
    ResourceAccountPageOutDTO accountPageList(String accountId);

}
