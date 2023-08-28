package com.imapcloud.nest.common.netty.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.CheckTypeEnum;
import com.imapcloud.nest.enums.G600NestBatteryStateEnum;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.sdk.DJIDockService;
import com.imapcloud.nest.service.AirLineService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.NestParamOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.manager.sql.NestParamManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.out.DJIAerographyInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DJIDockInfoOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.media.MediaManagerCf;
import com.imapcloud.sdk.manager.media.entity.AvailableCaptureCountEntity;
import com.imapcloud.sdk.manager.media.entity.AvailableRecordTimeEntity;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.djido.DjiDockPropertyOsdDO;
import com.imapcloud.sdk.pojo.entity.G503NestBatteryState;
import com.imapcloud.sdk.pojo.entity.MiniNestBatteryState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务开始前检查
 *
 * @author daolin
 */
@Component
@Slf4j
public class BeforeStartCheckService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private NestService nestService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private CommonNestStateService commonNestStateService;

    @Autowired
    private AirLineService airLineService;

    @Resource
    private DJIDockService djiDockService;

    @Resource
    private NestParamManager nestParamManager;

    @Resource
    private BaseNestManager baseNestManager;

    public CheckRes startCheck(String uuid, Integer missionId,Integer uavWhich) {
        CheckRes checkRes = new CheckRes();
//        Integer type = nestService.getNestTypeByUuidInCache(uuid);
        NestTypeEnum nestType = baseNestService.getNestTypeByUuidCache(uuid);
        //大疆校验
        if(NestTypeEnum.DJI_DOCK.equals(nestType)){
            return checkDji(uuid);
        }
        //天气一样，G503不需要区分
        checkRes.setCheckAerographyRes(checkAerography(uuid));
        checkRes.setCheckNestRes(checkNest(uuid,uavWhich));
        if (missionId != null) {
            checkRes.setCheckStorageRes(checkStorage(uuid, missionId,uavWhich));
        }

        //固定机巢的时候才会去检查天线和电池
        if (NestTypeEnum.G600.equals(nestType)) {
            checkRes.setCheckBatteryRes(checkBatteryG600(uuid));
        }
        if (NestTypeEnum.G900.equals(nestType)) {
            checkRes.setCheckBatteryRes(checkBatteryG900(uuid));
        }
        if (NestTypeEnum.G900_CHARGE.equals(nestType)) {
            checkRes.setCheckBatteryRes(checkBatteryG900Charge(uuid));
        }
        if (NestTypeEnum.S100_V2.equals(nestType) ||
                NestTypeEnum.S100_V1.equals(nestType) ||
                NestTypeEnum.S110_AUTEL.equals(nestType) ||
                NestTypeEnum.S110_MAVIC3.equals(nestType)
        ) {
            checkRes.setCheckBatteryRes(checkBatteryS100(uuid, nestType));
        }
        if(NestTypeEnum.G503.equals(nestType)) {
            checkRes.setCheckBatteryRes(checkBatteryG503(uuid,uavWhich));
        }
        return checkRes;
    }


    /**
     * 阶段1，检查气象状态
     */
    private CheckDto checkAerography(String uuid) {
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(1);
        checkDto.setCheckType(CheckTypeEnum.AEROGRAPH_CHECK);
        AerographyInfoDto aerography = commonNestStateService.getAerographyInfoDto(uuid);

        if (aerography == null) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_ERROR.getContent()));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
            return checkDto;
        }

        if (aerography.getProductType() == 255) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_NO_DATA.getContent()));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
            return checkDto;
        }

        //正常条件下，压强和空气湿度不可能为0，如果为0代表气象站出了问题，或者说没有获得气象站的消息
        if (aerography.getPressure() == 0.0 || aerography.getHumidity() == 0.0) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_02"));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
            return checkDto;
        }

        //风速过大
        if (aerography.getSpeed() > 5 && aerography.getSpeed() < 7.5) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_03") + aerography.getSpeed() + MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_04"));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
            return checkDto;
        }

        if (aerography.getSpeed() > 7.5) {
            checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_03") + aerography.getSpeed() + MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_04"));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
            return checkDto;
        }

        if (aerography.getRainfall() > 0.0D && aerography.getRainfall() < 2.0D) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_RAINING.getContent()) + ":" + aerography.getRainfall() + "(mm)");
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
            return checkDto;
        }

        if (aerography.getRainfall() >= 2.0D) {
            checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_RAINING.getContent()) + ":" + aerography.getRainfall() + "(mm)");
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
            return checkDto;
        }

        //正在下雨
        if (aerography.getRainsnow() != null && 1 == aerography.getRainsnow()) {

            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_RAINING.getContent()));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
            return checkDto;
        }
        //检查通过
        checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
        checkDto.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_NORMAL.getContent()));
        checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_28"));
        return checkDto;
    }

    /**
     * 阶段2，检查机巢状态
     */
    private CheckDto checkNest(String uuid,Integer which) {
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(2);
        checkDto.setCheckType(CheckTypeEnum.NEST_CHECK);
        NestStateEnum nestStateEnum = commonNestStateService.getNestStateEnum(uuid,AirIndexEnum.getInstance(which));
        //数据为空
        if (nestStateEnum == NestStateEnum.OFF_LINE) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_NEST_OFFLINE.getContent()));
            checkDto.setCheckState("OFF_LINE");
            return checkDto;
        }
        checkDto.setCheckState(nestStateEnum.getValue());
        //准备就绪
        if (NestStateEnum.STANDBY.equals(nestStateEnum) ||
                NestStateEnum.BATTERY_LOADED.equals(nestStateEnum) ||
                NestStateEnum.READY_TO_GO.equals(nestStateEnum) ||
                NestStateEnum.DRONE_STARTED.equals(nestStateEnum)) {
            checkDto.setPass(1);
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_NEST_ABLE_TO_PERFORM_TASKS.getContent()));
            return checkDto;
        }

        checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
        checkDto.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_NEST_UNABLE_TO_PERFORM_TASKS.getContent()));
        return checkDto;
    }

    private NestParamOutDO getNestParamOutDOByUuid(String uuid) {

        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByUuid(uuid);
        if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
            return defaultNestParamOutDO();
        }
        List<NestParamOutDO> paramOutDOList = nestParamManager
                .selectListByNestIdCollection(Lists.newArrayList(baseNestEntityOutDOList.get(0).getNestId()));
        if (CollUtil.isEmpty(paramOutDOList)) {
            return defaultNestParamOutDO();
        }
        return paramOutDOList.get(0);
    }

    private NestParamOutDO defaultNestParamOutDO() {
        NestParamOutDO nestParamOutDO = new NestParamOutDO();
        nestParamOutDO.setBaseNestId("");
        nestParamOutDO.setAlarmCircleNum(150);
        nestParamOutDO.setForbiddenCircleNum(200);
        return nestParamOutDO;
    }

    /**
     * 阶段4，电池检测
     */
    private CheckDto checkBatteryG600(String uuid) {
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(4);
        checkDto.setCheckType(CheckTypeEnum.BATTERY_CHECK);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", -1);
        G600NestBatteryInfoDto g600NestBatteryInfoDto = commonNestStateService.getG600NestBatteryInfoDto(uuid);
        if (g600NestBatteryInfoDto == null) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_05"));
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            return checkDto;
        }
        int readyUseBatteryIndex = g600NestBatteryInfoDto.getReadyUseBatteryIndex();
        List<NestBatteryInfoDto> list = g600NestBatteryInfoDto.getNestBatteryInfoDtoList();
        if (CollectionUtil.isEmpty(list)) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_05"));
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            return checkDto;
        }

        String msg = null;
        G600NestBatteryStateEnum checkState = null;
        //1.判断有没有正在使用的电池状态，如果有则拿无人机的电池电量来判断
        for (int i = 0; i < list.size(); i++) {
            NestBatteryInfoDto nestBatteryInfoDto = list.get(i);
            if (G600NestBatteryStateEnum.INUSE.getCode().equals(nestBatteryInfoDto.getState())) {
                int battaryInCharge = commonNestStateService.getAircraftBatteryChargeInPercent(uuid);
                if (battaryInCharge < 50) {
                    checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
                    checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_06") + battaryInCharge + "%");
                    checkDto.setCheckState(G600NestBatteryStateEnum.INUSE.name());
                    return checkDto;
                }
                if (battaryInCharge < 75) {
                    checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
                    checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_06") + battaryInCharge + "%");
                    checkDto.setCheckState(G600NestBatteryStateEnum.INUSE.name());
                    return checkDto;
                }
                msg = MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_06") + battaryInCharge + "%";
                checkState = G600NestBatteryStateEnum.INUSE;
            }
        }


        //2.如果没有机巢的电池都在，则拿取上一次正在使用电池序号，然后判断下一次捉的电池电量是否大于75%,如果不知道上一次使用的是第几块，则检查全部的电池是否电量都大于75%
        if (readyUseBatteryIndex == -1) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_07"));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
            return checkDto;
        }
        NestBatteryInfoDto nestBatteryInfoDto = list.get(readyUseBatteryIndex);
        Integer useCount = nestBatteryInfoDto.getChargeCount();

        // 查询基站电池循环次数
        NestParamOutDO nestParamOutDO = getNestParamOutDOByUuid(uuid);
        if (useCount > nestParamOutDO.getForbiddenCircleNum()) {
            checkDto.setPass(2);

            String format = String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEFORESTARTCHECKSERVICE_G600.getContent())
                    , (readyUseBatteryIndex + 1), nestParamOutDO.getForbiddenCircleNum(), nestBatteryInfoDto.getChargeCount());
            checkDto.setMsg(format);
            checkDto.setCheckState(G600NestBatteryStateEnum.CHARGING.name());
            return checkDto;
        }

        if (useCount > nestParamOutDO.getAlarmCircleNum()) {
            checkDto.setPass(0);
            String format = String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEFORESTARTCHECKSERVICE_G600.getContent())
                    , (readyUseBatteryIndex + 1), nestParamOutDO.getAlarmCircleNum(), nestBatteryInfoDto.getChargeCount());
            checkDto.setMsg(format);
            checkDto.setCheckState(G600NestBatteryStateEnum.CHARGING.name());
            return checkDto;
        }
        if (nestBatteryInfoDto.getPercentage() < 50) {
            checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_11") + nestBatteryInfoDto.getPercentage() + "%");
            checkDto.setCheckState(G600NestBatteryStateEnum.CHARGING.name());
            return checkDto;
        }

        if (nestBatteryInfoDto.getPercentage() < 75) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_11") + nestBatteryInfoDto.getPercentage() + "%");
            checkDto.setCheckState(G600NestBatteryStateEnum.CHARGING.name());
            return checkDto;
        }

        if (nestBatteryInfoDto.getPercentage() >= 75 && checkState == null) {
            msg = MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_09")
                    + (readyUseBatteryIndex + 1)
                    + MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_30")
                    + nestBatteryInfoDto.getPercentage() + "%";
            checkState = G600NestBatteryStateEnum.CHARGING;
            checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
        }
        if (checkState == null) {
            msg = MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_12");
            checkState = G600NestBatteryStateEnum.FULL;
        }
        checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
        checkDto.setMsg(msg);
        checkDto.setCheckState(checkState.name());
        return checkDto;
    }

    private CheckDto checkBatteryG900(String uuid) {
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(4);
        checkDto.setCheckType(CheckTypeEnum.BATTERY_CHECK);
        G900NestBatteryInfoDto g900NestBatteryInfoDto = commonNestStateService.getG900NestBatteryInfoDto(uuid);
        if (g900NestBatteryInfoDto == null ||
                CollectionUtil.isEmpty(g900NestBatteryInfoDto.getM300BatteryInfoDTOList())
                || g900NestBatteryInfoDto.getAvailable() <= 0

        ) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_BATTERY_ERROR.getContent()));
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            return checkDto;
        }
        int readyUseBatteryIndex = g900NestBatteryInfoDto.getReadyUseBatteryIndex();
        List<M300BatteryInfoDTO> list = g900NestBatteryInfoDto.getM300BatteryInfoDTOList();

        //判断有没有正在使用的电池
        for (int i = 0; i < list.size(); i++) {
            M300BatteryInfoDTO m300BatteryInfoDTO = list.get(i);
            if (m300BatteryInfoDTO.getUseState() == 1) {
                Integer aircraftBatteryChargeInPercent = g900NestBatteryInfoDto.getAircraftBatteryChargeInPercent();
                if (aircraftBatteryChargeInPercent < 50) {
                    checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
                    checkDto.setMsg(MessageUtils.getMessage(MessageEnum.BATTERY_INUSE_CAPCITY.getContent() + " NO(" + (readyUseBatteryIndex + 1) + "):" + aircraftBatteryChargeInPercent + "%"));
                    checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
                    return checkDto;
                }

                if (aircraftBatteryChargeInPercent < 75) {
                    checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
                    checkDto.setMsg(MessageUtils.getMessage(MessageEnum.BATTERY_INUSE_CAPCITY.getContent() + " NO(" + (readyUseBatteryIndex + 1) + "):" + aircraftBatteryChargeInPercent + "%"));
                    checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
                    return checkDto;
                }
                checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
                checkDto.setMsg(MessageUtils.getMessage(MessageEnum.BATTERY_INUSE_CAPCITY.getContent() + " NO(" + (readyUseBatteryIndex + 1) + "):" + aircraftBatteryChargeInPercent + "%"));
                checkDto.setCheckState(G600NestBatteryStateEnum.FULL.name());
                return checkDto;
            }
        }

        //1.如果获取不到预备使用的电池，则提示如下
        if (readyUseBatteryIndex == -1) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.UNABLE_TO_GET_BATTERY_READY_TO_USE.getContent()));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
            return checkDto;
        }

        //2.如果机巢的电池都在，则拿取上一次正在使用电池序号，然后判断下一次捉的电池电量是否大于75%,如果不知道上一次使用的是第几块，则检查全部的电池是否电量都大于75%
        M300BatteryInfoDTO m300BatteryInfoDTO = list.get(readyUseBatteryIndex);
        boolean res = checkBatteryG900Chi(readyUseBatteryIndex, checkDto, m300BatteryInfoDTO, uuid);
        if (!res) {
            return checkDto;
        }
        checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
        checkDto.setMsg(MessageUtils.getMessage(MessageEnum.BATTERY_READY_TO_USE_NORMAL.getContent()));
        checkDto.setCheckState(G600NestBatteryStateEnum.FULL.name());
        return checkDto;
    }

    /**
     * 先判断充电次数，再判断电量
     *
     * @param uuid
     * @return
     */
    private CheckDto checkBatteryG900Charge(String uuid) {
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(4);
        checkDto.setCheckType(CheckTypeEnum.BATTERY_CHECK);
        G900NestBatteryInfoDto g900NestBatteryInfoDto = commonNestStateService.getG900NestBatteryInfoDto(uuid);

        if (Objects.isNull(g900NestBatteryInfoDto) ||
                CollectionUtil.isEmpty(g900NestBatteryInfoDto.getM300BatteryInfoDTOList())
        ) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
//            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_BATTERY_ERROR.getContent()));
            checkDto.setMsg("获取电池数据异常");
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            return checkDto;
        }
        Integer aircraftBatteryChargeInPercent = g900NestBatteryInfoDto.getAircraftBatteryChargeInPercent();
        List<M300BatteryInfoDTO> list = g900NestBatteryInfoDto.getM300BatteryInfoDTOList();
        M300BatteryInfoDTO m300BatteryInfoDTO = list.get(0);

        //先判断机场电池，再判断无人机电池
        List<M300BatteryInfoDTO.BatteryInfo> batteryInfoList = m300BatteryInfoDTO.getBatteryInfoList();
        M300BatteryInfoDTO.BatteryInfo batteryInfo1 = batteryInfoList.get(0);
        M300BatteryInfoDTO.BatteryInfo batteryInfo2 = batteryInfoList.get(1);

        //获取电池电量异常
        if (Objects.isNull(batteryInfo1) ||
                Objects.isNull(batteryInfo1.getChargeCount()) ||
                Objects.isNull(batteryInfo1.getPercentage()) ||
                Objects.isNull(batteryInfo2) ||
                Objects.isNull(batteryInfo2.getChargeCount()) ||
                Objects.isNull(batteryInfo2.getPercentage())
        ) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
