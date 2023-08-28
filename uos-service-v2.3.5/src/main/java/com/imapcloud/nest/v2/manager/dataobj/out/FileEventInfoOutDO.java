package com.imapcloud.nest.v2.manager.dataobj.out;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import com.imapcloud.nest.enums.MinioEventTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文件事件信息
 *
 * @author boluo
 * @date 2022-10-27
 */
@Slf4j
@Data
public class FileEventInfoOutDO {

    private Long id;

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

    @Data
    public static class EventDataInfoDO {

        /**
         * 存储桶
         */
        private String bucket;

        /**
         * 存储桶中的对象
         */
        private String object;

        /**
         * 事件类型0：未识别类型 1：删除对象 2：设置对象tag
         */
        private Integer eventType;
    }

    public EventDataInfoDO getEventDataInfoDO() {

        EventDataInfoDO eventDataInfoDO = new EventDataInfoDO();
        eventDataInfoDO.setEventType(0);
        eventDataInfoDO.setBucket("");
        eventDataInfoDO.setObject("");

        try {
            Map eventDataMap = JSONUtil.toBean(eventData, Map.class);
            java.lang.Object records = eventDataMap.get("Records");
            if (records == null) {
                return eventDataInfoDO;
            }
            List<Record> recordList = JSONUtil.toList(JSONUtil.toJsonStr(records), Record.class);
            if (CollUtil.isEmpty(recordList)) {
                return eventDataInfoDO;
            }
            Record record = recordList.get(0);
            // 事件类型
            String eventName = record.getEventName();
            if (StringUtils.isNotBlank(eventName)) {
                // s3:ObjectCreated:PutTagging
                if (eventName.contains("PutTagging")) {
                    eventDataInfoDO.setEventType(MinioEventTypeEnum.PUT_TAGGING.getCode());
                } else if (eventName.contains("Delete")) {
                    eventDataInfoDO.setEventType(MinioEventTypeEnum.DELETE.getCode());
                }
            }
            // Bucket
            if (record.s3 != null) {
                if (record.s3.bucket != null && record.s3.bucket.name != null) {
                    eventDataInfoDO.setBucket(record.s3.bucket.name);
                }
                if (record.s3.object != null && record.s3.object.key != null) {
                    eventDataInfoDO.setObject(URLDecoder.decode(record.s3.object.key, CharsetUtil.CHARSET_UTF_8));
                }
            }
        } catch (Exception e) {
            log.error("#FileEventInfoOutDO.getEventDataInfoDO# eventData={} error:", eventData, e);
        }
        return eventDataInfoDO;
    }


    @Data
    private static class Record {

        private String eventName;

        private S3 s3;
    }

    @Data
    private static class S3 {
        private Bucket bucket;

        private Object object;
    }

    @Data
    private static class Bucket {
        private String name;
    }

    @Data
    private static class Object {
        private String key;
    }
}
