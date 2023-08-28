package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.out.DJIPilotCommonResultOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.DJIPilotCommonResultRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIPilotCommonResultTransformer.java
 * @Description DJIPilotCommonResultTransformer
 * @createTime 2022年10月19日 17:59:00
 */
@Mapper(componentModel = "spring")
public interface DJIPilotCommonResultTransformer {

    DJIPilotCommonResultTransformer INSTANCES = Mappers.getMapper(DJIPilotCommonResultTransformer.class);

    /**
     * 转换出口
     * @param dto
     * @return
     */
    DJIPilotCommonResultRespVO.PilotCommonResultRespVO transform(DJIPilotCommonResultOutDTO.PilotCommonResultOutDTO dto);

    /**
     *
     * @param dto
     * @return
     */
    DJIPilotCommonResultRespVO.PilotLicenseResultRespVO transform(DJIPilotCommonResultOutDTO.PilotLicenseResultOutDTO dto);

    /**
     *
     * @param dto
     * @return
     */
    DJIPilotCommonResultRespVO.PilotMqttInfoRespVO transform(DJIPilotCommonResultOutDTO.PilotMqttResultOutDTO dto);

}