//            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_BATTERY_ERROR.getContent()));
            checkDto.setMsg("获取电池数据异常");
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            return checkDto;
        }

        // 查询基站电池循环次数
        NestParamOutDO nestParamOutDO = getNestParamOutDOByUuid(uuid);
        //有充电次数大于150次
        long chargeGe150Count = batteryInfoList.stream()
                .filter(b -> b.getChargeCount() > nestParamOutDO.getForbiddenCircleNum()).count();
        if (chargeGe150Count > 0) {
            checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            String format = String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEFORESTARTCHECKSERVICE_G900_CHARGE.getContent())
                    , nestParamOutDO.getForbiddenCircleNum(), batteryInfo1.getChargeCount(), batteryInfo2.getChargeCount());
            checkDto.setMsg(format);
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            return checkDto;
        }

        //有电池电量少去20
        long le20count = batteryInfoList.stream().filter(b -> b.getPercentage() < 20).count();
        if (le20count > 0 && aircraftBatteryChargeInPercent < 20) {
            checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
//            Object[] objects = new Object[]{batteryInfo1.getPercentage(), batteryInfo2.getPercentage()};
//            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_BATTERY_G900C_CAPCITY_LOWER_20.getContent(), objects, null));
            checkDto.setMsg(String.format("电池电量小于20,1号电池电量: %s,2号电池电量: %s",batteryInfo1.getPercentage(),batteryInfo2.getPercentage()));
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            return checkDto;
        }

        //有充电次数大于100次
        long chargeGe100Count = batteryInfoList.stream()
                .filter(b -> b.getChargeCount() > nestParamOutDO.getAlarmCircleNum()).count();
        if (chargeGe100Count > 0) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            String format = String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEFORESTARTCHECKSERVICE_G900_CHARGE.getContent())
                    , nestParamOutDO.getAlarmCircleNum(), batteryInfo1.getChargeCount(), batteryInfo2.getChargeCount());
            checkDto.setMsg(format);
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            return checkDto;
        }


        //有电池电量少去50
        long le50count = batteryInfoList.stream().filter(b -> b.getPercentage() < 50).count();
        if (le50count > 0 && aircraftBatteryChargeInPercent < 50) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
