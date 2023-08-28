package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.TaskFileEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName TaskFileMapper.java
 * @Description TaskFileMapper
 * @createTime 2022年07月13日 15:01:00
 */
@Mapper
public interface TaskFileMapper extends BaseMapper<TaskFileEntity> {

    /**
     * 新增
     * @author double
     * @date 2022/10/24
     **/
    int insertTaskFile(TaskFileEntity taskFile);

    /**
     * 刪除
     * @author double
     * @date 2022/10/24
     **/
    int deleteTaskFile(String taskFileId);

    /**
     * 刪除
     * @author double
     * @date 2022/10/24
     **/
    int deleteByTaskIdMissionIds(String taskId, List<String> missionId);

    /**
     * 更新
     * @author double
     * @date 2022/10/24
     **/
    int updateTaskFile(TaskFileEntity taskFile);

    /**
     * 查询已删除的数据
     * @return
     */
    List<TaskFileEntity> getAllByDeletedTaskFile();


}
