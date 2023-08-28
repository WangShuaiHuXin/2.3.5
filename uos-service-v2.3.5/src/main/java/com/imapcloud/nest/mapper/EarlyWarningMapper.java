package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.EarlyWarningEntity;
import com.imapcloud.nest.pojo.DO.EarlyWarningDo;
import com.imapcloud.nest.pojo.vo.EarlyWarningVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 天气/区域警告配置 DAO层
 */
public interface EarlyWarningMapper extends BaseMapper<EarlyWarningEntity> {

    IPage<EarlyWarningVO> queryPage(@Param("page") IPage<EarlyWarningEntity> page, @Param("params") Map<String ,Object> params);

    EarlyWarningVO byId(@Param("id") Integer id);

    List<EarlyWarningDo> getEarlyWarningByUnitId(@Param("nestId") String nestId, @Param("orgCode") String orgCode);

}
