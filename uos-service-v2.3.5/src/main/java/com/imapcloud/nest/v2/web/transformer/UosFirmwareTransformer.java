package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.FirmwarePackageInDTO;
import com.imapcloud.nest.v2.service.dto.in.FirmwareVersionInDTO;
import com.imapcloud.nest.v2.service.dto.in.NestFirmwareVersionInDTO;
import com.imapcloud.nest.v2.service.dto.out.FirmwarePackageInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestFirmwarePackageInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.req.FirmwarePackageReqVO;
import com.imapcloud.nest.v2.web.vo.req.FirmwareVersionQueryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.FirmwarePackageInfoRespVO;
import com.imapcloud.nest.v2.web.vo.resp.NestFirmwarePackageInfoRespVO;
import org.mapstruct.Mapper;

/**
 * UOS固件安装包信息转换器
 * @author Vastfy
 * @date 2022/07/13 09:09
 * @since 1.9.7
 */
@Mapper(componentModel = "spring")
public interface UosFirmwareTransformer {

    FirmwarePackageInDTO transform(FirmwarePackageReqVO req);

    FirmwareVersionInDTO transform(FirmwareVersionQueryReqVO req);

    FirmwarePackageInfoRespVO transform(FirmwarePackageInfoOutDTO req);

    NestFirmwareVersionInDTO transform2(FirmwareVersionQueryReqVO req);

    NestFirmwarePackageInfoRespVO transform(NestFirmwarePackageInfoOutDTO req);

}
