package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.v2.common.utils.AuditUtils;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisDetailMapper;
import com.imapcloud.nest.v2.manager.event.ConfirmPicEvent;
import com.imapcloud.nest.v2.service.DataAnalysisMarkService;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkDrawInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailDrawOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName CreateGatherPicListener.java
 * @Description CreateGatherPicListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class CreateGatherPicListener extends AbstractEventListener<ConfirmPicEvent> {

    @Resource
    private DataAnalysisMarkService dataAnalysisMarkService;

    @Resource
    private DataAnalysisDetailMapper dataAnalysisDetailMapper;

    /**
     * 消息监听-处理
     *
     * @param confirmPicEvent 消息事件
     */
    @Override
    @Async("dataAnalysisExecutor")
    @EventListener
    public void eventListener(ConfirmPicEvent confirmPicEvent) {
        log.info("【ConfirmPicEvent】-【CreateGatherPicListener】事件:{}",confirmPicEvent.toString());

        List<DataAnalysisMarkDrawInDTO> dataAnalysisMarkDrawInDTOS = confirmPicEvent.getSource();

        if(CollectionUtil.isNotEmpty(dataAnalysisMarkDrawInDTOS)){

            Map<Long,List<DataAnalysisMarkDrawInDTO>> detailToDrawMap = dataAnalysisMarkDrawInDTOS.stream()
                    .collect(Collectors.groupingBy(DataAnalysisMarkDrawInDTO::getDetailId));

            detailToDrawMap.forEach((k, v)->{
                DataAnalysisDetailDrawOutDTO outDTO = this.dataAnalysisMarkService.drawAllMarkPic(v);
                //回写数据
                DataAnalysisCenterDetailEntity detailEntity = new DataAnalysisCenterDetailEntity();
                detailEntity.setId(v.get(0).getDetailIndexId());
//                detailEntity.setCenterDetailId(k);
                detailEntity.setImageMarkPath(outDTO.getImageMarkPath());
                detailEntity.setThumImageMarkPath(outDTO.getThumImageMarkPath());
                detailEntity.setModifierId(AuditUtils.getAudit());
                this.dataAnalysisDetailMapper.updateById(detailEntity);
            });

        }
    }

}
