package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.TopicInDTO;
import com.imapcloud.nest.v2.service.dto.out.AIProblemTypeMappingOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicOutDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 专题服务
 *
 * @author boluo
 * @date 2022-07-14
 */
public interface TopicService {
    /**
     * 查询专题级别列表
     *
     * @param topicKey 主题关键值
     * @return {@link List}<{@link TopicOutDTO.LevelInfoOut}>
     */
    List<TopicOutDTO.LevelInfoOut> levelList(String topicKey);

    /**
     * 新增/修改专题行业
     *
     * @param insertIndustryIn 插入行业
     */
    void editIndustry(TopicInDTO.EditIndustryIn insertIndustryIn);

    /**
     * 行业列表
     *
     * @param orgId    org id
     * @param topicKey 主题关键
     * @return {@link List}<{@link TopicOutDTO.IndustryListOut}>
     */
    List<TopicOutDTO.IndustryListOut> industryList(String orgId, String topicKey);

    /**
     * 编辑行业问题
     *
     * @param editIndustryProblemIn 编辑行业问题
     */
    void editIndustryProblem(TopicInDTO.EditIndustryProblemIn editIndustryProblemIn);

    /**
     * 行业问题列表
     *
     * @param orgId           org id
     * @param topicKey        主题关键
     * @param topicIndustryId 主题行业id
     * @return {@link List}<{@link TopicOutDTO.IndustryProblemListOut}>
     * @deprecated 2.1.4，使用接口替代 {@link TopicService#industryProblemList(java.lang.String, java.lang.String, int)}替代，将在后续版本移除
     */
    List<TopicOutDTO.IndustryProblemListOut> industryProblemList(String orgId, String topicKey, long topicIndustryId);

    /**
     * 行业问题列表
     *
     * @param orgId           org id
     * @param topicKey        主题关键
     * @param industryType 行业类型
     * @return {@link List}<{@link TopicOutDTO.IndustryProblemListOut}>
     */
    List<TopicOutDTO.IndustryProblemListOut> industryProblemList(String orgId, String topicKey, int industryType);

    /**
     * 获取默认的行业ID
     * @return  行业ID
     */
    Long getDefaultTopicLevelId();

    /**
     * 根据AI问题类型ID列表获取问题类型信息
     * @param aiProblemTypeIds  AI问题类型ID列表（UDA）
     * @return  问题类型信息
     */
    List<AIProblemTypeMappingOutDTO> fetchProblemInfos(Collection<String> aiProblemTypeIds);

    Map<String, String> getIndustryMappings();

    /**
     * 选择行业问题列表
     *
     * @param topicProblemIdCollection 主题问题id集合
     * @return {@link List}<{@link TopicOutDTO.IndustryProblemListOut}>
     */
    List<TopicOutDTO.IndustryProblemListOut> selectIndustryProblemList(Collection<Long> topicProblemIdCollection);
}
