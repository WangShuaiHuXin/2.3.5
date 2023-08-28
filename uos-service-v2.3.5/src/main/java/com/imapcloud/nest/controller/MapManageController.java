package com.imapcloud.nest.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.MapManageEntity;
import com.imapcloud.nest.service.MapManageService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.OrgAccountService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地图管理 控制层
 */
@RestController
@RequestMapping("/mapManage")
public class MapManageController {

    @Resource
    private MapManageService mapManageService;

    @Resource
    private OrgAccountService orgAccountService;

    private RestRes setResult(Object result){
        RestRes res = new RestRes();
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        res.setParam(map);
        return res;
    }

    @GetMapping("/queryPage")
    public RestRes queryPage(@RequestParam Map<String, Object> params){
        return setResult(mapManageService.queryPage(params));
    }

    @GetMapping("/listAll")
    public RestRes listAll(@RequestParam Map<String, Object> params){

        String key = "orgCode";
        if (params.get(key) == null) {
            // 查询单位ID
            String unitId = TrustedAccessTracerHolder.get().getOrgCode();
            params.put(key, unitId);
        }
        return setResult(mapManageService.listAll(params));
    }

    @GetMapping("/info/{id}")
    public RestRes info(@PathVariable Integer id){
        return setResult(mapManageService.byId(id));
    }

    @PostMapping("/save")
    public RestRes save(@RequestBody MapManageEntity entity){

        if (!StringUtils.hasText(entity.getOrgCode())){
            String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
            entity.setOrgCode(orgCode);
        }
        if (checkName(entity)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_TO_ADD.getContent()));
        }

        return setResult(mapManageService.saveMap(entity));
    }

    private boolean checkName(MapManageEntity entity) {
        // 查询当前单位的地图信息
        LambdaQueryWrapper<MapManageEntity> eq = Wrappers.lambdaQuery(MapManageEntity.class).eq(MapManageEntity::getOrgCode, entity.getOrgCode());
        List<MapManageEntity> list = mapManageService.list(eq);
        for (MapManageEntity mapManageEntity : list) {
            if (mapManageEntity.getName().equals(entity.getName())) {

                if (entity.getId() == null || !entity.getId().equals(mapManageEntity.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @PostMapping("/update")
    public RestRes update(@RequestBody MapManageEntity entity){
        if (checkName(entity)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_SAME_NAME_ALREADY_EXISTS.getContent()));
        }
        return setResult(mapManageService.updateById(entity));
    }

    @PostMapping("/delete")
    public RestRes delete(@RequestBody List<Integer> ids){
        return setResult(mapManageService.removeByIds(ids));
    }

}
