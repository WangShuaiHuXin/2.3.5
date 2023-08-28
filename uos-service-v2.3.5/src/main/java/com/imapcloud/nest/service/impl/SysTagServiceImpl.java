package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.base.Functions;
import com.imapcloud.nest.mapper.SysTagMapper;
import com.imapcloud.nest.model.DataProblemEntity;
import com.imapcloud.nest.model.SysTagEntity;
import com.imapcloud.nest.model.SysTaskTagEntity;
import com.imapcloud.nest.pojo.dto.SysTagDto;
import com.imapcloud.nest.pojo.dto.TaskTagDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.PhotoTagSourceDTO;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.TaskPhotoDTO;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.StationDefectPhotoDTO;
import com.imapcloud.nest.service.DataProblemService;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.service.SysTagService;
import com.imapcloud.nest.service.SysTaskTagService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.OrgAccountService;
import com.imapcloud.nest.v2.service.UosRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统标签表 服务实现类
 * </p>
 *
 * @author kings
 * @since 2020-12-03
 */
@Service
@Slf4j
public class SysTagServiceImpl extends ServiceImpl<SysTagMapper, SysTagEntity> implements SysTagService {

    @Autowired
    private SysTaskTagService sysTaskTagService;
    @Autowired
    protected MissionPhotoService missionPhotoService;
    @Autowired
    private DataProblemService dataProblemService;

    /**
     * 添加新的标签
     *
     * @param sysTagDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes addSysTag(SysTagDto sysTagDto) {
        List<SysTagEntity> sysTagEntityList = sysTagDto.getSysTagEntityList();
        String unitId = sysTagDto.getOrgCode();
        //为空，也就是删除所有的标签
        try {
            if (CollectionUtil.isEmpty(sysTagEntityList)) {
                //也就是删除所有的标签，直接硬删除吧，因为是属于关系的。跟别的订单的关系不同，一般实体的不删除
                //什么时候删除，要确定
                this.remove(new QueryWrapper<SysTagEntity>().eq("org_code", unitId));
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_EDIT.getContent()));
            }
            List<SysTagEntity> addSysTagEntity = sysTagEntityList.stream().filter(e -> e.getId() == null).collect(Collectors.toList());
            List<SysTagEntity> uptSysTagEntity = sysTagEntityList.stream().filter(e -> e.getId() != null).collect(Collectors.toList());
            if(addSysTagEntity.size()>0){
                int sysTagNameSize = addSysTagEntity.size();
                List<String> sysTagNames = addSysTagEntity.stream().map(SysTagEntity::getName).distinct().collect(Collectors.toList());
                if (sysTagNames.size() < sysTagNameSize) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_SAME_UNIT_TAG_NAME_REPEAT.getContent()));
                }
                if (uptSysTagEntity.size()>0) {
                    List<String> uptTagNames = uptSysTagEntity.stream().map(SysTagEntity::getName).collect(Collectors.toList());
                    for (SysTagEntity sysTagEntity:addSysTagEntity) {
                        if(uptTagNames.contains(sysTagEntity.getName())) {
                            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_SAME_UNIT_TAG_NAME_REPEAT.getContent()));
                        }
                    }
                }
            }
            /*String unitName = sysUnitService.getById(unitId).getName();
            List<SysTagEntity> sysTagEntities = sysTagEntityList.stream().filter(e->!unitName.equals(e.getName())).collect(Collectors.toList());*/
            /*int sysTagNameSize = sysTagEntityList.size();
            List<String> sysTagNames = sysTagEntityList.stream().map(SysTagEntity::getName).distinct().collect(Collectors.toList());*/
            int sysTagSize = sysTagEntityList.size();

            List<String> sysTagUnitIds = sysTagEntityList.stream().map(SysTagEntity::getOrgCode).distinct().collect(Collectors.toList());
            List<Integer> sysTagSeqs = sysTagEntityList.stream().map(SysTagEntity::getSeq).distinct().collect(Collectors.toList());

            //要新增的
            List<SysTagEntity> sysTagInsertEntities = sysTagEntityList.stream().filter(it -> it.getId() == null).distinct().collect(Collectors.toList());
            //编辑的；除了编辑的，就是删除的，能这样确定
            List<Integer> sysTagUpdateIds = sysTagEntityList.stream().filter(it -> it.getId() != null).map(it -> it.getId().intValue()).distinct().collect(Collectors.toList());
            List<Integer> sysTagDeleteIds = new ArrayList<>();
            List<SysTagEntity> oriSysTagList = this.list(new QueryWrapper<SysTagEntity>().eq("org_code", unitId));
            if (CollectionUtil.isNotEmpty(oriSysTagList)) {
                sysTagDeleteIds = oriSysTagList.stream().filter(it -> !sysTagUpdateIds.contains(it.getId().intValue())).map(it -> it.getId().intValue()).collect(Collectors.toList());
            }
            //下面可能有逻辑问题，但还没发现
            /*if (sysTagNames.size() < sysTagNameSize) {
                return RestRes.err("同单位标签名不能重复！");
            }*/
            if (sysTagUnitIds.size() > 1) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MUST_BE_SAME_UNIT_UNDER_THE_LABEL.getContent()));
            }
            if (sysTagSeqs.size() < sysTagSize) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_ORDER_REPEAT.getContent()));
            }

            sysTagEntityList.forEach(it -> {
                if (it.getId() != null) {
                    this.updateById(it);
                }
            });
