package com.imapcloud.nest.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wmin
 */
public enum InfraredColorEnum {
    /**
     * 白热
     */
    WHITE_HOT("白热", "WHITE_HOT", "#000000,#FFFFFF", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA,CameraParamsEnum.AUTEL_EVO_2_CAMERA)),
    BLACK_HOT("黑热", "BLACK_HOT", "#FFFFFF,#000000", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA,CameraParamsEnum.AUTEL_EVO_2_CAMERA)),
    RED_HOT("描红", "RED_HOT", "#000000,#FDFDFD,#EF0D0D", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA)),
    IRONBOW_1("铁红", "IRONBOW_1", "#000000,#540084,#FF5C00,#FFFFA4", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA,CameraParamsEnum.AUTEL_EVO_2_CAMERA)),
    COLOR_2("热铁", "COLOR_2", "#050000,#008484,#F9FC02,#E30700", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA)),
    ICE_FIRE("北极", "ICE_FIRE", "#000000,#1F2DFE,#01A100,#FFF714,#FF3C3C,#FC0BFC", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA)),
    GREEN_HOT("医疗", "GREEN_HOT", "#000000,#D83EE1,#1F2DFE,#12EBE2,#01A100,#FFF714,#FF3C3C,#FFEDED", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA)),
    COLOR_1("熔岩", "COLOR_1", "#050000,#FF3200,#FFFF1F", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA)),
    RAINBOW("彩虹1", "RAINBOW", "#810002,#D83EE1,#1F2DFE,#12EBE2,#0BFF00,#FFF714,#FF3C3C", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA,CameraParamsEnum.AUTEL_EVO_2_CAMERA)),
    RAIN("彩虹2", "RAIN", "#1F2DFE,#00FEFE,#FFF714,#FF1100,#880000", Arrays.asList(CameraParamsEnum.ZENMUSE_H20T, CameraParamsEnum.M3T, CameraParamsEnum.MAVIC_2_ENTERPRISE_ADVANCED_CAMERA,CameraParamsEnum.AUTEL_EVO_2_CAMERA)),


    HOT_SPOT("灼热", "HOT_SPOT", "#A01301,#EC8B16,#8C8C8C,#070C08", Arrays.asList(CameraParamsEnum.MAVIC_2_ENTERPRISE_DUAL_VISUAL_CAMERA)),
    RAINBOW2("彩虹", "RAINBOW2", "#F8E0C5,#F7253A,#FCE51C,#23B812,#0066DB", Arrays.asList(CameraParamsEnum.MAVIC_2_ENTERPRISE_DUAL_VISUAL_CAMERA)),
    GRAY("灰度", "GRAY", "#FAFAFA,#000000", Arrays.asList(CameraParamsEnum.MAVIC_2_ENTERPRISE_DUAL_VISUAL_CAMERA)),

//    HOT_METAL("铁红","HOT_METAL","#FFFFA4,#FBCD2B,#FF5C00,#91009F,#0B0084,#000000",new ArrayList<>()),
//    COLD_SPOT("医疗","COLD_SPOT","#101010,#7A7A7A,#00A8FE,#132A94",new ArrayList<>()),
    //SEPIA
    //GLOWBOW
    //IRONBOW_2
    //FUSION


    ;
    private String name;
    private String key;
    private String code;
    private List<CameraParamsEnum> cameraList;

    InfraredColorEnum(String name, String key, String code, List<CameraParamsEnum> cameraList) {
        this.name = name;
        this.key = key;
        this.code = code;
        this.cameraList = cameraList;
    }

    public static InfraredColorEnum getInfraredColorEnumByKey(String key) {
        if (Objects.nonNull(key)) {
            final String key1 = key.trim();
            return Arrays.stream(InfraredColorEnum.values()).filter(e -> Objects.equals(e.key, key1)).findFirst().orElse(null);
        }
        return null;
    }

    public static List<InfraredColorEnum> listColorEnumByCamera(CameraParamsEnum cameraParamsEnum) {
        if (Objects.isNull(cameraParamsEnum)) {
            return Collections.emptyList();
        }
        return Arrays.stream(InfraredColorEnum.values()).filter(e -> e.cameraList.contains(cameraParamsEnum)).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }
}
