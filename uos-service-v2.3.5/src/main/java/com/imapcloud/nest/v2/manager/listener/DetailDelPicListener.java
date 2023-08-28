package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.v2.common.utils.FileUtils;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisDetailMapper;
import com.imapcloud.nest.v2.manager.event.DetailDelEvent;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DetailDelPicListener.java
 * @Description DetailDelPicListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class DetailDelPicListener extends AbstractEventListener<DetailDelEvent> {

    @Resource
    private DataAnalysisDetailMapper dataAnalysisDetailMapper;

    @Resource
    private FileManager fileManager;

    /**
     * 消息监听-处理
     *
     * @param detailDelEvent 消息事件
     */
    @Override
    @Async("dataAnalysisExecutor")
    @EventListener
    public void eventListener(DetailDelEvent detailDelEvent) {
        log.info("【DetailDelEvent】-【DetailDelPicListener】事件:{}",detailDelEvent.toString());

        List<Long> detailIds = detailDelEvent.getSource();
        List<DataAnalysisCenterDetailEntity> detailEntities = this.dataAnalysisDetailMapper.queryDeleteDataById(detailIds);

        if(CollectionUtil.isNotEmpty(detailEntities)){
            List<String> imagePathList = detailEntities.stream()
                            .map(DataAnalysisCenterDetailEntity::getImagePath)
                            .filter(StringUtil::isNotEmpty)
                            .collect(Collectors.toList())
                    ,thumImagePathList = detailEntities.stream()
                            .map(DataAnalysisCenterDetailEntity::getThumImagePath)
                            .filter(StringUtil::isNotEmpty)
                            .collect(Collectors.toList())
                    ,imageMarkPathList = detailEntities.stream()
                            .map(DataAnalysisCenterDetailEntity::getImageMarkPath)
                            .filter(StringUtil::isNotEmpty)
                            .collect(Collectors.toList())
                    ,thumImageMarkPathList = detailEntities.stream()
                            .map(DataAnalysisCenterDetailEntity::getThumImageMarkPath)
                            .filter(StringUtil::isNotEmpty)
                            .collect(Collectors.toList());

            List<Long> detailList = detailEntities.stream()
                        .map(DataAnalysisCenterDetailEntity::getCenterDetailId)
                        .collect(Collectors.toList());
            deleteFile(detailList,imagePathList,thumImagePathList,imageMarkPathList,thumImageMarkPathList);
        }
    }

    /**
     *
     * @param imagePathList
     * @param thumImagePathList
     * @param thumImageMarkPathList
     */
    public void deleteFile(List<Long> detailList , List<String> imagePathList , List<String> thumImagePathList,List<String> imageMarkPathList, List<String> thumImageMarkPathList){
        //删除数据，讲究实时记录日志，不采用debug
        log.info("【DetailDelEvent】-【DetailDelPicListener】 -【deleteFile】 删除明细原图数据:\r\n{}"
                ,CollectionUtil.isEmpty(detailList)?"-1":detailList.stream().map(String::valueOf).collect(Collectors.joining(",")));

        log.info("【DetailDelEvent】-【DetailDelPicListener】 -【deleteFile】 删除完整汇总数据:\r\n{}"
                ,CollectionUtil.isEmpty(imageMarkPathList)?"-1":imageMarkPathList.stream().collect(Collectors.joining(",")));
//        FileUtils.deleteFile(imageMarkPathList,true);
        fileManager.deleteFiles(imageMarkPathList);
        log.info("【DetailDelEvent】-【DetailDelPicListener】 -【deleteFile】 删除完整汇总数据:\r\n{}"
                ,CollectionUtil.isEmpty(thumImageMarkPathList)?"-1":thumImageMarkPathList.stream().collect(Collectors.joining(",")));
//        FileUtils.deleteFile(thumImageMarkPathList,true);
        fileManager.deleteFiles(thumImageMarkPathList);
    }



}
