package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author daolin
 * @since 2020-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("station_device")
public class StationDeviceEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 设备uuid
     */
    private String uuid;

    /**
     * 父id
     */
    private Integer parentId;

    /**
     * 单元入口辅助点大地坐标
     */
    private String entryPoint;
    /**
     * 单元入口辅助点唯一码
     */
    private String entryAssistPoint;

    /**
     * 标签
     */
    private String tag;

    /**
     * 设备区域：
        "1": "主变区域",
        "2": "10kV区域",
        "3": "35kV区域",
        "4": "110kV区域",
        "5": "220kV区域",
        "6": "330kV区域",
        "7": "500kV区域",
        "8": "800kV区域",
        "9": "其他区域"
     */
    private String deviceArea;

    /**
     * 识别类型：
        "1": "表计读取",
        "2": "红外测温",
        "3": "设备外观"
     */
    private String identifyType;

    /**
     * 空间属性：
        "L": "低空",默认属性,
        "H": "高空",
        "M":"中"
     */
    private String spatialAttr;

    /**
     * 设备类型：
        "1": "开关",
        "2": "刀闸",
        "3": "避雷器",
        "4": "电容器",
        "5": "CT"
     */
    private String deviceType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 变电站id，此处绑定机巢id
     */
    @Deprecated
    private Integer nestId;

    private String baseNestId;
    /**
     * 设备目标点
     */
    private String alginpoints;

    /**
     * 照片列表，json格式：
        {"photo1" : "aaa", "photo2" : "bbb"}
     */
    @JsonIgnore
    private String photoList;

    /**
     * 删除标记，0-未删除，1-删除
     */
    private Boolean deleted;


}
