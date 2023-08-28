//package com.imapcloud.nest.model;
//
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableId;
//
//import java.time.LocalDateTime;
//import java.io.Serializable;
//import java.util.List;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
///**
// * <p>
// * 单位信息表
// * </p>
// *
// * @author wmin
// * @since 2020-07-16
// */
//@Data
//@EqualsAndHashCode(callSuper = false)
//@TableName("sys_unit")
//public class SysUnitEntity implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id", type = IdType.AUTO)
//    private Integer id;
//
//    /**
//     * 部门名字
//     */
//    private String name;
//    /**
//     * 单位描述
//     */
//    private String description;
//
//    /**
//     * 单位LOGO url
//     */
//    private String iconUrl;
//
//    /**
//     * 单位图标url
//     */
//    private String faviconUrl;
//
//    /**
//     * 单位标题
//     */
//    private String title;
//
//    /**
//     * 单位移动终端数
//     */
//    @TableField(exist = false)
//    private Integer appCount;
//
//    /**
//     * 单位机巢数
//     */
//    @TableField(exist = false)
//    private Integer nestCount;
//
//    /**
//     * 创建用户id
//     */
//    @Deprecated
//    private Integer createUserId = 0;
//
//    /**
//     * 创建用户id
//     */
//    private String creatorId;
//
//    private LocalDateTime createTime;
//
//    private LocalDateTime modifyTime;
//
//    /**
//     * 是否删除, 0:否,1:是
//     */
//    @JsonIgnore
//    private Boolean deleted;
//
//    /**
//     * 经度
//     */
//    private Double latitude;
//
//    /**
//     * 纬度
//     */
//    private Double longitude;
//
//    /**
//     * 系统主题：0-绿色，1-蓝色
//     */
//    private Integer theme;
//
//    private Integer parentId;
//
//    /**
//     * 单位所有的机巢
//     */
//    @TableField(exist = false)
//    private List<NestEntity> nestEntityList;
//
//    /**
//     * 单位所有的终端
//     */
//    @TableField(exist = false)
//    private List<SysAppEntity> sysAppEntityList;
//
//    @TableField(exist = false)
//    private List<SysUnitEntity> childList;
//
//    private String orgCode;
//
//}
