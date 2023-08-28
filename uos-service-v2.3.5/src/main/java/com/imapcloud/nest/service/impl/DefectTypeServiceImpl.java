package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.model.DefectInfoEntity;
import com.imapcloud.nest.model.DefectTypeEntity;
import com.imapcloud.nest.mapper.DefectTypeMapper;
import com.imapcloud.nest.service.DefectInfoService;
import com.imapcloud.nest.service.DefectTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
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
 * @since 2021-02-23
 */
@Service
public class DefectTypeServiceImpl extends ServiceImpl<DefectTypeMapper, DefectTypeEntity> implements DefectTypeService {

    @Resource
    DefectInfoService defectInfoService;

    @Override
    public RestRes saveOrUpdateDefectType(DefectTypeEntity defectTypeEntity) {
        if (defectTypeEntity.getId() != null) {
            // 编辑
            this.updateById(defectTypeEntity);
        } else {
            // 新增
            DefectTypeEntity oldDefectTypeEntity = this.getOne(new QueryWrapper<DefectTypeEntity>().eq("deleted", false).eq("name", defectTypeEntity.getName()));
            if (ToolUtil.isEmpty(oldDefectTypeEntity)) {
                this.save(defectTypeEntity);
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_DEFECT_NAME_ALREADY_EXISTS_AND_CANNOT_BE_REPEATED.getContent()));
            }

        }
        return RestRes.ok();
    }

    @Override
    public RestRes getAllList(String name) {
        List<DefectTypeEntity> defectTypeEntityList = baseMapper.getAllListByUnitId(name);
        Map<String, Object> map = new HashMap<>(2);
        map.put("list", defectTypeEntityList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes deleteDefectType(List<Integer> idList) {
        List<Integer> removeIdList = new ArrayList<>();
        List<String> failNameList  = new ArrayList<>();
        idList.forEach(e -> {
            List<DefectInfoEntity> defectInfoEntityList = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("deleted", false).eq("type_id", e));
            if (ToolUtil.isEmpty(defectInfoEntityList)) {
                removeIdList.add(e);
            } else {
                DefectTypeEntity defectTypeEntity = this.getOne(new QueryWrapper<DefectTypeEntity>().eq("id", e));
                failNameList.add(defectTypeEntity.getName());
            }
        });
        if (removeIdList.size() > 0) {
            this.removeByIds(removeIdList);
        }
        if (failNameList.size() > 0) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_IN_USE_CANNOT_BE_DELETED.getContent()));
        }
        return RestRes.ok();
    }
}
