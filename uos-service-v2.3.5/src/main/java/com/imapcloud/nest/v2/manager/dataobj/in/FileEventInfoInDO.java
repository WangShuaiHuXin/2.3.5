package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

/**
 * 文件事件信息
 *
 * @author boluo
 * @date 2022-10-27
 */
@Data
public class FileEventInfoInDO {

    private Long id;

    /**
     * event_data解析状态 0：未解析 1：已解析
     */
    private int eventStatus;

    /**
     * 同步处理状态-0：未同步 1：已同步
     */
    private int synStatus;

    /**
     * 事件类型0：未识别类型 1：删除对象 2：设置对象tag
     */
    private Integer eventType;

    /**
     * 存储桶
     */
    private String bucket;

    /**
     * 存储桶中的对象
     */
    private String object;
}
