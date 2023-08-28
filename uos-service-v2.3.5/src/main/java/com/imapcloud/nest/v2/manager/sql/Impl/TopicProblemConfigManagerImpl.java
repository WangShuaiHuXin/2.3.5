package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.core.util.BizIdUtils;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisAlgoProblemTypeRefEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisTopicProblemEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisAlgoProblemTypeRefMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisTopicProblemMapper;
import com.imapcloud.nest.v2.manager.sql.TopicProblemConfigManager;
import com.imapcloud.nest.v2.service.dto.in.TopicProblemConfigInDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicProblemConfigOutDTO;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 专题
 *
 * @author boluo
 * @date 2022-07-14
 */
@Service
@Slf4j
public class TopicProblemConfigManagerImpl implements TopicProblemConfigManager {

    @Resource
    private DataAnalysisTopicProblemMapper dataAnalysisTopicProblemMapper;

    @Resource
    private DataAnalysisAlgoProblemTypeRefMapper dataAnalysisAlgoProblemTypeRefMapper;

    /**
     * source字段系统默认
     */
    private final String commonProject = "-1";

    @Resource
    private TopicProblemConfigManager topicProblemConfigManager;

    @Override
    public IPage<TopicProblemConfigOutDTO.IndustryProblemListOut> getIndustryProblemListOutIPage(
            TopicProblemConfigInDTO.TopicProblemConfigQueryIn in, String orgCode) {
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                .eq(StringUtil.isNotEmpty(in.getIndustryType()),
                        DataAnalysisTopicProblemEntity::getIndustryType, in.getIndustryType())
                .eq(StringUtil.isNotEmpty(in.getSource()),
                        DataAnalysisTopicProblemEntity::getSource, in.getSource())
                .likeRight(StringUtil.isNotEmpty(in.getTopicProblemName()),
                        DataAnalysisTopicProblemEntity::getTopicProblemName, in.getTopicProblemName())
                .likeRight(StringUtil.isNotEmpty(in.getSource()) && "0".equals(in.getSource())
                        , DataAnalysisTopicProblemEntity::getOrgCode, orgCode)
                .and(StringUtil.isEmpty(in.getSource()), a -> {
                    a.eq(DataAnalysisTopicProblemEntity::getSource, commonProject)
                            .or().likeRight(DataAnalysisTopicProblemEntity::getOrgCode, orgCode);
                }).orderByDesc(DataAnalysisTopicProblemEntity::getModifiedTime);

        IPage<TopicProblemConfigOutDTO.IndustryProblemListOut> dataAnalysisTopicProblemPageList = dataAnalysisTopicProblemMapper.selectPage(
                new Page(in.getPageNo(), in.getPageSize() == 0 ? 10 : in.getPageSize())
                , wrapper).convert(e1 -> {
            TopicProblemConfigOutDTO.IndustryProblemListOut industryProblemListOut =
                    new TopicProblemConfigOutDTO.IndustryProblemListOut();
            BeanUtil.copyProperties(e1, industryProblemListOut);
            industryProblemListOut.setIndustryType(((DataAnalysisTopicProblemEntity) e1).getIndustryType() != null ? ((DataAnalysisTopicProblemEntity) e1).getIndustryType().toString() : "");
            return industryProblemListOut;
        });
        return dataAnalysisTopicProblemPageList;
    }

