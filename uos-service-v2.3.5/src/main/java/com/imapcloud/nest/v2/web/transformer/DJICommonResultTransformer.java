package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.ChargeLiveLensInDTO;
import com.imapcloud.nest.v2.service.dto.in.LimitDistanceInDTO;
import com.imapcloud.nest.v2.service.dto.in.LimitHeightInDTO;
import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;
import com.imapcloud.nest.v2.web.vo.req.ChargeLiveLensReqVO;
import com.imapcloud.nest.v2.web.vo.req.LimitDistanceReqVO;
import com.imapcloud.nest.v2.web.vo.req.LimitHeightReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DJICommonResultRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonResultTransformer.java
 * @Description DJICommonResultTransformer
 * @createTime 2022年10月19日 17:59:00
 */
@Mapper(componentModel = "spring")
public interface DJICommonResultTransformer {

    DJICommonResultTransformer INSTANCES = Mappers.getMapper(DJICommonResultTransformer.class);

    /**
     * 转换出口
     * @param reqVO
     * @return
     */
    DJICommonResultRespVO.CommonResultRespVO transform(DJICommonResultOutDTO.CommonResultOutDTO reqVO);

    /**
     * 转换出口
     * @param reqVO
     * @return
     */
    DJICommonResultRespVO.LiveResultRespVO transform(DJICommonResultOutDTO.LiveResultOutDTO reqVO);

    ChargeLiveLensInDTO transform(ChargeLiveLensReqVO chargeLiveLensReqVO);

    LimitHeightInDTO transform(LimitHeightReqVO limitHeightReqVO);

    LimitDistanceInDTO transform(LimitDistanceReqVO limitDistanceReqVO);
}
