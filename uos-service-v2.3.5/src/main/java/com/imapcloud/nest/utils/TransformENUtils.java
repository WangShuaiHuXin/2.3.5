package com.imapcloud.nest.utils;

import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import org.springframework.util.StringUtils;

/**
 * @Classname TransformWSUtil
 * @Description WS国际化转换
 * @Date 2022/11/22 16:32
 * @Author Carnival
 */
public class TransformENUtils {

    public static String transformNestState(String state) {
        if (StringUtils.hasText(state)) {
            switch (state) {
                case "初始化":
                    state = NestStateEnum.INIT.getEnglish();
                    break;
                case "自检中":
                    state = NestStateEnum.SELF_TEST.getEnglish();
                    break;
                case "等待重置":
                    state = NestStateEnum.WAIT_RESET.getEnglish();
                    break;
                case "重置中":
                    state = NestStateEnum.RESETTING.getEnglish();
                    break;
                case "待机":
                    state = NestStateEnum.STANDBY.getEnglish();
                    break;
                case "启动中":
                    state = NestStateEnum.PREPARING.getEnglish();
                    break;
                case "未装载开启模式":
                    state = NestStateEnum.OPEN_WITHOUT_BATTERY.getEnglish();
                    break;
                case "准备起飞":
                    state = NestStateEnum.READY_TO_GO.getEnglish();
                    break;
                case "执行任务中":
                    state = NestStateEnum.EXECUTING.getEnglish();
                    break;
                case "返航":
                    state = NestStateEnum.GOING_HOME.getEnglish();
                    break;
                case "着陆中":
                    state = NestStateEnum.LANDING.getEnglish();
                    break;
                case "回收中":
                    state = NestStateEnum.RECOVERING.getEnglish();
                    break;
                case "电池装载中":
                    state = NestStateEnum.BATTERY_LOADING.getEnglish();
                    break;
                case "电池卸载中":
                    state = NestStateEnum.BATTERY_UNLOADING.getEnglish();
                    break;
                case "电池已装载":
                    state = NestStateEnum.BATTERY_LOADED.getEnglish();
                    break;
                case "状态错误":
                    state = NestStateEnum.ERROR.getEnglish();
                    break;
                case "起飞中":
                    state = NestStateEnum.TAKE_OFF.getEnglish();
                    break;
                case "操作中":
                    state = NestStateEnum.OPERATING.getEnglish();
                    break;
                case "调试模式":
                    state = NestStateEnum.DEBUG.getEnglish();
                    break;
                case "电池替换中":
                    state = NestStateEnum.BATTERY_REPLACING.getEnglish();
                    break;
                case "系统挂起":
                    state = NestStateEnum.SYS_SUSPEND.getEnglish();
                    break;
                case "无人机开关机进行中":
                    state = NestStateEnum.DRONE_POWER_ON_DOING.getEnglish();
                    break;
                case "无人机对频进行中":
                    state = NestStateEnum.DRONE_PAIRING_DOING.getEnglish();
                    break;
                case "MPS固件更新初始化":
                    state = NestStateEnum.BOOTLOADER_IDLE.getEnglish();
                    break;
                case "MPS等待固件更新指令":
                    state = NestStateEnum.BOOTLOADER_WAITING_FOR_UPGRADE_CMD.getEnglish();
                    break;
                case "MPS准备接收固件数据":
                    state = NestStateEnum.BOOTLOADER_PREPARE_TO_RECEIVE_FW.getEnglish();
                    break;
                case "MPS正在接收固件数据":
                    state = NestStateEnum.BOOTLOADER_RECEIVING_FM.getEnglish();
                    break;
                case "MPS固件数据接收完成":
                    state = NestStateEnum.BOOTLOADER_FINISH_RECEIVE_FW.getEnglish();
                    break;
                case "MPS正在更新固件":
                    state = NestStateEnum.BOOTLOADER_UPGRADING.getEnglish();
                    break;
                case "MPS固件更新完成":
                    state = NestStateEnum.BOOTLOADER_UPGRADED.getEnglish();
                    break;
                case "MPS正在启动":
                    state = NestStateEnum.BOOTLOADER_MAIN_PROGRAM_LAUNCHING.getEnglish();
                    break;
                case "MPS固件更新异常":
                    state = NestStateEnum.BOOTLOADER_UPGRADE_ERR.getEnglish();
                    break;
                case "MPS连接中":
                    state = NestStateEnum.CONNECTING.getEnglish();
                    break;
                case "MPS连接断开":
                    state = NestStateEnum.DISCONNECTED.getEnglish();
                    break;
                case "未知":
                    state = NestStateEnum.UNKNOWN.getEnglish();
                    break;
                case "离线":
                    state = NestStateEnum.OFF_LINE.getEnglish();
                    break;
                case "无人机已开机":
                    state = NestStateEnum.DRONE_STARTED.getEnglish();
                default:
                    break;
                    
            }
        }
        return state;
    }

    public static String transformSensor(String sensor) {
        if (StringUtils.hasText(sensor)) {
            switch (sensor) {
                case "喊话器":
                    sensor = "Speaker";
                    break;
                case "夜航灯":
                    sensor = "Flashing lantern";
                    break;
                case "探照灯":
                    sensor = "Searchlights";
                    break;
                case "气体监测":
                    sensor = "Gas monito";
                    break;
                default:
                    break;
            }
        }
        return sensor;
    }
}
