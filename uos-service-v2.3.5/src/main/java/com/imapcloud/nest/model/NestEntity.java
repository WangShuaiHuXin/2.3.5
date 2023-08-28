package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 机巢信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 * @deprecated 2.0.0，使用{@link com.imapcloud.nest.v2.dao.entity.BaseNestEntity}替代
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("nest")
public class NestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机巢uuid
     */
    private String uuid;

    /**
     * 类型(0-P4R固定基站；1-mini1代；2-M300；3-简易基站；4-车载基站；5-mini2代)
     */
    private Integer type;

    /**
     * 型号(字典nestModel)
     */
    private String model;

    /**
     * 机巢名称
     */
    private String name;

    /**
     * 登录用户
     */
    private String username;

    /**
     * 登录密码
     */
    @JsonIgnore
    private String password;

    /**
     * 服务url
     */
    private String serverUrl;

    /**
     * 无人机拉流地址
     */
    private String rtmpUrl;

    /**
     * 无人机推流地址（rtmp）
     */
    private String pushUrl;

    /**
     * 巢外监控直播流Id,用于重新推流
     */
    private String outVideoId;

    /**
     * 外部摄像头视频服务
     */
    private String outVideoUrl;

    /**
     * 外部摄像头登录用户
     */
    private String outVideoUser;

    /**
     * 外部摄像头登录密码
     */
    private String outVideoPassword;

    /**
     * 巢内监控直播流Id,用于重新推流
     */
    private String innerVideoId;

    /**
     * 内部摄像头视频服务
     */
    private String innerVideoUrl;

    /**
     * 内部摄像头登录用户
     */
    private String innerVideoUser;


    /**
     * 内部摄像头登录密码
     */
    private String innerVideoPassword;

    /**
     * 经度
     */
    private Double latitude;

    /**
     * 纬度
     */
    private Double longitude;

    /**
     * 海拔
     */
    private Double altitude;

    /**
     * 飞机Id
     */
    private Integer aircraftId;

    /**
     * 机巢图片
     */
    private String nestPic;

    /**
     * 飞机图片
     */
    private String aircraftPic;

    /**
     * 监控图片
     */
    private String videoPic;

    /**
     * 区域id,原来是不为空的，1223改为可为空的，
     * 因为regionId，删除的时候，regionId置为空才是正常的。
     */
    private Integer regionId;

    /**
     * 备注
     */
    private String description;

    /**
     * 机巢地址，例如，广东省韶关市芙蓉变电站
     */
    private String address;

    /**
     * 机巢权限范围：0-公有，1-私有，2-未部署
     */
    private Integer purview;

    /**
     * 创建用户id（旧，待废弃）
     */
    @Deprecated
    private Integer createUserId = 0;

    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 动态规划编码
     */
    private String planCode;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    /**
     * 是否展示监控，0为不展示，默认为1展示
     */
    private Integer showStatus;

    /**
     * 基站维保状态
     */
    private Integer maintenanceState;



    /**
     * 机巢编号
     */
    private String number;

    /**
     * 是否使用国标
     */
    private Boolean useGb;

    /**
     * 巢外摄像头mac地址
     */
    private String outVideoMac;
    /**
     * 巢外摄像头IP地址
     */
    private String outVideoIp;
    /**
     * 巢外摄像头推流地址
     */
    private String outVideoPushUrl;
    /**
     * 巢外摄像头是否推流
     */
    private Boolean outVideoEnable;

    /**
     * 巢内摄像头mac地址
     */
    private String innerVideoMac;
    /**
     * 巢内摄像头IP地址
     */
    private String innerVideoIp;
    /**
     * 巢内摄像头推流地址
     */
    private String innerVideoPushUrl;
    /**
     * 巢内摄像头是否推流
     */
    private Boolean innerVideoEnable;
    /**
     * 是否需要转发 0，1
     */
    private Boolean relay;


    /**
     * rtk（1-无；2-大疆D-RTK基站）
     */
    private Integer rtkType;

    @TableField(exist = false)
    private String flowUrl;

    @TableField(exist = false)
    private Integer nestStatus;

    /**
     * 用户是否接管当前机巢，0为未接管，1为接管
     */
    @TableField(exist = false)
    private Integer isIncharge;

    @TableField(exist = false)
    private Integer nestCount;


}
