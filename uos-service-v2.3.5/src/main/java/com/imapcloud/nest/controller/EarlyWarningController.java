package com.imapcloud.nest.controller;

import com.imapcloud.nest.pojo.dto.reqDto.EarlyWarningAddDto;
import com.imapcloud.nest.service.EarlyWarningService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 天气/区域警告配置 控制层
 */
@RestController
@RequestMapping("/earlyWarning")
public class EarlyWarningController {

    @Resource
    private EarlyWarningService earlyWarningService;

    private RestRes getRes(Object object){
        RestRes res = RestRes.ok();
        Map<String, Object> map = new HashMap<>();
        map.put("result", object);
        res.setParam(map);
        return res;
    }


    /**
     * 返回分页数据
     * @param params 分页参数
     * @return RestRes
     */
    @GetMapping("/page")
    public RestRes queryPage(@RequestParam Map<String, Object> params){
        return getRes(earlyWarningService.queryPage(params));
    }

    /**
     * 根据id返回数据
     * @param id 主键
     * @return RestRes
     */
    @GetMapping("/info/{id}")
    public RestRes byId(@PathVariable("id") Integer id){
        return getRes(earlyWarningService.ById(id));
    }

    /**
     * 保存
     * @param dto 天气/区域警告配置 dto
     * @return RestRes
     */
    @PostMapping("/save")
    public RestRes save(@RequestBody EarlyWarningAddDto dto){
        earlyWarningService.saveEntity(dto);
        return RestRes.ok();
    }

    /**
     * 更新
     * @param dto 天气/区域警告配置 dto
     * @return RestRes
     */
    @PostMapping("/update")
    public RestRes update(@RequestBody EarlyWarningAddDto dto){
        earlyWarningService.updateEntity(dto);
        return RestRes.ok();
    }

    /**
     * 删除
     * @param ids id数组
     * @return RestRes
     */
    @PostMapping("/delete")
    public RestRes deleteByIds(@RequestBody Integer[] ids){
        earlyWarningService.deleteByIds(ids);
        return RestRes.ok();
    }

    @GetMapping("/getEarlyWarningByNestId/{nestId}")
    public RestRes getEarlyWarningByNestId(@PathVariable("nestId")String nestId){
        return getRes(earlyWarningService.FirstEarlyWarningByNestId(nestId));
    }

    @GetMapping("/getEarlyWarningByUserId/{userId}")
    public RestRes getEarlyWarningByUserId(@PathVariable("userId")Long userId){
        return getRes(earlyWarningService.FirstEarlyWarningByUserId(userId));
    }

    /**
     * 更新状态
     * @param state 状态 0-未启用  1-启用
     * @param id 主键
     * @return RestRes
     */
    @PostMapping("/updateState/{id}/{state}")
    public RestRes updateState(@PathVariable Integer id, @PathVariable Byte state){
        earlyWarningService.updateState(state, id);
        return RestRes.ok();
    }

}
