package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.v2.common.enums.DataAnalysisPicStatusEnum;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisDetailMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkNumOutPO;
import com.imapcloud.nest.v2.manager.event.WriteBackPicEvent;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName WriteBackPicListener.java
 * @Description WriteBackPicListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class WriteBackPicListener extends AbstractEventListener<WriteBackPicEvent> {

    @Resource
    private DataAnalysisDetailMapper dataAnalysisDetailMapper;

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    /**
     * 消息监听-处理
     *
     * @param writeBackPicEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(WriteBackPicEvent writeBackPicEvent) {
        log.info("【WriteBackPicEvent】-【WriteBackPicListener】事件:{}",writeBackPicEvent.toString());

        List<Long> markIds = writeBackPicEvent.getSource();
        //一键重置、删除，只有删除需要调整状态
        if(CollectionUtil.isNotEmpty(markIds) && Boolean.TRUE.equals(writeBackPicEvent.getFromMark())){
            List<DataAnalysisDetailMarkNumOutPO> markNumOutList = this.dataAnalysisDetailMapper.queryDetailMarkNum(markIds);
            List<Long> problemMarkNumList = markNumOutList.stream()
                            .filter(x->x.getDeleted()==0 && x.getMarkNum()>0)
                            .map(DataAnalysisDetailMarkNumOutPO::getDetailId)
                            .collect(Collectors.toList()),
                    noProblemMarkNumList = CollectionUtil.subtractToList(markNumOutList.stream()
                            .map(DataAnalysisDetailMarkNumOutPO::getDetailId).collect(Collectors.toList()),problemMarkNumList);
            //回写有问题
            this.dataAnalysisDetailService.writeBackPicState(problemMarkNumList, DataAnalysisPicStatusEnum.PROBLEM.getType(), null);
            //回写无问题
            this.dataAnalysisDetailService.writeBackPicState(noProblemMarkNumList, DataAnalysisPicStatusEnum.NO_PROBLEM.getType(), null);
        }
    }

}
