package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.pojo.dto.FlyPoint;
import com.imapcloud.nest.pojo.dto.ImportPcRouteDto;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.dto.out.FpiAirlinePackageParseOutDTO;
import com.imapcloud.sdk.pojo.entity.Waypoint;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 航线表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface AirLineService extends IService<AirLineEntity> {

    /**
     * 查询所有航线
     */
    List<AirLineEntity> listAirLineIdAndName();

    /**
     * 解析航线,并返回一个条适合机巢的航线
     *
     * @param missionId 架次Id
     * @param nestId    机巢Id
     */
    List<Waypoint> parseAirLine(Integer missionId, String nestId,Integer mold);

    /**
     * 导入航线
     */
    RestRes importPointCloudAirLine(ImportPcRouteDto routeDto);

    /**
     * 查询预计飞行距离和合并后的航点数
     */
    List<AirLineEntity> listPredicMilesAndMergeCountByIdList(List<Integer> idList);

    /**
     * 批量软删除
     */
    int batchSoftDeleteByIds(List<Integer> idList);


    Map<String, Object> computeWaypointList(List<Waypoint> waypointList, Double nestLng, Double nestLat, Double airSpeed, Integer startStopAlt);

    /**
     * 通过id获取航线类型
     */
    Integer getAirLineTypeById(Integer id);

    /**
     * 查询航线json数据通过任务id
     */
    RestRes listAirLineJsonByTaskId(Integer taskId);

    /**
     * 获取航线高度、照片覆盖率、协调转弯
     */
    Map<String, Object> queryIsCoorTurning(Integer taskId);

    /**
     * 获取航线照片拍照数
     */
    Integer queryAirLinePhotoCount(Integer taskId);

    Map<String, Object> computeFlyPointList(List<FlyPoint> flyPointList, Double nestLng, Double nestLat, Double airSpeed, Integer startStopAlt);

//    /**
//     * 上传精细巡检包到服务器，并且解析
//     * @deprecated 2.2.3，使用新接口{@link DataParseService#parseFinePatrolInspectionData(String, java.io.InputStream)}替代，将在后续版本删除
//     */
//    @Deprecated
//    RestRes uploadFineInspectionZip(String zipPath,String originalFilename);

    /**
     * 保存精细巡检航线包杆塔信息解析数据
     * @param data  解析数据
     * @return  精细巡检ZIP ID
     */
    String saveFpiPoleTowerInfo(FpiAirlinePackageParseOutDTO data);

    /**
     * 请求点云数据和航线数据
     */
    RestRes requestPointCloudAndRoute(Integer towerId);

    AirLineEntity getPicCountAndVideoCountAndLen(Integer missionId);

}
