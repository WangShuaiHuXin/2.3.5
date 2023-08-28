package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.TrimStr;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UavDetailsSaveVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 飞机Id
     */
    private Integer aircraftId;

    /**
     * 无人机标识
     */
    @LimitVal(values = {"0","1","2","3"}, message = "which值不在范围内")
    private Integer which;

    /**
     * 无人机型号值
     */
    private Integer aircraftTypeValue;

    /**
     * 飞机序列号
     */
    @Length(max = 80, message = "无人机序列号最大长度80")
    private String aircraftNumber;

    /**
     * 相机类型
     */
    @TrimStr
    @Length(max = 64, message = "相机类型最大长度64")
    private String cameraName;

    /**
     * 遥控器序列号
     */
    @Length(max = 80, message = "遥控器序列号最大长度80")
    private String controllerNumber;


    /**
     * 图传地址（拉流地址）
     */
    @TrimStr
    @Length(max = 255, message = "图传地址最大长度255")
    private String rtmpUrl;

    /**
     * rtk（1-无；2-大疆D-RTK基站）
     */
    private Integer rtkType;

    /**
     * rtk过期时间
     **/
    private LocalDate rtkExpireTime;

}
