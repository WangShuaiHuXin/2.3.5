package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.common.enums.RecDataTypeEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAlgorithmRepoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.UdaAnalysisServiceClient;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.manager.sql.DataAnalysisAlgoProblemTypeRefManager;
import com.imapcloud.nest.v2.manager.sql.TopicProblemConfigManager;
import com.imapcloud.nest.v2.service.TopicProblemConfigService;
import com.imapcloud.nest.v2.service.TopicService;
import com.imapcloud.nest.v2.service.dto.in.TopicProblemConfigInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisAlgoProblemTypeRefOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicProblemConfigOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicUDAProblemTypeConfigOutDTO;
import com.imapcloud.nest.v2.web.vo.req.TopicProblemConfigReqVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 专题
 *
 * @author boluo
 * @date 2022-07-14
 */
@Service
public class TopicProblemConfigServiceImpl implements TopicProblemConfigService {
    @Resource
    private TopicProblemConfigManager topicProblemConfigManager;

    @Resource
    private UosOrgManager uosOrgManager;

    @Resource
    private DataAnalysisAlgoProblemTypeRefManager dataAnalysisAlgoProblemTypeRefManager;

    @Resource
    private UdaAnalysisServiceClient udaAnalysisServiceClient;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private TopicService topicService;



    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean editIndustryProblem(TopicProblemConfigOutDTO.EditIndustryProblemOut editIndustryProblemIn) {
        // 校验名称重复
        topicProblemConfigManager.checkTopicProblemName(editIndustryProblemIn);
        // 查询指定行业下的问题
        if (editIndustryProblemIn.getSource() == -1) {
            return topicProblemConfigManager.editIndustryProblemBySystem(editIndustryProblemIn);
        } else {
            return topicProblemConfigManager.editIndustryProblemByOrg(editIndustryProblemIn);
        }
    }

    @Override
    public IPage<TopicProblemConfigOutDTO.IndustryProblemListOut> industryProblemList(TopicProblemConfigInDTO.TopicProblemConfigQueryIn inDto) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        // 查询指定行业下的问题
        IPage<TopicProblemConfigOutDTO.IndustryProblemListOut> dataAnalysisTopicProblemPageList =
                topicProblemConfigManager.getIndustryProblemListOutIPage(inDto, orgCode);
        // 获取操作人名称
        List<String> accountIdList = dataAnalysisTopicProblemPageList.getRecords().stream()
                .map(TopicProblemConfigOutDTO.IndustryProblemListOut::getModifierId).collect(Collectors.toList());
        Result<List<AccountOutDO>> accountResult = accountServiceClient.listAccountInfos(accountIdList);
        Map<String, String> accountMap = accountResult.getData().stream().collect(Collectors.toMap(AccountOutDO::getAccountId, d -> {
            String name = d.getName();
            if (StrUtil.isEmpty(name)) {
                name = d.getAccount();
            }
            return name;
        }));
        // 获取orgName
        List<String> orgCodeIdList = dataAnalysisTopicProblemPageList.getRecords().stream()
                .map(TopicProblemConfigOutDTO.IndustryProblemListOut::getOrgCode).collect(Collectors.toList());
        List<OrgSimpleOutDO> orgInfos = uosOrgManager.listOrgInfos(orgCodeIdList);
        if (!CollectionUtils.isEmpty(orgInfos)) {
            Map<String, String> map = orgInfos.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
            dataAnalysisTopicProblemPageList.getRecords().forEach(r -> r.setOrgName(map.get(r.getOrgCode())));
        }
        // 查询行业字典数据
        Map<String, String> industryMappings = topicService.getIndustryMappings();
        List<String> topicProblemIdList = dataAnalysisTopicProblemPageList.getRecords().stream()
                .map(TopicProblemConfigOutDTO.IndustryProblemListOut::getTopicProblemId).collect(Collectors.toList());
        List<DataAnalysisAlgoProblemTypeRefOutDTO> refs = new ArrayList<>();
        Map<String, List<DataAnalysisAlgoProblemTypeRefOutDTO>> refsMapList = new HashMap<>();
        if (!CollectionUtils.isEmpty(topicProblemIdList)) {
            // 获取uda识别共功能数据
            refs = dataAnalysisAlgoProblemTypeRefManager
                    .getDataAnalysisAlgoProblemTypeRefOutDTO(topicProblemIdList);

            if (!CollectionUtils.isEmpty(refs)) {
                refs.stream().forEach(e -> {
                    List<DataAnalysisAlgoProblemTypeRefOutDTO> dd = refsMapList.get(e.getTypeId().toString());
                    if (dd == null) {
                        dd = new ArrayList<>();
                    }
                    dd.add(e);
                    refsMapList.put(e.getTypeId().toString(), dd);
                });
            }
        }

        Map<String, String> orgMap = new HashMap<>();

        if (orgInfos != null) {
            orgMap = orgInfos.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
        }

