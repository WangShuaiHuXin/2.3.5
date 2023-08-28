package com.imapcloud.nest.v2.service.dto.in;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 基站流媒体保存信息
 * @author Carnival
 * @date 2023/4/3 14:47
 * @since 2.3.2
 */
@ApiModel("基站流媒体保存信息")
@Data
public class NestMediaSaveInfoInDTO implements Serializable {

    private String insideDeviceCode;

    private String outsideDeviceCode;

    private List<UavMeta> uavMetaList;

    @Data
    public static class UavMeta {

        private String uavId;

        private String serverId;

    }
}
