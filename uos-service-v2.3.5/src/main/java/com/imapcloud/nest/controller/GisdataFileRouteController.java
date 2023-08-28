package com.imapcloud.nest.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.annotation.TrimStr;
import com.imapcloud.nest.model.GisdataFileRouteEntity;
import com.imapcloud.nest.pojo.dto.GisdataFileRouteDto;
import com.imapcloud.nest.service.GisdataFileRouteService;
import com.imapcloud.nest.service.SysUnitService;
import com.imapcloud.nest.utils.HttpRequestUtil;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.UosMaplayerService;
import com.imapcloud.nest.v2.service.dto.UnitEntityDTO;
import com.imapcloud.nest.v2.web.UosMaplayerController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 地理信息文件路径表 前端控制器
 * </p>
 *
 * @author root
 * @since 2020-09-23
 */
@RestController
@Slf4j
@RequestMapping("/gisdataFileRoute")
public class GisdataFileRouteController {

    @Autowired
    private GisdataFileRouteService gisdataFileRouteService;
    @Autowired
    private SysUnitService sysUnitService;

    @Resource
    private UosMaplayerService uosMaplayerService;

    /**
     * 查询图层数据
     * @deprecated 2.1.0, 将在后续版本移除该方法
     * 使用 {@link UosMaplayerController#fetchVisibleMaplayerInfos(com.imapcloud.nest.v2.web.vo.req.MaplayerQueryReqVO)} 替代
     * 或 {@link UosMaplayerController#listUserOrgDisplayedMaplayerInfos(java.lang.Integer)}替代
     */
    @Deprecated
    @PostMapping("/listFileRouteBy")
    public RestRes listFileRouteBy(@RequestBody GisdataFileRouteEntity gisdataFileRouteEntity) {
//        Map<String, Object> map = new HashMap<>(2);
//        List<GisdataFileRouteEntity> gisdataFileRouteEntityList = gisdataFileRouteService.listFileRouteBy(gisdataFileRouteEntity);
//        //文件名编码处理
//        String route;
//        for (GisdataFileRouteEntity fileRouteEntity : gisdataFileRouteEntityList) {
//            route = fileRouteEntity.getRoute();
//            //部分电力行业图层名存在#符号需要编码处理
//            if (route != null && !route.isEmpty() && route.contains("#")) {
//                String base = route.substring(0, route.lastIndexOf("/") + 1);
//                String reg = route.substring(route.lastIndexOf("/") + 1);
//                try {
//                    String url = URLEncoder.encode(reg, "utf-8");
//                    fileRouteEntity.setRoute(base + url);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        map.put("gisdataFileRouteEntityList", gisdataFileRouteEntityList);
//        return CollectionUtils.isEmpty(gisdataFileRouteEntityList) ? RestRes.err("找不到相关的图层信息!") : RestRes.ok(map);
        throw new UnsupportedOperationException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INTERFACE_IS_OUT_OF_DATE_PLEASE_REFER_TO_THE_INTERFACE_DOCUMENTATION_AND_USE_THE_NEW_INTERFACE.getContent()));
    }

    /*添加相关类型的图层*/
    @PostMapping("/addGisDataFileRoute")
    public RestRes addGisDataFileRoute(@RequestBody @Valid @TrimStr GisdataFileRouteDto gisdataFileRouteDto, BindingResult br) {
        if (br.hasErrors()) {
            FieldError fieldError = br.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(defaultMessage);
        }
        String name = gisdataFileRouteDto.getName();
        Integer type = gisdataFileRouteDto.getType();
        String unitId = gisdataFileRouteDto.getOrgCode();

        if (0 == type && (gisdataFileRouteDto.getHierarchy() < 1 || gisdataFileRouteDto.getHierarchy() > 5)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }

        QueryWrapper<GisdataFileRouteEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        if (unitId != null) {

            UnitEntityDTO sysUnitEntity = sysUnitService.getById(unitId);
            if ((sysUnitEntity == null)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_FINDING_UNIT.getContent()));
            }
            queryWrapper.eq("org_code", unitId);
        }
        queryWrapper.eq("type", type);
        queryWrapper.eq("deleted", false);
        queryWrapper.last("limit 1");
        GisdataFileRouteEntity fileRouteEntity = gisdataFileRouteService.getOne(queryWrapper);
        if (fileRouteEntity != null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_A_LAYER_WITH_THE_SAME_TYPE_OF_NAME_HAS_BEEN_ADDED_TO_THE_CURRENT_UNIT.getContent()));
        }
        String route = gisdataFileRouteDto.getRoute();
        if (!(route.startsWith("https") || route.startsWith("http"))) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_WRONG_PUBLISH_ADDRESS_PATH.getContent()));
        }

        GisdataFileRouteEntity newFileRouteEntity = new GisdataFileRouteEntity();
        BeanUtils.copyProperties(gisdataFileRouteDto, newFileRouteEntity);

        newFileRouteEntity.setCreatorId(Long.valueOf(TrustedAccessTracerHolder.get().getAccountId()));

        return gisdataFileRouteService.save(newFileRouteEntity) ? RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ADDING_LAYER_INFORMATION_SUCCEEDED.getContent())) : RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ADDING_LAYER_INFORMATION_FAILED.getContent()));
    }

    /*更改图层信息*/
    @PostMapping("/updateGisDataFileRoute")
    public RestRes updateGisDataFileRoute(@RequestBody @Valid GisdataFileRouteDto gisdataFileRouteDto) {

        if (0 == gisdataFileRouteDto.getType() && (gisdataFileRouteDto.getHierarchy() < 1 || gisdataFileRouteDto.getHierarchy() > 5)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }

        String name = gisdataFileRouteDto.getName();
        String route = gisdataFileRouteDto.getRoute();
        Integer type = gisdataFileRouteDto.getType();
        Integer id = gisdataFileRouteDto.getId();
        Double longitude = gisdataFileRouteDto.getLongitude();
        Double altitude = gisdataFileRouteDto.getAltitude();
        Double latitude = gisdataFileRouteDto.getLatitude();
//        Integer isPreload = gisdataFileRouteDto.getIsPreload();
        Double geometricError = gisdataFileRouteDto.getGeometricError();
        String unitId = gisdataFileRouteDto.getOrgCode();
        String layerRange = gisdataFileRouteDto.getLayerRange();
        int hierarchy = gisdataFileRouteDto.getHierarchy();
        Double viewAltitude = gisdataFileRouteDto.getViewAltitude();
        int offsetHeight = gisdataFileRouteDto.getOffsetHeight() == null ? 0 : gisdataFileRouteDto.getOffsetHeight();
        int safeCheck = gisdataFileRouteDto.getSafeCheck() == null ? 0 : gisdataFileRouteDto.getSafeCheck();
        if (id == null) {
            return RestRes.errorParam();
        }
        QueryWrapper<GisdataFileRouteEntity> queryWrapper = new QueryWrapper<>();
        if ((unitId != null)) {
            UnitEntityDTO sysUnitEntity = sysUnitService.getById(unitId);
            if ((sysUnitEntity == null)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_FINDING_UNIT.getContent()));
            }
            queryWrapper.eq("org_code", unitId);
        }
        GisdataFileRouteEntity gisdataFileRouteEntity = gisdataFileRouteService.getById(id);
        if (gisdataFileRouteEntity == null) {
            return RestRes.errorParam();
        }
        queryWrapper.eq("name", name);
        queryWrapper.eq("type", type);
        queryWrapper.eq("deleted", false);
        queryWrapper.last("limit 1");
        GisdataFileRouteEntity fileRouteEntity = gisdataFileRouteService.getOne(queryWrapper);

        if (fileRouteEntity != null && !id.equals(fileRouteEntity.getId())) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_A_LAYER_WITH_THE_SAME_TYPE_OF_NAME_HAS_BEEN_ADDED_TO_THE_CURRENT_UNIT.getContent()));
        }
        gisdataFileRouteEntity.setName(name);
        gisdataFileRouteEntity.setRoute(route);
        gisdataFileRouteEntity.setOrgCode(unitId);
        gisdataFileRouteEntity.setLongitude(longitude);
        gisdataFileRouteEntity.setLatitude(latitude);
        gisdataFileRouteEntity.setAltitude(altitude);
        /**
         * @Deprecated 2.1.0，将在后续版本中删除，使用新接口{@link com.imapcloud.nest.v2.web.UosMaplayerController.setUserOrgMaplayerPreloadStatus} 替代
         */
        // gisdataFileRouteEntity.setIsPreload(isPreload);
        gisdataFileRouteEntity.setGeometricError(geometricError);
        gisdataFileRouteEntity.setLayerRange(layerRange);
        gisdataFileRouteEntity.setHierarchy(hierarchy);
        gisdataFileRouteEntity.setViewAltitude(viewAltitude);
        gisdataFileRouteEntity.setOffsetHeight(offsetHeight);
        gisdataFileRouteEntity.setSafeCheck(safeCheck);
        return gisdataFileRouteService.updateById(gisdataFileRouteEntity) ? RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MODIFY_LAYER_INFORMATION_SUCCESS.getContent())) : RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MODIFY_LAYER_INFORMATION_FAILED.getContent()));
    }

    /*更改图层信息*/
    @PostMapping("/deleteGisDataFileRoute")
    public RestRes deleteGisDataFileRoute(@RequestBody GisdataFileRouteDto gisdataFileRouteDto) {
        Integer id = gisdataFileRouteDto.getId();
        if (id == null) {
            return RestRes.errorParam();
        }
        return uosMaplayerService.deleteMaplayer(id.longValue()) ? RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DELETING_A_UNIT_LAYER_SUCCEEDED.getContent())) : RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DELETING_UNIT_LAYERS_FAILED.getContent()));
    }

    /*批量删除图层*/
    @PostMapping("/batchDeleteGisDataFileRoute")
    public RestRes batchDeleteGisDataFileRoute(@RequestBody GisdataFileRouteDto gisdataFileRouteDto) {
        List<Integer> routeIds = gisdataFileRouteDto.getRouteIds();
        if (CollectionUtils.isEmpty(routeIds)) {
            return RestRes.errorParam();
        }
        //是否要判断都存在
        QueryWrapper<GisdataFileRouteEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", routeIds);

        GisdataFileRouteEntity updateRouteEntity = new GisdataFileRouteEntity();
        updateRouteEntity.setDeleted(true);

        return gisdataFileRouteService.update(updateRouteEntity, queryWrapper) ? RestRes.ok() : RestRes.errorParam();
    }

    /*获取航线信息*/
    @PostMapping("/getAirline")
    public RestRes getAirline(@RequestBody GisdataFileRouteDto gisdataFileRouteDto) {
        Integer id = gisdataFileRouteDto.getId();
        if (id == null) {
            return RestRes.errorParam();
        }
        GisdataFileRouteEntity gisdataFileRouteEntity = gisdataFileRouteService.getById(id);
        if (gisdataFileRouteEntity == null) {
            return RestRes.errorParam();
        }
        String route = gisdataFileRouteEntity.getRoute();
        String jsonBody;
        try {
            if (route.startsWith("https")) {
                RestTemplate httpsRestTemplate = HttpRequestUtil.getHttpsRestTemplate();
                jsonBody = httpsRestTemplate.getForObject(route, String.class);
            } else {
                RestTemplate restTemplate = new RestTemplate();
                jsonBody = restTemplate.getForObject(route, String.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_PATH_FILE_IS_EMPTY_OR_THE_PATH_IS_WRONG.getContent()));
        }


        Map<String, Object> map = new HashMap<>(2);
        map.put("jsonBody", jsonBody);
        return StringUtils.isBlank(jsonBody) ? RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_PATH_FILE_IS_EMPTY_OR_THE_PATH_IS_WRONG.getContent())) : RestRes.ok(map);
    }

    /**
     * 开启图层展示
     * @deprecated 2.1.0, 将在后续版本移除该方法，使用新接口{@link UosMaplayerController#setUserOrgMaplayerDisplayStatus(java.lang.Long, java.lang.String)}替代
     */
    @Deprecated
    @PostMapping("/enableGisDataFileRouteCheckStatus")
    public RestRes enableGisDataFileRouteCheckStatus(@RequestBody GisdataFileRouteDto gisdataFileRouteDto) {
        /*Integer id = gisdataFileRouteDto.getId();
        if (id == null) {
            return RestRes.errorParam();
        }
        GisdataFileRouteEntity gisdataFileRouteEntity = gisdataFileRouteService.getById(id);
        if (gisdataFileRouteEntity == null) {
            return RestRes.errorParam();
        }
        gisdataFileRouteEntity.setCheckStatus(GisDataFileRouteCheckStatusEnum.LOAD_DEFAULT.getCode());
        return gisdataFileRouteService.updateById(gisdataFileRouteEntity) ? RestRes.ok("设置图层查看权限成功!") : RestRes.err("设置图层查看权限失败!");*/
        throw new UnsupportedOperationException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INTERFACE_IS_OUT_OF_DATE_PLEASE_REFER_TO_THE_INTERFACE_DOCUMENTATION_AND_USE_THE_NEW_INTERFACE.getContent()));
    }

    /**
     * 关闭图层展示
     * @deprecated 2.1.0, 将在后续版本移除该方法，使用新接口{@link UosMaplayerController#setUserOrgMaplayerDisplayStatus(java.lang.Long, java.lang.String)}替代
     */
    @Deprecated
    @PostMapping("/disableGisDataFileRouteCheckStatus")
    public RestRes disableGisDataFileRouteCheckStatus(@RequestBody GisdataFileRouteDto gisdataFileRouteDto) {
        /*Integer id = gisdataFileRouteDto.getId();
        if (id == null) {
            return RestRes.errorParam();
        }
        GisdataFileRouteEntity gisdataFileRouteEntity = gisdataFileRouteService.getById(id);
        if (gisdataFileRouteEntity == null) {
            return RestRes.errorParam();
        }
        gisdataFileRouteEntity.setCheckStatus(GisDataFileRouteCheckStatusEnum.NOT_LOAD_DEFALUT.getCode());
        return gisdataFileRouteService.updateById(gisdataFileRouteEntity) ? RestRes.ok("取消图层查看权限成功!") : RestRes.err("取消图层查看权限失败!");*/
        throw new UnsupportedOperationException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INTERFACE_IS_OUT_OF_DATE_PLEASE_REFER_TO_THE_INTERFACE_DOCUMENTATION_AND_USE_THE_NEW_INTERFACE.getContent()));
    }
}

