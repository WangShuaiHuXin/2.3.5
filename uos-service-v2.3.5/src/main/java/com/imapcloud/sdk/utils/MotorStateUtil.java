package com.imapcloud.sdk.utils;

import java.util.HashMap;
import java.util.Map;

public class MotorStateUtil {
    /**
     * 电机运行状态
     */
    private final static Map<Integer, String> RUNNING_STATE_MAP = new HashMap<>(32);
    /**
     * 电机设备状态
     */
    private final static Map<Integer, String> DEVICE_STATE_MAP = new HashMap<>(32);
    /**
     * 电机传感器状态
     */
    private final static Map<Integer, String> SENSOR_STATE_MAP = new HashMap<>(8);

    /**
     * 电机错误码
     */
    private final static Map<Integer, String> ERROR_CODE_MAP = new HashMap<>(64);

    static {
        RUNNING_STATE_MAP.put(0, "初始");
        RUNNING_STATE_MAP.put(1, "待机");
        RUNNING_STATE_MAP.put(2, "匀加速");
        RUNNING_STATE_MAP.put(3, "匀速（整数部分）");
        RUNNING_STATE_MAP.put(4, "匀速（余数部分）");
        RUNNING_STATE_MAP.put(5, "匀减速");
        RUNNING_STATE_MAP.put(6, "匀速（其他）");
        RUNNING_STATE_MAP.put(7, "复位");
        RUNNING_STATE_MAP.put(8, "零点脉冲-静止");
        RUNNING_STATE_MAP.put(9, "零点脉冲-运行");
        RUNNING_STATE_MAP.put(10, "复位完成");
        RUNNING_STATE_MAP.put(11, "运行结束");
        RUNNING_STATE_MAP.put(12, "数据处理");
        RUNNING_STATE_MAP.put(13, "电机失能");
        RUNNING_STATE_MAP.put(127, "无效数据");
        RUNNING_STATE_MAP.put(252, "电机过载");
        RUNNING_STATE_MAP.put(253, "EEPROM错误");
        RUNNING_STATE_MAP.put(154, "通讯错误");
        RUNNING_STATE_MAP.put(255, "状态错误");

        DEVICE_STATE_MAP.put(0, "初始");
        DEVICE_STATE_MAP.put(1, "原点");
        DEVICE_STATE_MAP.put(2, "终点");
        DEVICE_STATE_MAP.put(3, "中间点");
        DEVICE_STATE_MAP.put(4, "复位点");
        DEVICE_STATE_MAP.put(5, "未知");
        DEVICE_STATE_MAP.put(32, "在复位传感器上");
        DEVICE_STATE_MAP.put(33, "中间点1");
        DEVICE_STATE_MAP.put(34, "中间点2");
        DEVICE_STATE_MAP.put(35, "中间点3");
        DEVICE_STATE_MAP.put(36, "中间点4");
        DEVICE_STATE_MAP.put(37, "中间点5");
        DEVICE_STATE_MAP.put(127, "无效数据");
        DEVICE_STATE_MAP.put(128, "初始化错误");
        DEVICE_STATE_MAP.put(129, "位置错误");
        DEVICE_STATE_MAP.put(130, "脉冲数错误");
        DEVICE_STATE_MAP.put(131, "控制错误");
        DEVICE_STATE_MAP.put(132, "硬件错误");

        SENSOR_STATE_MAP.put(0, "复位传感器");
        SENSOR_STATE_MAP.put(1, "原点传感器");
        SENSOR_STATE_MAP.put(2, "终点传感器");
        SENSOR_STATE_MAP.put(3, "中间点传感器");
        SENSOR_STATE_MAP.put(4, "预留（无含义）");
        SENSOR_STATE_MAP.put(5, "预留（无含义）");
        SENSOR_STATE_MAP.put(6, "预留（无含义）");
        SENSOR_STATE_MAP.put(7, "预留（无含义）");

        ERROR_CODE_MAP.put(0, "正常");
        ERROR_CODE_MAP.put(1, "过电流");
        ERROR_CODE_MAP.put(2, "过电压");
        ERROR_CODE_MAP.put(3, "低电压");
        ERROR_CODE_MAP.put(4, "电机匹配异常");
        ERROR_CODE_MAP.put(5, "回生错误");
        ERROR_CODE_MAP.put(6, "过负荷");
        ERROR_CODE_MAP.put(7, "过速度");
        ERROR_CODE_MAP.put(8, "异常脉冲");
        ERROR_CODE_MAP.put(9, "位置控制误差过大");
        ERROR_CODE_MAP.put(17, "位置检出器异常");
        ERROR_CODE_MAP.put(18, "校正异常");
        ERROR_CODE_MAP.put(19, "紧急停止");
        ERROR_CODE_MAP.put(20, "反向极限异常");
        ERROR_CODE_MAP.put(21, "正向极限异常");
        ERROR_CODE_MAP.put(22, "IGBT过热");
        ERROR_CODE_MAP.put(23, "参数内存异常");
        ERROR_CODE_MAP.put(24, "检出器输出异常");
        ERROR_CODE_MAP.put(25, "串行通讯异常");
        ERROR_CODE_MAP.put(32, "串行通讯逾时");
        ERROR_CODE_MAP.put(34, "主回路电源缺相");
        ERROR_CODE_MAP.put(35, "预先过负载警告");
        ERROR_CODE_MAP.put(36, "编码器初始磁场错误");
        ERROR_CODE_MAP.put(37, "编码器内部错误");
        ERROR_CODE_MAP.put(38, "编码器数据可靠度错误");
        ERROR_CODE_MAP.put(39, "编码器内部重置错误");
        ERROR_CODE_MAP.put(40, "内部UVW错误");
        ERROR_CODE_MAP.put(41, "内部地址错误");
        ERROR_CODE_MAP.put(48, "电机碰撞错误");
        ERROR_CODE_MAP.put(49, "UVW接线错误");
        ERROR_CODE_MAP.put(53, "温度超限");
        ERROR_CODE_MAP.put(72, "检出器输出异常");
        ERROR_CODE_MAP.put(103, "温度告警");
        ERROR_CODE_MAP.put(131, "输出电流过大");
        ERROR_CODE_MAP.put(133, "回生异常");
        ERROR_CODE_MAP.put(153, "DSP韧体升级");
        ERROR_CODE_MAP.put(1365, "系统故障");
        ERROR_CODE_MAP.put(2176, "系统故障");
    }

}
