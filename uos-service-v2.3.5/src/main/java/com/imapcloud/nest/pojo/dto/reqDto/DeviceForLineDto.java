package com.imapcloud.nest.pojo.dto.reqDto;

import com.imapcloud.nest.pojo.dto.NestPos;
import com.imapcloud.nest.pojo.dto.PhotoPos;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 设备列表（调用规划航线）
 */
@Data
public class DeviceForLineDto {

    private String requestId;

    private Integer missionType;

    private NestPos nestPos;

    private Integer split;

    private List<PhotoPos> photoPos;

}
