package com.imapcloud.nest.enums;


/**
 * Created by wmin on 2020/11/19 17:14
 * 相机参数枚举类
 *
 * @author wmin
 */
public enum CameraParamsEnum {
    DEFAULT("default", 3.61D, 6.32D, 4.74D, 1.58D),

    PHANTOM_3_ADVANCED_CAMERA("Phantom 3 Advanced Camera", 3.700D, 6.400D, 4.800D, 1.613D),
    PHANTOM_3_PROFESSIONAL_CAMERA("Phantom 3 Professional Camera", 3.700D, 6.400D, 4.800D, 1.613D),
    PHANTOM_3_STANDARD_CAMERA("Phantom 3 Standard Camera", 3.700D, 6.400D, 4.800D, 1.613D),
    PHANTOM_3_4K_CAMERA("Phantom 3 4K Camera", 3.700D, 6.400D, 4.800D, 1.613D),


    /**
     * 大疆精灵4系列镜头参数
     */
    PHANTOM("Phantom", 8.800D, 13.200D, 8.800D, 2.412D),
    PHANTOM_4_RTK_CAMERA("Phantom 4 RTK Camera", 8.800D, 13.200D, 8.800D, 2.412D),
    PHANTOM_4_Advanced_CAMERA("Phantom 4 advanced Camera", 8.800D, 13.200D, 8.800D, 2.412D),
    PHANTOM_4_RRO_CAMERA("Phantom 4 PRO Camera", 8.800D, 13.200D, 8.800D, 2.412D),
    PHANTOM_4_PV2_CAMERA("Phantom 4 PV2 Camera", 8.800D, 13.200D, 8.800D, 2.412D),
    PHANTOM_4_CAMERA("Phantom 4 Camera", 3.700D, 6.400D, 4.800D, 1.600D),

    /**
     * 御2系列镜头参数
     */
    MAVIC("Mavic", 5.000D, 6.400D, 4.800D, 1.600D),
    MAVIC_PRO_CAMERA("Mavic PRO Camera", 5.000D, 6.400D, 4.800D, 1.600D),
    MAVIC_2_ENTERPRISE_CAMERA("Mavic 2 Enterprise", 5.000D, 6.400D, 4.800D, 1.600D),
    MAVIC_2_PRO_CAMERA("Mavic 2 PRO Camera", 5.000D, 6.400D, 4.800D, 1.600D),
    MAVIC_2_ENTERPRISE_DUAL_VISUAL_CAMERA("Mavic 2 Enterprise Dual-Visual", 5.000D, 6.400D, 4.800D, 1.600D, 1, 480.000D, 240.000D),
    MAVIC_2_ZOOM_CAMERA("Mavic 2 Zoom Camera", 4.270D, 6.160D, 4.620D, 1.540D, 0, 480.000D, 240.000D),
    //    MAVIC_2_ENTERPRISE_ADVANCED_CAMERA("Mavic 2 Enterprise Advanced", 4.500D, 6.750D, 4.500D, 0.800D),
    MAVIC_2_ENTERPRISE_ADVANCED_CAMERA("Mavic 2 Enterprise Advanced", 4.500D, 6.750D, 4.500D, 0.800D, 1, -1.0, null),

    MAVIC_3("Hassel", 5.000D, 6.400D, 4.800D, 1.600D,0,13441D,240D),
    /**
     * M300镜头参数,默认H20
     */
    M300("M300", 6.830D, 7.600D, 5.700D, 1.466D),


    SPARK_CAMERA("Spark Camera", 4.500D, 6.400D, 4.800D, 1.613D),

    X5("X5", 15.000D, 17.300D, 13.000D, 3.277D),
    X5R("X5R", 15.000D, 17.300D, 13.000D, 3.277D),
    X5S("X5S", 15.000D, 17.300D, 13.000D, 3.277D),

    X4S("X4S", 8.800D, 13.200D, 8.800D, 2.412D),

    Z3("Z3", 4.300D, 6.200D, 4.650D, 1.550D),
    Z30("Z30", 4.000D, 4.620D, 3.465D, 1.550D),

    X7("X7", 35.00D, 23.500D, 15.700D, 3.91),

    SEQUOIA_MONO("Sequoia mono", 3.98D, 4.8D, 3.6D, 3.75D),
    SEQUOIA_RGB("Sequoia RGB", 4.88D, 6.17472D, 4.63104D, 1.34D),

    ZENMUSE_H20("Zenmuse H20", 4.500D, 6.1600D, 4.6200D, 1.519D, 0, 48000.0D, 317.0D),
    ZENMUSE_H20T("Zenmuse H20T", 4.500D, 6.1600D, 4.6200D, 1.519D, 1, 48000.0D, 317.0D),
    ZENMUSE_P1("Zenmuse p1", 24D, 35.9D, 24D, 4.4D),
    ZENMUSE_P1_24MM("Zenmuse p1 24mm", 24D, 35.9D, 24D, 4.4D),
    ZENMUSE_P1_35MM("Zenmuse p1 35mm", 35D, 35.9D, 24D, 4.4D),
    ZENMUSE_P1_50MM("Zenmuse p1 50mm", 50D, 35.9D, 24D, 4.4D),

