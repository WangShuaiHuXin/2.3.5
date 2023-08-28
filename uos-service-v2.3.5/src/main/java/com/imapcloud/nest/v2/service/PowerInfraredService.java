package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDataOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDetailInfraredOutDO;
import com.imapcloud.nest.v2.manager.listener.rocketmq.PowerAiTaskResultListener;
import com.imapcloud.nest.v2.service.dto.in.PowerInfraredInDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerInfraredOutDTO;

import java.util.List;

/**
 * 红外测温
 *
 * @author boluo
 * @date 2022-12-28
 */
public interface PowerInfraredService {
    /**
     * 红外测温
     *
     * @param powerInfraredInDTO 电力红外dto
     * @return {@link PowerInfraredOutDTO.PictureOutDTO}
     */
    PowerInfraredOutDTO.PictureOutDTO pictureGet(PowerInfraredInDTO.PictureInDTO powerInfraredInDTO);

    /**
     * 保存红外测温
     *
     * @param saveInDTO 保存在dto
     */
    String save(PowerInfraredInDTO.SaveInDTO saveInDTO);

    /**
     * 自动
     *
     * @param detailIdList 细节id列表
     */
    void auto(List<String> detailIdList);

    /**
     * 处理结果
     *
     * @param mqInfo            mq信息
     * @param businessParamInfo 业务参数信息
     */
    void result(PowerAiTaskResultListener.MqInfo mqInfo, RocketmqInDO.BusinessParamInfo businessParamInfo);

    /**
     * 红外识别过程中，任务进度
     *
     * @param taskMqInfo 任务mq信息
     */
    void infrared(RocketmqInDO.TaskMqInfo taskMqInfo);

    /**
     * 更新任务状态
     *
     * @param detailIdList       细节id列表
     * @param dataId       dataId
     */
    void updateTaskStatePre(List<String> detailIdList, String dataId);

    /**
     * 删除标注记录
     *
     * @param infraredRecordId 红外记录id
     */
    void delete(String infraredRecordId);

    /**
     * 发送ws
     *
     * @param powerMeterFlightDataOutDO powerMeterFlightDataOutDO
     * @param powerTaskStateEnum powerTaskStateEnum
     */
    void taskChange(PowerMeterFlightDataOutDO powerMeterFlightDataOutDO, PowerTaskStateEnum powerTaskStateEnum);

    /**
     * 发送ws
     *
     * @param dataId      数据标识
     * @param initialized 初始化
     */
    void sendWs(String dataId, boolean initialized);

    /**
     * 手动发送ws
     *
     * @param orgCode 组织代码
     */
    void manualSendWs(String orgCode);

    /**
     * 检查任务是否在运行
     *
     * @param detailIdList 细节id列表
     * @param verification 是否检测核实
     */
    void checkRunning(List<String> detailIdList, boolean verification);
}
