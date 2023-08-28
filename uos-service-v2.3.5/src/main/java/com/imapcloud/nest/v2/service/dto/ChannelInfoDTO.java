package com.imapcloud.nest.v2.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 通道信息
 *
 * @author boluo
 * @date 2023-02-15
 */
@Data
public class ChannelInfoDTO implements Serializable {

    /**
     * 消息类型
     */
    private List<String> actionList;

    /**
     * 基站uuid
     */
    private String uuid;

    /**
     * 语言
     */
    private String language;

    private String channelId;
}
