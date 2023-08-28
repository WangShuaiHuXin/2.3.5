package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.AiStreamingAlarmSettingInDTO;
import com.imapcloud.nest.v2.service.dto.in.AiStreamingExitInDTO;
import com.imapcloud.nest.v2.service.dto.in.AiStreamingOpenInDTO;
import com.imapcloud.nest.v2.service.dto.in.AiStreamingSwitchInDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAiStreamingInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.req.AiStreamingAlarmSettingReqVO;
import com.imapcloud.nest.v2.web.vo.req.AiStreamingExitReqVO;
import com.imapcloud.nest.v2.web.vo.req.AiStreamingOpenReqVO;
import com.imapcloud.nest.v2.web.vo.req.AiStreamingSwitchReqVO;
import com.imapcloud.nest.v2.web.vo.resp.AiStreamingInfoRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * AI流转换器
 * @author Vastfy
 * @date 2022/12/24 16:08
 * @since 2.1.7
 */
@Mapper(componentModel = "spring")
public interface AIStreamingTransformer {

    AIStreamingTransformer INSTANCES = Mappers.getMapper(AIStreamingTransformer.class);

    AiStreamingInfoRespVO transform(NestAiStreamingInfoOutDTO data);
    AiStreamingOpenInDTO transform(AiStreamingOpenReqVO data);
    AiStreamingExitInDTO transform(AiStreamingExitReqVO data);
    AiStreamingSwitchInDTO transform(AiStreamingSwitchReqVO data);
    AiStreamingAlarmSettingInDTO transform(AiStreamingAlarmSettingReqVO data);

}
