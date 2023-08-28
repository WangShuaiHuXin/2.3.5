package com.imapcloud.nest.v2.service.dto.in;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 设备配置页面映射参数
 *
 * @author Vastfy
 * @date 2023/4/17 19:22
 * @since 2.3.2
 */
@Data
public class DeviceSettingParamInDTO {

    private FrpcServer common;
    @JSONField(name = "proxy_addes", unwrapped = true)
    private List<FrpcClient> proxy_addes;

    @Data
    public static class FrpcServer {
        @JSONField(name = "server_addr")
        private String serverAddr;
        @JSONField(name = "server_port")
        private Integer serverPort;
        private String token;
    }

    @Data
    public static class FrpcClient {
        private String name;
        private String type = "tcp";
        @JSONField(name = "local_ip")
        private String localIp;
        @JSONField(name = "local_port")
        private Integer localPort = 80;
        @JSONField(name = "remote_port")
        private Integer remotePort;
    }

}
