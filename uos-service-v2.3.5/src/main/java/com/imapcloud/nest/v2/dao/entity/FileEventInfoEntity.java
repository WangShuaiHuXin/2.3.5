package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.AncientEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * minio文件事件信息实体
 *
 * @author boluo
 * @date 2022-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_event_info")
public class FileEventInfoEntity extends AncientEntity {

    /**
     * event_time
     */
    private LocalDateTime eventTime;

    /**
     * event_data
     */
    private String eventData;

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
