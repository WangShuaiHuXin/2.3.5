package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.out.FpiAirlinePackageParseOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.FpiAirlinePackageParseRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 账号信息转换器
 * @author Vastfy
 * @date 2023/02/22 10:09
 * @since 2.2.3
 */
@Mapper(componentModel = "spring")
public interface DataParseTransformer {

    DataParseTransformer INSTANCE = Mappers.getMapper(DataParseTransformer.class);

    FpiAirlinePackageParseRespVO transform(FpiAirlinePackageParseOutDTO data);

}
