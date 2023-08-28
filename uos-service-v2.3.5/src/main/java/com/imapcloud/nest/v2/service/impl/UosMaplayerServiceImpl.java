package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.mapper.GisdataFileRouteMapper;
import com.imapcloud.nest.model.GisdataFileRouteEntity;
import com.imapcloud.nest.v2.common.enums.MaplayerDisplayStatusEnum;
import com.imapcloud.nest.v2.common.enums.MaplayerPreloadStatusEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.dao.entity.OrgMaplayerRefEntity;
import com.imapcloud.nest.v2.dao.mapper.OrgMaplayerRefMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.UosMaplayerService;
import com.imapcloud.nest.v2.service.dto.in.MaplayerQueryDTO;
import com.imapcloud.nest.v2.service.dto.out.MaplayerInfoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UOS地图图层业务接口实现
 *
 * @author Vastfy
 * @date 2022/9/27 14:33
 * @since 2.1.0
 */
@Slf4j
@Service
public class UosMaplayerServiceImpl implements UosMaplayerService {

    @Resource
    private GisdataFileRouteMapper gisdataFileRouteMapper;

    @Resource
    private OrgMaplayerRefMapper orgMaplayerRefMapper;

    @Resource
    private UosOrgManager uosOrgManager;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setUserOrgMaplayerDisplayStatus(Long maplayerId, Integer status, boolean managed) {
        GisdataFileRouteEntity entity = checkMaplayerExistsAndGot(maplayerId);
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        if(managed){
            // 操作的是所属单位图层
            orgCode = entity.getOrgCode();
        }
        OrgMaplayerRefEntity omRefEntity = getOrgMaplayerRefEntity(maplayerId, orgCode);
        // 展示状态：开启 --> 关闭，删除单位-图层关系记录
        if(Objects.nonNull(omRefEntity) && MaplayerDisplayStatusEnum.OFF.matchEquals(status)){
            orgMaplayerRefMapper.delete(getOrgMaplayerConditionWrapper(maplayerId, orgCode));
        }
        // 展示状态：关闭 --> 开启，新增单位-图层关系记录
        if(Objects.isNull(omRefEntity) && MaplayerDisplayStatusEnum.ON.matchEquals(status)){
            OrgMaplayerRefEntity omRef = new OrgMaplayerRefEntity();
            omRef.setOrgCode(orgCode);
            omRef.setMaplayerId(maplayerId.intValue());
            omRef.setPreloadStatus(MaplayerPreloadStatusEnum.OFF.getStatus());
            orgMaplayerRefMapper.insert(omRef);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setUserOrgMaplayerPreloadStatus(Long maplayerId, Integer status, boolean managed) {
        GisdataFileRouteEntity entity = checkMaplayerExistsAndGot(maplayerId);
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        if(managed){
            // 操作的是所属单位图层
            orgCode = entity.getOrgCode();
        }
        OrgMaplayerRefEntity omRefEntity = getOrgMaplayerRefEntity(maplayerId, orgCode);
        if(Objects.isNull(omRefEntity)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_UNIT_HAS_NOT_OPENED_THE_LAYER_DISPLAY_STATUS_THE_OPERATION_IS_NOT_ALLOWED_TO_BE_EXECUTED.getContent()));
        }
        OrgMaplayerRefEntity updateEntity = new OrgMaplayerRefEntity();
        updateEntity.setId(omRefEntity.getId());
        updateEntity.setPreloadStatus(status);
        orgMaplayerRefMapper.updateById(updateEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteMaplayer(Long maplayerId) {
        LambdaQueryWrapper<GisdataFileRouteEntity> con1 = Wrappers.lambdaQuery(GisdataFileRouteEntity.class).eq(GisdataFileRouteEntity::getId, maplayerId.intValue());
        gisdataFileRouteMapper.delete(con1);
        // 删除父级单位的数据
        LambdaQueryWrapper<OrgMaplayerRefEntity> con = Wrappers.lambdaQuery(OrgMaplayerRefEntity.class).eq(OrgMaplayerRefEntity::getMaplayerId, maplayerId.intValue());
        orgMaplayerRefMapper.delete(con);
        return true;
    }

    @Override
    public List<MaplayerInfoOutDTO> fetchVisibleMaplayerInfos(MaplayerQueryDTO condition) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        List<GisdataFileRouteEntity> entities = fetchVisibleFileRouteEntities(condition, orgCode);
        // 查询单位-图层展示、预加载数据
        Map<Integer, Map<String, Boolean>> orgOrgPreloadStatusMappings = getOrgDisplayedMaplayerPreloadStatusMappings(entities);
        Map<String, String> orgMappings = getOrgMappings();
        return entities.stream()
                .map(r -> {
                    MaplayerInfoOutDTO info = new MaplayerInfoOutDTO();
                    info.setId(r.getId().toString());
                    info.setName(r.getName());
                    info.setOrgCode(r.getOrgCode());
                    info.setOrgName(orgMappings.get(r.getOrgCode()));
                    try {
                        URLEncoder.encode(r.getRoute(), StandardCharsets.UTF_8.name());
                        info.setRoute(r.getRoute());
                    } catch (UnsupportedEncodingException e) {
                        log.warn("图层发布地址URL编码失败 ==> {}", e.getMessage());
                        info.setRoute(r.getRoute());
                    }
                    info.setLongitude(r.getLongitude());
                    info.setLatitude(r.getLatitude());
                    info.setAltitude(r.getAltitude());
                    info.setViewAltitude(r.getViewAltitude());
                    info.setHierarchy(r.getHierarchy());
                    // safeCheck = 1
                    info.setSafeCheck(r.getSafeCheck());
                    info.setGeometricError(r.getGeometricError());
                    info.setOffsetHeight(r.getOffsetHeight());
                    info.setDisplay(Boolean.FALSE);
                    info.setDisplay0(Boolean.FALSE);
                    info.setPreload(Boolean.FALSE);
                    info.setPreload0(Boolean.FALSE);
                    if(orgOrgPreloadStatusMappings.containsKey(r.getId())){
                        Map<String, Boolean> orgMaplayerMap = orgOrgPreloadStatusMappings.get(r.getId());
                        // 【用户单位】是否展示，预加载
                        if(orgMaplayerMap.containsKey(orgCode)){
                            info.setDisplay(true);
                            info.setPreload(orgMaplayerMap.get(orgCode));
                        }
                        // 【图层单位】是否展示、预加载
                        if(orgMaplayerMap.containsKey(r.getOrgCode())){
                            info.setDisplay0(true);
                            info.setPreload0(orgMaplayerMap.get(r.getOrgCode()));
                        }
                    }
                    return info;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MaplayerInfoOutDTO> listDisplayedMaplayerInfos(Integer type) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        // 查关联表数据
        LambdaQueryWrapper<OrgMaplayerRefEntity> cond = Wrappers.lambdaQuery(OrgMaplayerRefEntity.class)
                .eq(OrgMaplayerRefEntity::getOrgCode, orgCode);
        List<OrgMaplayerRefEntity> orgMaplayerRefEntities = orgMaplayerRefMapper.selectList(cond);
        if(CollectionUtils.isEmpty(orgMaplayerRefEntities)){
            return Collections.emptyList();
        }
        Map<Integer, Boolean> preloadStatusMappings = orgMaplayerRefEntities.stream()
                .collect(Collectors.toMap(OrgMaplayerRefEntity::getMaplayerId, r -> MaplayerPreloadStatusEnum.ON.matchEquals(r.getPreloadStatus())));
        LambdaQueryWrapper<GisdataFileRouteEntity> con = Wrappers.lambdaQuery(GisdataFileRouteEntity.class)
                .in(GisdataFileRouteEntity::getId, preloadStatusMappings.keySet())
                .eq(GisdataFileRouteEntity::getDeleted, false);
        if(Objects.nonNull(type)){
            con.eq(GisdataFileRouteEntity::getType, type);
        }
        List<GisdataFileRouteEntity> entities = gisdataFileRouteMapper.selectList(con);
        return entities.stream()
                .map(r -> {
                    MaplayerInfoOutDTO info = new MaplayerInfoOutDTO();
                    info.setId(r.getId().toString());
                    info.setName(r.getName());
                    info.setOrgCode(r.getOrgCode());
                    try {
                        URLEncoder.encode(r.getRoute(), StandardCharsets.UTF_8.name());
                        info.setRoute(r.getRoute());
                    } catch (UnsupportedEncodingException e) {
                        log.warn("图层发布地址URL编码失败 ==> {}", e.getMessage());
                        info.setRoute(r.getRoute());
                    }
                    info.setLongitude(r.getLongitude());
                    info.setLatitude(r.getLatitude());
                    info.setAltitude(r.getAltitude());
                    info.setViewAltitude(r.getViewAltitude());
                    info.setHierarchy(r.getHierarchy());
                    // safeCheck = 1
                    info.setSafeCheck(r.getSafeCheck());
                    info.setGeometricError(r.getGeometricError());
                    info.setType(r.getType());
                    info.setDisplay(preloadStatusMappings.containsKey(r.getId()));
                    info.setPreload(preloadStatusMappings.get(r.getId()));
                    info.setOffsetHeight(r.getOffsetHeight());
                    return info;
                })
                .collect(Collectors.toList());
    }

    private Map<String, String> getOrgMappings() {
        List<OrgSimpleOutDO> orgInfos = uosOrgManager.listAllOrgInfos();
        if(!CollectionUtils.isEmpty(orgInfos)){
            return orgInfos.stream()
                    .collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
        }
        return Collections.emptyMap();
    }

    private Map<Integer, Map<String, Boolean>> getOrgDisplayedMaplayerPreloadStatusMappings(List<GisdataFileRouteEntity> entities) {
        if(!CollectionUtils.isEmpty(entities)){
            Set<Integer> maplayerIds = entities.stream().map(GisdataFileRouteEntity::getId).collect(Collectors.toSet());
            // 格式示例 ==> {111: {"000": true, "000001": false}}
            return getDisplayedMaplayerPreloadStatusMappings(maplayerIds);
        }
        return Collections.emptyMap();
    }

    private Map<Integer, Map<String, Boolean>> getDisplayedMaplayerPreloadStatusMappings(Collection<Integer> maplayerIds) {
        LambdaQueryWrapper<OrgMaplayerRefEntity> cond = Wrappers.lambdaQuery(OrgMaplayerRefEntity.class)
                .in(OrgMaplayerRefEntity::getMaplayerId, maplayerIds);
        List<OrgMaplayerRefEntity> entities = orgMaplayerRefMapper.selectList(cond);
        if(!CollectionUtils.isEmpty(entities)){
            return entities.stream()
                    .collect(Collectors.groupingBy(OrgMaplayerRefEntity::getMaplayerId, Collectors.toMap(OrgMaplayerRefEntity::getOrgCode, r -> MaplayerPreloadStatusEnum.ON.matchEquals(r.getPreloadStatus()))));
        }
        return Collections.emptyMap();
    }

    private List<GisdataFileRouteEntity> fetchVisibleFileRouteEntities(MaplayerQueryDTO condition, String orgCode) {
        LambdaQueryWrapper<GisdataFileRouteEntity> con = Wrappers.lambdaQuery(GisdataFileRouteEntity.class)
                .likeRight(GisdataFileRouteEntity::getOrgCode, orgCode)
                .eq(GisdataFileRouteEntity::getDeleted, false)
                // 保持跟之前一致，以名称升序排列【会影响性能】
                .last("ORDER BY CONVERT(`name` USING GBK) ASC");
        if(StringUtils.hasText(condition.getName())){
            con.like(GisdataFileRouteEntity::getName, condition.getName());
        }
        if(StringUtils.hasText(condition.getOrgCode())){
            con.eq(GisdataFileRouteEntity::getOrgCode, condition.getOrgCode());
        }
        if(Objects.nonNull(condition.getType())){
            con.eq(GisdataFileRouteEntity::getType, condition.getType());
        }
        return gisdataFileRouteMapper.selectList(con);
    }

    private GisdataFileRouteEntity checkMaplayerExistsAndGot(Long maplayerId) {
        LambdaQueryWrapper<GisdataFileRouteEntity> condition = Wrappers.lambdaQuery(GisdataFileRouteEntity.class)
                .eq(GisdataFileRouteEntity::getId, maplayerId.intValue())
                .eq(GisdataFileRouteEntity::getDeleted, false);
        GisdataFileRouteEntity entity = gisdataFileRouteMapper.selectOne(condition);
        if(Objects.nonNull(entity)){
            return entity;
        }
        throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_LAYER_DATA_DOES_NOT_EXIST.getContent()));
    }

    private OrgMaplayerRefEntity getOrgMaplayerRefEntity(Long maplayerId, String orgCode) {
        LambdaQueryWrapper<OrgMaplayerRefEntity> condition2 = getOrgMaplayerConditionWrapper(maplayerId, orgCode);
        return orgMaplayerRefMapper.selectOne(condition2);
    }

    private LambdaQueryWrapper<OrgMaplayerRefEntity> getOrgMaplayerConditionWrapper(Long maplayerId, String orgCode) {
        return Wrappers.lambdaQuery(OrgMaplayerRefEntity.class)
                .eq(OrgMaplayerRefEntity::getMaplayerId, maplayerId)
                .eq(OrgMaplayerRefEntity::getOrgCode, orgCode);
    }

}
