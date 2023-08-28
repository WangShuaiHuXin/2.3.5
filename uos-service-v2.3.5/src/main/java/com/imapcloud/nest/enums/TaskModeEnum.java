package com.imapcloud.nest.enums;


/**
 * Created by wmin on 2020/11/16 16:46
 *
 * @author wmin
 */
public enum TaskModeEnum {
    MANUAL_FLY(-1, "APP手动飞行"),
    CUSTOM(0, "航点航线"),
    PANORAMA(1, "全景采集"),
    LINEAR(2, "线状巡视"),
    LOCAL(3, "本地上传"),
    DYNAMIC(4, "动态规划"),
    DELICACY(5, "精细巡检"),
    ORTHOPHOTO(6, "正射影像"),
    SLOPEPHOTO(7, "倾斜摄影"),
    POINTCLOUD(8, "点云易飞"),
    NEST(9, "机巢格式"),
    SUBSSTATION_PLANING(10, "变电站规划"),
    AROUND_FLY(11, "环绕飞行"),
    DJI_KML(13,"大疆本地航线"),
    GRID(14,"网格化");

    private Integer value;
    private String chinese;

    TaskModeEnum(Integer value, String chinese) {
        this.value = value;
        this.chinese = chinese;
    }

    public Integer getValue() {
        return value;
    }

    public String getChinese() {
        return chinese;
    }

    public static TaskModeEnum getInstance(Integer value) {
        if (value != null) {
            for (TaskModeEnum e : TaskModeEnum.values()) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
        }
        return null;
    }

}
