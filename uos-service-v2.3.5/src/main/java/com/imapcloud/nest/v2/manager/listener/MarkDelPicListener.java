package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.manager.event.MarkDelEvent;
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
 * @ClassName MarkDelPicListener.java
 * @Description MarkDelPicListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class MarkDelPicListener extends AbstractEventListener<MarkDelEvent> {

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private FileManager fileManager;

    /**
     * 消息监听-处理
     *
     * @param markDelEvent 消息事件
     */
    @Override
    @Async("dataAnalysisExecutor")
    @EventListener
    public void eventListener(MarkDelEvent markDelEvent) {
        log.info("【MarkDelEvent】-【MarkDelPicListener】事件:{}",markDelEvent.toString());

        List<Long> markIds = markDelEvent.getSource();
        List<DataAnalysisMarkEntity> markEntities = this.dataAnalysisMarkMapper.queryDeleteDataById(markIds);
        if(CollectionUtil.isNotEmpty(markEntities)){
            List<String> imagePathList = markEntities.stream()
                            .map(DataAnalysisMarkEntity::getMarkImagePath)
                            .filter(StringUtil::isNotEmpty)
                            .collect(Collectors.toList())
                    ,thumImagePathList = markEntities.stream()
                            .map(DataAnalysisMarkEntity::getThumImagePath)
                            .filter(StringUtil::isNotEmpty)
                            .collect(Collectors.toList())
                    ,addrImagePathList = markEntities.stream()
                            .map(DataAnalysisMarkEntity::getAddrImagePath)
                            .filter(StringUtil::isNotEmpty)
                            .collect(Collectors.toList());
            deleteFile(markIds,imagePathList,thumImagePathList,addrImagePathList);
        }
    }

    /**
     *
     * @param imagePathList
     * @param thumImagePathList
     * @param thumImageMarkPathList
     */
    public void deleteFile(List<Long> markList , List<String> imagePathList , List<String> thumImagePathList,List<String> thumImageMarkPathList){
        //删除数据，讲究实时记录日志，不采用debug
        log.info("【MarkDelEvent】-【MarkDelPicListener】 -【deleteFile】 主键Id:\r\n{}"
                ,CollectionUtil.isEmpty(markList)?"-1":markList.stream().map(String::valueOf).collect(Collectors.joining(",")));

        log.info("【MarkDelEvent】-【MarkDelPicListener】 -【deleteFile】 删除标注原图数据:\r\n{}"
                ,CollectionUtil.isEmpty(imagePathList)?"-1":imagePathList.stream().collect(Collectors.joining(",")));
//        FileUtils.deleteFile(imagePathList,true);
        fileManager.deleteFiles(imagePathList);
        log.info("【MarkDelEvent】-【MarkDelPicListener】 -【deleteFile】 删除标注缩略图数据:\r\n{}"
                ,CollectionUtil.isEmpty(imagePathList)?"-1":thumImagePathList.stream().collect(Collectors.joining(",")));
//        FileUtils.deleteFile(thumImagePathList,true);
        fileManager.deleteFiles(thumImagePathList);
        log.info("【MarkDelEvent】-【MarkDelPicListener】 -【deleteFile】 删除地址截图数据:\r\n{}"
                ,CollectionUtil.isEmpty(imagePathList)?"-1":thumImageMarkPathList.stream().collect(Collectors.joining(",")));
//        FileUtils.deleteFile(thumImageMarkPathList,true);
        fileManager.deleteFiles(thumImageMarkPathList);
    }



}
