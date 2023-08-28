package com.imapcloud.nest.common.constant;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 数据相关常量
 *
 * @author: zhengxd
 * @create: 2020/11/19
 **/
public class DataConstant {
    public static final String APP = "app";

    public static final String HTTP = "http";
    public static final String RTMP = "rtmp";


    public static final String JPG = "jpg";
    public static final String TILES = "tiles";
    public static final String XML = "xml";
    public static final String PCM = "pcm";
    public static final String MP3 = "mp3";



    public static final String ZIP = "zip";
    public static final String RAR = "rar";

    // 原视频
    public static final String VIDEO = "video/";
    // 原图
    public static final String PHOTO = "photo/";
    // 单位的LOGO
    public static final String UNIT_ICON = "unitIcon/";
    // 单位的图标
    public static final String UNIT_FAVIICON = "unitFavicon/";
    // 缺陷识别
    public static final String DEFECT = "defect/";
    // 表计读数
    public static final String METER = "meter/";
    // 其他类型
    public static final String OTHER = "other/";
    // power
    public static  final  String POWERJSON="powerjson";
    /**
     * 图像类型列表
     */
    public static final List<String> IMAGE_TYPE_LIST = Lists.newArrayList("jpg", "png", "jpeg");
}
