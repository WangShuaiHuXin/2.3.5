package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.MeterDataDetailQueryDTO;
import com.imapcloud.nest.v2.service.dto.in.MeterDataQueryDTO;
import com.imapcloud.nest.v2.service.dto.in.MeterReadingInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.DiscernFunctionSettingOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDataDetailInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDataInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDeviceStateStatsOutDTO;
import com.imapcloud.nest.v2.web.vo.req.MeterDataDetailQueryReqVO;
import com.imapcloud.nest.v2.web.vo.req.MeterDataQueryReqVO;
import com.imapcloud.nest.v2.web.vo.req.MeterReadingInfoReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DiscernFunctionInfoRespVO;
import com.imapcloud.nest.v2.web.vo.resp.MeterDataDetailRespVO;
import com.imapcloud.nest.v2.web.vo.resp.MeterDataRespVO;
import com.imapcloud.nest.v2.web.vo.resp.MeterDeviceStateStatsRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PowerDiscernFunctionTransformer {
    PowerDiscernFunctionTransformer INSTANCE = Mappers.getMapper(PowerDiscernFunctionTransformer.class);

    MeterDataQueryDTO transform(MeterDataQueryReqVO in);

    MeterDataRespVO transform(MeterDataInfoOutDTO in);

    MeterDataDetailQueryDTO transform(MeterDataDetailQueryReqVO in);

    @Mapping(source = "verificationStatus",target = "verificationStatus")
    MeterDataDetailRespVO transform(MeterDataDetailInfoOutDTO in);

    MeterReadingInfoInDTO transform(MeterReadingInfoReqVO in);

    MeterDeviceStateStatsRespVO transform(MeterDeviceStateStatsOutDTO in);

    DiscernFunctionInfoRespVO transform(DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO dto);
}
