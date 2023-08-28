package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.enums.InfraredColorEnum;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.dto.out.BaseNestListOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.general.GeneralManager;
import com.imapcloud.sdk.manager.media.MediaManager;
import com.imapcloud.sdk.manager.motor.MotorManagerCf;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 机巢信息表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Deprecated
public interface NestService extends IService<NestEntity> {

    /**
     * 查询所有的机巢id和名字
     *
     * @return
     */
    List<NestEntity> listIdAndName();

    /**
     * 初始化连接机巢
     *
     * @param nestId
     */
    void initNest(String nestId);

    void batchInitNest(List<Integer> nestIdList);

    void asynchronousBatchInitNest(List<String> nestIdList);

    /**
     * 机巢是否连接
     *
     * @param nestUuid
     * @return
     */
    boolean isNestLinked(String nestUuid);

    /**
     * 通过id查询机巢的UUID
     *
     * @param id
     * @return
     */
    String getUuidById(int id);

    /**
     * 根据基站ID获取基站信息
     *
     * @param nestIds 基站ID列表
     * @return 基站信息
     */
    List<NestEntity> listNestByIds(Collection<Integer> nestIds);

    /**
     * 该方法已经过期不能使用，请用baseNestService的方法
     * @param id
     * @return
     */
    @Deprecated
    String getUuidByIdCache(Integer id);

    /**
     * 机巢断开连接
     *
     * @param id
     * @return
     */
    NestEntity getNestByIdIsCache(Integer id);

    /**
     * 查询机巢和机巢相关的区域信息
     *
     * @return
     */
    List<BaseNestListOutDTO> listNestAndRegion();

    BaseNestListOutDTO getNestAndRegion(String nestUuid);

    /**
     * 通过missionId获取机巢
     *
     * @param missionId
     * @return
     */
    NestEntity getNestByMissionId(Integer missionId);

    String getNestUuidByMissionId(Integer missionId);

    /**
     * 获取机巢详情
     *
     * @param nestId
     * @param nestUuid
     * @return
     */
    RestRes getNestDetailsById(String nestId, String nestUuid);

    RestRes saveOrUpdateNestDetailsDto2(NestDetailsDto nestDetailsDto);

    /**
     * 软删除机巢
     *
     * @param id
     * @return
     */
    RestRes softDeleteById(Integer id);


    List<NestEntity> listNestByPages();

    IPage<NestEntity> listFlowUrlBy(Integer pageNo, Integer pageSize, Integer flowType);

    /**
     * @deprecated 2.3.2，将在后续版本删除
     */
    @Deprecated
    RestRes listFlowUrlBy2(Integer pageNo, Integer pageSize, Integer flowType);

    /**
     * 根据架次id获取飞机的图片地址
     *
     * @param missionId
     * @return
     */
    String getRtmpUrlByMissionId(Integer missionId);

    /**
     * 获取app请求的机巢航线数据分页列表
     *
     * @param limit
     * @param nestUuid
     * @param lastTime
     * @return
     */
    Map<String, Object> getAppNestData(Integer limit, String nestUuid, Long lastTime);

    /**
     * 通过机巢nestUuid查询机巢id
     *
     * @param uuid
     * @return
     */
    Integer getIdByUuid(String uuid);

    /**
     * 获取机巢类型通过uuid
     *
     * @param uuid
     * @return
     */
    @Deprecated
    Integer getNestTypeByUuidInCache(String uuid);

    @Deprecated
    String getAircraftStateByUuidInCache(String uuid);

    RestRes updateNestShowStatus(String nestId, String regionId, Integer showStatus);

    /**
     * 获取基站名称通过uuid
     *
     * @param uuid
     * @return
     */
    @Deprecated
    String getNestNameByUuidInCache(String uuid);

    /**
     * 查询nestUuid通过account
     *
     * @param account
     */
    List<String> listNestUuidByAccountInCache(String account);

    /**
     * 获取所有基站uuid
     *
     * @return
     */
    @Deprecated
    List<String> listNestUuidInCache();

    /**
     * 基站ID列表
     *
     * @return {@link List}<{@link String}>
     */
    List<String> listNestEntityInCache();

    /**
     * 拍照变焦
     *
     * @param nestUuid
     * @param ratio
     * @return
     */
    RestRes setZoomRatio(String nestUuid, Float ratio);

    /**
     * 获取相机变焦倍数
     *
     * @param nestUuid
     * @return
     */
    RestRes getPhotoZoomRatio(String nestUuid);

    /**
     * 重置相机变焦倍数
     *
     * @param nestUuid
     * @return
     */
    RestRes resetPhotoZoomRatio(String nestUuid);