//            Object[] objects = new Object[]{batteryInfo1.getPercentage(), batteryInfo2.getPercentage()};
//            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_BATTERY_G900C_CAPCITY_LOWER_50.getContent(), objects, null));
            checkDto.setMsg(String.format("电池电量小于50,1号电池电量: %s,2号电池电量: %s",batteryInfo1.getPercentage(),batteryInfo2.getPercentage()));
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            return checkDto;
        }

        checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
        checkDto.setMsg(MessageUtils.getMessage(MessageEnum.BATTERY_READY_TO_USE_NORMAL.getContent()));
        checkDto.setCheckState(G600NestBatteryStateEnum.FULL.name());
        return checkDto;
    }

    /**
     * 修改电池检测逻辑，S110的先拿chargeExactPercentage值，如果没有再拿chargePercentage值，s100都拿chargePercentage
     *
     * @param uuid
     * @return
     */
    private CheckDto checkBatteryS100(String uuid, NestTypeEnum nestTypeEnum) {
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(4);
        checkDto.setCheckType(CheckTypeEnum.BATTERY_CHECK);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", -1);
        MiniNestBatteryState miniNestBatteryState = commonNestStateService.getMiniNestBatteryState(uuid);
        Integer uavBatteryPercentage = commonNestStateService.getUavBatteryPercentage(uuid);
        Integer percentage = miniNestBatteryState.getChargePercentage();
        // 飞行次数报警校验
        // 查询基站电池循环次数
        NestParamOutDO nestParamOutDO = getNestParamOutDOByUuid(uuid);
        Integer chargeCount = miniNestBatteryState.getChargeCount();
        if (chargeCount < 1) {
            checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            checkDto.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEFORESTARTCHECKSERVICE_OTHER_NONE.getContent()));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
            return checkDto;
        }
        if (chargeCount > nestParamOutDO.getForbiddenCircleNum()) {
            checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            String msg = String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEFORESTARTCHECKSERVICE_OTHER.getContent())
                    , nestParamOutDO.getForbiddenCircleNum(), chargeCount);
            checkDto.setMsg(msg);
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
            return checkDto;
        }

        if (chargeCount > nestParamOutDO.getAlarmCircleNum()) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            String msg = String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEFORESTARTCHECKSERVICE_OTHER.getContent())
                    , nestParamOutDO.getAlarmCircleNum(), chargeCount);
            checkDto.setMsg(msg);
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
            return checkDto;
        }

        if (NestTypeEnum.S110_AUTEL.equals(nestTypeEnum) || NestTypeEnum.S110_MAVIC3.equals(nestTypeEnum)) {
            Integer chargeExactPercentage = miniNestBatteryState.getChargeExactPercentage();
            if (Objects.nonNull(chargeExactPercentage) && chargeExactPercentage != 0) {
                if (chargeExactPercentage < 50 && uavBatteryPercentage < 50) {
                    checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
                    checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08") + (chargeExactPercentage + "%") + MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_13"));
                    checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
                    return checkDto;
                }

                if (chargeExactPercentage < 75 && uavBatteryPercentage < 75) {
                    checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
                    checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08") + (chargeExactPercentage + "%") + MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_13"));
                    checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
                    return checkDto;
                }
                checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
                checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_14"));
                checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
                return checkDto;
            }
        }

        if (percentage <= 0 && uavBatteryPercentage <= 0) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08") + miniNestBatteryState.computedChargePercentage("") + MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_13"));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
            return checkDto;
        }
        if (percentage <= 3 && uavBatteryPercentage < 50) {
            checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08") + miniNestBatteryState.computedChargePercentage("") + MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_13"));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
            return checkDto;
        }

        if (percentage <= 7 && uavBatteryPercentage < 75) {
            checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08") + miniNestBatteryState.computedChargePercentage(""));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
            return checkDto;
        }

        checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
        checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_14"));
        checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
        return checkDto;
    }

    private CheckDto checkBatteryG503(String uuid,Integer which){
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(4);
        checkDto.setCheckType(CheckTypeEnum.BATTERY_CHECK);
        G503NestBatteryState g503NestBatteryState = commonNestStateService.getG503NestBatteryState(uuid);
        if (Objects.isNull(g503NestBatteryState)) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg("未获取到电量信息，谨慎飞行");
            checkDto.setCheckState("电池电量");
            return checkDto;
        }
        List<G503NestBatteryState.BatteryGroup> batteryGroups = g503NestBatteryState.getBatteryGroups();
        if(CollectionUtils.isEmpty(batteryGroups)) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg("未获取到电量信息，谨慎飞行");
            checkDto.setCheckState("电池电量");
            return checkDto;
        }
        G503NestBatteryState.BatteryGroup group = batteryGroups.get(which - 1);
        if(Objects.isNull(group)) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg("未获取到电量信息，谨慎飞行");
            checkDto.setCheckState("电池电量");
            return checkDto;
        }
        G503NestBatteryState.BmuState bmuState = group.getBmuState();
        Integer nextUse = bmuState.getNextUse();
        List<G503NestBatteryState.Battery> batteries = group.getBatteries();
        //1、先检测有没有正在使用的电池
        Integer using = bmuState.getUsing();
        Integer percentage = 0;
        if(Objects.nonNull(using) && using > 0 && using < 5) {
            G503NestBatteryState.Battery battery = batteries.get(using - 1);
            if(Objects.nonNull(battery)) {
                percentage = battery.getBatteryChargePercent();
            }
        }

        //2、检测预备电池
        if(Objects.nonNull(nextUse) && nextUse > 0 && nextUse < 5) {
            G503NestBatteryState.Battery battery = batteries.get(nextUse - 1);
            if(Objects.nonNull(battery)) {
                percentage = battery.getBatteryChargePercent();
            }
        }

        if (percentage <= 0) {
            checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            checkDto.setMsg("电池电量：" + G503NestBatteryState.BatteryChargePercentEnum.getPrePercentByValue(percentage) + "，谨慎飞行");
            checkDto.setCheckState("电池电量");
            return checkDto;
        }

        if (percentage <= 3) {
            checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            checkDto.setMsg("电池电量：" + G503NestBatteryState.BatteryChargePercentEnum.getPrePercentByValue(percentage) + "，谨慎飞行");
            checkDto.setCheckState("电池电量");
            return checkDto;
        }

        if (percentage <= 7) {
            checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
            checkDto.setMsg("电池电量：" + G503NestBatteryState.BatteryChargePercentEnum.getPrePercentByValue(percentage));
            checkDto.setCheckState("电池电量");
            return checkDto;
        }

        checkDto.setPass(CheckDto.PassTypeEnum.PASS.getValue());
        checkDto.setMsg("电池状态正常");
        checkDto.setCheckState("电池电量");
        return checkDto;
    }


    /**
     * 阶段5，无人机是否开机,简易机巢需要检查
     */
    private void checkDronePowerOn(String uuid) {
        boolean airConn = commonNestStateService.getAircraftConnected(uuid);
        CheckDto checkDto = new CheckDto();
        Map<String, Object> data = new HashMap<>(2);
        if (airConn) {
            checkDto.setPass(1);
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_15"));
            checkDto.setPeriod(3);
            checkDto.setCheckType(CheckTypeEnum.AIRCRAFT_POWER_ON);
        } else {
            checkDto.setPass(2);
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_16"));
            checkDto.setPeriod(3);
            checkDto.setCheckType(CheckTypeEnum.AIRCRAFT_POWER_ON);
        }
    }

    private CheckDto checkStorage(String uuid, Integer missionId,Integer which) {
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        CheckDto checkDto = new CheckDto();
        if (cm != null) {
            AirLineEntity airLineEntity = airLineService.getPicCountAndVideoCountAndLen(missionId);
            if (airLineEntity == null) {
                checkDto.setPass(0);
                checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_17"));
                checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_18"));
                return checkDto;
            }

            MediaManagerCf mediaManagerCf = cm.getMediaManagerCf();
            if (airLineEntity.getPhotoCount() == null) {
                MqttResult<AvailableCaptureCountEntity> res1 = mediaManagerCf.getSdCardAvailableCaptureCount(AirIndexEnum.getInstance(which));
                if (res1.isSuccess()) {
                    AvailableCaptureCountEntity res = res1.getRes();
                    Integer availableCaptureCount = res.getAvailableCaptureCount();
                    if (availableCaptureCount < airLineEntity.getPhotoCount()) {
                        checkDto.setPass(0);
                        checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_19"));
                        checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_20"));
                        return checkDto;
                    }
                } else {
                    checkDto.setPass(0);
                    checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_21"));
                    checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_20"));
                    return checkDto;
                }
            }

            if (airLineEntity.getPredicTime() != null) {
                MqttResult<AvailableRecordTimeEntity> res2 = mediaManagerCf.getSdCardAvailableRecordTimes(AirIndexEnum.getInstance(which));
                if (res2.isSuccess()) {
                    AvailableRecordTimeEntity res = res2.getRes();
                    Integer availableRecordingTimeInSeconds = res.getAvailableRecordingTimeInSeconds();
                    if (availableRecordingTimeInSeconds < airLineEntity.getPredicTime()) {
                        checkDto.setPass(0);
                        checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_22"));
                        checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_23"));
                        return checkDto;
                    }
                } else {
                    checkDto.setPass(0);
                    checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_31"));
                    checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_23"));
                    return checkDto;
                }
            }

            checkDto.setPass(1);
            checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_24"));
            checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_25"));
            return checkDto;
        }
        checkDto.setPass(0);
        checkDto.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_26"));
        checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_25"));
        return checkDto;
    }

    private boolean checkBatteryG900Chi(Integer readyUseBatteryIndex, CheckDto checkDto
            , M300BatteryInfoDTO m300BatteryInfoDTO, String uuid) {
        Map<Integer, String> letterMap = new HashMap<>(2);
        letterMap.put(0, "A");
        letterMap.put(1, "B");
        List<M300BatteryInfoDTO.BatteryInfo> batteryInfoList = m300BatteryInfoDTO.getBatteryInfoList();
        // 查询基站电池循环次数
        NestParamOutDO nestParamOutDO = getNestParamOutDOByUuid(uuid);

        for (int i = 0; i < batteryInfoList.size(); i++) {
            M300BatteryInfoDTO.BatteryInfo batteryInfo = batteryInfoList.get(i);
            Integer chargeCount = batteryInfo.getChargeCount();
            if (chargeCount > nestParamOutDO.getForbiddenCircleNum()) {
                checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
                String format = String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEFORESTARTCHECKSERVICE_G900.getContent())
                        , (readyUseBatteryIndex + 1), letterMap.get(i), nestParamOutDO.getForbiddenCircleNum(), chargeCount);
                checkDto.setMsg(format);
                checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_27"));
                return false;
            }
            if (chargeCount > nestParamOutDO.getAlarmCircleNum()) {
                checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
                String format = String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEFORESTARTCHECKSERVICE_G900.getContent())
                        , (readyUseBatteryIndex + 1), letterMap.get(i), nestParamOutDO.getAlarmCircleNum(), chargeCount);
                checkDto.setMsg(format);
                checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_27"));
                return false;
            }
            if (batteryInfo.getPercentage() < 50) {
                checkDto.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
                checkDto.setMsg("[" + (readyUseBatteryIndex + 1) + letterMap.get(i) + "]" + MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_BATTERY_G900_CAPCITY_LOWER_50.getContent()) + ":" + batteryInfo.getPercentage() + "%");
                checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
                return false;
            }

            if (batteryInfo.getPercentage() < 75) {
                checkDto.setPass(CheckDto.PassTypeEnum.WARN.getValue());
                checkDto.setMsg("[" + (readyUseBatteryIndex + 1) + letterMap.get(i) + "]" + MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_BATTERY_G900_CAPCITY_LOWER_75.getContent()) + ":" + batteryInfo.getPercentage() + "%");
                checkDto.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_08"));
                return false;
            }
        }
        return true;
    }

    /**
     * 大疆机场飞行检查
     * @return
     */
    public CheckRes checkDji(String uuid ){
        CheckRes checkRes = new CheckRes();

        DJIAerographyInfoOutDTO djiAerographyInfoOutDTO = this.djiDockService.getAerographyInfoDto(uuid);
        CheckDto aerographyDTO = new CheckDto();
        aerographyDTO.setPass(CheckDto.PassTypeEnum.PASS.getValue())
        	.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_28"))
        	.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_NORMAL.getContent()))
        	.setPeriod(1)
        	.setCheckType(CheckTypeEnum.AEROGRAPH_CHECK);
        //默认检查通过
        checkRes.setCheckAerographyRes(aerographyDTO);


        //气象
        if (djiAerographyInfoOutDTO == null) {
            aerographyDTO.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            aerographyDTO.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_ERROR.getContent()));
            aerographyDTO.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
        }

        //风速过大-警告
        else if (new BigDecimal("5.0").compareTo( djiAerographyInfoOutDTO.getWindSpeed() ) < 0
            && new BigDecimal("7.5").compareTo( djiAerographyInfoOutDTO.getWindSpeed() ) > 0) {
            aerographyDTO.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            aerographyDTO.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_03") + djiAerographyInfoOutDTO.getWindSpeed() + MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_04"));
            aerographyDTO.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
        }

        //风速过大-报错
        else if (new BigDecimal("7.5").compareTo( djiAerographyInfoOutDTO.getWindSpeed() ) <= 0) {
            aerographyDTO.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            aerographyDTO.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_03") + djiAerographyInfoOutDTO.getWindSpeed() + MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_04"));
            aerographyDTO.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
        }

        //雨量-警告
        else if (BigDecimal.ZERO.compareTo( djiAerographyInfoOutDTO.getRainfall() ) < 0
            && new BigDecimal("2.0").compareTo( djiAerographyInfoOutDTO.getRainfall() ) > 0) {
            aerographyDTO.setPass(CheckDto.PassTypeEnum.WARN.getValue());
            aerographyDTO.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_RAINING.getContent()) + ":" + djiAerographyInfoOutDTO.getRainfall() + "(mm)");
            aerographyDTO.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
        }

        //雨量-报错
        else if (new BigDecimal("2.0").compareTo( djiAerographyInfoOutDTO.getRainfall() ) <= 0) {
            aerographyDTO.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            aerographyDTO.setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_RAINING.getContent()) + ":" + djiAerographyInfoOutDTO.getRainfall() + "(mm)");
            aerographyDTO.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_01"));
        }

        //基站状态
        CheckDto nestStateDTO = new CheckDto();
        nestStateDTO.setPass(CheckDto.PassTypeEnum.PASS.getValue())
                        .setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_32"))
                        .setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_NEST_ABLE_TO_PERFORM_TASKS.getContent()))
                        .setPeriod(2)
                        .setCheckType(CheckTypeEnum.NEST_CHECK);
        checkRes.setCheckNestRes(nestStateDTO);
        DJIDockInfoOutDTO djiDockInfoOutDTO = this.djiDockService.getDJIDockInfoOutDTO(uuid);
        if (DjiDockPropertyOsdDO.ModeCodeEnum.IDLE.getValue() != djiDockInfoOutDTO.getModeCode()) {
            nestStateDTO.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
            nestStateDTO.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_NEST_UNABLE_TO_PERFORM_TASKS.getContent()));
            nestStateDTO.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_33"));
        }

        //基站存储-剩余10%提示
        CheckDto nestStorageDTO = new CheckDto();
        nestStorageDTO.setPass(CheckDto.PassTypeEnum.PASS.getValue())
                .setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_32"))
                .setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_24"))
                .setPeriod(3);
        checkRes.setCheckStorageRes(nestStorageDTO);
        DJIDockInfoOutDTO.Storage djiStorage = djiDockInfoOutDTO.getStorage();
        Optional.ofNullable(djiStorage).ifPresent(storage -> {
            BigDecimal total = BigDecimal.valueOf(storage.getTotal()==null?0:storage.getTotal())
                , used = BigDecimal.valueOf(storage.getUsed()==null?0:storage.getUsed());
            if(new BigDecimal("0.90").compareTo(used.divide(total, 2, RoundingMode.HALF_UP)) < 0){
                nestStorageDTO.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
                nestStorageDTO.setMsg(MessageUtils.getMessage(MessageEnum.GEOAI_CHECK_NEST_UNABLE_TO_PERFORM_TASKS.getContent()));
                nestStorageDTO.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_33"));
            }
        });


        //电池信息
        CheckDto droneChargeDTO = new CheckDto();
        droneChargeDTO.setPass(CheckDto.PassTypeEnum.PASS.getValue())
                .setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_32"))
                .setMsg(MessageUtils.getMessage(MessageEnum.AEROGRAPHY_CHECK_NORMAL.getContent()))
                .setPeriod(4)
                .setCheckType(CheckTypeEnum.BATTERY_CHECK);
        checkRes.setCheckBatteryRes(droneChargeDTO);
        DJIDockInfoOutDTO.DroneChargeState droneChargeState = djiDockInfoOutDTO.getDroneChargeState();
        Optional.ofNullable(droneChargeState)
                .map(DJIDockInfoOutDTO.DroneChargeState::getCapacityPercent)
                .ifPresent(capacityPercent-> {
                    if(capacityPercent<50){
                        droneChargeDTO.setPass(CheckDto.PassTypeEnum.ERROR.getValue());
                        droneChargeDTO.setCheckState(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_33"));
                    }else if(capacityPercent<75){
                        droneChargeDTO.setPass(CheckDto.PassTypeEnum.WARN.getValue());
                    }
                    droneChargeDTO.setMsg(MessageUtils.getMessage("geoai_uos_BeforeStartCheckService_11") + capacityPercent + "%");
                });

        return checkRes;
    }


    @Data
    public static class CheckRes {
        private CheckDto checkAerographyRes;
        private CheckDto checkNestRes;
        private CheckDto checkBatteryRes;
        private CheckDto checkStorageRes;

        public CheckResEnum getState() {
            List<CheckDto> checkDtoList = Arrays.asList(this.checkAerographyRes, this.checkNestRes, this.checkBatteryRes, this.checkStorageRes);
            for (CheckDto checkDto : checkDtoList) {
                if (CheckDto.PassTypeEnum.ERROR.getValue() == checkDto.getPass()) {
                    return CheckResEnum.ERROR;
                }
            }

            for (CheckDto checkDto : checkDtoList) {
                if (CheckDto.PassTypeEnum.WARN.getValue() == checkDto.getPass()) {
                    return CheckResEnum.WARN;
                }
            }

            return CheckResEnum.PASS;
        }

        public List<String> listWarnMsgsByPass(CheckDto.PassTypeEnum passTypeEnum) {
            List<CheckDto> checkDtoList = Arrays.asList(this.checkAerographyRes, this.checkNestRes, this.checkBatteryRes, this.checkStorageRes);
            if (passTypeEnum == null) {
                return checkDtoList.stream().map(CheckDto::getMsg).collect(Collectors.toList());
            }
            return checkDtoList.stream().filter(checkDto -> passTypeEnum.getValue() == checkDto.getPass()).map(CheckDto::getMsg).collect(Collectors.toList());
        }

        public String listWarnStrByPass(CheckDto.PassTypeEnum passTypeEnum) {
            List<String> strings = listWarnMsgsByPass(passTypeEnum);
            if (CollectionUtil.isNotEmpty(strings)) {
                return String.join(";", strings);
            }
            return "";
        }

        public List<CheckDto> listCheckDto() {
            return Arrays.asList(this.checkAerographyRes, this.checkNestRes, this.checkBatteryRes, this.checkStorageRes);
        }
    }

    public enum CheckResEnum {
        ERROR,
        WARN,
        PASS
    }

}
