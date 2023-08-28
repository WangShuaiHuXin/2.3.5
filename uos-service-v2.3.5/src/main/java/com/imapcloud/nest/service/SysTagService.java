package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.SysTagEntity;
import com.imapcloud.nest.model.SysTaskTagEntity;
import com.imapcloud.nest.pojo.dto.SysTagDto;
import com.imapcloud.nest.pojo.dto.TaskTagDTO;
import com.imapcloud.nest.utils.RestRes;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 系统标签表 服务类
 * </p>
 *
 * @author kings
 * @since 2020-12-03
 */
public interface SysTagService extends IService<SysTagEntity> {

    RestRes addSysTag(SysTagDto sysTagDto);

    /**
     * 获取当前用户所单位的所有标签
     * @return
     * @param defectStatus
     * @param startTime
     * @param endTime
     */
    RestRes getAllTagList(Integer defectStatus, String startTime, String endTime,List<Integer> types,Integer flag);

    /**
     * 获取当前用户所单位的所有标签
     * @param problemStatus 问题状态
     * @param startTime
     * @param endTime
     * @param problemSourceList 问题来源
     * @param flag 有无问题
     * @return
     */
    RestRes getAllDataTagList(Integer problemStatus, String startTime, String endTime, List<Integer> problemSourceList, Integer flag);

    /**
     * 获取当前用户下的所以标签
     * @return
     */
    List<SysTagEntity> getAllTagByUser();

    /**
     * @deprecated 2.0.0，使用{@link SysTagService#getTagIdsByOrgCode(java.lang.String)}替代
     */
    @Deprecated
    List<Integer> getTagIdListByUnitId(String unitId);

    List<SysTaskTagEntity> getListByTagId(Long id,String nestId, String orgCode);

    /**
     * @deprecated 2.0.0，使用{@link SysTagService#getAllTagsByOrgCode(java.lang.String)}替代
     */
    @Deprecated
    List<SysTagEntity> getAllTagListByUnitId(Integer unitId);

    List<Integer> selectMissionByType(Integer dataType);

    /**
     * 获取标签的单位ID
     * @param tagId 标签ID
     * @return  单位ID
     */
    Optional<String> findUnitIdByTagId(Integer tagId);

    /**
     * 获取单位下所有标签
     * @param orgCode   单位编码
     * @return  标签
     */
    List<SysTagEntity> getAllTagsByOrgCode(String orgCode);

    /**
     * 获取单位下所有标签ID
     * @param orgCode   单位编码
     * @return  标签
     */
    List<Integer> getTagIdsByOrgCode(String orgCode);

    /**
     * 获取标签名称使用taskId
     * @param taskId
     * @return
     */
    List<TaskTagDTO> listTagNameByTaskIds(List<Integer> taskId);

}
