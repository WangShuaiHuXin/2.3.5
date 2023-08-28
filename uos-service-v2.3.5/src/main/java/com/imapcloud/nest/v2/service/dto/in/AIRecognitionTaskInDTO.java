package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AI识别任务信息
 * @author Vastfy
 * @date 2022/11/02 13:52
 * @since 2.1.4
 */
@Data
public class AIRecognitionTaskInDTO implements Serializable {

    /**
     * 基础数据ID
     */
    private String baseDataId;

    /**
     * AI任务名称
     */
    private String aiTaskName;

    /**
     * AI任务类型
     */
    private Integer aiTaskType;

    /**
     * 分析图片ID列表
     */
    private List<String> dataPhotoIds;

    /**
     * 识别功能ID列表
     */
    private List<String> recognitionFunIds;

    private String orgCode;

    private String accountId;

}
