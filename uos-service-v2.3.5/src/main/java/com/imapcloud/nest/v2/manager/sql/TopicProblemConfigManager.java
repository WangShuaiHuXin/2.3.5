package com.imapcloud.nest.v2.manager.sql;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.v2.service.dto.in.TopicProblemConfigInDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicProblemConfigOutDTO;

import java.util.List;

public interface TopicProblemConfigManager {
    IPage<TopicProblemConfigOutDTO.IndustryProblemListOut> getIndustryProblemListOutIPage(TopicProblemConfigInDTO.TopicProblemConfigQueryIn problemConfigReq, String orgCode);

    Boolean addIndustryProblemBySystem(TopicProblemConfigOutDTO.AddIndustryProblemOut addIndustryProblemReq);

    Boolean addIndustryProblemByOrg(TopicProblemConfigOutDTO.AddIndustryProblemOut addIndustryProblemReq);

    Boolean editIndustryProblemBySystem(TopicProblemConfigOutDTO.EditIndustryProblemOut addIndustryProblemReq);

    Boolean editIndustryProblemByOrg(TopicProblemConfigOutDTO.EditIndustryProblemOut addIndustryProblemReq);

    Boolean delProblemType(List<String> topicProblemIdList);

    void checkTopicProblemName(TopicProblemConfigOutDTO.EditIndustryProblemOut editIndustryProblemIn);

    void checkTopicProblemName(TopicProblemConfigOutDTO.AddIndustryProblemOut addIndustryProblemReq);

    List<String> getRepeatUdARelationIdList(List<String> idList, String functionId);

    void checkRepeatUdARelationList(List<String> idList, String topicProblemId);
}
