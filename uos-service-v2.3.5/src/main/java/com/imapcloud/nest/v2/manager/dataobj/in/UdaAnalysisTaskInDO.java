package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * UDA分析任务数据
 * @author Vastfy
 * @date 2022/11/2 14:59
 * @since 2.1.4
 */
@Data
public class UdaAnalysisTaskInDO implements Serializable {

    private String orgCode;

    private List<String> storageIdList;

    private List<TaskPic> taskPicList;

    @Data
    public static class TaskPic implements Serializable {

        /**
         * 图片ID
         */
        private String picId;

        /**
         * 图片访问URL
         */
        private String picUrl;

    }

}
