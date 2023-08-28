package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.out.SysOrgPilotSpaceOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.SysOrgPilotSpaceRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SysOrgPilotSpaceTransformer.java
 * @Description SysOrgPilotSpaceTransformer
 * @createTime 2022年10月19日 17:59:00
 */
@Mapper(componentModel = "spring")
public interface SysOrgPilotSpaceTransformer {

    SysOrgPilotSpaceTransformer INSTANCES = Mappers.getMapper(SysOrgPilotSpaceTransformer.class);

    /**
     * 转换出口
     * @param dto
     * @return
     */
    SysOrgPilotSpaceRespVO transform(SysOrgPilotSpaceOutDTO dto);

}
