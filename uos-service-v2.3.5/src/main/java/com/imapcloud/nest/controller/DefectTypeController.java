package com.imapcloud.nest.controller;


import com.imapcloud.nest.model.DefectTypeEntity;
import com.imapcloud.nest.service.DefectTypeService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-02-23
 */
@RestController
@RequestMapping("/defectType")
public class DefectTypeController {

    @Resource
    private DefectTypeService defectTypeService;

    @PostMapping("/save/or/update/defect/type")
    public RestRes addSysTag(@RequestBody DefectTypeEntity defectTypeEntity) {
        return defectTypeService.saveOrUpdateDefectType(defectTypeEntity);
    }

    @GetMapping("/all/list")
    public RestRes getAllList(@RequestParam(required = false) String name) {
        return defectTypeService.getAllList(name);
    }

    @PostMapping("/delete")
    public RestRes deleteDefectType(@RequestBody Integer[] ids) {
        return defectTypeService.deleteDefectType(Arrays.asList(ids));
    }
}

