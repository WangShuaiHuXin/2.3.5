package com.imapcloud.nest.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName ElectronicFenceVO.java
 * @Description ElectronicFenceVO
 * @createTime 2022年05月17日 14:31:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ElectronicFenceVO {

    private static final long serialVersionUID=1L;

    /**
     * 电子围栏id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单位id
     */
    private String unitId;

    /**
     * 围栏名称
     */
    private String name;

    /**
     * 开启状态（1-开启；2-关闭）
     */
    private Integer state;

    /**
     * 围栏类型（1-适飞区； 2-禁飞区）
     */
    private Integer type;

    /**
     * 缓冲范围
     */
    private Integer bufferRange;

    /**
     * 围栏的坐标信息
     */
    private String coordinate;

    /**
     * 高度
     */
    private Integer height;

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



}