    /**
     * 云台控制：控制云台的俯仰、朝向
     *
     * @param nestUuid
     * @param pitchAngle
     * @param rollAngle
     * @return
     */
    RestRes setCameraAngle(String nestUuid, Float pitchAngle, Float rollAngle);

    /**
     * 云台回中并朝下
     *
     * @param nestUuid
     * @return
     */
    RestRes resetCameraAngle(String nestUuid);

    /**
     * 获取CPS剩余储存空间
     *
     * @param generalManager
     * @param nestId
     * @return
     */
    Long getCpsMemoryRemainSpaceCache(GeneralManager generalManager, String nestId);

    /**
     * 获取剩余无人机SD卡剩余内存空间
     *
     * @param mediaManager
     * @param nestId
     * @return
     */
    Long getSdCardRemainSpaceCache(MediaManager mediaManager, String nestId);

    /**
     * 后去SD卡总容量
     *
     * @param mediaManager
     * @param nestId
     * @return
     */
    Long getSdCardTotalSpaceCache(MediaManager mediaManager, String nestId);

    /**
     * 获取版本信息
     *
     * @param nestId
     * @return
     */
    RestRes getVersionInfo(String nestId, Boolean clearCache);

    /**
     * 获取维保状态，带缓存
     *
     * @param nestUuid
     * @return
     */
    @Deprecated
    Integer getMaintenanceStateCache(String nestUuid);

    List<NestEntity> getAllNests();

    List<NestAppNameHttpUrlDTO> getNestAppNameHttpUrlList();

    RestRes getTxDefaultLive(Integer nestId);

    Page<NestEntity> selectPage(Page<NestEntity> page, QueryWrapper<NestEntity> queryWrapper);


    RestRes clearCpsUpdateState(Integer nestId);

    /**
     * 获取基站的Alt，使用redis缓存
     *
     * @param nestUuid
     * @return
     */
    @Deprecated
    double getNestAltCache(String nestUuid);

    /**
     * 设置开启或者关闭功能
     *
     * @param nestId
     * @param enable
     * @return
     */
    RestRes setAutoGoToDefaultBackLandPointFun(String nestId, Boolean enable);

    /**
     * 查询备降功能是否开启
     *
     * @param nestId
     * @return
     */
    RestRes getAutoGoToDefaultBackLandPointFun(String nestId);

    /**
     * 立即前往默认备降点
     *
     * @param nestId
     * @return
     */
    RestRes immediatelyGoToDefaultBackLandPoint(String nestId, Double alt);

    /**
     * 立即前往指定备降点
     *
     * @param backLandFunDto
     * @return
     */
    RestRes immediatelyGoToGoToDesignBackLandPoint(BackLandFunDto backLandFunDto);

    /**
     * 获取备降点信息
     *
     * @param nestId
     * @return
     */
    RestRes getBackLandPointInfo(String nestId);

    /**
     * 根据单位编码获取机巢信息
     * @param orgCode 单位编码
     * @return List<NestEntity>
     */
    List<NestDto> listNestByOrgCode(String orgCode);

    /**
     * 控制云台
     *
     * @param controlGimbalDto
     * @return
     */
    RestRes controlGimbal(ControlGimbalDto controlGimbalDto);

    /**
     * 获取国标推流地址
     *
     * @param nestId
     * @return
     */
    RestRes getGbFlv(Integer nestId);

    /**
     * 获取SD卡容量可以存的照片数
     *
     * @param nestId
     * @return
     */
    RestRes getSdCardAvailableCaptureCount(String nestId,Integer which);

    /**
     * 获取SD卡容量可以录制视频的长度(单位是秒)
     *
     * @param nestId
     * @return
     */
    RestRes getSdCardAvailableRecordTimes(String nestId,Integer which);

    RestRes resetMqttClient(Integer nestId);

    /**
     * 设置备降点
     *
     * @param backLandFunDto
     * @return
     */
    RestRes setBackLandPointInfo(BackLandFunDto backLandFunDto);

    Double getCpsMemoryUseRate(String nestUuid, AirIndexEnum airIndexEnum);

    Double getSdMemoryUseRate(String nestUuid, AirIndexEnum airIndexEnum);

    /**
     * 一键回巢检测（异地回巢检测）
     *
     * @param nestId
     * @return
     */
    RestRes oneKeyGoHomeCheck(String nestId);

    /**
     * 一键回巢
     *
     * @param nestId
     * @return
     */
    RestRes oneKeyGoHome(String nestId);

    /**
     * 一键开启
     *
     * @param nestId
     * @return
     */
    RestRes oneKeyOpen(String nestId);

    RestRes oneKeyRecycle(String nestId);

    RestRes oneKeyReset(String nestId);

    RestRes batteryLoad(String nestId);

    RestRes batteryUnload(String nestId);

    RestRes openCabin(String nestId);

