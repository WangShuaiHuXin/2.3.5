package com.imapcloud.nest.v2.service;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import com.imapcloud.nest.v2.dao.po.NestQueryCriteriaPO;
import com.imapcloud.nest.v2.service.dto.in.NestFlowUrlInDTO;
import com.imapcloud.nest.v2.service.dto.in.NestQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import org.bytedeco.opencv.presets.opencv_core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 机巢信息表 服务类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseNestService {

    /**
     * 查询UUID通过nestId
     *
     * @param nestId
     * @return
     */
    String getNestUuidByNestIdInCache(String nestId);

    /**
     * 查询ComponentManager
     *
     * @param nestId
     * @return
     */
    ComponentManager getComponentManagerByNestId(String nestId);

    /**
     * @param nestIdList
     * @return
     */
    List<MqttInitParamOutDTO> listMqttInitParams(List<String> nestIdList);


    /**
     * 查询老方法
     *
     * @return
     */
    List<BaseNestListOutDTO> listNestInfos();

    BaseNestListOutDTO getNestAndRegion(String nestUuid);

    boolean setShowStatusByNestId(String nestId, Integer showStatus);

    boolean setShowStatusByRegionId(String regionId, Integer showStatus);

    /**
     * 多屏监控查询流地址
     *
     * @param dto
     * @return
     */
    List<NestFlowUrlOutDTO> listNestFlowUrls(NestFlowUrlInDTO dto);

    /**
     * 获取单位名称
     *
     * @param nestUuid
     * @return
     */
    String getNestNameByUuid(String nestUuid);

    /**
     * 获取基站编号
     *
     * @param nestUuid
     * @return
     */
    String getNestNumberByUuid(String nestUuid);

    /**
     * 设置基站编号
     *
     * @param nestUuid
     * @return
     */
    boolean setNestNumberByUuid(String nestUuid, String nestNumber);

    StartMissionNestInfoOutDTO getStartMissionParamByMissionId(Integer missionId);

    List<NestQueryOutDTO> searchNestFromDb(NestQueryInDTO condition);

    Long countTotalByCondition(QueryCriteriaDo<NestQueryCriteriaPO> queryCriteriaPO);

    NestTypeEnum getNestTypeByUuidCache(String nestUuid);

    NestTypeEnum getNestTypeByNestIdCache(String nestId);

    /**
     * 获取维保状态，带缓存
     *
     * @param nestUuid
     * @return
     */
    Integer getMaintenanceStateCache(String nestUuid);

    Integer getMaintenanceStateByNestId(String nestId);

    String getNestNameByUuidInCache(String uuid);

    /**
     * 获取基站的Alt，使用redis缓存
     *
     * @param nestUuid
     * @return
     */
    double getNestAltCache(String nestUuid);

    Map<String, String> getNestNameMap(List<String> nestIdList);

    Map<String, String> getAllNestNameMap();

    List<BatchOperNestOutDTO> listBatchOperParam(List<String> nestIdList);

    StartMissionQueueNestInfoOutDTO getStartMissionQueueNestInfoByNestId(String nestId);

    StartMissionQueueNestInfoOutDTO getStartMissionQueueNestInfoByUuid(String nestUuid);

//    /**
//     * @deprecated 2.3.2，将在后续版本删除
//     */
//    @Deprecated
//    CreateRelayParamOutDTO getCreateRelayParamCache(String nestId,Integer uavWhich);

    BaseNestLocationOutDTO getNestLocation(String nestId);

    DynamicAirLineOutDTO getDynamicAirLineParam(String nestId);

    BaseNestInfoOutDTO getBaseNestInfo(String nestId);

    BaseNestInfoOutDTO getBaseNestInfoByNestUuid(String nestUuid);

    Boolean setPlanCodeByNestId(String nestId, String planCode);

    List<String> listAllNestUuids();

    List<String> listAllNestIds();

    Boolean setMaintenanceStateByNestId(Integer state, String nestId);

    String getNestIdByNestUuid(String nestUUID);

    /**
     * 批量获取基站信息
     *
     * @param nestIds 基站ID列表
     * @return 基站简要信息
     */
    List<NestSimpleOutDTO> listNestInfos(Collection<String> nestIds);

    List<BaseNestInfoOutDTO> listBaseNestInfosByOrgCode(String orgCode);

    BaseNestInfoOutDTO getBaseNestInfoByUuid(String nestUuid);

    /**
     * 查询所有基站的uuid,使用缓存
     * @return
     */
    List<String> listAllUuidsCache();

    /**
     * 批量获取基站信息
     *
     * @return 基站简要信息
     */
    List<NestSimpleOutDTO> listAllNestInfos();

    /**
     * 获取NestType不通过缓存
     * @param nestId
     * @return
     */
    NestTypeEnum getNestType(String nestId);

    /**
     * 根据nestId查询基站类型
     * @param nestIdList
     * @return
     */
    Map<String, Integer> getNestTypeMap(List<String> nestIdList);


    BaseNestInfoOutDTO findNestByUavId(String uavId);

    String findDJIStreamId(String nestId);

}
