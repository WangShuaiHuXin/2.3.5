package com.imapcloud.nest.enums;

import java.util.HashMap;
import java.util.Map;

public enum UploadTypeEnum {
    PHOTO("000","manualUpload/photo/","人工上传图片"),
    VIDEO("001","manualUpload/video/","人工上传视频"),
    ORTHOR("002","manualUpload/ortho/","人工上传正射"),
    POINTCLOUD("003","manualUpload/pointcloud/","人工上传点云"),
    TILT("004","manualUpload/tilt/","人工上传倾斜"),
    VECTOR("005","manualUpload/vector/","人工上传矢量"),
    PANORAMA("006","manualUpload/panorama/","人工上传全景"),
    POLLUTION_GRID("007","manualUpload/pollution/","人工上传污染网格"),
    MULTISPECTRAL("009","manualUpload/multispectral/","人工上传多光谱"),
    MAINTENACE("010","maintenance/","基站维保"),
    PHOTOLOCATION("020","photoLocation/","报告"),
    ILLEGALVECTOR("030","illegalVector/","违建矢量"),
    FINEINSP("040","fineInsp/","航线"),
    CPSAPKS("050","cpsApks/","cps安装包"),
    MPSBINS("051","mpsBins/","mps安装包"),
    UNITICON("060","unitIcon/","单位icon"),
    SRT("070","srt/","srt文件"),
    AUTOPHOTO("100","autoUpload/photo/","自动上传图片"),
    AUTOVIDEO("101","autoUpload/video/","自动上传视频"),
    NESTLOGS("110","nestLogs/","日志"),
    MESSAGE_PHOTO("120","message/photo/","公告图片"),
    MESSAGE_VIDEO("121","message/video/","公告视频"),
    DATA_SCENE_VIDEO("200","dataScene/photo/","现场取证"),
    DATA_SCENE_VIDEO_THUMBNAIL("201","dataScene/photo/thumbnail/","现场取证缩略图"),
    DATA_ANALYSIS_IMAGE_PATH("202","dataAnalysis/photo/","分析统计原图"),
    DATA_ANALYSIS_IMAGE_THUMBNAIL_PATH("203","dataScene/photo/thumbnail/","分析统计缩略图路径"),
    DATA_ANALYSIS_MARK_PATH("204","dataAnalysis/photo/mark/","分析统计标注原图"),
    DATA_ANALYSIS_MARK_THUMBNAIL_PATH("205","dataAnalysis/photo/mark/thumbnail/","分析统计标注缩略图路径"),
    DATA_ANALYSIS_ALL_MARK_PATH("206","dataAnalysis/photo/markAll/","分析统计标注原图"),
    DATA_ANALYSIS_ALL_MARK_THUMBNAIL_PATH("207","dataAnalysis/photo/markAll/thumbnail/","分析统计标注缩略图路径"),
    DATA_ANALYSIS_ADDR_THUMBNAIL_PATH("208","dataAnalysis/photo/addr/thumbnail/","分析统计地址缩略图路径"),
    DATA_PANORAMA_PATH("210","dataPanorama/photo/","新全景图片路径"),

    DATA_ANALYSIS_RESULT_FROM("300","dataScene/photo/result/","分析统计结果 图片来源"),
    DATA_ANALYSIS_RESULT_THUMBNAIL("301","dataScene/photo/result/thumbnail/","分析统计结果 缩略图"),
    DATA_ANALYSIS_RESULT_ADDR("302","dataScene/photo/result/addr/","分析统计结果 地址图片"),
    DATA_ANALYSIS_RESULT_MARK("303","dataScene/photo/result/mark/","分析统计结果 标注图"),

    MISSION_VIDEO_PHOTO_PATH("304","mission/video/photo/","视频抽帧"),

    /**
     * 任务视频路径
     */
    MISSION_VIDEO_PATH("305","autoCapture/video/","录屏视频"),

    MISSION_VIDEO_PHOTO_THUMBNAIL_PATH("305","mission/video/photo/thumbnail/","视频抽帧缩略图"),
    THEME_ICON("400","theme/icon/","主题LOGO"),
    THEME_FAVICON("401","theme/favicon/","主题图标"),
    DEFAULT_ICON("402","theme/icon/default_icon.png","默认主题LOGO"),
    DEFAULT_FAVICON("403","theme/favicon/default_favicon.png","默认主题图标"),

    TASK_FILE_PATH("501","task/file/","航线文件"),

    DJI_AUTO_UPLOAD("502","dji/media/","大疆同步数据"),

    DJI_PILOT_AUTO_UPLOAD("503","dji/pilot/media","大疆同步数据"),

    AIRSPACE_APPROVAL_FILE("601","airspace/approval/","空域批复文件"),
    AIRSPACE_PHOTO("602","airspace/photo/","空域范围截图"),

    /**
     * 人员管理照片
     */
    ID_CARD_FRONT_URL("701","person/IDCard/","身份证正面"),
    ID_CARD_BACK_URL("702","person/IDCard/","身份证反面"),
    ID_CARD_DRIVE_URL("703","person/driveCard/","驾驶证图片"),

    /**
     * minio图片
     */
    MINIO_COMMON_PICTURE("1000","common/picture/","minio上传图片通用地址"),

    /**
     *
     */
    NH_WORK_ORDER_PDF("1001","nh/partol/report/","南海任务工单报告上传地址"),
    /**
     * 正常永远不会用到
     */
    UNKNOWN("-1","unknown/", "未知");
    private String code;
    private String path;
    private String desc;

    UploadTypeEnum(String code, String path, String desc) {
        this.code = code;
        this.path = path;
        this.desc = desc;
    }

    //用来存放enum集合
    public static Map<String,String> enmuMap = new HashMap<String,String>();

    public String getCode() {
        return code;
    }

    public String getPath() {
        return path;
    }

    public String getDesc() {
        return desc;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    //初始化map
    static{
        for(UploadTypeEnum item : values()){
            enmuMap.put(item.getCode(),item.getPath());
        }
    }

    public static UploadTypeEnum matchCode(String opCodeStr) {
        for (UploadTypeEnum uploadTypeEnum : UploadTypeEnum.values()) {
            if (uploadTypeEnum.getCode().equalsIgnoreCase(opCodeStr)) {
                return uploadTypeEnum;
            }
        }
        return null;
    }

}
