package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisDetailMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkOutPO;
import com.imapcloud.nest.v2.manager.event.ConfirmPicEvent;
import com.imapcloud.nest.v2.manager.event.ConfirmPicToResultEvent;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import com.imapcloud.nest.v2.service.DataAnalysisMarkService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisMarkConverter;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkDrawInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkDrawOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName CreateSinglePicListener.java
 * @Description CreateSinglePicListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class CreateSinglePicListener extends AbstractEventListener<ConfirmPicEvent> {

    @Resource
    private DataAnalysisMarkService dataAnalysisMarkService;

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    @Resource
    private ApplicationContext applicationContext;

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
        log.info("【ConfirmPicEvent】-【CreateSinglePicListener】事件:{}", confirmPicEvent.toString());

        List<DataAnalysisMarkDrawInDTO> dataAnalysisMarkDrawInDTOS = confirmPicEvent.getSource();

        if (CollectionUtil.isNotEmpty(dataAnalysisMarkDrawInDTOS)) {
            // 查询图片信息
            LambdaQueryWrapper<DataAnalysisCenterDetailEntity> queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                    .eq(DataAnalysisCenterDetailEntity::getCenterDetailId, dataAnalysisMarkDrawInDTOS.get(0).getDetailId());
            List<DataAnalysisCenterDetailEntity> entityList = dataAnalysisDetailMapper.selectList(queryWrapper);
            // 获取图片时间
            LocalDateTime photoCreateTime = null;
            if (CollUtil.isNotEmpty(entityList)) {
                photoCreateTime = entityList.get(0).getPhotoCreateTime();
            }
            if (photoCreateTime == null) {
                photoCreateTime = LocalDateTime.now();
            }
            for (DataAnalysisMarkDrawInDTO dataAnalysisMarkDrawInDTO : dataAnalysisMarkDrawInDTOS) {
                dataAnalysisMarkDrawInDTO.setPhotoCreateTime(photoCreateTime);
            }

            List<DataAnalysisMarkDrawOutDTO> outDTOs = this.dataAnalysisMarkService.drawSingleMarkPic(dataAnalysisMarkDrawInDTOS);
            List<DataAnalysisMarkEntity> markEntityList = outDTOs.stream().map(DataAnalysisMarkConverter.INSTANCES::convert).collect(Collectors.toList());
            markEntityList.stream().forEach(entity->{
                this.dataAnalysisMarkMapper.updateById(entity);
            });

            List<Long> detailIds = dataAnalysisMarkDrawInDTOS.stream().map(DataAnalysisMarkDrawInDTO::getDetailId).collect(Collectors.toList());
            List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS = this.dataAnalysisMarkMapper.queryDetailMark(detailIds);
            this.dataAnalysisDetailService.fillTopicData(dataAnalysisDetailMarkOutPOS);
            List<DataAnalysisMarkDrawInDTO> newDrawInDTOs = dataAnalysisDetailMarkOutPOS.stream().map(DataAnalysisMarkConverter.INSTANCES::convertDraw).collect(Collectors.toList());
            this.applicationContext.publishEvent(new ConfirmPicToResultEvent(newDrawInDTOs));
        }
    }

}
