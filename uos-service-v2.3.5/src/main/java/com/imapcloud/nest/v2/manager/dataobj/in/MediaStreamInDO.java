package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;
import lombok.ToString;

/**
 * media_stream实体包装
 *
 * @author boluo
 * @date 2022-08-25
 * @deprecated 2.3.2，将在后续版本删除
 */
@ToString
@Deprecated
public class MediaStreamInDO {

    private MediaStreamInDO() {}

    @Data
    public static class EntityInDO extends BaseInDO {
        /**
         * 流ID
         */
        private String streamId;

        /**
         * 推流地址
         */
        private String streamPushUrl;

        /**
         * 拉流地址
         */
        private String streamPullUrl;

        /**
         * 协议类型 RTMP/RTSP/HTTP/SRT/RTC/GB28181
         */
        private String protocol;
    }
}
