package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisCenterDetailEntity;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkOutPO;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMissionRecordOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisStateSumOutDTO;
import com.imapcloud.nest.v2.web.vo.req.DataAnalysisResultGroupReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DataAnalysisHisResultRespVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisDetailService.java
 * @Description DataAnalysisDetailService
 * @createTime 2022年07月13日 15:21:00
 */
public interface DataAnalysisDetailService {

    /**
     * 根据任务架次记录、照片状态分页查询照片数据
     *
     * @param dataAnalysisBasePageInDTO
     * @return
     */
    PageResultInfo<DataAnalysisDetailPageOutDTO> queryDetailPage(DataAnalysisDetailPageInDTO dataAnalysisBasePageInDTO);

    /**
     * 根据任务架次记录、查询明细ID
     *
     * @param centerBaseId
     * @param picStatus
     * @param picType
     * @return
     */
    List<DataAnalysisDetailOutDTO> queryAllDetail(Long centerBaseId, Integer picStatus, Integer picType);

    /**
     * 根据数据详情ID列表查询详情信息
     * @param centerDetailIds   详情ID列表
     * @return  详情信息列表
     */
    List<DataAnalysisDetailOutDTO> findDataAnalysisDetails(List<Long> centerDetailIds);

    /**
     * 根据任务架次记录、查询明细ID
     *
     * @param centerBaseId
     * @param picStatus
     * @param picType
     * @param desc
     * @return
     */
    List<DataAnalysisDetailOutDTO> queryPicForBrowseMark(Long centerBaseId, Integer picStatus, Integer picType, int desc);


    /**
     * 统计状态数量 - 汇总时间段
     *
     * @param dataAnalysisDetailSumInDTO
     * @return
     */
    DataAnalysisStateSumOutDTO countStateSum(DataAnalysisDetailSumInDTO dataAnalysisDetailSumInDTO);


    /**
     * 统计状态数量 - 按时间段内每天汇总
     *
     * @param dataAnalysisDetailSumInDTO
     * @return
     */
    List<DataAnalysisStateSumOutDTO> countStateSumByDate(DataAnalysisDetailSumInDTO dataAnalysisDetailSumInDTO, boolean isFill);

    /**
     * 根据传入信息统计数量
     *
     * @param dataAnalysisCenterDetailEntityList
     * @return
     */
    DataAnalysisStateSumOutDTO countNumForEntity(List<DataAnalysisCenterDetailEntity> dataAnalysisCenterDetailEntityList);

    /**
     * 核实照片
     *
     * @param photoId
     * @return
     */
    boolean confirmPic(Long photoId);

    /**
     * 批量核实图片 - 一键核实
     *
     * @param detailIds
     * @return
     */
    boolean confirmPicList(List<Long> detailIds);

    /**
     * 填充专题信息
     *
     * @param dataAnalysisDetailMarkOutPOS
     * @return
     */
    List<DataAnalysisDetailMarkOutPO> fillTopicData(List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS);

    /**
     * 回写照片状态
     *
     * @param detailIds
     * @param photoState
     * @param pushState
     * @return
     */
    boolean writeBackPicState(List<Long> detailIds, Integer photoState, Integer pushState);

    /**
     * 一键重置图片 - 一键重置
     *
     * @param detailIds
     * @return
     */
    boolean resetPicList(List<Long> detailIds);

    /**
     * 撤回
     *
     * @param detailIds
     * @return
     */
    boolean unDoPics(List<Long> detailIds);

    /**
     * 批量下载图片信息
     *
     * @param detailIds
     * @param response
     */
    void downloadData(List<Long> detailIds, HttpServletResponse response);

    /**
     * 删除数据
     *
     * @param detailIds
     * @return
     */
    boolean deleteDetails(List<Long> detailIds);

    /**
     * 批量保存数据
     *
     * @param dataAnalysisDetailSaveInDTOS
     * @return
     */
    List<Long> saveBatch(List<DataAnalysisDetailSaveInDTO> dataAnalysisDetailSaveInDTOS);

    /**
     * 批量保存更新数据
     *
     * @param dataAnalysisDetailSaveInDTOS
     * @return
     */
    List<Long> saveOrUpdateBatch(List<DataAnalysisDetailSaveInDTO> dataAnalysisDetailSaveInDTOS);


    /**
     * 批量保存数据
     *
     * @param dataAnalysisDetailSaveInDTOS
     * @return
     */
    List<Long> saveDetailAndMark(List<DataAnalysisDetailSaveInDTO> dataAnalysisDetailSaveInDTOS);

    /**
     * 查询历史照片，按任务ID以及正方形距离查询
     *
     * @param dataAnalysisHistoryPicPageDTO
     * @return
     */
    PageResultInfo<DataAnalysisDetailPageOutDTO> queryHistory(DataAnalysisHistoryPicPageDTO dataAnalysisHistoryPicPageDTO);


    /**
     * 查询是否有已经核实过
     *
     * @param detailIds
     * @param markIds
     * @return
     */
    List<Long> hasConfirmPic(List<Long> detailIds, List<Long> markIds);

    /**
     * 架次记录筛选项
     *
     * @param dataAnalysisHistoryPicPageDTO 数据分析历史图片页面dto
     * @return {@link List}<{@link DataAnalysisMissionRecordOutDTO}>
     */
    List<DataAnalysisMissionRecordOutDTO> missionRecordCondition(DataAnalysisHistoryPicPageDTO dataAnalysisHistoryPicPageDTO);

    /**
     * 填充
     *
     * @param dataAnalysisDetailMarkOutPOS
     * @return
     */
    List<DataAnalysisDetailMarkOutPO> fillMergeState(List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS);

    /**
     * 查找历史同类问题
     *
     * @param missionId
     * @param orgCode
     * @param topicProblemId
     * @param beginTIme
     * @param endTime
     * @return
     */
    List<DataAnalysisHisResultDTO.HisResultDTO> hisResults(String missionId, String orgCode, String topicProblemId, String beginTIme, String endTime);

    /**
     * 识别分析-照片标注-合并问题
     * @param vo
     * @return
     */
    Boolean mergeResult(DataAnalysisResultGroupReqVO.ResultMergeReqVO vo);

    /**
     * 识别分析-照片标注-取消合并
     * @param vo
     * @return
     */
    Boolean undoMergeResult(DataAnalysisResultGroupReqVO.ResultMergeReqVO vo);

    /**
     * 调用balance处理merge_group 及group数据关系
     * @param groupId
     */
    void resultBalance(String groupId);

    public void  checkResultStatus(List<Long> detailIds);

    /**
     * 核实情况下的填充专题信息
     * @param dataAnalysisDetailMarkOutPOS
     * @return
     */
    List<DataAnalysisDetailMarkOutPO> fillTopicDataWithCheck(List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS);

    /**
     * 查找下载
     * @param detailIds
     * @param orgCode
     * @return
     */
    int selectNum(List<String> detailIds, String orgCode);
}
