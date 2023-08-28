package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * <p>
 * 终端信息表
 * </p>
 *
 * @author kings
 * @since 2020-10-26
 * @deprecated 2.0.0，使用{@link com.imapcloud.nest.v2.dao.entity.BaseAppEntity}替代
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_app")
public class SysAppEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 终端名字
     */
    @Length(max = 255,message = "{geoai_uos_maximum_length_terminal_name_255}")
    private String name;

    /**
     * 终端设备id
     */
    @Length(max = 128,message = "{geoai_uos_maximum_length_terminal_name_128}")
    private String deviceId;

    /**
     * 图传地址
     */
//    private String picSendUrl;

    /**
     * 单位id
     * @deprecated 2.0.0，使用orgCode字段替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 推流地址
     */
    @Length(max = 255,message = "{geoai_uos_maximum_length_push_stream_address_255}")
    private String pushRtmp;
    /**
     * 拉流地址http
     */
    @Length(max = 255,message = "{geoai_uos_pull_stream_address_maximum_length_255}")
    private String pullHttp;
    /**
     * 拉流地址rtmp
     */
    private String pullRtmp;

    /**
     * 创建用户id
     */
    @Deprecated
    private Integer createUserId = 0;
    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    //展示监控的状态
    private Integer showStatus;

    /**
     * 是否删除, 0:否,1:是
     */
    @JsonIgnore
    private Boolean deleted;
    /**
     * 单位名称
     */
    @TableField(exist = false)
    private String unitName;

    @TableField(exist = false)
    private Integer appCount;

    @TableField(exist = false)
    private Integer state = -1;

}
