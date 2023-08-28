package com.imapcloud.nest.v2.service.converter;

import com.imapcloud.nest.v2.dao.entity.FirmwarePackageEntity;
import com.imapcloud.nest.v2.dao.entity.NestFirmwarePackageEntity;
import com.imapcloud.nest.v2.service.dto.in.FirmwarePackageInDTO;
import com.imapcloud.nest.v2.service.dto.out.FirmwarePackageInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestFirmwarePackageInfoOutDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * 固件安装包信息转换器
 * @author Vastfy
 * @date 2022/07/13 11:09
 * @since 1.9.7
 */
@Mapper(componentModel = "spring")
public interface FirmwarePackageConverter {

    @Mapping(source = "packageId", target = "id")
    FirmwarePackageInfoOutDTO convert(FirmwarePackageEntity in);

    NestFirmwarePackageInfoOutDTO convert(NestFirmwarePackageEntity in);

    @Mappings({
            @Mapping(source = "fileName", target = "name"),
            @Mapping(source = "filePath", target = "storePath"),
            @Mapping(source = "fileSize", target = "size")
    })
    FirmwarePackageEntity convert(FirmwarePackageInDTO in);

}
