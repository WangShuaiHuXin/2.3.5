package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.dao.entity.BaseUavEntity;
import com.imapcloud.nest.v2.service.dto.in.BaseUavInDTO;
import com.imapcloud.nest.v2.service.dto.in.DjiBuildPushUrlInDTO;
import com.imapcloud.nest.v2.service.dto.in.SaveUavInDTO;
import com.imapcloud.nest.v2.service.dto.in.UavQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.resp.LivePlayInfoRespVO;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;

import java.util.List;

/**
 * <p>
 * 无人机信息表 服务类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
public interface BaseUavService {


    /**
     * 分页获取无人机信息
     * @param condition 查询条件
     * @return 返回结果
     */
    PageResultInfo<UavInfoOutDTO> pageUavInfos(UavQueryInDTO condition);

    List<BaseUavInfoOutDTO> listUavInfos(List<String> uavIdList);

    List<BaseUavInfoOutDTO> listUavInfosByNestIds(List<String> nestIdList);

    BaseUavInfoOutDTO getUavInfoByNestId(String nestId);

    CameraParamsOutDTO getCameraParamByNestId(String nestId);

    CameraParamsOutDTO getCameraParamByAppId(String appId);

    /**
     * 查询无人机类型
     *
     * @param nestId
     * @return
     */
    String getUavTypeByNestId(String nestId, AirIndexEnum... airIndexEnums);

    /**
     * 通过基站uuid查询无人机
     *
     * @param nestUuid
     * @return
     */
    String getAirCodeByNestUuidCache(String nestUuid,AirIndexEnum... airIndexEnums);

    /**
     * 无人机信息保存
     *
     * @param uavInDTO 无人机在dto
     * @return {@link String} 无人机ID
     */
    String uavSave(BaseUavInDTO.UavInDTO uavInDTO);

    String getUavStreamIdByNestId(String nestId,AirIndexEnum... airIndexEnums);

    /**
     * 生成基站无人机的推流地址
     *
     * @param nestId 巢id
     * @return {@link BaseUavOutDTO.PushInfoOutDTO}
     * @deprecated 2.3.2，将在后续版本删除该接口
     */
    @Deprecated
    BaseUavOutDTO.PushInfoOutDTO buildPushUrl(String nestId);

    /**
     * @deprecated 2.3.2，将在后续版本删除该接口
     */
    @Deprecated
    BaseUavOutDTO.PushInfoOutDTO djiBuildPushUrl(DjiBuildPushUrlInDTO dto);

    BaseUavOutDTO.PushInfoOutDTO createMediaInfoForDJI(DjiBuildPushUrlInDTO dto);

    /**
     * 设置基站无人机的推流地址
     *
     * @param nestId  巢id
     * @param pushUrl 将url
     * @param accountId accountId
     */
    void setPushUrl(String nestId, String pushUrl);

    List<AppStreamOutDTO> listAppStreamsByAppIdList(List<String> appIdList);

    /**
     * 获取无人机名称
     *
     * @param deviceId
     * @return
     */
    String getUavCodeByDeviceId(String deviceId);

    BaseUavInfoOutDTO getUavInfoByAppId(String appId);

    String getUavStreamIdByAppId(String appId);

    String saveOrUpdateUav(SaveUavInDTO saveUavInDTO);

    List<BaseUavInfoOutDTO> listUavInfosByAppIds(List<String> appIdList);

    Boolean softDeleteUavByUavId(String uavId);

    String getUavStreamIdByDeviceId(String deviceId);

    void setPushUrl(String nestId, String pushUrl, String accountId ,Integer uavWhich);

    /**
     * 获取cps无人机推流地址
     *
     * @param nestId 巢id
     * @return {@link String}
     */
    String getPushUrl(String nestId,Integer uavWhich);

    void updateStreamIdByUavId(List<BaseUavEntity> list);

    /**
     * 点播无人机直播
     * @param uavId 无人机ID
     * @param repush 是否重新推流
     * @return  点播信息
     */
    LivePlayInfoRespVO playUavLive(String uavId, Boolean repush);


    /**
     * 获取数据库存的推流地址
     * @param uavSn
     * @param uavId
     * @return
     */
    String getDBPushUrl(String uavSn, String uavId);

}
