package com.imapcloud.nest.controller;

import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/sys/debug")
public class SystemDebugController {

    @GetMapping("/get/channel/service/details")
    public RestRes getChannelServiceDetails() {
        Map<String, Object> details = ChannelService.getDetails();
        return RestRes.ok(details);
    }
}
