package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.service.dto.TaskInfoDTO;
import com.imapcloud.nest.v2.service.dto.in.PowerTaskInDTO;

import java.util.List;

/**
 * 电力AI任务
 *
 * @author boluo
 * @date 2023-02-28
 */
public interface PowerTaskService {

    /**
     * 任务创建及识别
     *
     * @param autoTaskInDTO autoTaskInDTO
     */
    void autoTask(PowerTaskInDTO.AutoTaskInDTO autoTaskInDTO);

    /**
     * 人工任务
     *
     * @param orgCode 组织代码
     */
    void manualTask(String orgCode);

    /**
     * 任务是否为空 true:空
     *
     * @param taskMqInfo taskMqInfo
     * @return boolean
     */
    boolean taskEmpty(RocketmqInDO.TaskMqInfo taskMqInfo);

    /**
     * 删除并获取一个图片
     *
     * @param taskMqInfo 任务mq信息
     * @return {@link TaskInfoDTO.DetailInfoDTO}
     */
    TaskInfoDTO.DetailInfoDTO removeOneTaskPicture(RocketmqInDO.TaskMqInfo taskMqInfo);

    /**
     * 添加任务
     *
     * @param addTaskInDTO 添加任务dto
     */
    void addTask(PowerTaskInDTO.AddTaskInDTO addTaskInDTO);

    /**
     * 删除任务
     *
     * @param dataId 数据标识
     */
    void delTask(String dataId);

    /**
     * 获取任务信息  存在分布式问题，仅限于查询发送ws使用
     *
     * @param dataId 数据标识
     * @return {@link TaskInfoDTO}
     */
    TaskInfoDTO getTaskInfoByDataId(String dataId);
}
