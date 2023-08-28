package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.SysOrgPilotSpaceEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.DjiPilotFileUploadCallBackDO;
import com.imapcloud.nest.v2.service.dto.in.PilotFileUploadInDTO;
import com.imapcloud.nest.v2.service.dto.out.SysOrgPilotSpaceOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface SysOrgPilotSpaceConverter {

    SysOrgPilotSpaceConverter INSTANCES = Mappers.getMapper(SysOrgPilotSpaceConverter.class);

    /**
     * 转换出口
     * @param out
     * @return
     */
    SysOrgPilotSpaceOutDTO convert(SysOrgPilotSpaceEntity out);

    /**
     * 转换出口
     * @return
     */
    DjiPilotFileUploadCallBackDO convert(PilotFileUploadInDTO inDTO);

}
