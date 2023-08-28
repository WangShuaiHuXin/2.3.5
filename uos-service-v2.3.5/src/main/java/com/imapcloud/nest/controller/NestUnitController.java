package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.NestOrgRefService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hc
 * @since 2021-05-20
 */
@RestController
@RequestMapping("/nestUnit")
public class NestUnitController {

    @Resource
    private NestOrgRefService nestOrgRefService;

    /*获取每个单位及子单位的所有的在启用的机巢*/
    @PostMapping("/listNestBy/depart/subdepart")
    public RestRes listNestByDepartSubdepart(String unitId) {
        List<Object> nestList = nestOrgRefService.listOrgNestInfos(unitId);
        /*在返回的时候，要加上机巢数*/
        Map<String, Object> map = new HashMap<>(2);
        map.put("departNestList", nestList);
        return CollectionUtils.isEmpty(nestList) ? RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_FIND_THE_RELATED_NEST_INFORMATION.getContent())) : RestRes.ok(map);
    }

}

