package com.imapcloud.nest.v2.manager.sql;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentQueryDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerEquipmentInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerEquipmentLegerInfoOutDO;
import com.imapcloud.nest.v2.service.dto.in.PowerEquipmentJsonRootInDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerEquipmentDTO;
import com.imapcloud.nest.v2.web.vo.req.PowerArtificialReqVO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PowerEquipmentLegerInfoManager {
    void saveList(  List<PowerEquipmentInDO> objList);

    List<String> queryAllPmsIdByCondition(PowerEquipmentQueryDO powerEquipmentQueryDO);

    void updateList(  List<PowerEquipmentInDO> updateList);

    Page<PowerEquipmentLegerInfoEntity> equipmentListQuery(Integer pageNo, Integer pageSize, String orgCode, String equipmentName, String spacingUnitName, String voltageLevel, String beginTime, String endTime, String equipmentType);

    List<AccountOutDO> queryAccountInfoByOrg(List<String> accountList);

    void checkPmsIdIsExist(PowerEquipmentQueryDO queryDO);

    void updateEquipment(PowerEquipmentInDO inDO);

    List<PowerEquipmentLegerInfoEntity> queryEquipmentById(String equipmentId);

    boolean deleteEquipmentList(List<String> equipmentList);

    List<PowerEquipmentInfoOutDO> queryListByOrg(String orgCode);

    public List<PowerEquipmentInfoOutDO> queryListByOrgAKyeWord(String orgCode,String keyWord);

    void updateEquipmentId(PowerArtificialReqVO vo);

    void updateComponentId(PowerArtificialReqVO vo);

    PowerEquipmentLegerInfoEntity queryEquipmentByPmsAndOrg(String pmsID,String orgCode);

    /**
     * 查询设备通过id集合
     *
     * @param equipmentIdCollection 设备id
     * @return {@link List}<{@link PowerEquipmentLegerInfoOutDO}>
     */
    List<PowerEquipmentLegerInfoOutDO> queryEquipmentByIdCollection(Collection<String> equipmentIdCollection);

    List<PowerEquipmentLegerInfoEntity> queryEquipmentByPmsIdsAndOrg(Set<String> pmsSet, String orgCode);

    List<PowerEquipmentLegerInfoEntity> queryEquipmentByIds(List<String> equipmentIds);
}
