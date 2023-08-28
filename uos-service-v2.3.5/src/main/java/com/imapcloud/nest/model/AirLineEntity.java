package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 航线表
 * </p>
 *
 * @author wmin
 * @since 2020-08-27
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("air_line")
public class AirLineEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 航线名称
     */
    private String name;

    /**
     * 航线的类型，1->航点航线，2->易飞,点云,三维规划航线
     */
    private Integer type;

    /**
     * json格式的航点集合, [{},{},...]，修改过后的航线数据
     */
    private String waypoints;

    /**
     * json格式的航点集合, [{},{},...]，基站航线格式数据数据
     */
    private String originalWaypoints;

    /**
     * 航点数, 不包括头尾两个点
     */
    private Integer pointCount;

    /**
     * 合并后的点数,如果距离过近的两个点，由于机巢不能精确的飞行，需要合并两个点
     */
    private Integer mergeCount;

    /**
     * 预计飞行长度，单位米
     */
    private Double predicMiles;

    /**
     * 预计飞行时间，单位秒
     */
    private Long predicTime;

    /**
     * 航线拍照点的数量
     */
    private Integer photoCount;

    /**
     * 航线拍视频的数量（0说明没有拍视频）
     */
    private Integer videoCount;
    /**
     * 航线有拍摄视频的话，计算出视频的时长
     */
    private Long videoLength;

    /**
     * 复制次数
     */
    private Integer copyCount;


    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    /**
     * 是否是绝对航线（海拔）
     */
    private Boolean absolute;

    /**
     * 变焦模式
     * 0 - 变焦恢复
     * 1 - 变焦维持
     */
    private Integer focalMode;

    /**
     * 航线显示参数
     */
    private String showInfo;

    /**
     * dji-json格式的航点集合, [{},{},...]
     */
    private String djiWaypoints;

}
