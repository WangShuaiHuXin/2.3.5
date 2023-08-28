package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.DJICommonService;
import com.imapcloud.nest.v2.service.dto.in.ChargeLiveLensInDTO;
import com.imapcloud.nest.v2.service.dto.in.LimitDistanceInDTO;
import com.imapcloud.nest.v2.service.dto.in.LimitHeightInDTO;
import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.DjiMqttResult;
import com.imapcloud.sdk.manager.dji.DjiDockManagerCf;
import com.imapcloud.sdk.pojo.constant.DjiDockProperty;
import com.imapcloud.sdk.pojo.constant.dji.DjiErrorCodeEnum;
import com.imapcloud.sdk.pojo.djido.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonServiceImpl.java
 * @Description DJICommonServiceImpl
 * @createTime 2022年10月19日 15:53:00
 */
@Slf4j
@Service
public class DJICommonServiceImpl implements DJICommonService {

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private CommonNestStateService commonNestStateService;

    /**
     * 获取Manager
     *
     * @param nestId
     * @return
     */
    public DjiDockManagerCf getDjiDockManagerCf(String nestId) {
        String uuid = this.baseNestService.getNestUuidByNestIdInCache(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm == null) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_DOCK_OFFLINE.getContent(), new Object[]{uuid}, ""));
        }
        DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();
        return djiDockManagerCf;
    }

    /**
     * 组装返回DTO
     *
     * @param result
     * @return
     */
    public DJICommonResultOutDTO.CommonResultOutDTO getResult(DjiMqttResult<RemoteDebugReplyDO> result) {
        DJICommonResultOutDTO.CommonResultOutDTO resultOut = new DJICommonResultOutDTO.CommonResultOutDTO();
        resultOut.setResult(result.isSuccess() ? 1 : 0);
        String statusCode = Optional.ofNullable(result)
                .map(DjiMqttResult::getCommonDO)
                .map(DjiCommonDO::getData)
                .map(RemoteDebugReplyDO::getOutput)
                .map(RemoteDebugReplyDO.Output::getStatus)
                .orElseGet(() -> ""), statusStr = RemoteDebugReplyDO.StatusEnum.getInstance(statusCode).name();
        resultOut.setStatusCode(statusCode);
        resultOut.setStatusStr(statusStr);
        return resultOut;
    }


    /**
     * 开启/关闭DEBUG
     *
     * @param nestId
     * @param open
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO debugMode(String nestId, Boolean open) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO = new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<RemoteDebugReplyDO> outDO = new DjiMqttResult<RemoteDebugReplyDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        if (open) {
            outDO = djiDockManagerCf.openDebugMode();
        } else {
            outDO = djiDockManagerCf.closeDebugMode();
        }
        if (!outDO.isSuccess()) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent(), new Object[]{outDO.getErrMsg()}, ""));
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }

    /**
     * 开启/关闭 补光灯
     *
     * @param nestId
     * @param open
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO supplementLight(String nestId, Boolean open) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO = new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<RemoteDebugReplyDO> outDO = new DjiMqttResult<RemoteDebugReplyDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        if (open) {
            outDO = djiDockManagerCf.openSupplementLight();
        } else {
            outDO = djiDockManagerCf.closeSupplementLight();
        }
        if (!outDO.isSuccess()) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent(), new Object[]{outDO.getErrMsg()}, ""));
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }

    /**
     * 一键返航
     *
     * @param nestId
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO returnHome(String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO = new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<RemoteDebugReplyDO> outDO = new DjiMqttResult<RemoteDebugReplyDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        outDO = djiDockManagerCf.returnHome();
        if (!outDO.isSuccess()) {
//            throw new BusinessException( String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RETURN_FAILS_REASON_FOR_FAILURE.getContent())
//                    +"：%s",outDO.getErrMsg()) );
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent(), new Object[]{outDO.getErrMsg()}, ""));
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }

    /**
     * 机场重启
     *
     * @param nestId
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO deviceReboot(String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO = new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<RemoteDebugReplyDO> outDO = new DjiMqttResult<RemoteDebugReplyDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        outDO = djiDockManagerCf.rebootDevice();
        if (!outDO.isSuccess()) {
//            throw new BusinessException( String.format(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRPORT_RESTART_FAILED_REASON_OF_FAILURE.getContent())
//                    +"：%s",outDO.getErrMsg()) );
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent(), new Object[]{outDO.getErrMsg()}, ""));
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }

    /**
     * 开启/关闭 飞行器
     *
     * @param nestId
     * @param open
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO drone(String nestId, Boolean open) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO = new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<RemoteDebugReplyDO> outDO = new DjiMqttResult<RemoteDebugReplyDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        if (open) {
            outDO = djiDockManagerCf.openDrone();
        } else {
            outDO = djiDockManagerCf.closeDrone();
        }
        if (!outDO.isSuccess()) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent(), new Object[]{outDO.getErrMsg()}, ""));
//            throw new BusinessException( String.format("%s 飞行器失败，失败原因：%s",(open?"开启":"关闭"),outDO.getErrMsg()) );
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }

    /**
     * 数据格式化
     *
     * @param nestId
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO deviceFormat(String nestId) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO = new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<RemoteDebugReplyDO> outDO = new DjiMqttResult<RemoteDebugReplyDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        outDO = djiDockManagerCf.formatDevice();
        if (!outDO.isSuccess()) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent(), new Object[]{outDO.getErrMsg()}, ""));
//            throw new BusinessException( String.format("数据格式化失败，失败原因：%s",outDO.getErrMsg()) );
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }

    /**
     * 开启/关闭 舱盖
     *
     * @param nestId
     * @param open
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO cover(String nestId, Boolean open) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO = new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<RemoteDebugReplyDO> outDO = new DjiMqttResult<RemoteDebugReplyDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        if (open) {
            outDO = djiDockManagerCf.openCover();
        } else {
            outDO = djiDockManagerCf.closeCover();
        }
        if (!outDO.isSuccess()) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent(), new Object[]{outDO.getErrMsg()}, ""));
//            throw new BusinessException( String.format("%s 舱盖失败，失败原因：%s",(open?"开启":"关闭"),outDO.getErrMsg()) );
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }

    /**
     * 开启/关闭 推杆
     *
     * @param nestId
     * @param open
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO putter(String nestId, Boolean open) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO = new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<RemoteDebugReplyDO> outDO = new DjiMqttResult<RemoteDebugReplyDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        if (open) {
            outDO = djiDockManagerCf.openPutter();
        } else {
            outDO = djiDockManagerCf.closePutter();
        }
        if (!outDO.isSuccess()) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent(), new Object[]{outDO.getErrMsg()}, ""));
//            throw new BusinessException( String.format("%s 推杆失败，失败原因：%s",(open?"开启":"关闭"),outDO.getErrMsg()) );
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }

    /**
     * 开启/关闭 充电
     *
     * @param nestId
     * @param open
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO charge(String nestId, Boolean open) {
        DJICommonResultOutDTO.CommonResultOutDTO outDTO = new DJICommonResultOutDTO.CommonResultOutDTO();
        outDTO.setResult(0);
        DjiMqttResult<RemoteDebugReplyDO> outDO = new DjiMqttResult<RemoteDebugReplyDO>();
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        if (open) {
            outDO = djiDockManagerCf.openCharge();
        } else {
            outDO = djiDockManagerCf.closeCharge();
        }
        if (!outDO.isSuccess()) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_COMMON_ACTION_ERR.getContent(), new Object[]{outDO.getErrMsg()}, ""));
//            throw new BusinessException( String.format("%s 充电失败，失败原因：%s",(open?"开启":"关闭"),outDO.getErrMsg()) );
        }
        outDTO = this.getResult(outDO);
        return outDTO;
    }

    @Override
    public void switchSdrWorkMode(String uuid, Boolean open) {
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(uuid);
        SwitchSdrWorkModeDO switchSdrWorkModeDO = new SwitchSdrWorkModeDO();
        if (open) {
            switchSdrWorkModeDO.setLinkWorkmode(SwitchSdrWorkModeDO.ModeEnum.G4.ordinal());
        } else {
            switchSdrWorkModeDO.setLinkWorkmode(SwitchSdrWorkModeDO.ModeEnum.SDR.ordinal());
        }
        DjiMqttResult<DjiCommonDataDO> djiResult = djiDockManagerCf.switchSdrWorkMode(switchSdrWorkModeDO);
        if (!djiResult.isSuccess()) {
            throw new BusinessException(String.format("%s增强模式失败，失败原因：%s", open ? "开启" : "关闭", djiResult.getErrMsg()));
        }
    }

    /**
     * 切换直播镜头
     *
     * @param dto
     */
    @Override
    public void chargeLiveLens(ChargeLiveLensInDTO dto) {
        Integer videoTypeOrdinal = dto.getVideoType();
        ChangeLiveLensDO changeLiveLensDO = new ChangeLiveLensDO();
        String videoType = ChangeLiveLensDO.VideoTypeEnum.getVideoType(videoTypeOrdinal);

        String nestUuid = this.baseNestService.getNestUuidByNestIdInCache(dto.getNestId());
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm == null) {
            throw new BusinessException(MessageUtils
                    .getMessage(MessageEnum.GEOAI_UOS_DJI_DOCK_OFFLINE.getContent(), new Object[]{nestUuid}, ""));
        }
        DjiDockLiveIdDO djiDockLiveIdDO = commonNestStateService.getDjiDockLiveIdDO(nestUuid);
        changeLiveLensDO.setVideoId(djiDockLiveIdDO.getAircraftLiveVideoId());
        changeLiveLensDO.setVideoType(videoType);
        DjiMqttResult<DjiCommonDataDO> djiResult = cm.getDjiDockManagerCf().changeLiveLens(changeLiveLensDO);
        if (!djiResult.isSuccess()) {
            throw new BusinessException(String.format("切换镜头失败，失败原因：%s", djiResult.getErrMsg()));
        }
    }

    /**
     * 无人机数据格式化
     *
     * @param nestId
     * @return
     */
    @Override
    public DJICommonResultOutDTO.CommonResultOutDTO droneFormat(String nestId) {
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(nestId);
        DjiMqttResult<RemoteDebugReplyDO> djiResult = djiDockManagerCf.formatDrone();
        if (!djiResult.isSuccess()) {
            throw new BusinessException(String.format("无人机数据格式化失败，失败原因：%s", djiResult.getErrMsg()));
        }
        return this.getResult(djiResult);
    }

    /**
     * 限高设置
     *
     * @param limitHeightInDTO
     */
    @Override
    public void limitHeight(LimitHeightInDTO limitHeightInDTO) {
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(limitHeightInDTO.getNestId());
        DjiMqttResult<Object> djiResult = djiDockManagerCf.setDeviceProperty(DjiDockProperty.HEIGHT_LIMIT, limitHeightInDTO.getHeight(),HeightLimitPropertySetResultDO.class);
        HeightLimitPropertySetResultDO data = (HeightLimitPropertySetResultDO)djiResult.getCommonDO().getData();
        Integer result = data.getHeightLimit().getResult();
        boolean success = DjiErrorCodeEnum.isSuccess(result);
        if(!success) {
            String msg = DjiErrorCodeEnum.getMsg(result);
            throw new BusinessException(String.format("限高设置失败，失败原因：%s", msg));
        }
    }

    /**
     * 限远设置
     *
     * @param limitDistanceInDTO
     */
    @Override
    public void limitDistance(LimitDistanceInDTO limitDistanceInDTO) {
        DjiDockManagerCf djiDockManagerCf = this.getDjiDockManagerCf(limitDistanceInDTO.getNestId());
        LimitDistanceDO limitDistanceDO = new LimitDistanceDO();
        limitDistanceDO.setState(null);
        limitDistanceDO.setDistanceLimit(limitDistanceInDTO.getDistance());
        DjiMqttResult<Object> djiResult = djiDockManagerCf.setDeviceProperty(DjiDockProperty.DISTANCE_LIMIT_STATUS, limitDistanceDO,DistanceLimitStatusPropertySetResultDO.class);
        DistanceLimitStatusPropertySetResultDO data = (DistanceLimitStatusPropertySetResultDO) djiResult.getCommonDO().getData();
        Integer result = data.getDistanceLimitStatus().getDistanceLimit().getResult();
        boolean success = DjiErrorCodeEnum.isSuccess(result);
        if(!success) {
            String msg = DjiErrorCodeEnum.getMsg(result);
            throw new BusinessException(String.format("限远设置失败，失败原因：%s", msg));
        }
    }


}
