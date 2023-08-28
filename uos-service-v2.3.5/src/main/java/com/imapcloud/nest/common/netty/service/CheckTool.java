package com.imapcloud.nest.common.netty.service;

import com.imapcloud.nest.pojo.dto.CheckDto;
import com.imapcloud.nest.pojo.dto.NestInfoDto;
import com.imapcloud.nest.enums.CheckTypeEnum;
import com.imapcloud.nest.enums.MotorStateEnum;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.sdk.pojo.constant.AircraftStateEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import com.imapcloud.sdk.pojo.constant.RTKStateEnum;
import com.imapcloud.sdk.pojo.entity.NestState;
import com.imapcloud.sdk.pojo.entity.RTKState;
import com.imapcloud.sdk.pojo.entity.WaypointState;

import static com.imapcloud.sdk.pojo.constant.MissionStateEnum.*;

/**
 * 根据传入得状态检查当前状态是否匹配
 *
 * @author daolin
 */
public class CheckTool {

    public static class CheckResult {
        private Boolean pass;
        private String state;

        public Boolean getPass() {
            return pass;
        }

        public void setPass(Boolean pass) {
            this.pass = pass;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    public static CheckResult successCheck(String state) {
        CheckResult checkResult = new CheckResult();
        checkResult.setPass(true);
        checkResult.setState(state);
        return checkResult;
    }

    public static CheckResult failCheck(String state) {
        CheckResult checkResult = new CheckResult();
        checkResult.setPass(false);
        checkResult.setState(state);
        return checkResult;
    }


    public CheckDto getStateByTopic(String topic, String uuid) {
        CommonNestStateService commonNestStateService = SpringContextUtils.getBean(CommonNestStateService.class);

        switch (topic) {
            case "BATTERY_LOADED":
                NestState nestState2 = commonNestStateService.getNestState(uuid);
                CheckDto checkDto1 = new CheckDto();
                checkDto1.setPeriod(1);
                checkDto1.setCheckState(nestState2.getNestStateConstant().getValue());
                checkDto1.setCheckType(CheckTypeEnum.BATTERY_LOADED);
                if (NestStateEnum.BATTERY_LOADED.equals(nestState2.getNestStateConstant()) || NestStateEnum.PREPARING.equals(nestState2.getNestStateConstant())) {
                    checkDto1.setPass(1);
                    return checkDto1;
                }
                checkDto1.setPass(2);
                return checkDto1;
            case "CABIN_OPENING":
                NestInfoDto nestInfoDto1 = commonNestStateService.getFixNestInfoDto(uuid);
                CheckDto checkDto2 = new CheckDto();
                checkDto2.setPeriod(2);
                checkDto2.setCheckType(CheckTypeEnum.CABIN_OPENING);
                checkDto2.setCheckState(MotorStateEnum.getStateByCode(nestInfoDto1.getCabin()));
                if (nestInfoDto1.getCabin() == 1) {
                    checkDto2.setPass(1);
                    return checkDto2;
                }
                checkDto2.setPass(2);
                return checkDto2;
            case "LIFT_RISING":
                NestInfoDto nestInfoDto2 = commonNestStateService.getFixNestInfoDto(uuid);
                CheckDto checkDto3 = new CheckDto();
                checkDto3.setPeriod(3);
                checkDto3.setCheckType(CheckTypeEnum.LIFT_RISING);
                checkDto3.setCheckState(MotorStateEnum.getStateByCode(nestInfoDto2.getLift()));
                if (nestInfoDto2.getLift() != null && nestInfoDto2.getLift() == 1) {
                    checkDto3.setPass(1);
                    return checkDto3;
                }
                checkDto3.setPass(2);
                return checkDto3;
            case "SQUARE_Y":
                NestInfoDto nestInfoDto3 = commonNestStateService.getFixNestInfoDto(uuid);
                CheckDto checkDto4 = new CheckDto();
                checkDto4.setPeriod(4);
                checkDto4.setCheckType(CheckTypeEnum.SQUARE_Y);
                checkDto4.setCheckState(MotorStateEnum.getStateByCode(nestInfoDto3.getSquareY()));
                if (nestInfoDto3.getSquareY() == 1) {
                    checkDto4.setPass(1);
                    return checkDto4;
                }
                checkDto4.setPass(2);
                return checkDto4;
            case "FIXED_POINT":
                RTKState rtkState = commonNestStateService.getRtkState(uuid);
                CheckDto checkDto5 = new CheckDto();
                checkDto5.setPeriod(5);
                checkDto5.setPass(0);
                checkDto5.setCheckState(rtkState.getPositioningSolution().getValue());
                checkDto5.setCheckType(CheckTypeEnum.FIXED_POINT);
                if (RTKStateEnum.FIXED_POINT.equals(rtkState.getPositioningSolution())) {
                    checkDto5.setPass(1);
                    checkDto5.setMsg("搜星成功");
                } else {
                    checkDto5.setPass(2);
                    checkDto5.setMsg("搜星中");
                }
                return checkDto5;

            case "MISSION_UPLOADING":
                WaypointState waypointState = commonNestStateService.getWaypointState(uuid);
                CheckDto checkDto6 = new CheckDto();
                checkDto6.setPeriod(6);
                checkDto6.setCheckType(CheckTypeEnum.MISSION_UPLOADING);
                if (waypointState != null) {
                    checkDto6.setCheckState(waypointState.getMissionState().getValue());
                    if (UPLOADING.equals(waypointState.getMissionState()) || READY_TO_EXECUTE.equals(waypointState.getMissionState()) || START.equals(waypointState.getMissionState())) {
                        checkDto6.setPass(1);
                        checkDto6.setMsg("任务上传中");
                    } else if (NOT_SUPPORTED.equals(waypointState.getMissionState())) {
                        checkDto6.setPass(0);
                        checkDto6.setMsg("任务上传失败");
                    } else {
                        checkDto6.setPass(2);
                    }
                } else {
                    checkDto6.setPass(2);
                }
                return checkDto6;
            case "TAKE_OFF":
                NestState nestState8 = commonNestStateService.getNestState(uuid);
                CheckDto checkDto7 = new CheckDto();
                checkDto7.setPeriod(7);
                checkDto7.setCheckType(CheckTypeEnum.TAKE_OFF);
                checkDto7.setCheckState(nestState8.getAircraftStateConstant().getValue());
                if (AircraftStateEnum.FLYING.equals(nestState8.getAircraftStateConstant())) {
                    checkDto7.setPass(1);
                    checkDto7.setMsg("飞机离开平台");
                } else {
                    checkDto7.setPass(2);
                    checkDto7.setMsg("飞机未离开平台");
                }
                return checkDto7;
            case "FINISH":
                CheckDto checkDto8 = new CheckDto();
                checkDto8.setPass(1);
                checkDto8.setPeriod(8);
                checkDto8.setMsg("启动过程结束");
                checkDto8.setCheckType(CheckTypeEnum.FINISH);
                return checkDto8;
            default:
                CheckDto checkDto9 = new CheckDto();
                checkDto9.setMsg("未知状态");
                return checkDto9;
        }
    }
}
