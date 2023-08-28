package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.SysTaskTagEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.MissionRecordsDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 任务标签关系表 Mapper 接口
 * </p>
 *
 * @author kings
 * @since 2020-12-03
 */
public interface SysTaskTagMapper extends BaseMapper<SysTaskTagEntity> {

    List<SysTaskTagEntity> listTaskTagAndName(@Param("taskIds") List<Integer> taskIds);

    List<SysTaskTagEntity> listAllTaskTagAndName();

    /**
     * 根据airLineId查询tagId
     *
     * @param airLineId
     * @return
     */
    @Select("SELECT tag_id FROM sys_task_tag WHERE task_id = (SELECT task_id FROM mission WHERE air_line_id = #{airLineId})")
    List<Integer> selectTagIdByAirLineId(@Param("airLineId") Integer airLineId);

    @Select("select stt.* from sys_task_tag stt,task t where stt.task_id = t.id and stt.deleted = 0 and t.deleted = 0")
    List<SysTaskTagEntity> getSysTaskTag();

    IPage<MissionRecordsDto> getMissionRecords(@Param("page") IPage<TaskEntity> page, @Param("tagId") Integer tagId, @Param("dataType") Integer dataType, @Param("name") String name);

    List<Integer> getMissionRecordsIds(@Param("tagIds") List<Long> tagIds,@Param("startTime") String startTime, @Param("endTime") String endTime);

    String getTagNameByTaskId(Integer taskId);
}
