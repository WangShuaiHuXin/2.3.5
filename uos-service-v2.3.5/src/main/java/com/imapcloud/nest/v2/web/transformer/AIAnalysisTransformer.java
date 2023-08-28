package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.AIRecognitionTaskInDTO;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisRepoOutDTO;
import com.imapcloud.nest.v2.web.vo.req.AIRecognitionTaskReqVO;
import com.imapcloud.nest.v2.web.vo.resp.AIRecCategoryRespVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * AI分析转换器
 * @author Vastfy
 * @date 2022/11/02 14:09
 * @since 2.1.4
 */
@Mapper(componentModel = "spring")
public interface AIAnalysisTransformer {

    List<AIRecCategoryRespVO> transform(List<AIAnalysisRepoOutDTO> in);

    AIRecognitionTaskInDTO transform(AIRecognitionTaskReqVO in);

}
