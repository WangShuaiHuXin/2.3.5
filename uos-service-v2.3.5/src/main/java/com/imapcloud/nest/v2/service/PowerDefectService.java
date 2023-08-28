package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.manager.dataobj.in.RocketmqInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDataOutDO;
import com.imapcloud.nest.v2.manager.listener.rocketmq.PowerAiTaskResultListener;
import com.imapcloud.nest.v2.service.dto.in.PowerDefectInDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerDefectOutDTO;

import java.util.List;
import java.util.Map;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-07
 */
public interface PowerDefectService {

    /**
     * 列表
     *
     * @param listInDTO 在dto上市
     * @return {@link PageResultInfo}<{@link PowerDefectOutDTO.ListOutDTO}>
     */
    PageResultInfo<PowerDefectOutDTO.ListOutDTO> list(PowerDefectInDTO.ListInDTO listInDTO);

    /**
     * 详情
     *
     * @param detailId 详细id
     * @return {@link PowerDefectOutDTO.DetailOutDTO}
     */
    PowerDefectOutDTO.DetailOutDTO detail(String detailId);

    /**
     * 添加缺陷标记
     *
     * @param addMarkInDTO dto添加标记
     * @return {@link String} 标记ID
     */
    String addMark(PowerDefectInDTO.AddMarkInDTO addMarkInDTO);

    /**
     * 删除标记
     *
     * @param defectMarkIdList 缺陷标记id列表
     * @param accountId        帐户id
     */
    void deleteMark(List<String> defectMarkIdList, String accountId);

    /**
     * 删除
     *
     * @param detailIdList 细节id列表
     * @param accountId    帐户id
     */
    void delete(List<String> detailIdList, String accountId);

    /**
     * 指定巡检记录识别情况统计
     *
     * @param dataId 数据标识
     * @return {@link Map}<{@link Integer}, {@link Long}>
     */
    Map<Integer, Long> statistics(String dataId);

    /**
     * 更新任务状态
     *
     * @param detailIdList 细节id列表
     * @param dataId dataId
     */
    void updateTaskStatePre(List<String> detailIdList, String dataId);

    /**
     * 发送ws
     *
     * @param dataId      数据标识
     * @param initialized 初始化
     */
    void sendWs(String dataId, boolean initialized);

    /**
     * 缺陷
     *
     * @param taskMqInfo 任务mq信息
     */
    void defect(RocketmqInDO.TaskMqInfo taskMqInfo);

    /**
     * 暂停 取消暂停 终止
     *
     * @param powerMeterFlightDataOutDO 功率计飞行数据
     * @param powerTaskStateEnum        权力任务状态枚举
     */
    void taskChange(PowerMeterFlightDataOutDO powerMeterFlightDataOutDO, PowerTaskStateEnum powerTaskStateEnum);

    /**
     * 识别结果
     *
     * @param mqInfo            mq信息
     * @param businessParamInfo 业务参数信息
     */
    void result(PowerAiTaskResultListener.MqInfo mqInfo, RocketmqInDO.BusinessParamInfo businessParamInfo);

    /**
     * 批量删除
     *
     * @param dataIdList 数据id列表
     * @param accountId  帐户id
     */
    void batchDeleteByDataIdList(List<String> dataIdList, String accountId);

    /**
     * 手动发送ws
     *
     * @param orgCode 组织代码
     */
    void manualSendWs(String orgCode);

    /**
     * 检查任务是否在识别中   verification=true表示已核实也需要检测
     *
     * @param detailIdList 细节id列表
     * @param verification 验证
     */
    void checkRunning(List<String> detailIdList, boolean verification);
}
