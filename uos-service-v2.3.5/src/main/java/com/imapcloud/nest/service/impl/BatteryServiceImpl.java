package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.mapper.BatteryMapper;
import com.imapcloud.nest.model.BatteryEntity;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.dto.BatteryUseNumsDto;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.BatteryService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.ExcelUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.NestOrgRefService;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestOrgRefOutDTO;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 电池信息表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Service("batteryService")
public class BatteryServiceImpl extends ServiceImpl<BatteryMapper, BatteryEntity> implements BatteryService {

    @Resource
    private NestService nestService;

    @Resource
    private NestOrgRefService nestOrgRefService;

    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private BaseNestService baseNestService;

    @Override
    public void exportBatteryUseNums(String nestId, HttpServletResponse response){
        if (nestId != null) {
            List<String> unitNameList = getNestOrgNames(nestId);
            StringBuilder unitNameListStr = new StringBuilder();
            if (unitNameList != null && unitNameList.size() > 0){
                for (int i = 0; i < unitNameList.size(); i++) {
                    String unitName = unitNameList.get(i);
                    unitNameListStr.append(unitName);
                    if (i != unitNameList.size() - 1){
                        unitNameListStr.append(",");
                        unitNameListStr.append("\n");
                    }
                }
            }

//            NestEntity nestEntity = nestService.lambdaQuery()
//                    .eq(NestEntity::getId, nestId)
//                    .select(NestEntity::getUuid, NestEntity::getType)
//                    .one();
//            Assert.isNull(nestEntity, "未查询到基站");

            BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
            Assert.isNull(baseNestInfo, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_NOT_QUERIED.getContent()));

            List<BatteryUseNumsDto> batteryUseNums = commonNestStateService.getBatteryChargeCount(baseNestInfo.getUuid(), baseNestInfo.getType());


            List<List<String>> returnList = new ArrayList<>();
            //设置表头
            List<String> rowList = new ArrayList<>();
            rowList.add("序号");
            rowList.add("机巢地址");
            rowList.add("单位");
            rowList.add("设备类型");
            rowList.add("设备编号");
            rowList.add("基站名称");
            rowList.add("电池循环次数");
            rowList.add("RTK剩余天数");
            returnList.add(rowList);
            for (int num = 0; num < batteryUseNums.size(); num++){
                BatteryUseNumsDto dto = batteryUseNums.get(num);
                List<String> newRowList = new ArrayList<>();
                //newRowList.add(Integer.toString(num) + 1);//序号
                newRowList.add(dto.getBatteryNum());//序号
                newRowList.add(baseNestInfo.getAddress() == null?"":baseNestInfo.getAddress());//机巢地址
                newRowList.add(unitNameListStr.toString());//单位
                newRowList.add(NestTypeEnum.getInstance(baseNestInfo.getType()).name());//设备类型
                newRowList.add(baseNestInfo.getNumber() == null?"":baseNestInfo.getNumber());//设备编号
                newRowList.add(baseNestInfo.getName() == null?"":baseNestInfo.getName());//基站名称
                newRowList.add(dto.getCharges() == null?"": String.valueOf(dto.getCharges()));//电池循环次数

                newRowList.add("");//TODO RTK剩余天数 暂时置空
                returnList.add(newRowList);
            }
            //导出标记信息
            try {
                ExcelUtil.exportExcel(response, returnList.get(0), returnList, "电池信息", null);
            } catch (Exception e) {
                log.error("导出电池信息时发生错误", e);
                Assert.failure(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_WHEN_EXPORTING_BATTERY_INFORMATION.getContent()));
            }
        }
    }

    private List<String> getNestOrgNames(String nestId) {
        List<NestOrgRefOutDTO> noRefs = nestOrgRefService.listNestOrgRefs(Collections.singletonList(nestId), true);
        if(!CollectionUtils.isEmpty(noRefs)){
            return noRefs.stream()
                    .map(NestOrgRefOutDTO::getOrgName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
