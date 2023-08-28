package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.dto.MissionRecordsStatisticsDto;
import com.imapcloud.nest.pojo.dto.MissionRecordsTotalDTO;
import com.imapcloud.nest.v2.dao.entity.FirmwarePackageEntity;
import com.imapcloud.nest.v2.dao.po.in.MissionRecordsCriteriaPO;
import com.imapcloud.nest.v2.dao.po.out.MissionRecordsOutPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 架次记录表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
public interface MissionRecordsMapper extends BaseMapper<MissionRecordsEntity> {
    /**
     * 通过execId批量更新
     *
     * @param list
     * @return
     */
    int updateBatchByExecId(@Param("list") List<MissionRecordsEntity> list);

    /**
     * 通过execId更新
     *
     * @param missionRecordsEntity
     * @return
     */
    int updatePart5ByExecId(MissionRecordsEntity missionRecordsEntity);

    /**
     * 通过id获取架次id、架次执行状态、图片上传状态
     *
     * @param id
     * @return
     */
    MissionRecordsEntity getMissionRecordById(Integer id);

    /**
     * 通过id修改图片上传状态
     *
     * @param dataStatus
     * @param id
     */
    void updateDataStatusById(@Param("dataStatus") Integer dataStatus, @Param("id") Integer id);


    /**
     * 修改备份状态通过execId
     *
     * @param status
     * @param execId
     * @return
     */
    int updateBackUpStatusByExecId(@Param("status") Integer status, @Param("execId") String execId);


    /**
     * 通过execId获取missionId和recordId
     *
     * @param execId
     * @return
     */
    MissionRecordsEntity getByExecId(@Param("execId") String execId);

    /**
     * 通过架次执行id获取数据状态
     *
     * @param id
     * @return
     */
    Integer getDataStatusById(Integer id);

    /**
     * 获取数据回传选项状态
     *
     * @param id
     * @return
     */
    Integer getGainDataModeById(Integer id);

    /**
     * 通过架次id获取图片zip包名称
     *
     * @param id
     * @return
     */
    String getDataPathById(Integer id);

    /**
     * 通过架次id获取视频zip包名称
     *
     * @param id
     * @return
     */
    @Select("SELECT video_path FROM mission_records WHERE id = #{id}")
    String getVideoPathById(Integer id);

    /**
     * 通过id获取execId
     *
     * @param id
     * @return
     */
    @Select("SELECT exec_id FROM mission_records WHERE id = #{id}")
    String getExecIdById(Integer id);

    /**
     * 通过id获取机巢uuid
     *
     * @param id
     * @return
     */
    @Select("SELECT id, uuid,type FROM base_nest where nest_id = (SELECT base_nest_id FROM task WHERE id = ( SELECT task_id FROM mission WHERE id = (SELECT mission_id FROM mission_records WHERE id = #{id})))")
    NestEntity getNestUuidById(Integer id);

    @Select("SELECT uuid FROM base_nest where nest_id = (SELECT base_nest_id FROM task WHERE id = ( SELECT task_id FROM mission WHERE id = (SELECT mission_id FROM mission_records WHERE id = #{id})))")
    String getNestUuidByRecordsId(Integer id);


    /**
     * 通过id获取移动终端uuid
     *
     * @param missionRecordsId
     * @return
     */
    @Select("select device_id from sys_app where id = (select app_id from mission_records where id = #{id})")
    String getNestUuidByAppId(Integer missionRecordsId);

    /**
     * 通过id修改back_up_status为1：是（照片已备份到机巢）
     *
     * @param id
     */
    @Update("UPDATE mission_records SET back_up_status = 1 WHERE id = #{id} ")
    void updateBackUpStatus(Integer id);

    @Update("UPDATE mission_records SET data_status = #{status} WHERE exec_id = #{execId}")
    int updateDataStatusByExecId(String execId, Integer status);

    /**
     * 查询以一个架次的飞行架次
     *
     * @param missionId
     * @return
     */
    @Select("SELECT COUNT(*) FROM mission_records WHERE mission_id = #{missionId}")
    int selectFlyTimesByMissionId(Integer missionId);

