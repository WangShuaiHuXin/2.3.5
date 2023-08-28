package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.TrimStr;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import java.time.LocalDate;
import java.util.List;

/**
 * 机巢添加编辑传输类
 *
 * @author wmin
 */
@Data
public class NestDetailsDto {

    /**
     * 数据库nest表id,新建的保存没有，修改的保存有
     */
    private Integer nestId;

    /**
     * 机巢uuid
     */
    @TrimStr
    @Length(max = 80, message = "{geoai_uos_maximum_length_of_machine_nest_uuid_is_80}")
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
    @Length(max = 500, message = "{geoai_uos_maximum_length_of_nest_name_is_500}")
    private String name;

    /**
     * 机巢账号
     */
    @TrimStr
    @Length(max = 50, message = "{geoai_uos_maximum_length_of_management_account_50}")
    private String username;

    /**
     * 机巢密码
     */
    @TrimStr
    @Length(max = 100, message = "{geoai_uos_the_maximum_length_of_management_password_is_100}")
    private String password;

    /**
     * 服务地址
     */
    @TrimStr
    @Length(max = 255, message = "{geoai_uos_maximum_length_of_connection_address_255}")
    private String serverUrl;

    /**
     * 图传地址（拉流地址）
     */
    @TrimStr
    @Length(max = 255, message = "{geoai_uos_max_length_of_map_address_255}")
    private String rtmpUrl;

    /**
     * 无人机推流地址（rtmp）
     */
    @TrimStr
    @Length(max = 500, message = "{geoai_uos_maximum_length_of_push_stream_address_500}")
    private String pushUrl;

    /**
     * 巢外监控url
     */
    @TrimStr
    @Length(max = 500, message = "{geoai_uos_maximum_length_of_monitoring_outside_the_nest_500}")
    private String outVideoUrl;


    /**
     * 巢内监控url
     */
    @TrimStr
    @Length(max = 500, message = "{geoai_uos_maximum_length_of_innest_monitoring_500}")
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
    @Length(max = 500, message = "{geoai_uos_maximum_length_of_description_information_500}")
    private String description;

    /**
     * 地址
     */
    @Length(max = 255, message = "{geoai_uos_maximum_length_of_detailed_address_255}")
    private String address;

    /**
     * 飞机Id
     */
    private Integer aircraftId;


    /**
     * 无人机型号值
     */
    private Integer aircraftTypeValue;

    /**
     * 飞机序列号
     */
    @Length(max = 80, message = "{geoai_uos_drone_serial_number_max_length_80}")
    private String aircraftNumber;

    /**
     * 相机类型
     */
    @TrimStr
    @Length(max = 64, message = "{geoai_uos_maximum_length_of_camera_type_64}")
    private String cameraName;

    /**
     * 遥控器序列号
     */
    @Length(max = 80, message = "{geoai_uos_remote_control_serial_number_max_length_80}")
    private String controllerNumber;
    /**
     * 传感器idList
     */
    private List<Integer> sensorIdList;


    /**
     * 单位id
     */
    private List<String> unitIds;

    /**
     * 机巢编号
     */
    @Length(max = 255, message = "{geoai_uos_base_station_number_max_length_255}")
    private String nestNumber;


    private Long createUserId;

    /**
     * 巢外摄像头mac地址
     */
    @TrimStr
    @Length(max = 255, message = "{geoai_uos_outofnest_camera_mac_address_max_length_255}")
    private String outVideoMac;
    /**
     * 巢外摄像头IP地址
     */
    @TrimStr
    @Length(max = 255, message = "{geoai_uos_outofnest_camera_ip_address_max_length_255}")
    private String outVideoIp;
    /**
     * 巢外摄像头推流地址
     */
    @TrimStr
    @Length(max = 255, message = "{geoai_uos_outofnest_camera_push_stream_address_max_length_255}")
    private String outVideoPushUrl;
    /**
     * 巢外摄像头是否推流
     */
    private Boolean outVideoEnable;

    /**
     * 外部摄像头登录用户
     */
    @TrimStr
    @Length(max = 50, message = "{geoai_uos_outofnest_camera_login_account_max_length_50}")
    private String outVideoUser;

    /**
     * 外部摄像头登录密码
     */
    @TrimStr
    @Length(max = 50, message = "{geoai_uos_outofnest_camera_login_password_max_length_50}")
    private String outVideoPassword;

    /**
     * 巢内摄像头mac地址
     */
    @TrimStr
    @Length(max = 255, message = "{geoai_uos_max_length_of_nesting_camera_mac_address_255}")
    private String innerVideoMac;
    /**
     * 巢内摄像头IP地址
     */
    @TrimStr
    @Length(max = 40, message = "{geoai_uos_maximum_length_of_ip_address_of_innest_camera_255}")
    private String innerVideoIp;
    /**
     * 巢内摄像头推流地址
     */
    @TrimStr
    @Length(max = 255, message = "{geoai_uos_maximum_length_of_innest_camera_push_address_255}")
    private String innerVideoPushUrl;
    /**
     * 巢内摄像头是否推流
     */
    private Boolean innerVideoEnable;


    /**
     * 内部摄像头登录用户
     */
    @TrimStr
    @Length(max = 50, message = "{geoai_uos_maximum_length_of_innest_camera_login_account_50}")
    private String innerVideoUser;

    /**
     * 内部摄像头登录密码
     */
    @TrimStr
    @Length(max = 50, message = "{geoai_uos_maximum_length_of_nest_camera_login_password_50}")
    private String innerVideoPassword;

    /**
     * rtk（1-无；2-大疆D-RTK基站）
     */
    private Integer rtkType;

    /**
     * rtk过期时间
     **/
    private LocalDate rtkExpireTime;
}
