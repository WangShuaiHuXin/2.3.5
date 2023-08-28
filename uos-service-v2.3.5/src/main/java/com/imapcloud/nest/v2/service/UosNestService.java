package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.GimbalAutoFollowDTO;
import com.imapcloud.nest.v2.service.dto.in.GimbalAutoFollowResetDTO;
import com.imapcloud.nest.v2.service.dto.in.NestExtFirmwareQueryInDTO;
import com.imapcloud.nest.v2.service.dto.in.NestQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.NestExtFirmwareOutDTO;
import com.imapcloud.nest.v2.service.dto.out.RegionNestOutDTO;
import com.imapcloud.nest.v2.web.vo.req.NestMediaSaveInfoVO;
import com.imapcloud.nest.v2.web.vo.resp.LivePlayInfoRespVO;
import com.imapcloud.nest.v2.web.vo.resp.NestCodeOperationVO;

import java.util.List;

/**
 * 基站业务接口定义
 *
 * @author Vastfy
 * @date 2022/7/12 13:57
 * @since 1.9.7
 */
public interface UosNestService {

    /**
     * 重置基站当前相机设置
     *
     * @param nestId 基站ID
     */
    boolean resetCameraSettings(String nestId);

    /**
     * 根据条件获取所有基站信息，并按照区域维度进行分组返回
     *
     * @param condition 查询条件
     * @return 区域维度展示的基站信息
     */
    List<RegionNestOutDTO> listGroupByRegion(NestQueryInDTO condition);

    /**
     * 分页获取含固件信息的基站基本数据
     *
     * @param condition 查询条件
     * @return 返回结果
     */
    PageResultInfo<NestExtFirmwareOutDTO> pageNestInfoWithFirmwares(NestExtFirmwareQueryInDTO condition);

    /**
     * 查询监控页面人数
     * @return
     */
    List<String> listOnLineUsernames(String nestId);

    PageResultInfo<NestCodeOperationVO> listNestCodeOperationRecords(String nestId, Integer currPage, Integer pageSize);

    Boolean startGimbalAutoFollow(GimbalAutoFollowDTO dto);

    Boolean cancelGimbalAutoFollow(String nestId);

    Boolean exitGimbalAutoFollow(String nestId);

    Boolean execGimbalAutoFollow(String nestId);

    Boolean gimbalAutoFollowReset(GimbalAutoFollowResetDTO dto);

    Integer getCameraVideoSource(String nestId);

//    /**
//     * @deprecated 2.3.2，将在后续版本删除
//     */
//    @Deprecated
//    Result<Object> resetCameraStream(String nestId);

//    Boolean saveNestMediaInfo(String nestId, NestMediaSaveInfoInDTO body);

    /**
     * 获取设备内外穿透映射地址
     * @param nestId    基站ID
     * @param lanIp    内外IP
     * @return  内外穿透映射地址
     */
    String getNestDeviceSettingUrl(String nestId, String lanIp);

    Boolean saveNestMediaInfo(String nestId, NestMediaSaveInfoVO body);

    /**
     * 点播基站设备视频
     * @param nestId    基站ID
     * @param deviceUse 基站设备用途
     * @return  点播信息
     */
    LivePlayInfoRespVO playNestLive(String nestId, Integer deviceUse);

}
