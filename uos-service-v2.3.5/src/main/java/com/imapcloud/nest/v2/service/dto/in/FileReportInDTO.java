package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;

/**
 * 文件
 *
 * @author boluo
 * @date 2022-11-03
 */
public class FileReportInDTO {

    @Data
    public static class FileListInDTO extends PageInfo {

        private String orgCode;

        private String nestId;

        /**
         * 开始时间yyyy-MM-dd
         */
        private String startTime;

        /**
         * 结束时间 yyyy-MM-dd
         */
        private String endTime;

        /**
         * 排序字段 图片：picture 视频：video 视频抽帧：videoPicture 总计：total
         */
        private String orderBy;

        /**
         * asc 0:降序 1：升序
         */
        private int asc;
    }
}
