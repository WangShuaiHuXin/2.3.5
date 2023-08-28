package com.imapcloud.nest.controller;

import com.imapcloud.nest.model.DefectTypeTrafficEntity;
import com.imapcloud.nest.service.DefectTypeTrafficService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 */
@RestController
@RequestMapping("/defectTypeTraffic")
public class DefectTypeTrafficController {

    @Resource
    private DefectTypeTrafficService defectTypeTrafficService;

    @PostMapping("/save/or/update/defect/type")
    public RestRes addSysTag(@RequestBody DefectTypeTrafficEntity defectTypeTrafficEntity) {
        return defectTypeTrafficService.saveOrUpdateDefectType(defectTypeTrafficEntity);
    }

    @GetMapping("/all/list")
    public RestRes getAllList(String name,String unitId,Integer type,Integer tagId) {
        return defectTypeTrafficService.getAllList(name,unitId,type,tagId);
    }

    @PostMapping("/delete")
    public RestRes deleteDefectType(@RequestBody Integer[] ids) {
        return defectTypeTrafficService.deleteDefectType(Arrays.asList(ids));
    }
}

