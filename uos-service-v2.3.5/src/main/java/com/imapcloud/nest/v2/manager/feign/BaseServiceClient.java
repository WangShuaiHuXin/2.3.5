package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.ListDictItemInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DictItemInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.SimpleDictItemInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "base-service-client", name = "geoai-base-service",
        configuration = TokenRelayConfiguration.class)
public interface BaseServiceClient {

    @PostMapping("dictionaries/list/item")
    Result<List<DictItemInfoOutDO>> listDictItemInfo(@RequestBody ListDictItemInfoInDO dictItemInfoInDO);

    @GetMapping("dictionaries/items")
    Result<List<SimpleDictItemInfoOutDO>> listDictItemInfos(@RequestParam("dictCode") String dictCode);

}


