package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.imapcloud.nest.convert.ElectronicFenceDTOToEntityConvert;
import com.imapcloud.nest.convert.ElectronicFenceVOToEntityConvert;
import com.imapcloud.nest.enums.ElectronicFenceEnum;
import com.imapcloud.nest.mapper.ElectronicFenceMapper;
import com.imapcloud.nest.model.ElectronicFenceEntity;
import com.imapcloud.nest.pojo.dto.ElectronicFenceDTO;
import com.imapcloud.nest.pojo.vo.ElectronicFenceVO;
import com.imapcloud.nest.service.ElectronicFenceService;
import com.imapcloud.nest.service.SysUnitService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-09-26
 */
@Service
public class ElectronicFenceServiceImpl extends ServiceImpl<ElectronicFenceMapper, ElectronicFenceEntity> implements ElectronicFenceService {

    @Resource
    private SysUnitService sysUnitService;

    /**
     * 获取电子围栏信息
     * @param orgId
     * @param name
     * @param containsChild
     * @return
     */
    @Override
    public RestRes selectAllList(String orgId, String name ,Integer containsChild) {
        List<ElectronicFenceEntity> list = baseMapper.selectAllList(orgId, name, containsChild);
//        List<ElectronicFenceVO> returnList = ElectronicFenceVOToEntityConvert.INSTANCES.doToDto(list);
        List<ElectronicFenceVO> returnList = Lists.newLinkedList();
        for (ElectronicFenceEntity electronicFenceEntity : list) {
            ElectronicFenceVO electronicFenceVO = new ElectronicFenceVO();
            electronicFenceVO.setId(electronicFenceEntity.getId());
            electronicFenceVO.setUnitId(electronicFenceEntity.getOrgCode());
            electronicFenceVO.setName(electronicFenceEntity.getName());
            electronicFenceVO.setState(electronicFenceEntity.getState());
            electronicFenceVO.setType(electronicFenceEntity.getType());
            electronicFenceVO.setBufferRange(electronicFenceEntity.getBufferRange());
            electronicFenceVO.setCoordinate(electronicFenceEntity.getCoordinate());
            electronicFenceVO.setHeight(electronicFenceEntity.getHeight());
            electronicFenceVO.setCreateUserId(electronicFenceEntity.getCreateUserId());
            electronicFenceVO.setCreateTime(electronicFenceEntity.getCreateTime());
            electronicFenceVO.setModifyTime(electronicFenceEntity.getCreateTime());
            electronicFenceVO.setDeleted(electronicFenceEntity.getDeleted());
            returnList.add(electronicFenceVO);
        }
        Map map = new HashMap(2);
        map.put("list", returnList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes addElectronicFence(ElectronicFenceDTO electronicFenceDTO) {
        ElectronicFenceEntity electronicFenceEntity = ElectronicFenceDTOToEntityConvert.INSTANCES.dtoToDo(electronicFenceDTO);
        electronicFenceEntity.setOrgCode(electronicFenceDTO.getUnitId());
        this.save(electronicFenceEntity);
        Map map = new HashMap(2);
        map.put("id", electronicFenceEntity.getId());
        return RestRes.ok(map);
    }

    @Override
    public RestRes updateElectronicFence(ElectronicFenceDTO electronicFenceDTO) {
        ElectronicFenceEntity electronicFenceEntity = ElectronicFenceDTOToEntityConvert.INSTANCES.dtoToDo(electronicFenceDTO);
        electronicFenceEntity.setOrgCode(electronicFenceDTO.getUnitId());
        baseMapper.updateById(electronicFenceEntity);
        return RestRes.ok();
    }

    @Override
    public RestRes updateState(Integer id, Integer state) {
        this.lambdaUpdate().eq(ElectronicFenceEntity::getId, id).set(ElectronicFenceEntity::getState, state).update();
        return RestRes.ok();
    }

    @Override
    public RestRes deleteElectronicFence(List<Integer> ids) {
        baseMapper.deleteBatchIds(ids);
        return RestRes.ok();
    }

}
