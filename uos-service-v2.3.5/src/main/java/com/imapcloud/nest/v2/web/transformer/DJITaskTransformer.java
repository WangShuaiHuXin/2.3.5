package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.out.DJITaskOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.DJITaskRespVO;
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
public interface DJITaskTransformer {

    DJITaskTransformer INSTANCES = Mappers.getMapper(DJITaskTransformer.class);

    /**
     * 转换出口
     * @param reqVO
     * @return
     */
    DJITaskRespVO.DJITaskInfoRespVO transform(DJITaskOutDTO.DJITaskInfoOutDTO reqVO);


    /**
     * 转换出口
     * @param reqVO
     * @return
     */
    DJITaskRespVO.DJITaskFileInfoRespVO transform(DJITaskOutDTO.DJITaskFileInfoOutDTO reqVO);

}
