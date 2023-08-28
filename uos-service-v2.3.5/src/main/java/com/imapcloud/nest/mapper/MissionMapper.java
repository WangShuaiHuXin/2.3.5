package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.MissionVideoEntity;
import com.imapcloud.nest.pojo.dto.MissionDto;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.AppMissionAircraftDTO;
import com.imapcloud.nest.v2.dao.po.out.NhOrderRecordInfoOutPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 架次表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface MissionMapper extends BaseMapper<MissionEntity> {

    /**
     * 通过taskId批量查询架次的航线Id和架次名字
     *
     * @param taskIds
     * @return
     */
    List<MissionEntity> batchSelectByTaskIdList(@Param("taskIdList") List<Integer> taskIds);

    /**
     *  原查询记录接口
     * @param page
     * @param taskName
     * @param nestId
     * @param missionRecordsIds
     * @param unitList
     * @return
     */
//    IPage<MissionDto> getAllMissionPage(@Param("page") IPage<MissionDto> page, @Param("taskName") String taskName, @Param("nestId") Integer nestId,@Param("missionRecordsIds") List<Integer> missionRecordsIds,@Param("unitList")List<Integer> unitList);

    /**
     *  针对气体模块进行调整
     * @param page
     * @param taskName
     * @param nestId
     * @param execIds
     * @return
     */
    IPage<MissionDto> getAllMissionPage(@Param("page") IPage<MissionDto> page,
                                        @Param("taskName") String taskName,
                                        @Param("nestId") String nestId,
                                        @Param("execIds") List<String> execIds,
                                        @Param("orgCode")String orgCode);


    /**
     * 获取移动终端的任务架次记录分页列表
     *
     * @param page
     * @param appId
     * @param taskName
     * @return
     */
    IPage<MissionDto> getAppMissionRecordPage(@Param("page") IPage<MissionDto> page, @Param("appId") String appId, @Param("taskName") String taskName);

    /**
     * 通过id获取mession的uuid
     *
     * @param id
     * @return
     */
    String getUuidById(Integer id);

    /**
     * 通过missionId获取nestId
     *
     * @param id
     * @return
     */
    Integer getNestIdById(Integer id);

    /**
     * 通过missionId获取机巢的类型（固定、mini）
     *
     * @param id
     * @return
     */
    @Select("SELECT type FROM nest WHERE id =  (SELECT nest_id FROM task WHERE id = (SELECT task_id FROM mission WHERE id = #{id}))")
    Integer getNestTypeById(Integer id);

    List<MissionDto> listMissionByNestId(@Param("nestId") Integer nestId, @Param("taskName") String taskName);

    /**
     * 批量查询航线id,和任务Id
     *
     * @param taskIdList
     * @return
     */
    List<MissionEntity> batchSelectAirLineIdAndTaskIdByTaskIdList(@Param("taskIdList") List<Integer> taskIdList);

    /**
     * 通过ID进行批量软删除
     *
     * @param idList
     * @return
     */
    int batchSoftDeleteByIds(@Param("idList") List<Integer> idList);

    /**
     * 查询照片数量
     *
     * @param id
     * @return
     */
    @Select("SELECT photo_count AS photoCount, video_count AS videoCount FROM air_line WHERE id = (SELECT air_line_id FROM mission WHERE id = #{id})")
    Map<String, Integer> selectPhotoCountById(@Param("id") Integer id);

    @Select("")
    Integer getLastRecordIdById(Integer id);


    /**
     * 通过机巢uuid获取架次list
     *
     * @return
     */
    List<AppMissionAircraftDTO> getMissionByNestUuid(@Param("limit") Integer limit, @Param("nestUuid") String nestUuid, @Param("lastTime") Long lastTime);

    /**
     * @param id
     * @return
     */
    String selectNameById(@Param("id") Integer id);


    /**
     * 根据missionId获取airLineId
     *
     * @param missionId
     * @return
     */
    @Select("SELECT air_line_id FROM mission WHERE id = #{missionId} ")
    Integer getAirLineIdByMissionId(Integer missionId);

    /**
     * 根据missionId获取airLine
     *
     */
    @Select("SELECT * FROM air_line WHERE id IN (SELECT air_line_id FROM mission WHERE task_id = #{taskId} AND deleted = 0)")
    List<AirLineEntity> getAirLineByTaskId(Integer taskId);

    /**
     * 根据missionId获取airLineId
     *
     * @param missionId
     * @return
     */
    @Select("SELECT * FROM air_line WHERE id = (SELECT air_line_id FROM mission WHERE id = #{missionId}) ")
    AirLineEntity getAirLineByMissionId(Integer missionId);

    /**
     * 根据missionId获取taskId
     *
     * @param missionId
     * @return
     */
    @Select("SELECT task_id FROM mission WHERE id = #{missionId} ")
    Integer getTaskIdByMissionId(Integer missionId);

    /**
     * 根据机巢id获取所有架次任务列表
     *
     * @param taskName
     * @param nestId
     * @return
     */
    List<MissionDto> getAllMissionsByNestTaskName(@Param("taskName") String taskName, @Param("nestId") String nestId, @Param("orgCode") String orgCode);

    @Select("SELECT DISTINCT mission_records_id FROM `mission_air` where exec_id is not null and exec_id != ''")
    List<Integer> getMissionAirRecordsIds();

    @Select("SELECT DISTINCT exec_id FROM `mission_air` where exec_id is not null and exec_id != ''")
    List<String> getMissionAirExecIds();

    @Select("SELECT\n" +
            "\tmp.start_stop_point_altitude startStopAlt,\n" +
            "\tal.video_length videoLength,\n" +
            "\tal.photo_count photoCount,al.absolute  absolute\n" +
            "FROM\n" +
            "\tmission m,\n" +
            "\tmission_param mp,\n" +
            "\tair_line al \n" +
            "WHERE\n" +
            "\tm.mission_param_id = mp.id \n" +
            "\tAND m.air_line_id = al.id \n" +
            "\tAND m.id = #{missionId}")
    Map<String, Object> getMissionDetails(Integer missionId);

    @Select("select * from mission_video")
    List<MissionVideoEntity> getVideoList();

    @Select("SELECT t.type taskType,n.altitude nestAlt FROM mission m, task t, nest n WHERE m.task_id = t.id AND t.nest_id = n.id AND m.id = #{missionId}")
    Map<String, Object> selectTaskTypeAndNestAlt(Integer missionId);

    @Select("SELECT m.id FROM mission m, task t WHERE m.task_id = t.id AND t.base_nest_id = #{nestId}")
    List<Integer> selectMissionIdByNestId(String nestId);

    @Select("SELECT task.org_code orgCode FROM mission,task WHERE task.id = mission.task_id AND mission.deleted = 0 AND mission.id = #{id}")
    String selectUnitIdById(Integer id);

    @Select("SELECT m.seq_id missionIndex,t.id taskId, t.type taskType, t.sub_type subType FROM mission m ,task t WHERE m.task_id = t.id AND m.id = #{missionId}")
    Map<String, Object> selectMissionTypeDetails(Integer missionId);

    @Select("SELECT * FROM mission WHERE id = (SELECT mission_id FROM mission_records WHERE id = #{missionRecordsId})")
    MissionEntity queryMissionByMissionRecordsId(Integer missionRecordsId);

    List<NhOrderRecordInfoOutPO> selectRecordInfo(@Param("list") Set<Integer> missionKey);
}
