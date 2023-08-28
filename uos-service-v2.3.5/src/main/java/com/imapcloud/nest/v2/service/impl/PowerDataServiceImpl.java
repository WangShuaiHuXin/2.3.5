package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.RecDataTypeEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.PowerDiscernSettingEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.OrgAlgorithmQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerDiscernFunSettingInfosOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.SimpleDictItemInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAlgorithmRepoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.BaseServiceClient;
import com.imapcloud.nest.v2.manager.feign.UdaAnalysisServiceClient;
import com.imapcloud.nest.v2.manager.sql.PowerDataManager;
import com.imapcloud.nest.v2.service.PowerDataService;
import com.imapcloud.nest.v2.service.dto.out.AIAutoDiscernSettingsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DiscernFunctionSettingOutDTO;
import com.imapcloud.nest.v2.web.vo.req.AIDiscernFunctionInfoReqVO;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PowerDataServiceImpl implements PowerDataService {

    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private UdaAnalysisServiceClient udaAnalysisServiceClient;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private PowerDataManager powerDataManager;

    @Override
    public List<DiscernFunctionSettingOutDTO> getAllDiscernFunctionInfos(String orgCode) {
        //获取目前单位在UDA侧已授权的所有识别功能
        Result<List<UdaAlgorithmRepoOutDO>> result;
        try {
            OrgAlgorithmQueryInDO params = new OrgAlgorithmQueryInDO();
            params.setRecDataType(RecDataTypeEnum.IMAGE.getType());
            params.setOrgCode(orgCode);
            result = udaAnalysisServiceClient.getUdaGrantedFunctionsByOrg(params);
        } catch (Exception e) {
            log.error("获取uda算法识别功能列表失败", e);
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERDATASERVICEIMPL_001.getContent()));
        }
        //将获取结果转 functionId->q
        List<UdaAlgorithmRepoOutDO> data = result.getData();
        /*if (CollectionUtil.isEmpty(data)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERDATASERVICEIMPL_002.getContent()));
        }*/
        List<DiscernFunctionSettingOutDTO> dtoList = new ArrayList<>();
        List<UdaAlgorithmRepoOutDO.DiscernFunctionInfo> infos = new ArrayList<>();
        data.stream().forEach(e -> {
            List<UdaAlgorithmRepoOutDO.DiscernFunctionInfo> discernFunctionInfos = e.getDiscernFunctionInfos();
            if (CollectionUtil.isNotEmpty(discernFunctionInfos)) {
                infos.addAll(discernFunctionInfos);
            }
        });
        Map<String, UdaAlgorithmRepoOutDO.DiscernFunctionInfo> functionInfoMap = infos.stream().collect(Collectors.toMap(UdaAlgorithmRepoOutDO.DiscernFunctionInfo::getFunctionId, q -> q));
        //查询字典列表
        Result<List<SimpleDictItemInfoOutDO>> geoai_power_discern_type = baseServiceClient.listDictItemInfos("GEOAI_POWER_DISCERN_TYPE");
        List<SimpleDictItemInfoOutDO> discern_typeData = geoai_power_discern_type.getData();
        if (CollectionUtils.isEmpty(discern_typeData)) {
            throw new BusinessException("查询巡检类型失败，请稍后重试");
        }
        //查询当前单位的所有已开启的识别功能
        List<PowerDiscernFunSettingInfosOutDO> dos = powerDataManager.queryOrgFunctionSettings(orgCode, null);
        //查询单位下人员信息
        List<String> accountList = dos.stream().map(e -> e.getLastModifierId()).collect(Collectors.toList());
        Result<List<AccountOutDO>> listResult = accountServiceClient.listAccountInfos(accountList);
        List<AccountOutDO> accountData = listResult.getData();
        Map<String, AccountOutDO> accountOutDOMap = null;
        if (CollectionUtil.isNotEmpty(accountData)) {
            accountOutDOMap = accountData.stream().collect(Collectors.toMap(AccountOutDO::getAccountId, q -> q));
        }
        //按巡检类型为单位进行查询
        Map<Integer, List<PowerDiscernFunSettingInfosOutDO>> groupMap = dos.stream().collect(Collectors.groupingBy(PowerDiscernFunSettingInfosOutDO::getDiscernType));
        Map<String, AccountOutDO> finalAccountOutDOMap1 = accountOutDOMap;
        dtoList = discern_typeData.stream().map(item -> {
            DiscernFunctionSettingOutDTO dto = new DiscernFunctionSettingOutDTO();
            dto.setDiscernType(Integer.valueOf(item.getValue()));
            if (!CollectionUtils.isEmpty(dos)) {
                List<PowerDiscernFunSettingInfosOutDO> powerDiscernFunSettingInfosOutDOS = groupMap.get(Integer.valueOf(item.getValue()));
                if (!CollectionUtils.isEmpty(powerDiscernFunSettingInfosOutDOS)) {
                    List<DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO> collect = powerDiscernFunSettingInfosOutDOS.stream().map(powerDo -> {
                        DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO discernFunctionInfoOutDTO = new DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO();
                        discernFunctionInfoOutDTO.setFunctionId(powerDo.getDiscernFunId());
                        UdaAlgorithmRepoOutDO.DiscernFunctionInfo discernFunctionInfo = functionInfoMap.get(powerDo.getDiscernFunId());
                        if (ObjectUtils.isNotEmpty(discernFunctionInfo)) {
                            discernFunctionInfoOutDTO.setFunctionName(discernFunctionInfo.getName());
                            discernFunctionInfoOutDTO.setVersion(discernFunctionInfo.getVersion());
                        }
                        return discernFunctionInfoOutDTO;
                    }).collect(Collectors.toList());
                    dto.setDiscernFunctionInfos(collect);
                    dto.setLastModifierName("-");
                    if (ObjectUtils.isNotEmpty(powerDiscernFunSettingInfosOutDOS)) {
                        String lastModifierId = powerDiscernFunSettingInfosOutDOS.get(0).getLastModifierId();
                        dto.setLastModifierId(lastModifierId);
                        if (finalAccountOutDOMap1 != null) {
                            AccountOutDO accountOutDO = finalAccountOutDOMap1.get(lastModifierId);
                            if (accountOutDO != null) {
                                dto.setLastModifierName(accountOutDO.getName());
                            }
                        }
                        dto.setLastModifiedTime(powerDiscernFunSettingInfosOutDOS.get(0).getLastModifiedTime());
                    }
                }
            }
            return dto;
        }).collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public void modifyOrgAIAutoDiscernSettings(String orgCode) {
        PowerDiscernSettingEntity powerDiscernSettingEntity = powerDataManager.queryOrgDiscernFunctionSettings(orgCode);
        if (ObjectUtils.isEmpty(powerDiscernSettingEntity)) {
            //查询失败，则为新单位，需要插入
            powerDataManager.saveOrgDiscernSettings(orgCode,true);
            return;
           // throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERDATASERVICEIMPL_003.getContent()));
        }
        boolean flag = powerDiscernSettingEntity.getAutoDiscern() ? false : true;
        int i = powerDataManager.updateOrgDiscernFunctionSettings(orgCode, flag);
        if (i == 0) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERDATASERVICEIMPL_004.getContent()));
        }
    }

    @Override
    public AIAutoDiscernSettingsOutDTO getOrgAIAutoDiscernSettings(String orgCode) {
        PowerDiscernSettingEntity powerDiscernSettingEntity = powerDataManager.queryOrgDiscernFunctionSettings(orgCode);

        AIAutoDiscernSettingsOutDTO dto = new AIAutoDiscernSettingsOutDTO();
        dto.setLastModifierName(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERCONTROLLER_006.getContent()));
        if (ObjectUtils.isEmpty(powerDiscernSettingEntity)) {
            //查询失败，默认关闭
            dto.setEnabled(false);
            return dto;
        }
        dto.setEnabled(powerDiscernSettingEntity.getAutoDiscern());
        dto.setLastModifierId(powerDiscernSettingEntity.getModifierId());

        Result<List<AccountOutDO>> listResult = accountServiceClient.listAccountInfos(Lists.newArrayList(powerDiscernSettingEntity.getModifierId()));
        List<AccountOutDO> accountOutDOList = ResultUtils.getData(listResult);
        if(CollUtil.isNotEmpty(accountOutDOList)) {
            AccountOutDO accountOutDO = accountOutDOList.get(0);
            dto.setLastModifierName(accountOutDO.getName());
        }
        dto.setLastModifiedTime(powerDiscernSettingEntity.getModifiedTime());
        return dto;
    }

    @Override
    public List<DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO> getDiscernFunctionInfos(String orgCode, Integer discernType) {
        List<DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO> resultList = new ArrayList<>();
        List<PowerDiscernFunSettingInfosOutDO> dos = powerDataManager.queryOrgFunctionSettings(orgCode, String.valueOf(discernType));
        if (CollectionUtil.isEmpty(dos)) {
            return resultList;
        }
        //获取目前单位在UDA侧已授权的所有识别功能
        Result<List<UdaAlgorithmRepoOutDO>> result;
        try {
            OrgAlgorithmQueryInDO params = new OrgAlgorithmQueryInDO();
            params.setRecDataType(RecDataTypeEnum.IMAGE.getType());
            params.setOrgCode(orgCode);
            result = udaAnalysisServiceClient.getUdaGrantedFunctionsByOrg(params);
        } catch (Exception e) {
            log.error("获取算法识别功能列表失败", e);
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERDATASERVICEIMPL_001.getContent()));
        }
        //将获取结果转 functionId->q
        List<UdaAlgorithmRepoOutDO> data = result.getData();
        if (CollectionUtil.isEmpty(data)) {
            return resultList;
          //  throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERDATASERVICEIMPL_002.getContent()));
        }
        List<DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO> dtoList = null;
        List<UdaAlgorithmRepoOutDO.DiscernFunctionInfo> infos = new ArrayList<>();
        data.stream().forEach(e -> {
            List<UdaAlgorithmRepoOutDO.DiscernFunctionInfo> discernFunctionInfos = e.getDiscernFunctionInfos();
            if (CollectionUtil.isNotEmpty(discernFunctionInfos)) {
                infos.addAll(discernFunctionInfos);
            }
        });
        Map<String, UdaAlgorithmRepoOutDO.DiscernFunctionInfo> functionInfoMap = infos.stream().collect(Collectors.toMap(UdaAlgorithmRepoOutDO.DiscernFunctionInfo::getFunctionId, q -> q));
        dtoList = dos.stream().map(e -> {
            DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO dto = new DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO();
            dto.setFunctionId(e.getDiscernFunId());
            UdaAlgorithmRepoOutDO.DiscernFunctionInfo discernFunctionInfo = functionInfoMap.get(e.getDiscernFunId());
            if (ObjectUtils.isNotEmpty(discernFunctionInfo)) {
                dto.setFunctionName(discernFunctionInfo.getName());
                dto.setVersion(discernFunctionInfo.getVersion());
            }
            return dto;
        }).collect(Collectors.toList());
        return dtoList;
    }

    @Override
    @Transactional
    public void saveDiscernFunctionInfos(String orgCode, AIDiscernFunctionInfoReqVO body) {
        //每个巡检类型限制添加10个识别功能，提交时进行校验，超出限制则提示“单个巡检类型最多设置10个识别功能”
        //需要添加的识别功能
        List<String> discernFunctionIds = body.getDiscernFunctionIds();
        //先删除已有的配置
        powerDataManager.deleteOrgFunctionSettings(orgCode, String.valueOf(body.getDiscernType()));
        //插入新配置
        powerDataManager.saveOrgFunctionSettings(orgCode, body.getDiscernType(), discernFunctionIds);
    }
}