        Map<String, String> finalOrgMap = orgMap;
        dataAnalysisTopicProblemPageList.getRecords().forEach(r -> {
            if (!CollectionUtils.isEmpty(industryMappings) && industryMappings.get(r.getIndustryType()) != null) {
                r.setTopicIndustryName(industryMappings.get(r.getIndustryType()));
            } else {
                r.setTopicIndustryName("-");
            }
            if (!CollectionUtils.isEmpty(finalOrgMap) && finalOrgMap.get(r.getOrgCode()) != null) {
                r.setOrgName(finalOrgMap.get(r.getOrgCode()));
            } else {
                r.setOrgName("-");
            }
            if (!CollectionUtils.isEmpty(accountMap) && accountMap.get(r.getModifierId()) != null) {
                r.setModifierId(accountMap.get(r.getModifierId()));
            } else {
                r.setModifierId("-");
            }
            if (!CollectionUtils.isEmpty(refsMapList) && refsMapList.get(r.getTopicProblemId()) != null) {
                List<DataAnalysisAlgoProblemTypeRefOutDTO> dd = refsMapList.get(r.getTopicProblemId());
                List<TopicProblemConfigOutDTO.IndustryProblemListOut.ProblemTypeConfigUDAOut> tips = new ArrayList<>();
                List<String> associatedFunctionTag = new ArrayList<>();
                dd.stream().forEach(d -> {
                    TopicProblemConfigOutDTO.IndustryProblemListOut.ProblemTypeConfigUDAOut tip =
                            new TopicProblemConfigOutDTO.IndustryProblemListOut.ProblemTypeConfigUDAOut();
                    tip.setStorageId(d.getStorageId());
                    tip.setFunctionId(d.getFunctionId());
                    tip.setTypeRelationId(d.getTypeRelationId());
                    tip.setStorageName(d.storageName);
                    tip.setFunctionName(d.functionName);
                    tip.setTypeRelationName(d.typeRelationName);
                    tips.add(tip);
                    List<String> a = new ArrayList<>();
                    a.add(d.storageName);
                    a.add(d.functionName);
                    a.add(d.typeRelationName);
                    associatedFunctionTag.add(a.stream().collect(Collectors.joining("/")));
                });
                r.setAssociatedFunctionTagList(tips);
                r.setAssociatedFunctionTag(associatedFunctionTag.stream().collect(Collectors.joining(";")));
            }
        });
        return dataAnalysisTopicProblemPageList;
    }

    @Override
    public List<TopicProblemConfigReqVO.UDAProblemTypeReq> getUdaProblemTypeReqs(String typeString, String storageId, String functionId) {
        List<TopicProblemConfigReqVO.UDAProblemTypeReq> tu = new ArrayList<>();
        if ("storage".equals(typeString)) {
            Result<List<UdaAlgorithmRepoOutDO>> result = udaAnalysisServiceClient.getUdaGrantedFunctions(RecDataTypeEnum.IMAGE.getType());
            if (!result.isOk()) {
                throw new BusinessException("获取行业场景信息失败");
            }
            List<UdaAlgorithmRepoOutDO> data = result.getData();
            if (CollectionUtils.isEmpty(data)) {
                return tu;
            }
            tu = data.stream().map(e -> {
                TopicProblemConfigReqVO.UDAProblemTypeReq u = new TopicProblemConfigReqVO.UDAProblemTypeReq();
                u.setId(e.getStorageId());
                u.setName(e.getName());
                u.setStatus(1);
                return u;
            }).collect(Collectors.toList());
        } else if ("function".equals(typeString)) {
            Result<List<UdaAlgorithmRepoOutDO>> result = udaAnalysisServiceClient.getUdaGrantedFunctions(RecDataTypeEnum.IMAGE.getType());
            if (!result.isOk()) {
                throw new BusinessException("获取行业场景信息失败");
            }
            List<UdaAlgorithmRepoOutDO> data = result.getData();
            if (CollectionUtils.isEmpty(data)) {
                return tu;
            }
            List<TopicProblemConfigReqVO.UDAProblemTypeReq> finalTu = tu;
            data.stream().forEach(e -> {
                if (e.getStorageId().equals(storageId)) {
                    e.getDiscernFunctionInfos().stream().forEach(m -> {
                        TopicProblemConfigReqVO.UDAProblemTypeReq u = new TopicProblemConfigReqVO.UDAProblemTypeReq();
                        u.setId(m.getFunctionId());
                        u.setName(m.getName());
                        u.setStatus(1);
                        finalTu.add(u);
                    });
                }
            });
            tu = finalTu;
        } else {
            // typeString = “problemType”
            Result<List<TopicUDAProblemTypeConfigOutDTO>> result =
                    udaAnalysisServiceClient.queryUDAProblemTypeByFunction(functionId);
            if (!result.isOk()) {
                throw new BusinessException("获取uda问题类型信息失败");
            }
            List<TopicUDAProblemTypeConfigOutDTO> data = result.getData();
            if (CollectionUtils.isEmpty(data)) {
                return tu;
            }
            List<String> idList = data.stream().map(TopicUDAProblemTypeConfigOutDTO::getId).collect(Collectors.toList());
            List<String> repeatUdARelationIdList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(idList)) {
                repeatUdARelationIdList = topicProblemConfigManager.getRepeatUdARelationIdList(idList,functionId);
            }
            List<String> finalRepeatUdARelationIdList = repeatUdARelationIdList;
            tu = data.stream().map(e -> {
                        TopicProblemConfigReqVO.UDAProblemTypeReq u = new TopicProblemConfigReqVO.UDAProblemTypeReq();
                        u.setId(e.getId());
                        u.setName(e.getName());
                        u.setStatus(finalRepeatUdARelationIdList.contains(e.getId()) ? 0 : 1);
                        return u;
                    }
            ).collect(Collectors.toList());
        }
        return tu;
    }

    @Override
    @Transactional
    public Boolean addIndustryProblem(TopicProblemConfigOutDTO.AddIndustryProblemOut addIndustryProblemReq) {
        // 校验名称重复
        topicProblemConfigManager.checkTopicProblemName(addIndustryProblemReq);
        if (addIndustryProblemReq.getSource() == -1) {
            return topicProblemConfigManager.addIndustryProblemBySystem(addIndustryProblemReq);
        } else {
            return topicProblemConfigManager.addIndustryProblemByOrg(addIndustryProblemReq);
        }
    }

    @Override
    public Boolean delProblemType(List<String> topicProblemIdList) {
        return topicProblemConfigManager.delProblemType(topicProblemIdList);
    }
}
