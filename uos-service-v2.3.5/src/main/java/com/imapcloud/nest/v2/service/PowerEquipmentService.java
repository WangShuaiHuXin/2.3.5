package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.PowerEquipmentInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.req.PowerArtificialReqVO;
import com.imapcloud.nest.v2.web.vo.req.PowerEquipmentMatchReqVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PowerEquipmentService {
    /**
     * 导入设备台账
     * @param file
     * @param orgCode
     * @return
     */
    PowerEquipmentDTO.PowerEquipmentUploadDto equipmentExcelUpload(MultipartFile file, String orgCode);

    /**
     * 查询列表数据
     * @param pageNo
     * @param pageSize
     * @param orgCode
     * @param equipmentName
     * @param spacingUnitName
     * @param voltageLevel
     * @param beginTime
     * @param endTime
     * @return
     */
    PowerEquipmentListOutDTO equipmentListQuery(Integer pageNo, Integer pageSize, String orgCode, String equipmentName, String spacingUnitName, String voltageLevel, String beginTime, String endTime
            , String equipmentType);

    /**
     * 更新或保存设备
     * @param saveOrUpdateDTO
     */
    void equipmentSaveOrUpdate(PowerEquipmentInDTO.PowerEquipmentSaveOrUpdateDTO saveOrUpdateDTO);

    PowerEquipmentListOutDTO.PowerEquipmentObj queryEquipmentById(String equipmentId);

    boolean deleteEquipment(String equipmentId);

    boolean deleteEquipments(List<String> equipmentIds);

    boolean waypointEquipmentJsonUpload(String orgCode, MultipartFile file);

    PowerWaypointListInfoOutDTO waypointEquipmentList(String orgCode, String deviceLayer, String unitLayer, String subRegion, String equipmentArea, String equipmentStatu, String componentStatu, Integer pageNo, Integer pageSize);

    List<EquipmentOptionListOutDTO> equipmentOptionList(String orgCode,String keyWord);

    /**
     * 匹配设备
     * @param vo
     */
    void equipmatchArtificial(PowerArtificialReqVO vo);

    /**
     * 匹配部件
     * @param vo
     */
    void componentmatchArtificial(PowerArtificialReqVO vo);

    /**
     * 自动匹配部件
     * @param vo
     * @return
     */
    PowerEquipmentMatchOutDTO componentmatchAuto(PowerEquipmentMatchReqVO vo);

    /**
     * 自动匹配设备
     * @param vo
     * @return
     */
    PowerEquipmentMatchOutDTO equipmatchAuto(PowerEquipmentMatchReqVO vo);
}
