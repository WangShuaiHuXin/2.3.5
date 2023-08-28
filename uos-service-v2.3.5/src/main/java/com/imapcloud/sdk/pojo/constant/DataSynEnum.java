package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 */

public enum DataSynEnum {
    /**
     * 飞机状态
     */
    FILE_NOT_EXIST("4000", "媒体文件不存在"),
    FILE_NOT_FIND("4001", "媒体文件匹配不到"),
    TASK_NOT_EXIST("4002", "飞行任务匹配不到"),
    FILE_SYS_INIT_ERR("4010", "媒体管理器初始化失败"),
    CHANGE_DOWNLOAD_ERR("4011", "切换到下载模式失败"),
    FILE_FLUSH_ERR("4012", "文件列表刷新失败"),
    SDK_ERR("4020", "SDK报错"),
    FILE_NOT_DOWNLOAD("4021", "文件未下载"),
    AIRCRAFT_NOT_EXIST("4022", "无人机不在线"),
    OPEN_AIRCRAFT_ERR("4023", "无人机自动开机失败"),
    NET_ERR("4030", "网络错误"),
    SERVER_ERR("4031", "服务器错误"),
    ;
    private String code;

    private String value;

    DataSynEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static DataSynEnum getInstance(String value) {
        return valueOf(value);
    }

    public static String getValueByCode(String code) {
        DataSynEnum[] nums = values();
        for (DataSynEnum num : nums) {
            if (num.code.equals(code)) {
                return num.value;
            }
        }
        return null;
    }
    public  static Boolean  isContaionCode(String code){
        DataSynEnum[] nums = values();
        for (DataSynEnum num : nums) {
            if (num.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}
