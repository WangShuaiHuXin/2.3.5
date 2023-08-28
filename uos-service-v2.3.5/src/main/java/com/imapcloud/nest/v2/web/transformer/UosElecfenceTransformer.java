package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.ElecfenceCreationInDTO;
import com.imapcloud.nest.v2.service.dto.in.ElecfenceModificationInDTO;
import com.imapcloud.nest.v2.service.dto.out.ElecfenceInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.req.ElecfenceCreationReqVO;
import com.imapcloud.nest.v2.web.vo.req.ElecfenceUpdatedReqVO;
import com.imapcloud.nest.v2.web.vo.resp.ElecfenceInfoRespVO;
import org.mapstruct.Mapper;

/**
 * 转化器
 * @author boluo
 * @date 2022-09-26
 */
@Mapper(componentModel = "spring")
public interface UosElecfenceTransformer {

    ElecfenceInfoRespVO transform(ElecfenceInfoOutDTO in);

    ElecfenceCreationInDTO transform(ElecfenceCreationReqVO in);

    ElecfenceModificationInDTO transform(ElecfenceUpdatedReqVO in);

}
