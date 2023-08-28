package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 固定航线表
 * </p>
 *
 * @author wmin
 * @since 2020-10-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("fix_air_line")
public class FixAirLineEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机巢id
     */
    private Integer nestId;

    /**
     * 航线名称
     */
    private String name;

    /**
     * 航线的类型，1->航点航线，2->易飞,点云,三维规划航线，3->机巢上的航线数据类型
     */
    private Integer type;

    /**
     * json格式的航点集合, [{},{},...]，修改过后的航线数据
     */
    private String waypoints;

    /**
     * 是否是多架次航线，0->不是，1->是
     */
    private Boolean multiMission;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private Integer copyCount;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;


}
