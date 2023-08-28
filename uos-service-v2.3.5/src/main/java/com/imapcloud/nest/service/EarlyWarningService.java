package com.imapcloud.nest.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.EarlyWarningEntity;
import com.imapcloud.nest.pojo.DO.EarlyWarningDo;
import com.imapcloud.nest.pojo.dto.reqDto.EarlyWarningAddDto;
import com.imapcloud.nest.pojo.vo.EarlyWarningVO;

import java.util.List;
import java.util.Map;

/**
 * 天气/区域警告配置 服务层
 */
public interface EarlyWarningService extends IService<EarlyWarningEntity> {

    /**
     * 气象预警分页查询
     * @param params 查询参数
     * @return Map
     */
    IPage<EarlyWarningVO> queryPage(Map<String, Object> params);

    /**
     * 根据id查询预警
     * @param id 主键
     * @return EarlyWarningVO
     */
    EarlyWarningVO ById(Integer id);

    /**
     * 保存一条预警
     * @param dto 天气/区域警告配置 dto
     */
    void saveEntity(EarlyWarningAddDto dto);

    void updateEntity(EarlyWarningAddDto dto);

    /**
     * 获取机巢天气预警
     * @param nestId 机巢id
     * @return JSONObject
     */
    JSONObject FirstEarlyWarningByNestId(String nestId);

    /**
     * 根据机巢id或单位id查出预警列表
     * @param nestId 机巢id
     * @param orgCode 单位编码
     * @return List<EarlyWarningDo>
     */
    List<EarlyWarningDo> getEarlyWarningByUnitId(String nestId, String orgCode);


    void getWeather();

    /**
     * 首页根据用户id查询单位下所有机巢的天气预警信息
     * @param userId 用户id
     * @return JSONObject
     */
    JSONArray FirstEarlyWarningByUserId(Long userId);

    /**
     * 删除在指定id数组里的数据
     * @param ids id数组
     */
    void deleteByIds(Integer[] ids);

    /**
     * 更新状态
     * @param state 状态 0-未启用  1-启用
     * @param id 主键
     */
    void updateState(Byte state, Integer id);
}