//            // 校验该标签下是否已有数据，有则不删除
//            List<Integer> notUseTagIdList = new ArrayList<>();
//            List<Integer> isUseTagIdList = new ArrayList<>();
//            if (CollectionUtil.isNotEmpty(sysTagDeleteIds)) {
//                sysTagDeleteIds.forEach(e -> {
//                    Integer tagUseNum = baseMapper.getTagUseNum(e);
//                    if (tagUseNum > 0) {
//                        isUseTagIdList.add(e);
//                    } else {
//                        notUseTagIdList.add(e);
//                    }
//                });
//            }

            if (CollectionUtil.isNotEmpty(sysTagDeleteIds)) {
                this.remove(new QueryWrapper<SysTagEntity>().in("id", sysTagDeleteIds));
                sysTaskTagService.remove(new QueryWrapper<SysTaskTagEntity>().in("tag_id", sysTagDeleteIds));
            }
            if (CollectionUtil.isNotEmpty(sysTagInsertEntities)) {
                sysTagInsertEntities.forEach(it -> it.setOrgCode(unitId));
                this.saveBatch(sysTagInsertEntities);
            }
        } catch (Exception e) {
            log.error("内部错误：{}", e);
            throw e;
        }
        //这里还要做当前用户选择接管的机巢，要在sysUserNest进行操作
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_ADD_TAG.getContent()));
    }

    @Resource
    private UosRoleService roleService;

    @Resource
    private OrgAccountService orgAccountService;

    @Override
    public RestRes getAllTagList(Integer defectStatus, String startTime, String endTime, List<Integer> types, Integer flag) {

        // 如果不是超级管理员，则查询出用户所属单位的数据即可
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        List<SysTagEntity> tagList = baseMapper.getAllTagListByUnitId(orgCode);

        Integer type = types.get(0);
        tagList.forEach(it -> {

            List<StationDefectPhotoDTO> infoList = missionPhotoService.getAllStationDefectPhotoDTO1(type,it.getId(), startTime, endTime,defectStatus);
            /*Long count = infoList.stream().filter(e -> e.getTagId() == it.getId().intValue()).count();
            //infoList.contains(it.getId().intValue());*/
            if (CollectionUtils.isEmpty(infoList)){
                it.setHasMissionPhoto(false);
                it.setExceptions(0l);
                return;
            }
            it.setHasMissionPhoto(infoList.size() > 0);
            Long exceptions = (long) infoList.size();
            if (flag == null) {
                if (type == 0||type==31||type==33) {
                    exceptions = infoList.stream().filter(e ->e.getStatus()!=null && (2 == e.getStatus())).count();
                }else if (type == 1){
                    //exceptions = infoList.stream().filter(e ->e.getFlag()!=null &&  1 == e.getFlag()).count();
                    exceptions = infoList.stream().filter(e ->e.getStatus()!=null &&  2 == e.getStatus()).count();
                }else if (type == 2||type==32||type==34){
                    exceptions = infoList.stream().filter(e ->e.getFlagInfrared()!=null && 1 == e.getFlagInfrared()).count();
                }
            }
            it.setExceptions(exceptions);
        });
        //剔除没有图片的
        Iterator<SysTagEntity> iterator = tagList.iterator();
        while (iterator.hasNext()) {
            SysTagEntity tmp = iterator.next();
            if(tmp.getHasMissionPhoto() == null || !tmp.getHasMissionPhoto()) {
                iterator.remove();
            }
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("list", tagList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes getAllDataTagList(Integer problemStatus, String startTime, String endTime, List<Integer> problemSourceList, Integer flag) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        List<SysTagEntity> tagList = baseMapper.getAllTagListByUnitId(orgCode);

        if (flag == null || flag == 0) {
            // 分析中台， 查出全部照片
            tagList.forEach(tag -> {
                 List<PhotoTagSourceDTO> photoTagSourceList = missionPhotoService.getPhotoTagSourceList(problemStatus, startTime, endTime, problemSourceList, tag.getId().intValue());
                Long count = ToolUtil.isNotEmpty(photoTagSourceList) ? photoTagSourceList.size() : 0L;
                tag.setHasMissionPhoto(count > 0);
                if (ToolUtil.isNotEmpty(photoTagSourceList)) {
                    List<Long> photoIdList = photoTagSourceList.stream().map(PhotoTagSourceDTO::getPhotoId).collect(Collectors.toList());
                    Long exceptions = Long.valueOf(dataProblemService.list(new QueryWrapper<DataProblemEntity>().in("data_id", photoIdList)
                            .in("problem_source", problemSourceList).eq("flag", 1).eq("deleted", 0)).size());
                    tag.setExceptions(exceptions);
                }
            });
        } else {
        	if(problemSourceList.contains(0) || problemSourceList.contains(1) 
        			|| problemSourceList.contains(2)) {
        		Map<Long,SysTagEntity> maps = tagList.stream().collect(Collectors.toMap(SysTagEntity::getId, Functions.identity()));
        		for(Integer problemSource: problemSourceList) {
        			RestRes res =  getAllTagList(2, startTime, endTime, Arrays.asList(problemSource) ,null);
        			Map<String, Object> map = res.getParam();
            		tagList = (List<SysTagEntity>) map.get("list");
            		tagList.forEach(tag -> {
            			SysTagEntity tmp = maps.get(tag.getId());
            			Long exceptions = tmp.getExceptions() == null ? 0:tmp.getExceptions();
            			tmp.setExceptions(exceptions + tag.getExceptions());
            			if(tmp.getExceptions() > 0)
            				tmp.setHasMissionPhoto(true);
            			else
            				tmp.setHasMissionPhoto(false);
            		});
        		}
        		tagList = maps.values().stream().collect(Collectors.toList());
        		Iterator<SysTagEntity> iterator = tagList.iterator();
        		while (iterator.hasNext()){
                    SysTagEntity tmp = iterator.next();
        		    if(tmp.getHasMissionPhoto() == null || !tmp.getHasMissionPhoto())
        		        iterator.remove();
                }
        	}
        	else {
	            // 分析应用，查出有问题的列表
	            tagList.forEach(tag -> {
	                List<TaskPhotoDTO> illegalProblemList = dataProblemService.getTagProblemList(tag.getId().intValue(), problemSourceList, startTime, endTime);
	                Long count = ToolUtil.isNotEmpty(illegalProblemList) ? illegalProblemList.size() : 0L;
	                tag.setExceptions(count);
	                tag.setHasMissionPhoto(count > 0);
	            });
        	}
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("list", tagList);
        return RestRes.ok(map);
    }

    @Override
    public List<SysTagEntity> getAllTagByUser() {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        return baseMapper.getAllTagListByUnitId(orgCode);
    }

    @Override
    public List<Integer> getTagIdListByUnitId(String unitId) {
        return this.getTagIdsByOrgCode(unitId);
    }

    @Override
    public List<SysTaskTagEntity> getListByTagId(Long id, String nestId, String orgCode) {
        return baseMapper.getListByTagId(id, nestId, orgCode);
    }

    @Override
    public List<SysTagEntity> getAllTagListByUnitId(Integer unitId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer> selectMissionByType(Integer dataType) {
        return baseMapper.selectMissionByType(dataType);
    }

    @Override
    public Optional<String> findUnitIdByTagId(Integer tagId) {
        SysTagMapper baseMapper = this.getBaseMapper();
        LambdaQueryWrapper<SysTagEntity> condition = Wrappers.lambdaQuery(SysTagEntity.class)
                .eq(SysTagEntity::getId, tagId);
        SysTagEntity sysTagEntity = baseMapper.selectOne(condition);
        if(Objects.nonNull(sysTagEntity)){
            return Optional.ofNullable(sysTagEntity.getOrgCode());
        }
        return Optional.empty();
    }

    @Override
    public List<SysTagEntity> getAllTagsByOrgCode(String orgCode) {
        SysTagMapper baseMapper = this.getBaseMapper();
        LambdaQueryWrapper<SysTagEntity> condition = Wrappers.lambdaQuery(SysTagEntity.class)
                .eq(SysTagEntity::getOrgCode, orgCode);
        return baseMapper.selectList(condition);
    }

    @Override
    public List<Integer> getTagIdsByOrgCode(String orgCode) {
        List<SysTagEntity> entities = getAllTagsByOrgCode(orgCode);
        if(!CollectionUtils.isEmpty(entities)){
            return entities.stream()
                    .map(SysTagEntity::getId)
                    .map(Long::intValue)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<TaskTagDTO> listTagNameByTaskIds(List<Integer> taskIdList) {
        if(!CollectionUtils.isEmpty(taskIdList)) {
            return baseMapper.batchSelectByTaskIds(taskIdList);
        }
        return Collections.emptyList();
    }

}
