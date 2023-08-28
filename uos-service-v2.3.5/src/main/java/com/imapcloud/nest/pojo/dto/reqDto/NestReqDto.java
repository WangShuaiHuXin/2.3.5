package com.imapcloud.nest.pojo.dto.reqDto;

import com.imapcloud.nest.pojo.dto.PageInfoDto;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class NestReqDto extends PageInfoDto {

    private static final long serialVersionUID=1L;
    private String nestId;

    /**
     * 机巢uuid
     */
    private String uuid;

    /**
     * 类型,0-固定机巢，1-mini机巢
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

    private String taskName;

    /**
     * 登录用户
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 服务url
     */
    private String serverUrl;

    /**
     * 无人机实时图传地址
     */
    private String rtmpUrl;

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
    private String outVideoPasswd;

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
    private String innerVideoPasswd;

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
     * 区域id
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
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    //展示监控的状态
    private Integer showStatus;

    //流的类型
    private Integer flowType;

    //数据类型
    private Integer dataType;

}
