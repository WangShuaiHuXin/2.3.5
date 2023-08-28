package com.imapcloud.nest.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.model.BackLandFunEntity;
import com.imapcloud.nest.pojo.vo.req.BackLandFunReqVO;
import com.imapcloud.nest.pojo.vo.resp.BackLandFunRespVO;
import com.imapcloud.nest.service.BackLandFunService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 备降点表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-10-07
 */
@RestController
@RequestMapping("/backLandFun")
public class BackLandFunController {
    @Autowired
    private BackLandFunService backLandFunService;

    @GetMapping("/list/{nestId}")
    public RestRes getNestAllBackLandPoint(@PathVariable String nestId) {
        if (nestId != null) {
            List<BackLandFunEntity> backLandFunEntities= backLandFunService.getNestAllBackLandPoint(nestId);
            List<BackLandFunRespVO> backLandFunRespVOList = Lists.newLinkedList();
            if (CollectionUtil.isNotEmpty(backLandFunEntities)) {
                for (BackLandFunEntity backLandFunEntity : backLandFunEntities) {
                    BackLandFunRespVO backLandFunRespVO = new BackLandFunRespVO();
                    backLandFunRespVO.setBackLandFunId(backLandFunEntity.getBackLandFunId());
                    backLandFunRespVO.setName(backLandFunEntity.getName());
                    backLandFunRespVO.setNestId(backLandFunEntity.getBaseNestId());
                    backLandFunRespVO.setBackLandPointLng(backLandFunEntity.getBackLandPointLng());
                    backLandFunRespVO.setBackLandPointLat(backLandFunEntity.getBackLandPointLat());
                    backLandFunRespVO.setGotoBackLandPointAlt(backLandFunEntity.getGotoBackLandPointAlt());
                    backLandFunRespVOList.add(backLandFunRespVO);
                }
            }
            Map map = new HashMap(2);
            map.put("backLandFunEntities", backLandFunRespVOList);
            return RestRes.ok(map);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @PostMapping("/update")
    public RestRes update(@RequestBody BackLandFunReqVO backLandFunReqVO){
        LambdaUpdateWrapper<BackLandFunEntity> updateWrapper = Wrappers.lambdaUpdate(BackLandFunEntity.class)
                .eq(BackLandFunEntity::getBackLandFunId, backLandFunReqVO.getBackLandFunId())
                .set(BackLandFunEntity::getBaseNestId, backLandFunReqVO.getNestId())
                .set(BackLandFunEntity::getName, backLandFunReqVO.getName())
                .set(BackLandFunEntity::getBackLandPointLat, backLandFunReqVO.getBackLandPointLat())
                .set(BackLandFunEntity::getBackLandPointLng, backLandFunReqVO.getBackLandPointLng())
                .set(BackLandFunEntity::getGotoBackLandPointAlt, backLandFunReqVO.getGotoBackLandPointAlt());
        backLandFunService.update(updateWrapper);
        return RestRes.ok();
    }

    @GetMapping("/delete")
    public RestRes deleteByIds(String backLandFunId){
        LambdaQueryWrapper<BackLandFunEntity> wrapper = Wrappers.lambdaQuery(BackLandFunEntity.class).eq(BackLandFunEntity::getBackLandFunId, backLandFunId);
        backLandFunService.remove(wrapper);
        return RestRes.ok();
    }

    @PostMapping("/save")
    public RestRes save(@RequestBody BackLandFunReqVO backLandFunReqVO){
        BackLandFunEntity backLandFunEntity = new BackLandFunEntity();
        backLandFunEntity.setBackLandFunId(BizIdUtils.snowflakeIdStr());
        backLandFunEntity.setName(backLandFunReqVO.getName());
        backLandFunEntity.setBaseNestId(backLandFunReqVO.getNestId());
        backLandFunEntity.setBackLandPointLng(backLandFunReqVO.getBackLandPointLng());
        backLandFunEntity.setBackLandPointLat(backLandFunReqVO.getBackLandPointLat());
        backLandFunEntity.setGotoBackLandPointAlt(backLandFunReqVO.getGotoBackLandPointAlt());

        backLandFunService.save(backLandFunEntity);
        return RestRes.ok();
    }
}
