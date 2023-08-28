package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.SysTagEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.SysTaskTagEntity;
import com.imapcloud.nest.pojo.dto.TaskTagDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 系统标签表 Mapper 接口
 * </p>
 *
 * @author kings
 * @since 2020-12-03
 */
public interface SysTagMapper extends BaseMapper<SysTagEntity> {
    /**
     * 获取当前单位的全部tagId
     * @param unitId
     * @return
     */
    List<SysTagEntity> getAllTagListByUnitId(@Param("orgCode") String orgCode);

    List<Integer> getTagIds(@Param("type") Integer type,@Param("defectStatus") Integer defectStatus,@Param("startTime")String startTime,@Param("endTime")String endTime);

    /**
     * 查询改标签是否有在使用
     * @param tagId
     * @return
     */
    Integer getTagUseNum(Integer tagId);

    List<SysTaskTagEntity> getListByTagId(@Param("tagId") Long id, @Param("nestId") String nestId, @Param("orgCode") String orgCode);

    List<Integer> selectMissionByType(Integer dataType);

    @Select("SELECT st.name FROM sys_task_tag stt,sys_tag st WHERE stt.task_id = #{taskId} AND stt.tag_id = st.id AND stt.deleted = 0;")
    String selectTagNameByTaskId(@Param("taskId") Integer taskId);


    List<TaskTagDTO> batchSelectByTaskIds(@Param("taskIdList") List<Integer> taskIdList);
}
