package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.PilotFileUploadInDTO;
import com.imapcloud.nest.v2.service.dto.out.PilotStsCredentialsOutDTO;
import com.imapcloud.nest.v2.web.vo.req.PilotFileUploadReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PilotStsCredentialsRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIPilotMediaTransformer.java
 * @Description DJIPilotMediaTransformer
 * @createTime 2022年10月19日 17:59:00
 */
@Mapper(componentModel = "spring")
public interface DJIPilotMediaTransformer {

    DJIPilotMediaTransformer INSTANCES = Mappers.getMapper(DJIPilotMediaTransformer.class);

    /**
     * 转换出口
     * @param dto
     * @return
     */
    PilotStsCredentialsRespVO transform(PilotStsCredentialsOutDTO dto);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    PilotFileUploadInDTO transform(PilotFileUploadReqVO vo);


}
