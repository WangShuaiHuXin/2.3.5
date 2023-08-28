package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.dao.po.in.PowerMeterFlightDetailInfraredInDTO;
import com.imapcloud.nest.v2.dao.po.out.PowerMeterFlightDetailInfraredOutDTO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerInspectionReportInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDetailInfraredOutDO;
import com.imapcloud.nest.v2.service.dto.out.PowerMeterInfraredRecordOutDTO;
import com.imapcloud.nest.v2.web.vo.req.PowerMeterFlightDetailInfraredReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PowerMeterFlightDetailInfraredRespVO;
import com.imapcloud.nest.v2.web.vo.resp.PowerMeterInfraredRecordRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author wmin
 */
@Mapper(componentModel = "spring")
public interface PowerMeterFlightDetailInfraredTransformer {
    PowerMeterFlightDetailInfraredTransformer INSTANCE = Mappers.getMapper(PowerMeterFlightDetailInfraredTransformer.class);

    PowerMeterFlightDetailInfraredInDTO transform(PowerMeterFlightDetailInfraredReqVO vo);

}
