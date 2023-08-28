package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.SysTaskTagEntity;
import com.imapcloud.nest.pojo.dto.MissionRecordsDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务标签关系表 服务类
 * </p>
 *
 * @author kings
 * @since 2020-12-03
 */
public interface SysTaskTagService extends IService<SysTaskTagEntity> {

    List<SysTaskTagEntity> listTaskTagAndName(List<Integer> taskIds);

    List<SysTaskTagEntity> listAllTaskTagAndName();

    /**
     * 更具航线表id查询tagId
     * @param airLineId
     * @return
     */
    List<Integer> selectTagIdByAirLineId(Integer airLineId);

    List<SysTaskTagEntity> getSysTaskTag();

    IPage<MissionRecordsDto> getMissionRecords(Map<String, Object> params, Integer tagId, Integer dataType, String name);

    List<Integer> getMissionRecordsIds(List<Long> tagIds,String startTime,String endTime);

    String getTagNameByTaskIdCache(Integer taskId);
}
