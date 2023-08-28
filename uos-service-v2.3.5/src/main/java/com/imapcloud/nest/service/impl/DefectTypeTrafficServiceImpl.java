package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.enums.StationIdentifyRecordDefectContentEnum;
import com.imapcloud.nest.mapper.DefectTypeTrafficMapper;
import com.imapcloud.nest.model.DefectInfoEntity;
import com.imapcloud.nest.model.DefectTypeTrafficEntity;
import com.imapcloud.nest.model.SysTagEntity;
import com.imapcloud.nest.service.DefectInfoService;
import com.imapcloud.nest.service.DefectTypeTrafficService;
import com.imapcloud.nest.service.SysTagService;
import com.imapcloud.nest.utils.PinYinUtil;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 */
@Service
public class DefectTypeTrafficServiceImpl extends ServiceImpl<DefectTypeTrafficMapper, DefectTypeTrafficEntity> implements DefectTypeTrafficService {

    @Resource
    DefectInfoService defectInfoService;
    @Autowired
    SysTagService sysTagService;

    @Override
    public RestRes saveOrUpdateDefectType(DefectTypeTrafficEntity defectTypeTrafficEntity) {

        //效验
        if (defectTypeTrafficEntity.getOrgCode() == null || defectTypeTrafficEntity.getType() == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }

        // 给问题类型定义code
        String name = defectTypeTrafficEntity.getName();
        if (StationIdentifyRecordDefectContentEnum.GJBS.getDesc().equals(name)) {
            defectTypeTrafficEntity.setCode(StationIdentifyRecordDefectContentEnum.GJBS.getCode());
        } else if (StationIdentifyRecordDefectContentEnum.YWCL.getDesc().equals(name)) {
            defectTypeTrafficEntity.setCode(StationIdentifyRecordDefectContentEnum.YWCL.getCode());
        } else if (StationIdentifyRecordDefectContentEnum.HDPFW.getDesc().equals(name)) {
            defectTypeTrafficEntity.setCode(StationIdentifyRecordDefectContentEnum.HDPFW.getCode());
        } else if (StationIdentifyRecordDefectContentEnum.NORMAL.getDesc().equals(name)) {
            defectTypeTrafficEntity.setCode(StationIdentifyRecordDefectContentEnum.NORMAL.getCode());
        } else if (StationIdentifyRecordDefectContentEnum.PERSON.getDesc().equals(name)) {
            defectTypeTrafficEntity.setCode(StationIdentifyRecordDefectContentEnum.PERSON.getCode());
        } else if (StationIdentifyRecordDefectContentEnum.GARBAGE.getDesc().equals(name)) {
            defectTypeTrafficEntity.setCode(StationIdentifyRecordDefectContentEnum.GARBAGE.getCode());
        } else if (StationIdentifyRecordDefectContentEnum.BOAT.getDesc().equals(name)) {
            defectTypeTrafficEntity.setCode(StationIdentifyRecordDefectContentEnum.BOAT.getCode());
        } else if (StationIdentifyRecordDefectContentEnum.DFC.getDesc().equals(name)) {
            defectTypeTrafficEntity.setCode(StationIdentifyRecordDefectContentEnum.DFC.getCode());
        } else if (StationIdentifyRecordDefectContentEnum.CAR.getDesc().equals(name)) {
            defectTypeTrafficEntity.setCode(StationIdentifyRecordDefectContentEnum.CAR.getCode());
        }else {
            defectTypeTrafficEntity.setCode(PinYinUtil.getPingYin(name));
        }

        defectTypeTrafficEntity.setModifyTime(LocalDateTime.now());
        if (defectTypeTrafficEntity.getId() != null) {
            // 编辑
            this.updateById(defectTypeTrafficEntity);
        } else {
            // 新增
            defectTypeTrafficEntity.setCreateTime(LocalDateTime.now());
            DefectTypeTrafficEntity oldDefectTypeTrafficEntity = this.getOne(new QueryWrapper<DefectTypeTrafficEntity>()
                    .eq("deleted", false)
                    .eq("name", defectTypeTrafficEntity.getName())
                    .eq("type", defectTypeTrafficEntity.getType())
                    .eq("org_code", defectTypeTrafficEntity.getOrgCode()));
            if (ToolUtil.isEmpty(oldDefectTypeTrafficEntity)) {
                this.save(defectTypeTrafficEntity);
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_QUESTION_NAME.getContent()));
            }

        }
        return RestRes.ok();
    }

    @Override
    public RestRes getAllList(String name, String orgCode, Integer type,Integer tagId) {
        //效验
        if (type == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        if(tagId!=null) {
            SysTagEntity sysTagEntity = sysTagService.getById(tagId);
            orgCode = sysTagEntity.getOrgCode();
        }
        List<DefectTypeTrafficEntity> allList = baseMapper.getAllList(name, orgCode, type);
        Map<String, Object> map = new HashMap<>(2);
        map.put("list", allList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes deleteDefectType(List<Integer> idList) {
        List<Integer> removeIdList = new ArrayList<>();
        List<String> failNameList = new ArrayList<>();
        idList.forEach(e -> {
            List<DefectInfoEntity> defectInfoEntityList = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("deleted", false).eq("type_id", e));
            if (ToolUtil.isEmpty(defectInfoEntityList)) {
                removeIdList.add(e);
            } else {
                DefectTypeTrafficEntity defectTypeTrafficEntity = this.getOne(new QueryWrapper<DefectTypeTrafficEntity>().eq("id", e));
                failNameList.add(defectTypeTrafficEntity.getName());
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
