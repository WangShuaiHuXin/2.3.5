package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.ImMessageInDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * im消息客户端
 *
 * @author boluo
 * @date 2023-02-15
 */
@FeignClient(contextId = "im-message-client", name = "geoai-im-service",
        configuration = TokenRelayConfiguration.class)
public interface ImMessageClient {

    /**
     * 发送
     *
     * @param message 消息
     * @return {@link Result}<{@link String}>
     */
    @PostMapping("message/send")
    Result<String> send(@RequestBody ImMessageInDO message);
}
