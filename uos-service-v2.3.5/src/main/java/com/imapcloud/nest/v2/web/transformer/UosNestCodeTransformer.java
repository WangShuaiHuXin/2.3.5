package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.UosNestCodeInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosNestCodeOutDTO;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.NestNetworkStateVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosNestCodeTransformer.java
 * @Description UosNestCodeTransformer
 * @createTime 2022年07月08日 17:03:00
 */
@Mapper(componentModel = "spring")
public interface UosNestCodeTransformer {

    UosNestCodeTransformer INSTANCES = Mappers.getMapper(UosNestCodeTransformer.class);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosNestCodeInDTO.NestProcessControlInDTO transform(NestProcessControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosNestCodeInDTO.NestRoutineControlInDTO transform(NestRoutineControlVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosNestCodeInDTO.NestRotateLiftInDTO transform(NestRotateLiftVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosNestCodeInDTO.NestSquareControlInDTO transform(NestSquareControlVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosNestCodeInDTO.NestDetectionNetworkInDTO transform(NestDetectionNetworkVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosNestCodeInDTO.NestBackLandPointInDTO transform(NestBackLandPointVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosNestCodeInDTO.NestPowerControlInDTO transform(NestPowerControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosNestCodeInDTO.NestSysControlInDTO transform(NestSysControlVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    NestNetworkStateVO transform(UosNestCodeOutDTO.NestNetworkStateOutDTO vo);
}
