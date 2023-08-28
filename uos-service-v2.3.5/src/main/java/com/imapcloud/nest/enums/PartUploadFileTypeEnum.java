package com.imapcloud.nest.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 分片上传大文件的类型枚举
 */
public enum PartUploadFileTypeEnum {
    //图片
    PHOTO(0,"photo/"),
    //视频
    VIDEO(1,"video/"),
    // 正射
    ORTHOPHOTO(2, "ortho/"),
    // 点云
    POINT_CLOUD(3, "pointCloud/"),
    //倾斜
    TILT(4,"tilt/"),
    //矢量
    VECTOR(5,"vector/"),
    // 全景
    PANORAMA(6, "panorama/");

    private Integer code;
    private String desc;


    PartUploadFileTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    //用来存放enum集合
    private static Map<Integer, PartUploadFileTypeEnum> enumMap = new HashMap<Integer, PartUploadFileTypeEnum>();

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    //初始化map
    static{
        for(PartUploadFileTypeEnum item : values()){
            enumMap.put(item.getCode(),item);
        }
    }

    /**
     *从初始化的map集合中获取
     */
    public static String getDescByCode(Integer code){
        return  enumMap.get(code).getDesc();
    }
}
