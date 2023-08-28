package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.DjiStartTaskParamDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 任务表 Mapper 接口
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface TaskMapper extends BaseMapper<TaskEntity> {

    /**
     * 通过userId和nestId批量查询任务信息
     *
     * @param userId
     * @param nestId
     * @return
     */
    @Select("select id,name,description from task where nest_id = #{nestId} and create_user_id = #{userId} and deleted = 0")
    List<TaskEntity> batchSelectByUserIdAndNestId(@Param("userId") Integer userId, @Param("nestId") Integer nestId);

    /**
     * 软删除
     *
     * @param id
     * @return
     */
    @Update("update task set deleted = 1 where id = #{id}")
    int softDeleteById(@Param("id") Integer id);

    /**
     * 批量软删除
     *
     * @param idList
     * @return
     */
    int batchSoftDeleteByIds(@Param("idList") List<Integer> idList);

    /**
     * 查询任务类型
     *
     * @param id
     * @return
     */
    @Select("SELECT type FROM task WHERE id = #{id} AND deleted = 0")
    Integer selectTypeById(Integer id);

    @Select("SELECT * FROM task WHERE nest_id = #{nestId} AND deleted = 0")
    List<TaskEntity> batchSelectByNestId(@Param("nestId") Integer nestId);

    @Select("SELECT * FROM task WHERE base_nest_id = #{baseNestId} AND deleted = 0 AND org_code LIKE #{visibleOrgCode}")
    List<TaskEntity> batchSelectByBaseNestId(@Param("baseNestId") String baseNestId, @Param("visibleOrgCode") String visibleOrgCode);

    /**
     * 获取该单位的手动飞行航线id
     *
     * @param unitId
     * @return
     */
    @Select("SELECT id FROM task WHERE mold = 1 AND nest_id = 0 AND type = -1 AND deleted = 0 AND org_code = #{orgCode} ")
    Integer getAPPManualFlyTaskId(String orgCode);

    /**
     * 查询基站id通过航线id
     *
     * @param airLineId
     * @return
     */
    @Select("SELECT t.id,nest_id,mold,type,t.org_code FROM task t JOIN mission m ON t.id = m.task_id AND m.air_line_id = #{airLineId}")
    TaskEntity selectNestIdByAirLineId(@Param("airLineId") Integer airLineId);


    String selectBaseNestIdByRecordsId(Integer missionRecordsId);

    DjiStartTaskParamDTO selectDjiStartTaskParamDTOByMissionId(Integer missionId);
}
