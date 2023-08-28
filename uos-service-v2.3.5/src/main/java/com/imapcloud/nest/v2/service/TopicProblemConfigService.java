package com.imapcloud.nest.v2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.v2.service.dto.in.TopicInDTO;
import com.imapcloud.nest.v2.service.dto.in.TopicProblemConfigInDTO;
import com.imapcloud.nest.v2.service.dto.out.AIProblemTypeMappingOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicProblemConfigOutDTO;
import com.imapcloud.nest.v2.web.vo.req.TopicProblemConfigReqVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 专题服务
 *
 * @author boluo
 * @date 2022-07-14
 */
public interface TopicProblemConfigService {
    /**
     * 编辑行业问题
     */
    Boolean editIndustryProblem(TopicProblemConfigOutDTO.EditIndustryProblemOut ta);

    /**
     * 行业问题列表
     *
     * @param problemConfigReq
     */
    IPage<TopicProblemConfigOutDTO.IndustryProblemListOut> industryProblemList(TopicProblemConfigInDTO.TopicProblemConfigQueryIn problemConfigReq);

    List<TopicProblemConfigReqVO.UDAProblemTypeReq> getUdaProblemTypeReqs(
            String typeString, String storageId, String functionId);

    Boolean addIndustryProblem(TopicProblemConfigOutDTO.AddIndustryProblemOut addIndustryProblemReq);

    Boolean delProblemType(List<String> topicProblemIdList);
}
