package com.imapcloud.nest.controller;

import com.imapcloud.nest.model.EarlyWarningKeyEntity;
import com.imapcloud.nest.pojo.dto.EarlyWarningKeyDto;
import com.imapcloud.nest.service.EarlyWarningKeyService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/earlyWarning/key")
public class EarlyWarningKeyController {

    @Resource
    private EarlyWarningKeyService earlyWarningKeyService;

    private RestRes setResult(Object result){
        RestRes res = new RestRes();
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        res.setParam(map);
        return res;
    }

    @PostMapping("/saveAll")
    public RestRes saveAll(@RequestBody EarlyWarningKeyDto entity){
        return setResult(earlyWarningKeyService.saveAll(entity));
    }

    @PostMapping("/saveByNestId")
    public RestRes saveByNestId(@RequestBody EarlyWarningKeyEntity entity){
        return setResult(earlyWarningKeyService.saveByNestId(entity));
    }

    @PostMapping("/saveByUnitId")
    public RestRes saveByUnitId(@RequestBody EarlyWarningKeyDto entity){
        return setResult(earlyWarningKeyService.saveByUnitId(entity));
    }

}
