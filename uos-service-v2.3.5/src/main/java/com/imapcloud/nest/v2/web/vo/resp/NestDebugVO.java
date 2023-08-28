package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.util.List;

@Data
public class NestDebugVO {
//    private String innerVideoUrl;
    private String name;
    private String nestId;
//    private String outVideoUrl;
    private String password;
//    private String rtmpUrl;
//    private String rtmpUrl2;
//    private String rtmpUrl3;
    private String serverUrl;
    private String type;
    private String username;
    private String uuid;
    private List<String> uavIds;
}
