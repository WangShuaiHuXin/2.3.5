package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * im消息在做
 *
 * @author boluo
 * @date 2023-02-15
 */
@Data
public class ImMessageInDO implements Serializable {

    private final String sender = "UOS";

    /**
     * channel ID列表
     */
    private List<String> receiverList;

    /**
     * 业务描述
     */
    private String title;

    /**
     * 业务数据
     */
    private String data;

    /**
     * 消息类型（主题）
     */
    private String action;
}
