package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AI分析仓库信息
 * @author Vastfy
 * @date 2022/10/26 09:52
 * @since 2.1.4
 */
@Data
public class AIAnalysisRepoOutDTO implements Serializable {

    /**
     * 算法仓库ID
     */
    private String categoryId;

    /**
     * 算法仓库名称
     */
    private String categoryName;

    /**
     * 识别功能列表
     */
    private List<AIRecFunction> recFunctions;

    @Data
    public static class AIRecFunction implements Serializable {

        /**
         * 识别功能ID
         */
        private String functionId;

        /**
         * 识别功能名称
         */
        private String functionName;

        /**
         * 识别功能版本号
         */
        private String version;

    }

}
