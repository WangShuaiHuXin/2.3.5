package com.imapcloud.nest.controller;


import com.imapcloud.nest.model.IllegalVectorEntity;
import com.imapcloud.nest.service.IllegalVectorService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-07-02
 */
@RestController
@RequestMapping("/illegalVector")
public class IllegalVectorController {

    @Resource
    private IllegalVectorService illegalVectorService;

    @GetMapping("/getAllList")
    public RestRes getVectorList(Integer dataType) {
        List<IllegalVectorEntity> illegalVectorEntityList = illegalVectorService.getVectorList(dataType);
        Map<String, Object> map = new HashMap();
        map.put("list", illegalVectorEntityList);
        return RestRes.ok(map);
    }

    @GetMapping("/rename")
    public RestRes rename(Integer id, String name) {
        illegalVectorService.rename(id, name);
        return RestRes.ok();
    }

    @PostMapping("/delete")
    public RestRes delete(@RequestBody List<Integer> idList) {
        illegalVectorService.delete(idList);
        return RestRes.ok();
    }

    @PostMapping("/upload")
    public RestRes upload(String name, String unitId, Integer dataType, String filePath) {
        illegalVectorService.upload(name, unitId, dataType, filePath);
        return RestRes.ok();
    }


}

