package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.utils.AuditUtils;
import com.imapcloud.nest.v2.manager.event.ConfirmPicToResultEvent;
import com.imapcloud.nest.v2.service.DataAnalysisResultGroupService;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import com.imapcloud.nest.v2.service.GridService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisMarkConverter;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkDrawInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultInDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PushResultListener.java
 * @Description PushResultListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class PushResultListener extends AbstractEventListener<ConfirmPicToResultEvent> {

    @Resource
    private DataAnalysisResultService dataAnalysisResultService;

    @Resource
    private DataAnalysisResultGroupService dataAnalysisResultGroupService;

    @Resource
    private GridService gridService;

    /**
     * 推送结果消息
     *
     * @param confirmPicEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(ConfirmPicToResultEvent confirmPicEvent) {
        log.info("【ConfirmPicToResultEvent】-【PushResultListener】事件:{}", confirmPicEvent.toString());
        List<DataAnalysisMarkDrawInDTO> dataAnalysisMarkDrawInDTOS = confirmPicEvent.getSource();
        List<DataAnalysisResultInDTO.InsertInfoIn> insertInfoInList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(dataAnalysisMarkDrawInDTOS)) {
            insertInfoInList = dataAnalysisMarkDrawInDTOS.stream()
                    .map(DataAnalysisMarkConverter.INSTANCES::convertForResult)
                    .collect(Collectors.toList());
            insertInfoInList = insertInfoInList.stream()
                    .filter(x -> this.filterData(x))
                    .collect(Collectors.toList());
            log.info("【ConfirmPicToResultEvent】-【PushResultListener】：{}", insertInfoInList.toString());
            try {
//                DataAnalysisResultInDTO.InsertInfoIn insertInfoIn = insertInfoInList.get(0);
                //将最大、最小经纬度查找出来，然后以该值为条件，查询出网格，然后将网格与问题一一匹配
//                String gridManageId = gridService.findGridIdByLngAndLat(insertInfoIn.getLongitude(), insertInfoIn.getLatitude(), insertInfoIn.getMissionRecordsId());
//                log.info("gridManageId:{}", gridManageId);
//                if (Objects.nonNull(gridManageId)) {
//                    insertInfoInList.forEach(info -> info.setGridManageId(gridManageId));
//                }

                log.info("insertInfoInList:{}", JSONArray.toJSONString(insertInfoInList));
                //插入结果表
                this.dataAnalysisResultService.batchInsert(insertInfoInList, AuditUtils.getAudit());
                //插入结果分组表
            } catch (BusinessException e) {
                log.error("【分析中心】推送【结果】，失败：", e);
            }
        }

    }

    /**
     * @return
     */
    public boolean filterData(DataAnalysisResultInDTO.InsertInfoIn insertInfoIns) {
        if (StringUtils.isEmpty(insertInfoIns.getImagePath())) {
            return false;
        }
        if (StringUtils.isEmpty(insertInfoIns.getThumImagePath())) {
            return false;
        }
        if (StringUtils.isEmpty(insertInfoIns.getResultImagePath())) {
            return false;
        }
        return true;
    }

}
