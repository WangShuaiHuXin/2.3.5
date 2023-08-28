package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.mapper.NestRtkMapper;
import com.imapcloud.nest.model.NestRtkEntity;
import com.imapcloud.nest.service.NestRtkService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.power.PowerManagerCf;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-09-15
 */
@Service
public class NestRtkServiceImpl extends ServiceImpl<NestRtkMapper, NestRtkEntity> implements NestRtkService {

    @Resource
    private NestService nestService;

    @Resource
    private BaseNestService baseNestService;

    @Override
    public RestRes getRtkIsEnable(String nestId) {
//        ComponentManager cm = getComponentManager(nestId);
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<Boolean> res = cm.getRtkManagerCf().isRtkEnable();
            if (res.isSuccess()) {
                return RestRes.ok("isEnable", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_WHETHER_RTK_IS_ON_OR_NOT.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes openRtk(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getRtkManagerCf().openRtk();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_OPEN_RTK.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_TURN_ON_RTK.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes closeRtk(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getRtkManagerCf().closeRtk();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CLOSE_RTK.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_TURN_OFF_RTK.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getRtkType(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<Integer> res = cm.getRtkManagerCf().getRtkConnectType();
            if (res.isSuccess()) {
                return RestRes.ok("type", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_RTK_STATUS.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes setRtkType(String nestId, Integer type) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getRtkManagerCf().setRTKConnectType(type);
            if (res.isSuccess()) {
                NestRtkEntity nestRtkEntity = this.getOne(new QueryWrapper<NestRtkEntity>().eq("base_nest_id", nestId).eq("deleted", false));
                nestRtkEntity = nestRtkEntity == null ? new NestRtkEntity() : nestRtkEntity;
                nestRtkEntity.setType(type);
                nestRtkEntity.setBaseNestId(nestId);
                this.saveOrUpdate(nestRtkEntity);
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_RTK_STATUS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SET_RTK_STATUS.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getRtkInfo(String nestId) {
        Map map = new HashMap(2);
        NestRtkEntity nestRtkEntity = this.getOne(new QueryWrapper<NestRtkEntity>().eq("base_nest_id", nestId).eq("deleted", false));
        if (ToolUtil.isNotEmpty(nestRtkEntity)) {
            if (nestRtkEntity.getIp() != null && nestRtkEntity.getPort() != null && nestRtkEntity.getMountPoint() != null && nestRtkEntity.getUserName() != null && nestRtkEntity.getPassword() != null) {
                map.put("rtkInfo", nestRtkEntity);
                return RestRes.ok(map);
            }
        }
//        String nestUuid = nestService.getUuidById(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (nestUuid == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CORRESPONDING_NEST_CANNOT_BE_QUERIED.getContent()));
        }
//        ComponentManager cm = getComponentManager(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MqttResult<NestRtkEntity> res = cm.getRtkManagerCf().getRtkAccountParam();
            if (res.isSuccess()) {
                NestRtkEntity rtkInfo = res.getRes();
                Integer id = nestRtkEntity == null ? null : nestRtkEntity.getId();
                rtkInfo.setId(id);
                rtkInfo.setBaseNestId(nestId);
                this.saveOrUpdate(rtkInfo);
                return RestRes.ok("rtkInfo", rtkInfo);
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_RTK_ACCOUNT_INFORMATION.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes setRtkInfo(NestRtkEntity nestRtkEntity) {
//        ComponentManager cm = getComponentManager(nestRtkEntity.getNestId());
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestRtkEntity.getBaseNestId());
        if (cm != null) {
            MqttResult<NullParam> res = cm.getRtkManagerCf().setRtkAccountParam(nestRtkEntity);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_RTK_ACCOUNT_INFORMATION.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_RTK_ACCOUNT_INFORMATION_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes setExpireTime(String nestId, LocalDate expireTime) {
        NestRtkEntity nestRtkEntity = new NestRtkEntity();
        nestRtkEntity.setBaseNestId(nestId);
        nestRtkEntity.setExpireTime(expireTime);
        this.saveOrUpdate(nestRtkEntity, new UpdateWrapper<NestRtkEntity>().lambda().eq(NestRtkEntity::getBaseNestId, nestId));
        return RestRes.ok();
    }

    @Override
    public RestRes getExpireRtkList() {
        Map map = new HashMap<>(7);
        List<String> nestIdList = nestService.listNestEntityInCache();
        List<NestRtkEntity> expireRtkList15 = new ArrayList<>();
        List<NestRtkEntity> expireRtkList7 = new ArrayList<>();
        List<NestRtkEntity> expireRtkList3 = new ArrayList<>();
        List<NestRtkEntity> expireRtkList2 = new ArrayList<>();
        List<NestRtkEntity> expireRtkList1 = new ArrayList<>();
        List<NestRtkEntity> expireRtkList0 = new ArrayList<>();
        if (ToolUtil.isNotEmpty(nestIdList)) {
            List<NestRtkEntity> expireRtkList = baseMapper.getExpireRtkList(15, nestIdList);
            if (ToolUtil.isNotEmpty(expireRtkList)) {
                expireRtkList15 = expireRtkList.stream().filter(e -> e.getExpireTime().equals(LocalDate.now().plusDays(15))).collect(Collectors.toList());
                expireRtkList7 = expireRtkList.stream().filter(e -> e.getExpireTime().equals(LocalDate.now().plusDays(7))).collect(Collectors.toList());
                expireRtkList3 = expireRtkList.stream().filter(e -> e.getExpireTime().equals(LocalDate.now().plusDays(3))).collect(Collectors.toList());
                expireRtkList2 = expireRtkList.stream().filter(e -> e.getExpireTime().equals(LocalDate.now().plusDays(2))).collect(Collectors.toList());
                expireRtkList1 = expireRtkList.stream().filter(e -> e.getExpireTime().equals(LocalDate.now().plusDays(1))).collect(Collectors.toList());
                expireRtkList0 = expireRtkList.stream().filter(e -> e.getExpireTime().equals(LocalDate.now())).collect(Collectors.toList());
            }
        }
        map.put("15", expireRtkList15);
        map.put("7", expireRtkList7);
        map.put("3", expireRtkList3);
        map.put("2", expireRtkList2);
        map.put("1", expireRtkList1);
        map.put("0", expireRtkList0);
        return RestRes.ok(map);
    }

    @Override
    public RestRes getNestExpireRtk(String nestId) {
        //TODO 临时处理，后续要调整数据结构为无人机对应rtk，而不是基站对rtk
        List<NestRtkEntity> nestRtkEntityList = this.list(new QueryWrapper<NestRtkEntity>().eq("base_nest_id", nestId).eq("deleted", 0));
        NestRtkEntity nestRtkEntity = CollectionUtils.isEmpty(nestRtkEntityList)?null:nestRtkEntityList.get(0);
        LocalDate expireTime = nestRtkEntity != null ? nestRtkEntity.getExpireTime() : null;
        Map map = new HashMap(2);
        map.put("expireTime", expireTime);
        return RestRes.ok(map);
    }

    @Override
    public RestRes drtkPowerSwitch(String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
//        ComponentManager cm = getComponentManager(nestId);
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
        MqttResult<NullParam> res = powerManagerCf.drtkPowerOn();
        if (res.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_TURN_ON_DRTK_POWER_TRIGGER.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TURN_ON_DRTK_POWER_TRIGGER_FAILURE.getContent()) + res.getMsg());
    }

//    private ComponentManager getComponentManager(int nestId) {
//        NestEntity nest = nestService.getNestByIdIsCache(nestId);
//        if (nest != null) {
//            return ComponentManagerFactory.getInstance(nest.getUuid());
//        }
//        return null;
//    }

}