    /**
     * 通过架次执行id获取是否录制视频
     *
     * @param recordId
     * @return
     */
    @Select("SELECT gain_video FROM mission_records WHERE id = #{recordId}")
    Integer selectGainVideo(Integer recordId);

    /**
     * 通过架次执行id获取架次名称
     *
     * @param recordId
     * @return
     */
    @Select("SELECT name FROM mission WHERE id = (SELECT mission_id FROM mission_records WHERE id = #{recordId})")
    String selectMissionName(Integer recordId);

    /**
     * 获取架次执行的第几次飞行
     *
     * @param recordId
     * @return
     */
    @Select("SELECT fly_index FROM mission_records WHERE id = #{recordId}")
    Integer selectFlyIndex(Integer recordId);

    /**
     * 通过missionId获取最新的recordId
     *
     * @param missionId
     * @return
     */
    @Select("SELECT id FROM mission_records WHERE mission_id = #{missionId} AND deleted = 0 ORDER BY id DESC LIMIT 1 ")
    Integer selectIdByMissionId(Integer missionId);

    /**
     * 通过recordId获取missionId
     *
     * @param recordId
     * @return
     */
    @Select("SELECT mission_id FROM mission_records WHERE id = #{recordId}")
    Integer selectMissionIdByRecordId(Integer recordId);

    /**
     * 通过recordId获取app的图传地址
     *
     * @param recordId
     * @return
     */
    @Select("SELECT pull_http FROM sys_app WHERE id = (SELECT app_id FROM mission_records WHERE id = #{recordId} )")
    String getAppPullHttpUrl(Integer recordId);

    /**
     * 首页-获取巡检记录统计的飞行里程、飞行时间
     *
     * @param type
     * @return
     */
    Map getRecordTotalMilesAndTime(@Param("type") Integer type, @Param("orgCode") String orgCode, @Param("userId") Long userId);

    /**
     * 首页-获取巡检记录分页列表
     *
     * @param page
     * @return
     */
    IPage<MissionRecordsTotalDTO> getMissionRecordPageList(IPage<MissionRecordsTotalDTO> page,
                                                           @Param("orgCode") String orgCode,
                                                           @Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("accountId") String accountId);

    List<MissionRecordsEntity> getMissionRecordListBy(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("missionIds") List<Integer> missionIds);

    List<MissionRecordsStatisticsDto> getMissionRescordsByNest(@Param("orgCode") String orgCode);

    /**
     * 根据架次id获取任务id
     *
     * @param missionRecordsId
     * @return
     */
    Integer getTaskIdByRecordsId(Integer missionRecordsId);

    /**
     * 根据任务id获取架次id
     *
     * @param taskId
     * @return
     */
    List<Integer> getMissionRecordsIdByTaskId(Integer taskId);

    @Select("SELECT COUNT(*) FROM mission_records WHERE exec_id =#{execId} AND deleted = 0")
    Integer countRecordsByExecId(@Param("execId") String execId);

    Map getTotalInspectTimes(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("orgCode") String orgCode, @Param("accountId") String accountId);

    @Select("SELECT exec_id FROM mission_records WHERE id = (SELECT MAX(id) FROM mission_records WHERE mission_id = #{missionId}) ")
    String selectMaxIdByMissionId(Integer missionId);

    @Select("SELECT id,exec_id FROM mission_records WHERE id = (SELECT MAX(id) FROM mission_records WHERE mission_id = #{missionId})")
    MissionRecordsEntity selectLastRecordsByMissionId(Integer missionId);

    long countRecordsByCondition(@Param("criteria")  MissionRecordsCriteriaPO var1);

    List<MissionRecordsOutPO> selectRecordsByCondition(@Param("criteria") MissionRecordsCriteriaPO var1, @Param("restrict") PagingRestrictDo var2);

    /**
     * 根据架次记录id查询航线信息
     * @param missionRecordsId
     * @return
     */
    String getAirLineId(@Param("missionRecordsId") String missionRecordsId,@Param("taskId") String taskId);

    /**
     * 批量通过id获取架次id
     */
    List<MissionRecordsEntity> getMissionRecordByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据架次获取架次顺序
     */
    Integer getMissionSeqById(@Param("missionId") Integer missionId);
}
