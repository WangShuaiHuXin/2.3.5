package com.imapcloud.nest.v2.manager.job;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisDetailMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkOutPO;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import com.imapcloud.nest.v2.service.DataAnalysisMarkService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisMarkConverter;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkDrawInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkDrawOutDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName CreateSinglePicProcessor.java
 * @Description CreateSinglePicProcessor
 * @createTime 2022年07月26日 09:11:00
 */
@Component
public class CreateSinglePicProcessor implements BasicProcessor {

    @Resource
    private DataAnalysisMarkService dataAnalysisMarkService;

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    @Resource
    private DataAnalysisDetailMapper dataAnalysisDetailMapper;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        // 在线日志功能，可以直接在控制台查看任务日志，非常便捷
        OmsLogger omsLogger = context.getOmsLogger();
        omsLogger.info("BasicProcessorDemo start to process, current JobParams is {}.", context.getJobParams());

        List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS = this.dataAnalysisMarkMapper.queryDetailMarkForSinglePic();
        this.dataAnalysisDetailService.fillTopicData(dataAnalysisDetailMarkOutPOS);
        omsLogger.info("【CreateSinglePicProcessor】【process】 {}", dataAnalysisDetailMarkOutPOS.toString());
        List<DataAnalysisMarkDrawInDTO> dataAnalysisMarkDrawInDTOS = dataAnalysisDetailMarkOutPOS.stream().map(DataAnalysisMarkConverter.INSTANCES::convertDraw).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(dataAnalysisMarkDrawInDTOS)) {

            List<DataAnalysisMarkDrawOutDTO> outDTOs = this.dataAnalysisMarkService.drawSingleMarkPic(dataAnalysisMarkDrawInDTOS);
            List<DataAnalysisMarkEntity> markEntityList = outDTOs.stream().map(DataAnalysisMarkConverter.INSTANCES::convert).collect(Collectors.toList());
            markEntityList.stream().forEach(entity->{
                this.dataAnalysisMarkMapper.updateById(entity);
            });
        }

        return new ProcessResult(true, "创建批量标注图片成功");
    }
}