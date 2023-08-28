package com.imapcloud.nest.v2.dao.po.in;

import com.imapcloud.nest.v2.dao.entity.AIAnalysisPhotoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI分析数据扩展信息
 *
 * @author Vastfy
 * @date 2022/11/3 11:23
 * @since 2.1.4
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AIAnalysisPhotoExtPO extends AIAnalysisPhotoEntity {

    private String imageUrl;

}