    M30("M30", 50D, 35.9D, 24D, 4.4D,0 , 48000.0D ,1130.0D),
    M30T("M30T", 50D, 35.9D, 24D, 4.4D ,0 , 48000.0D ,1130.0D),
    //道通系列
    AUTEL_EVO_2_CAMERA("Autel EVO 2 Camera", 8.800D, 13.200D, 8.800D, 2.412D, 1),

    M3T("M3T", 8.800D, 13.200D, 8.800D, 2.412D, 1,13441D,240D),
    M3E("M3E", 8.800D, 13.200D, 8.800D, 2.412D,0,13441D,240D),
    ;
    /**
     * 相机名称
     */
    private String cameraName;
    /**
     * 焦距
     */
    private Double focalLength;
    /**
     * 传感器宽
     */
    private Double sensorWidth;
    /**
     * 传感器高
     */
    private Double sensorHeight;
    /**
     * 每个像素的宽
     */
    private Double pixelSizeWidth;

    private int infraredMode = 0;

    private Double focalLengthMax = -1.0;


    private Double focalLengthMin;

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public Double getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(Double focalLength) {
        this.focalLength = focalLength;
    }

    public Double getSensorWidth() {
        return sensorWidth;
    }

    public void setSensorWidth(Double sensorWidth) {
        this.sensorWidth = sensorWidth;
    }

    public Double getSensorHeight() {
        return sensorHeight;
    }

    public void setSensorHeight(Double sensorHeight) {
        this.sensorHeight = sensorHeight;
    }

    public Double getPixelSizeWidth() {
        return pixelSizeWidth;
    }

    public void setPixelSizeWidth(Double pixelSizeWidth) {
        this.pixelSizeWidth = pixelSizeWidth;
    }

    public Double getFocalLengthMin() {
        return focalLengthMin;
    }

    public void setFocalLengthMin(Double focalLengthMin) {
        this.focalLengthMin = focalLengthMin;
    }

    public Double getFocalLengthMax() {
        return focalLengthMax;
    }

    public void setFocalLengthMax(Double focalLengthMax) {
        this.focalLengthMax = focalLengthMax;
    }

    public int getInfraredMode() {
        return infraredMode;
    }

    public void setInfraredMode(int infraredMode) {
        this.infraredMode = infraredMode;
    }

    CameraParamsEnum(String cameraName, Double focalLength, Double sensorWidth, Double sensorHeight, Double pixelSizeWidth) {
        this.cameraName = cameraName;
        this.focalLength = focalLength;
        this.sensorWidth = sensorWidth;
        this.sensorHeight = sensorHeight;
        this.pixelSizeWidth = pixelSizeWidth;
    }

    CameraParamsEnum(String cameraName, Double focalLength, Double sensorWidth, Double sensorHeight, Double pixelSizeWidth, Integer infraredMode, Double focalLengthMax, Double focalLengthMin) {
        this.cameraName = cameraName;
        this.focalLength = focalLength;
        this.sensorWidth = sensorWidth;
        this.sensorHeight = sensorHeight;
        this.pixelSizeWidth = pixelSizeWidth;
        this.focalLengthMax = focalLengthMax;
        this.focalLengthMin = focalLengthMin;
        this.infraredMode = infraredMode;
    }

    CameraParamsEnum(String cameraName, Double focalLength, Double sensorWidth, Double sensorHeight, Double pixelSizeWidth, Integer infraredMode) {
        this.cameraName = cameraName;
        this.focalLength = focalLength;
        this.sensorWidth = sensorWidth;
        this.sensorHeight = sensorHeight;
        this.pixelSizeWidth = pixelSizeWidth;
        this.infraredMode = infraredMode;
    }

    public static CameraParamsEnum getInstanceByCameraName(String cameraName) {
        if (cameraName != null) {
            CameraParamsEnum[] values = CameraParamsEnum.values();
            for (CameraParamsEnum cp : values) {
                if (eqIgnoreCaseAndSpace(cp.getCameraName(), cameraName)) {
                    return cp;
                }
            }
            if (cameraName.toUpperCase().contains("PHANTOM")) {
                return PHANTOM;
            }
            if (cameraName.toUpperCase().contains("MAVIC")) {
                return MAVIC;
            }
            if (cameraName.toUpperCase().contains("M300")) {
                return M300;
            }
        }
        return null;
    }

    public static boolean eqIgnoreCaseAndSpace(String str1, String str2) {
        if (str1 != null && str2 != null) {
            String replace1 = str1.replace(" ", "").toUpperCase();
            String replace2 = str2.replace(" ", "").toUpperCase();
            return replace1.equals(replace2);
        }
        return false;
    }
}
