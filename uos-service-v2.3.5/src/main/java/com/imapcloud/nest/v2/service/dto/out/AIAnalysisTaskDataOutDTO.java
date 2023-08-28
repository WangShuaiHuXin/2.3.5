package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AI分析数据信息
 * @author Vastfy
 * @date 2022/10/26 16:26
 * @since 2.1.4
 */
@Data
public class AIAnalysisTaskDataOutDTO implements Serializable {

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * AI分析任务ID
     */
    private String aiTaskId;

    /**
     * AI分析任务名称
     */
    private String taskName;

    /**
     * AI分析任务状态【-1：已暂停；0：识别中；1：已完成；2：已终止】
     */
    private Integer taskState;

    /**
     * AI分析图片总数
     */
    private int imageTotalCounts;

    /**
     * AI分析图片成功数
     */
    private int imageSuccessCounts;

    /**
     * AI分析图片失败数
     */
    private int imageFailedCounts;

    /**
     * 耗时：秒
     */
    private int costSecs;

    /**
     * 分析统计基础ID
     */
    private String centerBaseId;

    /**
     * 航线任务ID
     */
    private String taskId;

    /**
     * 任务标签名称
     */
    private String tagName;

    /**
     * 是否初始化
     */
    private boolean initialized;

    /**
     * 标识是否自动任务
     */
    private boolean auto;

    private Integer aiTaskType;

    /**
     * 消息发送时间戳
     */
    private final long timestamp = System.currentTimeMillis();

    private List<AnalysisPhotoRecord> changedRecords;

    @Data
    public static class AnalysisPhotoRecord implements Serializable {

        /**
         * AI识别图片记录ID
         */
        private String recordId;

        /**
         * AI识别图片名称
         */
        private String photoName;

        /**
         * AI识别图片状态【-1：已暂停；0：识别中；1：识别成功；2：识别失败】
         */
        private Integer state;

    }

}
