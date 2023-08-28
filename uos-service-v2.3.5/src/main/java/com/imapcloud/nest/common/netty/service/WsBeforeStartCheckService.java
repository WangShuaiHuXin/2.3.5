package com.imapcloud.nest.common.netty.service;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.*;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务开始前检查
 *
 * @author daolin
 */
@Deprecated
@Component
@Slf4j
public class WsBeforeStartCheckService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private NestService nestService;

    @Autowired
    private CommonNestStateService commonNestStateService;

    public void startCheck(String uuid) throws InterruptedException {
        Integer type = -1;
//        if (type == NestTypeEnum.I_CREST2.getValue()) {
//            return;
//        }

        Thread.sleep(1000);
        checkAerograph(uuid, NestTypeEnum.getInstance(type));
        Thread.sleep(1000);
        checkNest(uuid);
        //固定机巢的时候才会去检查天线和电池
        if (type == NestTypeEnum.G600.getValue()) {
            Thread.sleep(1000);
            checkBatteryG600(uuid);
        }

        if (type == NestTypeEnum.G900.getValue()) {
            Thread.sleep(1000);
            checkBatteryG900(uuid);
        }

        if (type == NestTypeEnum.S100_V2.getValue() || type == NestTypeEnum.S100_V1.getValue() || type == NestTypeEnum.S110_AUTEL.getValue()) {
            Thread.sleep(1000);
            checkBatteryS100(uuid);
        }
        if (type == NestTypeEnum.T50.getValue()) {
            Thread.sleep(1000);
            checkDronePowerOn(uuid);
        }
    }

    /**
     * 阶段1，检查气象状态
     */
    private void checkAerograph(String uuid, NestTypeEnum nestType) {
        Map<String, Object> data = new HashMap<>(2);
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(1);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkAerograph", -1);
        checkDto.setCheckType(CheckTypeEnum.AEROGRAPH_CHECK);
        AerographyInfoDto aerograph = commonNestStateService.getAerographyInfoDto(uuid);

        if (aerograph == null) {
            checkDto.setPass(0);
            checkDto.setMsg("获取气象数据出错");
            checkDto.setCheckState("气象检查不通过");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkAerograph", 0);
            return;
        }

        //正常条件下，压强和空气湿度不可能为0，如果为0代表气象站出了问题，或者说没有获得气象站的消息
        if (aerograph.getPressure() == 0.0 || aerograph.getHumidity() == 0.0) {
            checkDto.setPass(0);
            checkDto.setMsg("未获得气象站相关信息");
            checkDto.setCheckState("气象检查不通过");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkAerograph", 0);
            return;
        }

        //M300抗风7级，其他5级
        double airWindMax = nestType.equals(NestTypeEnum.G900) ? 7 : 5;
        //风速过大
        if (aerograph.getSpeed() >= airWindMax) {
            checkDto.setPass(0);
            checkDto.setMsg("风速过大");
            checkDto.setCheckState("气象检查不通过");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkAerograph", 0);
            return;
        }

        //正在下雨
        if (aerograph.getRainfall() > 0.0D || (aerograph.getRainsnow() != null && 1 == aerograph.getRainsnow())) {
            checkDto.setPass(0);
            checkDto.setMsg("当前正在下雨");
            checkDto.setCheckState("气象检查不通过");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkAerograph", 0);
            return;
        }
        //检查通过
        checkDto.setPass(1);
        checkDto.setMsg("气象条件正常");
        checkDto.setCheckState("气象检查通过");
        data.put("dto", checkDto);
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
        sendMessageByWs(uuid, message);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkAerograph", 1);
    }

    /**
     * 阶段2，检查机巢状态
     */
    private void checkNest(String uuid) {
        Map<String, Object> data = new HashMap<>(2);
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(2);
        checkDto.setCheckType(CheckTypeEnum.NEST_CHECK);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkNest", -1);
        NestStateEnum nestStateEnum = commonNestStateService.getNestStateEnum(uuid);
        //数据为空
        if (nestStateEnum == NestStateEnum.UNKNOWN) {
            checkDto.setPass(0);
            checkDto.setMsg("获取机巢数据出错");
            checkDto.setCheckState("UNKNOWN");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkNest", 0);
            return;
        }
        checkDto.setCheckState(nestStateEnum.getValue());
        //准备就绪
        if (NestStateEnum.STANDBY.equals(nestStateEnum) || NestStateEnum.BATTERY_LOADED.equals(nestStateEnum) || NestStateEnum.READY_TO_GO.equals(nestStateEnum)) {
            checkDto.setPass(1);
            checkDto.setMsg("机巢可以发送执行任务指令");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkNest", 1);
            return;
        } else {
            checkDto.setPass(0);
            checkDto.setMsg("当前状态不允许执行任务");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkNest", 0);
            return;
        }

    }

    /**
     * 阶段3，检查天线状态
     */
    private void checkFold(String uuid) {
        Map<String, Object> data = new HashMap<>(2);
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(3);
        checkDto.setCheckType(CheckTypeEnum.ANTENNA_CHECK);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkFold", -1);
        int code = commonNestStateService.getAntennaState(uuid);
        //数据为空
        if (code == -1) {
            checkDto.setPass(0);
            checkDto.setMsg("获取天线数据出错");
            checkDto.setCheckState(MotorStateEnum.UNKNOWN.getState());
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkFold", 0);
            return;
        }
        //打开状态
        checkDto.setCheckState(MotorStateEnum.getStateByCode(code));
        if (MotorStateEnum.OPEN.getCode().equals(code)) {
            checkDto.setPass(1);
            checkDto.setMsg("天线已打开");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkFold", 1);
        } else {
            checkDto.setPass(0);
            checkDto.setMsg("当前天线未打开");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkFold", 0);
        }
    }

    /**
     * 阶段4，电池检测
     */
    private void checkBatteryG600(String uuid) {
        Map<String, Object> data = new HashMap<>(2);
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(4);
        checkDto.setCheckType(CheckTypeEnum.BATTERY_CHECK);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", -1);
        G600NestBatteryInfoDto g600NestBatteryInfoDto = commonNestStateService.getG600NestBatteryInfoDto(uuid);
        if (g600NestBatteryInfoDto == null) {
            checkDto.setPass(0);
            checkDto.setMsg("获取电池信息异常");
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
            return;
        }
        int readyUseBatteryIndex = g600NestBatteryInfoDto.getReadyUseBatteryIndex();
        List<NestBatteryInfoDto> list = g600NestBatteryInfoDto.getNestBatteryInfoDtoList();
        if (CollectionUtil.isEmpty(list)) {
            checkDto.setPass(0);
            checkDto.setMsg("获取电池信息异常");
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
            return;
        }


        //1.判断是否四块电池都在机巢，如果机巢空缺一块电池，判断空缺的电池是否在飞机上使用（根据飞机返回的电量不为0判断，顺便判断电池电量是否大于75%）
        for (int i = 0; i < list.size(); i++) {
            NestBatteryInfoDto nestBatteryInfoDto = list.get(i);
            if (G600NestBatteryStateEnum.INUSE.getCode().equals(nestBatteryInfoDto.getState())) {
                int battaryInCharge = commonNestStateService.getAircraftBatteryChargeInPercent(uuid);
                if (battaryInCharge < 75) {
                    checkDto.setPass(0);
                    checkDto.setMsg("机巢电池有空缺，在飞机中的电池电量为" + battaryInCharge + "%");
                    checkDto.setCheckState(G600NestBatteryStateEnum.INUSE.name());
                    data.put("dto", checkDto);
                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                    sendMessageByWs(uuid, message);
                    redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
                    return;
                } else {
                    checkDto.setPass(1);
                    checkDto.setMsg("机巢空缺电池，在飞机中的电池电量为" + battaryInCharge + "%");
                    checkDto.setCheckState(G600NestBatteryStateEnum.INUSE.name());
                    data.put("dto", checkDto);
                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                    sendMessageByWs(uuid, message);
                    redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 1);
                    return;
                }
            }
        }

        //2.如果没有机巢的电池都在，则拿取上一次正在使用电池序号，然后判断下一次捉的电池电量是否大于75%,如果不知道上一次使用的是第几块，则检查全部的电池是否电量都大于75%
        if (readyUseBatteryIndex == -1) {
            for (int i = 0; i < list.size(); i++) {
                NestBatteryInfoDto nestBatteryInfoDto = list.get(i);
                if (nestBatteryInfoDto.getPercentage() < 75) {
                    checkDto.setPass(0);
                    checkDto.setMsg("电池(" + (i + 1) + ")电量为" + nestBatteryInfoDto.getPercentage() + "%");
                    checkDto.setCheckState(G600NestBatteryStateEnum.CHARGING.name());
                    data.put("dto", checkDto);
                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                    sendMessageByWs(uuid, message);
                    redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
                    return;
                }
            }
        } else {
            NestBatteryInfoDto nestBatteryInfoDto = list.get(readyUseBatteryIndex);
            Integer useCount = nestBatteryInfoDto.getChargeCount();
            if (useCount >= 150) {
                checkDto.setPass(2);
                checkDto.setMsg("预备使用电池(" + (readyUseBatteryIndex + 1) + ")充电次数超过150次(" + nestBatteryInfoDto.getChargeCount() + ")");
                checkDto.setCheckState(G600NestBatteryStateEnum.CHARGING.name());
                data.put("dto", checkDto);
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                sendMessageByWs(uuid, message);
                redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
                return;
            } else if (useCount >= 100) {
                checkDto.setPass(0);
                checkDto.setMsg("预备使用电池(" + (readyUseBatteryIndex + 1) + ")充电次数超过100次(" + nestBatteryInfoDto.getChargeCount() + ")");
                checkDto.setCheckState(G600NestBatteryStateEnum.CHARGING.name());
                data.put("dto", checkDto);
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                sendMessageByWs(uuid, message);
                redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
                return;
            }

            if (nestBatteryInfoDto.getPercentage() < 75) {
                checkDto.setPass(0);
                checkDto.setMsg("预备使用电池剩余量为" + nestBatteryInfoDto.getPercentage() + "%");
                checkDto.setCheckState(G600NestBatteryStateEnum.CHARGING.name());
                data.put("dto", checkDto);
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                sendMessageByWs(uuid, message);
                redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
                return;
            } else {
                checkDto.setPass(1);
                checkDto.setMsg("预备使用电池(" + (readyUseBatteryIndex + 1) + ")剩余电量为" + nestBatteryInfoDto.getPercentage() + "%");
                checkDto.setCheckState(G600NestBatteryStateEnum.CHARGING.name());
                data.put("dto", checkDto);
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                sendMessageByWs(uuid, message);
                redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 1);
                return;
            }
        }

        checkDto.setPass(1);
        checkDto.setMsg("机巢电池正常");
        checkDto.setCheckState(G600NestBatteryStateEnum.FULL.name());
        data.put("dto", checkDto);
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
        sendMessageByWs(uuid, message);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 1);
    }

    private void checkBatteryG900(String uuid) {
        Map<String, Object> data = new HashMap<>(2);
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(4);
        checkDto.setCheckType(CheckTypeEnum.BATTERY_CHECK);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", -1);
        G900NestBatteryInfoDto g900NestBatteryInfoDto = commonNestStateService.getG900NestBatteryInfoDto(uuid);
        if (g900NestBatteryInfoDto == null ||
                CollectionUtil.isEmpty(g900NestBatteryInfoDto.getM300BatteryInfoDTOList())
                || g900NestBatteryInfoDto.getAvailable() <= 0

        ) {
            checkDto.setPass(0);
            checkDto.setMsg("获取电池信息异常");
            checkDto.setCheckState(G600NestBatteryStateEnum.UNKNOWN.name());
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
            return;
        }
        int readyUseBatteryIndex = g900NestBatteryInfoDto.getReadyUseBatteryIndex();
        List<M300BatteryInfoDTO> list = g900NestBatteryInfoDto.getM300BatteryInfoDTOList();

        //1.判断是否四组电池都在机巢，如果机巢空缺一组电池，判断空缺的电池是否在飞机上使用（根据飞机返回的电量不为0判断，顺便判断电池电量是否大于75%）
        if (readyUseBatteryIndex == -1) {
            for (int i = 0; i < list.size(); i++) {
                M300BatteryInfoDTO m300BatteryInfoDTO = list.get(i);
                boolean res = checkBatteryG900Chi(data, readyUseBatteryIndex, uuid, checkDto, m300BatteryInfoDTO, 0);
                if (!res) {
                    return;
                }
            }
        }

        //2.如果机巢的电池都在，则拿取上一次正在使用电池序号，然后判断下一次捉的电池电量是否大于75%,如果不知道上一次使用的是第几块，则检查全部的电池是否电量都大于75%
        if (readyUseBatteryIndex != -1) {
            M300BatteryInfoDTO m300BatteryInfoDTO = list.get(readyUseBatteryIndex);
            boolean res = checkBatteryG900Chi(data, readyUseBatteryIndex, uuid, checkDto, m300BatteryInfoDTO, g900NestBatteryInfoDto.getAircraftBatteryChargeInPercent());
            if (!res) {
                return;
            }
        }

        checkDto.setPass(1);
        checkDto.setMsg("机巢电池正常");
        checkDto.setCheckState(G600NestBatteryStateEnum.FULL.name());
        data.put("dto", checkDto);
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
        sendMessageByWs(uuid, message);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 1);
    }

    private void checkBatteryS100(String uuid) {
        Map<String, Object> data = new HashMap<>(2);
        CheckDto checkDto = new CheckDto();
        checkDto.setPeriod(4);
        checkDto.setCheckType(CheckTypeEnum.BATTERY_CHECK);
        redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", -1);
        Integer chargeCount = commonNestStateService.getS100BatteryChargeCount(uuid);
        if (chargeCount == -1) {
            checkDto.setPass(0);
            checkDto.setMsg("没有获取充电次数,可以先开机获取充电次数");
            checkDto.setCheckState("充电次数");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
            return;
        }
        if (chargeCount >= 150) {
            checkDto.setPass(2);
            checkDto.setMsg("电池充电次数超过150次(" + chargeCount + ")");
            checkDto.setCheckState("充电次数");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
            return;
        } else if (chargeCount >= 100) {
            checkDto.setPass(0);
            checkDto.setMsg("电池充电次数超过100次(" + chargeCount + ")");
            checkDto.setCheckState("充电次数");
            data.put("dto", checkDto);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
            redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
            return;
        }
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
            checkDto.setMsg("飞机已连接");
            checkDto.setPeriod(3);
            checkDto.setCheckType(CheckTypeEnum.AIRCRAFT_POWER_ON);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
        } else {
            checkDto.setPass(2);
            checkDto.setMsg("飞机未连接");
            checkDto.setPeriod(3);
            checkDto.setCheckType(CheckTypeEnum.AIRCRAFT_POWER_ON);
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
            sendMessageByWs(uuid, message);
        }
    }


    private void sendMessageByWs(String uuid, String message) {
        ChannelService.sendMessageByType3Channel(uuid, message);
    }

    private boolean checkBatteryG900Chi(Map<String, Object> data, Integer readyUseBatteryIndex, String uuid, CheckDto checkDto, M300BatteryInfoDTO m300BatteryInfoDTO, Integer airBatteryPercent) {
        Map<Integer, String> letterMap = new HashMap<>(2);
        letterMap.put(0, "A");
        letterMap.put(1, "B");
        List<M300BatteryInfoDTO.BatteryInfo> batteryInfoList = m300BatteryInfoDTO.getBatteryInfoList();
        for (int i = 0; i < batteryInfoList.size(); i++) {
            M300BatteryInfoDTO.BatteryInfo batteryInfo = batteryInfoList.get(i);
            Integer chargeCount = batteryInfo.getChargeCount();
            if (airBatteryPercent > 0) {
                batteryInfo.setPercentage(airBatteryPercent);
            }
            if (chargeCount > 150) {
                checkDto.setPass(2);
                checkDto.setMsg("预备使用电池(" + (readyUseBatteryIndex + 1) + letterMap.get(i) + ")充电次数超过150次(" + chargeCount + ")");
                checkDto.setCheckState("电池充电次数");
                data.put("dto", checkDto);
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                sendMessageByWs(uuid, message);
                redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
                return false;
            } else if (chargeCount > 100) {
                checkDto.setPass(0);
                checkDto.setMsg("预备使用电池(" + (readyUseBatteryIndex + 1) + letterMap.get(i) + ")充电次数超过100次(" + chargeCount + ")");
                checkDto.setCheckState("电池充电次数");
                data.put("dto", checkDto);
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                sendMessageByWs(uuid, message);
                redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
                return false;
            }
            if (batteryInfo.getPercentage() < 75) {
                checkDto.setPass(0);
                checkDto.setMsg("预备使用电池(" + (readyUseBatteryIndex + 1) + letterMap.get(i) + ")容量为" + batteryInfo.getPercentage() + "%");
                checkDto.setCheckState("电池电量");
                data.put("dto", checkDto);
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                sendMessageByWs(uuid, message);
                redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 0);
                return false;
            } else {
                checkDto.setPass(1);
                checkDto.setMsg("预备使用电池(" + (readyUseBatteryIndex + 1) + letterMap.get(i) + ")容量为" + batteryInfo.getPercentage() + "%");
                checkDto.setCheckState("电池电量");
                data.put("dto", checkDto);
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                sendMessageByWs(uuid, message);
                redisService.hSet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, uuid), "checkBattery", 1);
                return true;
            }
        }
        return true;
    }


}
