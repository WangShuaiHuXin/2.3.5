package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.NestExtFirmwareQueryInDTO;
import com.imapcloud.nest.v2.service.dto.in.NestQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.FirmwarePackageBasicOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestBasicOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestExtFirmwareOutDTO;
import com.imapcloud.nest.v2.service.dto.out.RegionNestOutDTO;
import com.imapcloud.nest.v2.web.vo.req.NestExtFirmwareQueryReqVO;
import com.imapcloud.nest.v2.web.vo.req.NestExtMonitorQueryReqVO;
import com.imapcloud.nest.v2.web.vo.req.NestQueryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.FirmwarePackageBasicRespVO;
import com.imapcloud.nest.v2.web.vo.resp.NestBasicRespVO;
import com.imapcloud.nest.v2.web.vo.resp.NestExtFirmwareRespVO;
import com.imapcloud.nest.v2.web.vo.resp.RegionNestRespVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * UOS基站信息转换器
 * @author Vastfy
 * @date 2022/07/13 09:09
 * @since 1.9.7
 */
@Mapper(componentModel = "spring")
public interface UosNestTransformer {

    NestQueryInDTO transform(NestQueryReqVO req);

    NestExtFirmwareQueryInDTO transform(NestExtMonitorQueryReqVO req);
    NestExtFirmwareQueryInDTO transform(NestExtFirmwareQueryReqVO req);

    NestExtFirmwareRespVO transform(NestExtFirmwareOutDTO out);

    List<RegionNestRespVO> transform(List<RegionNestOutDTO> out);

    /**
     * 嵌套对象会自动调用当前类中接口
     */
    RegionNestRespVO transform(RegionNestOutDTO out);

    List<NestBasicRespVO> transformNestedObjects(List<NestBasicOutDTO> out);

    List<FirmwarePackageBasicRespVO> transformNestedObjects2(List<FirmwarePackageBasicOutDTO> outs);

}
