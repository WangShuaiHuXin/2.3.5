package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 文件统计
 *
 * @author boluo
 * @date 2022-11-03
 */
public class FileReportReqVO {

    @Data
    public static class FileTrendReqVO {

        /**
         * 时间 yyyy：月  yyyy-MM：日
         */
        @NotBlank(message = "{geoai_uos_FileReportServiceImpl_02}")
        private String time;

        private String nestId;
    }

    @Data
    public static class FileListReqVO extends PageInfo {

        /**
         * 组织代码
         */
        @NotBlank(message = "{geoai_uos_cannot_empty_unitid}")
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