    RestRes closeCabin(String nestId);

    RestRes riseLift(String nestId);

    RestRes downLift(String nestId);

    RestRes tightSquare(MotorManagerCf.SquareEnum squareEnum, String nestId);

    RestRes looseSquare(MotorManagerCf.SquareEnum squareEnum, String nestId);

    RestRes rtkReconnect(String nestId);

    RestRes rcSwitch(String nestId);

    RestRes rcOnOff(String nestId);

    RestRes restartPower(String nestId);

    RestRes startReturnToHome(String nestId);

    RestRes aircraftRePush(String nestId);

    RestRes getAircraftPushUrl(String nestId);

    RestRes setGimbalAngle(SetGimbalAngleDTO dto);

    RestRes s100AircraftChargeOn(String nestId);

    RestRes s100AircraftChargeOff(String nestId);

    RestRes miniCamerasThermalMode(String nestId);

    RestRes miniCamerasVisibleMode(String nestId);

    RestRes miniCameraMsxMode(String nestId);

    RestRes onAirConditioned(String nestId);

    RestRes offAirConditioned(String nestId);

    RestRes closeSteerPower(String nestId);

    RestRes openSteerPower(String nestId);

    RestRes mpsReset(String nestId);

    RestRes cpsReset(String nestId);

    RestRes androidBoardsRestart(String nestId);

    RestRes formatCpsMemoryCard(String nestId);

    RestRes formatAirSdCard(String nestId);

    RestRes stopStartUpProcess(String nestId);

    RestRes stopFinishProcess(String nestId);

    RestRes stopAllProcess(String nestId);

//    RestRes shutBoot(Integer nestId);

    RestRes onAircraftS100V2(String nestId);

    RestRes offAircraftS100V2(String nestId);

    RestRes m300ExchangeBattery(String nestId);

    RestRes resetLift(String nestId);

    RestRes m300SwitchCamera(String nestId, Integer model);

    RestRes startPhotograph(String nestId);

    RestRes getCpsVersion(String nestId);

    RestRes startCompassSet(String nestId, Boolean active);

    RestRes getNestErrMsg(String nestId,Integer which);

    RestRes rcPair(String nestId);

    RestRes calibrationCompass(Boolean enable, String nestId);

    RestRes loginDjiAccount(Map<String, Object> param);

    RestRes getDjiLoginStatus(String nestId);

    RestRes detectionNetworkState(DetectionNetworkDTO detectionNetworkDto);

    RestRes clearDjiCacheFile(String nestId);

    RestRes getWaypointList(String nestId, String missionId);

    RestRes mpsSystemSelfCheck(String nestId);

    RestRes getCpsAndSdMemoryUseRate(String nestId,Integer which);

    RestRes originalRoadGoHome(String nestId);

    RestRes forceLand(String nestId);

    /**
     * 自动降落
     *
     * @param nestId 机巢id
     * @return cps设置结果
     **/
    RestRes autoLand(String nestId);

    /**
     * 设置相机热红外模式的颜色
     *
     * @param nestId 机巢id
     * @return cps设置结果
     * @author sjx
     **/
    RestRes setCameraInfraredColor(String nestId, InfraredColorEnum infraredColor);

    RestRes againLand(String nestId);

    /**
     * 更新推流模式
     *
     * @param nestId
     * @return
     */
    RestRes updatePushStreamMode(String nestId);

    /**
     * 重置推流模式
     *
     * @param nestId
     * @return
     */
    RestRes resetPushStream(String nestId);

    /**
     * USB重连
     *
     * @param nestId
     * @return
     */
    RestRes reconnectUsb(String nestId);

    /**
     * 获取巢内巢外摄像头信息
     *
     * @param nestId
     * @return
     */
    RestRes listCameraInfosFromCps(String nestId);

    /**
     * 设置巢外巢内推流地址
     *
     * @param setCameraInfosDTO
     * @return
     */
    RestRes setCameraInfosToCps(SetCameraInfosDTO setCameraInfosDTO);

    /**
     * 设置无人机推流地址
     *
     * @param airPushStreamDTO
     * @return
     */
    RestRes setAirPushStreamUrl(AirPushStreamDTO airPushStreamDTO);

    RestRes chargeDeviceTight(String nestId);

    RestRes chargeDeviceLoose(String nestId);

    List<String> listNestUuidsCache();

    @Deprecated
    NestTypeEnum getNestTypeCache(String nestUuid);

    /**
     * 获取component
     * @param nestId
     * @return
     */
    public ComponentManager getComponentManager(String nestId);

    /**
     * 基站和单位关系变更
     *
     * @param nestId      巢id
     * @param orgCodeList 组织代码列表
     */
    void synNestOrg(String nestId, List<String> orgCodeList);
}
