package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.manager.dataobj.in.DjiPilotFileUploadCallBackDO;
import com.imapcloud.nest.v2.service.dto.in.PilotFileUploadInDTO;
import com.imapcloud.nest.v2.service.dto.out.DJIDockInfoOutDTO;
import com.imapcloud.sdk.pojo.djido.DjiPilotPropertyOsdDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface DJIPilotConverter {

    DJIPilotConverter INSTANCES = Mappers.getMapper(DJIPilotConverter.class);

    /**
     * 转换入口
     * @param in
     * @return
     */
//    @Mappings({
//            @Mapping(target = "droneCharge", source = "in.droneChargeState.state"),
//            @Mapping(target = "droneCapacityPercent", source = "droneChargeState.capacityPercent")
//    })
    DJIDockInfoOutDTO convert(DjiPilotPropertyOsdDO in);

    /**
     * 转换出口
     * @return
     */
    DjiPilotFileUploadCallBackDO convert(PilotFileUploadInDTO inDTO);

}
