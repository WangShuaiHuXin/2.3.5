package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.out.AudioOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.AudioRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName AudioTransformer.java
 * @Description AudioTransformer
 * @createTime 2022年08月17日 18:14:00
 */
@Mapper(componentModel = "spring")
public interface AudioTransformer {

    AudioTransformer INSTANCES = Mappers.getMapper(AudioTransformer.class);

    /**
     * 转换出口
     *
     * @param reqVO
     * @return
     */
    AudioRespVO transform(AudioOutDTO reqVO);


}
