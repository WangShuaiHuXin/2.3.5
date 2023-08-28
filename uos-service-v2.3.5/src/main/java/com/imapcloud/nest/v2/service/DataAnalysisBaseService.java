package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisAggSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisBasePageInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisBaseSaveInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisBasePageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisBaseSimpleOutDTO;

import java.util.List;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseService.java
 * @Description DataAnalysisBaseService
 * @createTime 2022年07月13日 14:39:00
 */
public interface DataAnalysisBaseService {

    /**
     *  提供时间、任务名、标签名分页查询功能
     * @param dataAnalysisBasePageInDTO
     * @return
     */
    PageResultInfo<DataAnalysisBasePageOutDTO> queryBasePage(DataAnalysisBasePageInDTO dataAnalysisBasePageInDTO);

    /**
     * 根据centBaseId查询基础数据
     * @param centerBaseId  基础数据ID
     * @return  基础数据
     */
    Optional<DataAnalysisBaseSimpleOutDTO> findDataAnalysisBase(Long centerBaseId);

    /**
     *  推送同个架次记录下“单个或者多个”照片数据 -- 来源数据管理
     * @param dataAnalysisAggSaveInDTO
     * @return
     */
    Long pushCenterAggMain(DataAnalysisAggSaveInDTO dataAnalysisAggSaveInDTO);

    /**
     *  推送数据 每次推一张照片，可以多个标注（预留口子） -- 数据来源现场取证
     * @param dataAnalysisAggSaveInDTO
     * @return
     */
    Long pushCenterAggAndMarkMain(DataAnalysisAggSaveInDTO dataAnalysisAggSaveInDTO);

    /**
     * 新增或修改数据 -基础数据
     * @param dataAnalysisBaseSaveInDTO
     * @return
     */
    Long saveBase(DataAnalysisBaseSaveInDTO dataAnalysisBaseSaveInDTO);

    /**
     * 删除数据接口
     * @param baseId
     * @param detailIds
     * @return
     */
    boolean deleteData(Long baseId, List<Long> detailIds);

    /**
     * 删除数据接口
     * @param baseIds
     * @return
     */
    boolean deleteData(List<Long> baseIds);

    /**
     * 批量保存数据
     * @param dataAnalysisBaseSaveInDTOS
     * @return
     */
    List<Long> saveBatch(List<DataAnalysisBaseSaveInDTO> dataAnalysisBaseSaveInDTOS);

    /**
     * 批量保存更新数据
     * @param dataAnalysisBaseSaveInDTOS
     * @return
     */
    List<Long> saveOrUpdateBatch(List<DataAnalysisBaseSaveInDTO> dataAnalysisBaseSaveInDTOS);
}
