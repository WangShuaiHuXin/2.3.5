package com.imapcloud.nest.v2.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.CellExtra;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.sql.PowerEquipmentLegerInfoManager;
import com.imapcloud.nest.v2.service.dto.out.PowerEquipmentDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class PowerEquipmentUploadListener extends AnalysisEventListener<PowerEquipmentDTO.PowerEquipmentFileDTO> {


    //单次最大读取数
    public final int MAX_LINE = 1000;
    //最大长度
    public final int MAX_LENGTH = 50;
    //数据集合
    public List<PowerEquipmentDTO.PowerEquipmentFileDTO> objList = new ArrayList<>();

    @Override
    public void invoke(PowerEquipmentDTO.PowerEquipmentFileDTO powerEquipmentFileDTO, AnalysisContext analysisContext) {
        Integer approximateTotalRowNumber = analysisContext.readSheetHolder().getApproximateTotalRowNumber();
        //包含了表头，所以这里是1001
        if (approximateTotalRowNumber > MAX_LINE + 1) {
            throw new ExcelAnalysisException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_001.getContent()));
        }
        //校验是否存在空行
        if (powerEquipmentFileDTO == null) {
            log.info(analysisContext.getCurrentRowNum() + "存在空行");
            throw new ExcelAnalysisException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_002.getContent()));
        }
        //校验是否存在空值
        if (StringUtils.isAnyEmpty(powerEquipmentFileDTO.getEquipmentName(), powerEquipmentFileDTO.getEquipmentType(),
                powerEquipmentFileDTO.getPmsId(), powerEquipmentFileDTO.getSpacingUnitName(),
                powerEquipmentFileDTO.getSubstationName(), powerEquipmentFileDTO.getVoltageLevel())) {
            log.info(analysisContext.getCurrentRowNum() + "行,存在空值");
            throw new ExcelAnalysisException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_002.getContent()));
        }
        //校验是否存在非文本数据
        //校验是否存在数据大小大于50个字符
        if (StringUtils.length(powerEquipmentFileDTO.getEquipmentName()) > MAX_LENGTH ||
                StringUtils.length(powerEquipmentFileDTO.getEquipmentType()) > MAX_LENGTH ||
                StringUtils.length(powerEquipmentFileDTO.getPmsId()) > MAX_LENGTH ||
                StringUtils.length(powerEquipmentFileDTO.getSpacingUnitName()) > MAX_LENGTH ||
                StringUtils.length(powerEquipmentFileDTO.getSubstationName()) > MAX_LENGTH ||
                StringUtils.length(powerEquipmentFileDTO.getVoltageLevel()) > MAX_LENGTH
        ) {
            log.info(analysisContext.getCurrentRowNum() + "行,字符长度过长");
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_003.getContent()));
        }
        //统计数据
        objList.add(powerEquipmentFileDTO);
    }

    //校验表头
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if (!headMap.containsKey(0) || !headMap.containsKey(1) || !headMap.containsKey(2) || !headMap.containsKey(3)
                || !headMap.containsKey(4) || !headMap.containsKey(5) || !headMap.get(0).equals("设备名称") || !headMap.get(1).equals("设备类型")
                || !headMap.get(2).equals("间隔单元") || !headMap.get(3).equals("电压等级") || !headMap.get(4).equals("变电站") || !headMap.get(5).equals("PMS_ID")) {
            throw new ExcelAnalysisException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_004.getContent()));
        }
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //检查是否有重复的数据
        Set<String> setList = objList.stream().map(e -> e.getPmsId()).collect(Collectors.toSet());
        if (setList.size() != objList.size()) {
            throw new ExcelAnalysisException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTUPLOADLISTENER_005.getContent()));
        }
    }

    public List<PowerEquipmentDTO.PowerEquipmentFileDTO> getObjList() {
        return objList;
    }
}
