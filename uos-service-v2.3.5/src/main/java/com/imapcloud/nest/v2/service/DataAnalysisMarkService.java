package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisDetailDrawOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkDrawOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisMarkPageOutDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisMarkService.java
 * @Description DataAnalysisMarkService
 * @createTime 2022年07月13日 15:21:00
 */
public interface DataAnalysisMarkService {

    /**
     *  根据Details 分页查询标注数据
     * @param dataAnalysisMarkPageInDTO
     * @return
     */
    PageResultInfo<DataAnalysisMarkPageOutDTO> queryMarkPage(DataAnalysisMarkPageInDTO dataAnalysisMarkPageInDTO);

    /**
     *  根据Details 查询标注数据
     * @param dataAnalysisMarkInDTO
     * @return
     */
    List<DataAnalysisMarkOutDTO> queryMark(DataAnalysisMarkInDTO dataAnalysisMarkInDTO);


    /**
     * 批量核实标注
     * @param markIds
     * @return
     */
    public boolean confirmMarks(List<Long> markIds);

    /**
     * 核实标注
     * @param markId
     * @return
     */
    public boolean confirmMark(Long markId);

    /**
     * 核实照片下所有标注
     * @param detailId
     * @return
     */
    public boolean confirmMarkForPic(Long detailId);

    /**
     * 根据Mark生成对应的单个标注图片以及缩略图
     * @param markInDTO
     * @return
     */
    public List<DataAnalysisMarkDrawOutDTO> drawSingleMarkPic(List<DataAnalysisMarkDrawInDTO> markInDTO);

    /**
     * 根据照片ID画完整的所有标注图片以及缩略图
     * @param markInDTO
     * @return
     */
    public DataAnalysisDetailDrawOutDTO drawAllMarkPic(List<DataAnalysisMarkDrawInDTO> markInDTO);


    /**
     * 根据detailIds查询标注Id
     * @param detailIds
     * @return
     */
    List<DataAnalysisMarkEntity> queryMarkByDetails(List<Long> detailIds);

    /**
     * 删除对应数据 根据detailId
     * @param detailIds
     * @return
     */
    boolean deleteMarksByDetail(List<Long> detailIds);

    /**
     * 删除对应数据
     * @param markIds
     * @return
     */
    boolean deleteMarks(List<Long> markIds,boolean fromMark);

    /**
     * 批量保存
     * @param dataAnalysisMarkSaveInDTOList
     * @return
     */
    List<Long> saveBatch(List<DataAnalysisMarkSaveInDTO> dataAnalysisMarkSaveInDTOList);

    /**
     * 批量更新保存
     * @param dataAnalysisMarkSaveInDTOList
     * @return
     */
    List<Long> saveOrUpdateBatch(List<DataAnalysisMarkSaveInDTO> dataAnalysisMarkSaveInDTOList);

//    /**
//     * 上传地址截图
//     * @deprecated 2.2.3，使用新接口{@link DataAnalysisMarkService#savePicAddrInfo(com.imapcloud.nest.v2.service.dto.in.MarkAddrInfoInDTO)}替代，将在后续版本删除
//     */
//    @Deprecated
//    String uploadAddrPic(Long markId , BigDecimal longitude, BigDecimal latitude
//            ,String addr , MultipartFile file);

    /**
     * 保存标记截图地址信息
     * @param markAddrInfoInDTO 信息
     */
    void savePicAddrInfo(MarkAddrInfoInDTO markAddrInfoInDTO);

    /**
     *  标注地址信息重置
     * @param markIds
     * @return
     */
    Long markAddrReset(Long markIds,BigDecimal longitude , BigDecimal latitude);

    /**
     *  标注地址截图清空
     * @param markIds
     * @return
     */
    Long markAddrDel(Long markIds);

}
