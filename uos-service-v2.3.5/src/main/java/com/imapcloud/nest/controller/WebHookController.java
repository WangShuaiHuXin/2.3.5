package com.imapcloud.nest.controller;

import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class WebHookController {

    @PostMapping("/web/hook")
    public void onWebHook(@RequestBody Map<String, String> param) {
        System.out.println(JSON.toJSONString(param));
    }

    @PostMapping("/cps/mqtt/log")
    public RestRes cpsMqttLog(@RequestBody List<Object> logs) {
        System.out.println(JSON.toJSONString(logs));
        return RestRes.ok();
    }
}