    @Override
    public List<String> getRepeatUdARelationIdList(List<String> idList, String functionId) {
        LambdaQueryWrapper<DataAnalysisAlgoProblemTypeRefEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisAlgoProblemTypeRefEntity.class)
                .in(DataAnalysisAlgoProblemTypeRefEntity::getTypeRelationId, idList)
                .eq(DataAnalysisAlgoProblemTypeRefEntity::getFunctionId,functionId)
                .select(DataAnalysisAlgoProblemTypeRefEntity::getTypeRelationId);
        return dataAnalysisAlgoProblemTypeRefMapper.selectList(wrapper).stream().distinct()
                .map(DataAnalysisAlgoProblemTypeRefEntity::getTypeRelationId).collect(Collectors.toList());
    }

    @Override
    public void checkRepeatUdARelationList(List<String> idList, String exceptProblemId) {
        LambdaQueryWrapper<DataAnalysisAlgoProblemTypeRefEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisAlgoProblemTypeRefEntity.class)
                .in(DataAnalysisAlgoProblemTypeRefEntity::getTypeRelationId, idList)
                .select(DataAnalysisAlgoProblemTypeRefEntity::getTypeRelationId,
                        DataAnalysisAlgoProblemTypeRefEntity::getTypeId,
                        DataAnalysisAlgoProblemTypeRefEntity::getTypeRelationName);
        List<String> list = dataAnalysisAlgoProblemTypeRefMapper.selectList(wrapper).stream().distinct()
                .filter(f -> {
                    return exceptProblemId == null ? true : !f.getTypeId().toString().equals(exceptProblemId);
                }).map(DataAnalysisAlgoProblemTypeRefEntity::getTypeRelationName).collect(Collectors.toList());
        if (list.size() > 0) {
            throw new BusinessException("操作失败，" + String.join("/", list) + "已存在配置关系");
        }
    }

    @Override
    public Boolean addIndustryProblemBySystem(TopicProblemConfigOutDTO.AddIndustryProblemOut addIndustryProblemReq) {
        DataAnalysisTopicProblemEntity d = new DataAnalysisTopicProblemEntity();
        BeanUtil.copyProperties(addIndustryProblemReq, d);
        Long id = BizIdUtils.snowflakeId();
        d.setTopicProblemId(id);
        d.setTopicKey(RoleIdenValueEnum.ANALYSIS_STATISTICS_ZH.getRoleKey());
        List<TopicProblemConfigOutDTO.AddIndustryProblemOut.UDAProblemTypeOut> tagList =
                addIndustryProblemReq.getAssociatedFunctionTagList();
        if (tagList != null && tagList.size() > 0) {
            List<String> idList = tagList.stream()
                    .map(TopicProblemConfigOutDTO.AddIndustryProblemOut.UDAProblemTypeOut::getTypeRelationId)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(idList)) {
                topicProblemConfigManager.checkRepeatUdARelationList(idList, null);
            }

            List<DataAnalysisAlgoProblemTypeRefEntity> entityList = new ArrayList<>();
            if (addIndustryProblemReq.getAssociatedFunctionTagList() != null) {
                addIndustryProblemReq.getAssociatedFunctionTagList().stream().forEach(e -> {
                    DataAnalysisAlgoProblemTypeRefEntity entity = new DataAnalysisAlgoProblemTypeRefEntity();
                    BeanUtil.copyProperties(e, entity);
                    entity.setTypeId(id);
                    entityList.add(entity);
                });
                dataAnalysisAlgoProblemTypeRefMapper.saveBatch(entityList);
            }
        }
        return dataAnalysisTopicProblemMapper.insert(d) > 0;
    }


    @Override
    public Boolean addIndustryProblemByOrg(TopicProblemConfigOutDTO.AddIndustryProblemOut addIndustryProblemReq) {
        DataAnalysisTopicProblemEntity d = new DataAnalysisTopicProblemEntity();
        BeanUtil.copyProperties(addIndustryProblemReq, d);
        Long id = BizIdUtils.snowflakeId();
        d.setTopicProblemId(id);
        d.setOrgCode(addIndustryProblemReq.getOrgCode());
        d.setTopicKey(RoleIdenValueEnum.ANALYSIS_STATISTICS_ZH.getRoleKey());
        dataAnalysisTopicProblemMapper.insert(d);
        return true;
    }

    @Override
    public Boolean editIndustryProblemBySystem(TopicProblemConfigOutDTO.EditIndustryProblemOut editIndustryProblemReq) {
        DataAnalysisTopicProblemEntity d = new DataAnalysisTopicProblemEntity();
        BeanUtil.copyProperties(editIndustryProblemReq, d);
        // 不能修改数据来源
        d.setSource(null);
        d.setTopicKey(RoleIdenValueEnum.ANALYSIS_STATISTICS_ZH.getRoleKey());
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> queryWrapper = Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                .eq(DataAnalysisTopicProblemEntity::getTopicProblemId, editIndustryProblemReq.getTopicProblemId());
        dataAnalysisTopicProblemMapper.update(d, queryWrapper);
        LambdaQueryWrapper<DataAnalysisAlgoProblemTypeRefEntity> queryWrapper1 = Wrappers.lambdaQuery(DataAnalysisAlgoProblemTypeRefEntity.class)
                .eq(DataAnalysisAlgoProblemTypeRefEntity::getTypeId, editIndustryProblemReq.getTopicProblemId());
        dataAnalysisAlgoProblemTypeRefMapper.delete(queryWrapper1);
        List<TopicProblemConfigOutDTO.EditIndustryProblemOut.UDAProblemTypeOut> tagList =
                editIndustryProblemReq.getAssociatedFunctionTagList();
        if (tagList != null && tagList.size() > 0) {
            List<DataAnalysisAlgoProblemTypeRefEntity> entityList = new ArrayList<>();
            List<String> idList = tagList.stream()
                    .map(TopicProblemConfigOutDTO.EditIndustryProblemOut.UDAProblemTypeOut::getTypeRelationId)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(idList)) {
                topicProblemConfigManager.checkRepeatUdARelationList(idList, editIndustryProblemReq.getTopicProblemId());
            }
            tagList.stream().forEach(e -> {
                DataAnalysisAlgoProblemTypeRefEntity entity = new DataAnalysisAlgoProblemTypeRefEntity();
                BeanUtil.copyProperties(e, entity);
                entity.setTypeId(Long.valueOf(editIndustryProblemReq.getTopicProblemId()));
                entityList.add(entity);
            });
            dataAnalysisAlgoProblemTypeRefMapper.saveBatch(entityList);
        }
        return true;
    }

    @Override
    public Boolean editIndustryProblemByOrg(TopicProblemConfigOutDTO.EditIndustryProblemOut editIndustryProblemReq) {
        DataAnalysisTopicProblemEntity d = new DataAnalysisTopicProblemEntity();
        BeanUtil.copyProperties(editIndustryProblemReq, d);
        // 不能修改数据来源
        d.setSource(null);
        if (StringUtil.isNotEmpty(editIndustryProblemReq.getOrgCode())) {
            d.setOrgCode(editIndustryProblemReq.getOrgCode());
        }
        d.setTopicKey(RoleIdenValueEnum.ANALYSIS_STATISTICS_ZH.getRoleKey());
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> queryWrapper1 = Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                .eq(DataAnalysisTopicProblemEntity::getTopicProblemId, editIndustryProblemReq.getTopicProblemId());
        dataAnalysisTopicProblemMapper.update(d, queryWrapper1);
        return true;
    }

    @Override
    public Boolean delProblemType(List<String> topicProblemIdList) {
        //删除条件
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> queryWrapper1 = Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                .in(DataAnalysisTopicProblemEntity::getTopicProblemId, topicProblemIdList);
        LambdaQueryWrapper<DataAnalysisAlgoProblemTypeRefEntity> queryWrapper2 = Wrappers.lambdaQuery(DataAnalysisAlgoProblemTypeRefEntity.class)
                .in(DataAnalysisAlgoProblemTypeRefEntity::getTypeId, topicProblemIdList);
        dataAnalysisAlgoProblemTypeRefMapper.delete(queryWrapper2);
        return dataAnalysisTopicProblemMapper.delete(queryWrapper1) > 0;
    }

    @Override
    public void checkTopicProblemName(TopicProblemConfigOutDTO.EditIndustryProblemOut editIndustryProblemIn) {
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> queryWrapper1 =
                Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                        .eq(DataAnalysisTopicProblemEntity::getTopicProblemName, editIndustryProblemIn.getTopicProblemName())
                        .eq(DataAnalysisTopicProblemEntity::getIndustryType, editIndustryProblemIn.getIndustryType())
                        .eq(DataAnalysisTopicProblemEntity::getSource, editIndustryProblemIn.getSource())
                        .eq(StringUtil.isNotEmpty(editIndustryProblemIn.getOrgCode())
                                , DataAnalysisTopicProblemEntity::getOrgCode, editIndustryProblemIn.getOrgCode())
                        .select(DataAnalysisTopicProblemEntity::getTopicProblemId);
        List<DataAnalysisTopicProblemEntity> dtpList = dataAnalysisTopicProblemMapper.selectList(queryWrapper1);
        // 查库相同名称只有1条，且等于当前修改问题类型ID,认为不是重复，否则就是重复
        if (dtpList.size() == 1 && dtpList.get(0).getTopicProblemId().equals(Long.valueOf(editIndustryProblemIn.getTopicProblemId()))) {
            // 正常修改
            log.info("正常修改");
        } else if (dtpList.size() == 0) {
            // 正常修改
            log.info("正常修改");
        } else {
            if (editIndustryProblemIn.getSource().equals(0)) {
                throw new BusinessException("该问题类型在所属单位内已存在");
            } else {
                throw new BusinessException("已存在系统默认的问题类型");
            }
        }
    }

    @Override
    public void checkTopicProblemName(TopicProblemConfigOutDTO.AddIndustryProblemOut addIndustryProblemIn) {
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> queryWrapper1 =
                Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                        .eq(DataAnalysisTopicProblemEntity::getTopicProblemName, addIndustryProblemIn.getTopicProblemName())
                        .eq(DataAnalysisTopicProblemEntity::getIndustryType, addIndustryProblemIn.getIndustryType())
                        .eq(DataAnalysisTopicProblemEntity::getSource, addIndustryProblemIn.getSource())
                        .eq(StringUtil.isNotEmpty(addIndustryProblemIn.getOrgCode())
                                , DataAnalysisTopicProblemEntity::getOrgCode, addIndustryProblemIn.getOrgCode())
                        .select(DataAnalysisTopicProblemEntity::getTopicProblemId);
        List<DataAnalysisTopicProblemEntity> dtpList = dataAnalysisTopicProblemMapper.selectList(queryWrapper1);
        // 查库相同名称大于1条，可认为重复名称
        if (dtpList.size() > 0) {
            if (addIndustryProblemIn.getSource().equals(0)) {
                throw new BusinessException("该问题类型在所属单位内已存在");
            } else {
                throw new BusinessException("已存在系统默认的问题类型");
            }
        }
    }
}
