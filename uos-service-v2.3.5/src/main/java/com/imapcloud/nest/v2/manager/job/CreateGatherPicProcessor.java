package com.imapcloud.nest.v2.manager.job;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.common.enums.DataAnalysisPicPushStatusEnum;
import com.imapcloud.nest.v2.common.enums.DataAnalysisPicStatusEnum;
import com.imapcloud.nest.v2.common.utils.AuditUtils;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisDetailMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkOutPO;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import com.imapcloud.nest.v2.service.DataAnalysisMarkService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisMarkConverter;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkDrawInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailDrawOutDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName CreateGatherPicProcessor.java
 * @Description CreateGatherPicProcessor
 * @createTime 2022年07月26日 09:11:00
 */
@Component
public class CreateGatherPicProcessor implements BasicProcessor {

    @Resource
    private DataAnalysisMarkService dataAnalysisMarkService;

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private DataAnalysisDetailMapper dataAnalysisDetailMapper;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        // 在线日志功能，可以直接在控制台查看任务日志，非常便捷
        OmsLogger omsLogger = context.getOmsLogger();
        omsLogger.info("BasicProcessorDemo start to process, current JobParams is {}.", context.getJobParams());

        //已核实、但却未生成
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(DataAnalysisCenterDetailEntity.class)
                .eq(DataAnalysisCenterDetailEntity::getPushState, DataAnalysisPicPushStatusEnum.VERIFY.getType())
                .eq(DataAnalysisCenterDetailEntity::getPhotoState, DataAnalysisPicStatusEnum.PROBLEM.getType())
                .and(item -> item.eq(DataAnalysisCenterDetailEntity::getImageMarkPath, "")
                        .or()
                        .eq(DataAnalysisCenterDetailEntity::getImageMarkPath, null))
                .select(DataAnalysisCenterDetailEntity::getCenterDetailId);
        List<Long> detailIds = ((List<DataAnalysisCenterDetailEntity>) this.dataAnalysisDetailMapper.selectList(queryWrapper)).stream()
                .map(DataAnalysisCenterDetailEntity::getCenterDetailId)
                .collect(Collectors.toList());
        if(CollectionUtil.isEmpty(detailIds)){
            omsLogger.info("没有数据需要生成");
            return new ProcessResult(true, "创建汇总图片成功");
        }
        List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS = this.dataAnalysisMarkMapper.queryDetailMark(detailIds);
        this.dataAnalysisDetailService.fillTopicData(dataAnalysisDetailMarkOutPOS);
        List<DataAnalysisMarkDrawInDTO> dataAnalysisMarkDrawInDTOS = dataAnalysisDetailMarkOutPOS.stream().map(DataAnalysisMarkConverter.INSTANCES::convertDraw).collect(Collectors.toList());
        omsLogger.info("【CreateGatherPicProcessor】【process】 {}", dataAnalysisDetailMarkOutPOS.toString());
        if (CollectionUtil.isNotEmpty(dataAnalysisMarkDrawInDTOS)) {

            Map<Long, List<DataAnalysisMarkDrawInDTO>> detailToDrawMap = dataAnalysisMarkDrawInDTOS.stream()
                    .collect(Collectors.groupingBy(DataAnalysisMarkDrawInDTO::getDetailId));

            detailToDrawMap.forEach((k, v) -> {
                DataAnalysisDetailDrawOutDTO outDTO = this.dataAnalysisMarkService.drawAllMarkPic(v);
                //回写数据
                DataAnalysisCenterDetailEntity detailEntity = new DataAnalysisCenterDetailEntity();
                detailEntity.setId(v.get(0).getDetailIndexId());
                detailEntity.setImageMarkPath(outDTO.getImageMarkPath());
                detailEntity.setThumImageMarkPath(outDTO.getThumImageMarkPath());
                detailEntity.setModifierId(AuditUtils.getAudit());
                this.dataAnalysisDetailMapper.updateById(detailEntity);
            });

        }

        return new ProcessResult(true, "创建汇总图片成功");
    }
}