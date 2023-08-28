package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.DataInterestPointInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataInterestPointPageInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataInterestPointOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataInterestPointPageReqVO;
import com.imapcloud.nest.v2.web.vo.req.DataInterestPointReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataInterestPointRespVO;
import org.mapstruct.Mapper;

/**
 * @Classname DataInterestPointTransformer
 * @Description 全景兴趣点转换类
 * @Date 2022/9/26 14:20
 * @Author Carnival
 */
@Mapper(componentModel = "spring")
public interface DataInterestPointTransformer {

    DataInterestPointInDTO transform(DataInterestPointReqVO vo);

    DataInterestPointPageInDTO transform(DataInterestPointPageReqVO vo);

    DataInterestPointRespVO transform(DataInterestPointOutDTO dto);
}
