package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.TrimStr;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class NestDetailsSaveVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库nest表id,新建的保存没有，修改的保存有
     */
    private Integer nestId;

    /**
     * 机巢uuid
     */
    @TrimStr
    @Length(max = 80, message = "机巢uuid最大长度为80")
    private String uuid;

    /**
     * 机巢类型
     * 0 - 固定
     * 1 - mini
     */
    private Integer type;

    /**
     * 机巢名字
     */
    @TrimStr
    @Length(max = 500, message = "机巢名称最大长度500")
    private String name;

    /**
     * 机巢账号
     */
    @TrimStr
    @Length(max = 50, message = "管理账号最大长度50")
    private String username;

    /**
     * 机巢密码
     */
    @TrimStr
    @Length(max = 100, message = "管理密码最大长度100")
    private String password;

    /**
     * 服务地址
     */
    @TrimStr
    @Length(max = 255, message = "连接地址最大长度255")
    private String serverUrl;

    /**
     * 图传地址（拉流地址）
     */
    @TrimStr
    @Length(max = 255, message = "图传地址最大长度255")
    private String rtmpUrl;

    /**
     * 无人机推流地址（rtmp）
     */
    @TrimStr
    @Length(max = 500, message = "推流地址最大长度500")
    private String pushUrl;

    /**
     * 巢外监控url
     */
    @TrimStr
    @Length(max = 500, message = "巢外监控最大长度500")
    private String outVideoUrl;


    /**
     * 巢内监控url
     */
    @TrimStr
    @Length(max = 500, message = "巢内监控最大长度500")
    private String innerVideoUrl;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 海拔
     */
    private Double altitude;


    /**
     * 区域Id
     */
    private Integer regionId;

    /**
     * 备注
     */
    @Length(max = 500, message = "描述信息最大长度500")
    private String description;

    /**
     * 地址
     */
    @Length(max = 255, message = "详细地址最大长度255")
    private String address;

//    /**
//     * 飞机Id
//     */
//    private Integer aircraftId;
//
//
//    /**
//     * 无人机型号值
//     */
//    private Integer aircraftTypeValue;
//
//    /**
//     * 飞机序列号
//     */
//    @Length(max = 80, message = "无人机序列号最大长度80")
//    private String aircraftNumber;
//
//    /**
//     * 相机类型
//     */
//    @TrimStr
//    @Length(max = 64, message = "相机类型最大长度64")
//    private String cameraName;
//
//    /**
//     * 遥控器序列号
//     */
//    @Length(max = 80, message = "遥控器序列号最大长度80")
//    private String controllerNumber;
    /**
     * 传感器idList
     */
    private List<Integer> sensorIdList;


    /**
     * 单位id
     */
    private List<Integer> unitIds;

    /**
     * 机巢编号
     */
    @Length(max = 255, message = "基站编号最大长度255")
    private String nestNumber;


    private Long createUserId;

    /**
     * 巢外摄像头mac地址
     */
    @TrimStr
    @Length(max = 255, message = "巢外摄像头MAC地址最大长度255")
    private String outVideoMac;
    /**
     * 巢外摄像头IP地址
     */
    @TrimStr
    @Length(max = 255, message = "巢外摄像头IP地址最大长度255")
    private String outVideoIp;
    /**
     * 巢外摄像头推流地址
     */
    @TrimStr
    @Length(max = 255, message = "巢外摄像头推流地址最大长度255")
    private String outVideoPushUrl;
    /**
     * 巢外摄像头是否推流
     */
    private Boolean outVideoEnable;

    /**
     * 外部摄像头登录用户
     */
    @TrimStr
    @Length(max = 50, message = "巢外摄像头登录账号最大长度50")
    private String outVideoUser;

    /**
     * 外部摄像头登录密码
     */
    @TrimStr
    @Length(max = 50, message = "巢外摄像头登录密码最大长度50")
    private String outVideoPassword;

    /**
     * 巢内摄像头mac地址
     */
    @TrimStr
    @Length(max = 255, message = "巢内摄像头MAC地址最大长度255")
    private String innerVideoMac;
    /**
     * 巢内摄像头IP地址
     */
    @TrimStr
    @Length(max = 40, message = "巢内摄像头IP地址最大长度255")
    private String innerVideoIp;
    /**
     * 巢内摄像头推流地址
     */
    @TrimStr
    @Length(max = 255, message = "巢内摄像头推流地址最大长度255")
    private String innerVideoPushUrl;

    /**
     * 巢内摄像头是否推流
     */
    private Boolean innerVideoEnable;


    /**
     * 内部摄像头登录用户
     */
    @TrimStr
    @Length(max = 50, message = "巢内摄像头登录账号最大长度50")
    private String innerVideoUser;

    /**
     * 内部摄像头登录密码
     */
    @TrimStr
    @Length(max = 50, message = "巢内摄像头登录密码最大长度50")
    private String innerVideoPassword;

//    /**
//     * rtk（1-无；2-大疆D-RTK基站）
//     */
//    private Integer rtkType;
//
//    /**
//     * rtk过期时间
//     **/
//    private LocalDate rtkExpireTime;


    List<UavDetailsSaveVO> uavDetailsSaveVOList;
}
