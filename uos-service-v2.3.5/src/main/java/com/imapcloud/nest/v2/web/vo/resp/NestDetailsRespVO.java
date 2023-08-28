package com.imapcloud.nest.v2.web.vo.resp;

import com.imapcloud.nest.model.BackLandFunEntity;
import com.imapcloud.nest.pojo.dto.NestDetailsInfoDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NestDetailsRespVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 数据库nest表id,新建的保存没有，修改的保存有
     */
    private Integer nestId;

    /**
     * 机巢uuid
     */
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
    private String name;

    /**
     * 机巢账号
     */
    private String username;

    /**
     * 机巢密码
     */
    private String password;

    /**
     * 服务地址
     */
    private String serverUrl;

    /**
     * 图传地址
     */
    private String rtmpUrl;

    /**
     * 图传地址
     */
    private String pushUrl;

    /**
     * 巢外监控Id
     */
    private String outVideoId;

    /**
     * 巢外监控url
     */
    private String outVideoUrl;

    /**
     * 巢内监控id
     */
    private String innerVideoId;

    /**
     * 巢内监控url
     */
    private String innerVideoUrl;

    /**
     * 监控账号
     */
    private String monitorUsername;

    /**
     * 监控密码
     */
    private String monitorPassword;

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

    private String nestPic;

    private String aircraftPic;

    private String videoPic;

    /**
     * 区域Id
     */
    private Integer regionId;
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 单位ID
     */
    private List<Integer> unitIds;
    /**
     * 单位ID
     */
    private String unitName;

    /**
     * 备注
     */
    private String description;

    /**
     * 地址
     */
    private String address;

    /**
     * 飞机Id
     */
    private Integer aircraftId;

    /**
     * 无人机型号
     */
    private String aircraftCode;

    private Integer aircraftTypeValue;

    /**
     * 飞机序列号
     */
    private String aircraftNumber;

    /**
     * 相机名称
     */
    private String cameraName;

    /**
     * 遥控器序列号
     */
    private String controllerNumber;
    /**
     * 传感器idList
     */
    private List<Integer> sensorIdList;
    private List<Integer> delSensorIdList;

    /**
     * 传感器id、名称List
     */
    private List<NestDetailsInfoDto.sensorBean> sensorList;

    /**
     * 备降点
     */
    private List<BackLandFunEntity> backLandFunEntities;

    /**
     * 机巢编号
     */
    private String nestNumber;

    private Boolean useGb;

    private String outVideoUser;

    private String outVideoPassword;

    private String outVideoMac;

    private Boolean outVideoEnable;

    private String outVideoIp;

    private String outVideoPushUrl;

    private String innerVideoUser;

    private String innerVideoPassword;

    private String innerVideoMac;

    private Boolean innerVideoEnable;

    private String innerVideoIp;

    private String innerVideoPushUrl;

    /**
     * rtk（1-无；2-大疆D-RTK基站）
     */
    private Integer rtkType;

    /**
     * rtk过期时间
     **/
    private Object rtkExpireTime;

    @Data
    public static class sensorBean {
        int sensorId;
        String sensorName;
    }
}
