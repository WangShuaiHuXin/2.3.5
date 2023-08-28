package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.MaplayerQueryDTO;
import com.imapcloud.nest.v2.service.dto.out.MaplayerInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.req.MaplayerQueryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.MaplayerInfoRespVO;
import org.mapstruct.Mapper;

/**
 * 转化器
 * @author boluo
 * @date 2022-09-27
 */
@Mapper(componentModel = "spring")
public interface UosMaplayerTransformer {

    MaplayerQueryDTO transform(MaplayerQueryReqVO in);

    MaplayerInfoRespVO transform(MaplayerInfoOutDTO in);

}
