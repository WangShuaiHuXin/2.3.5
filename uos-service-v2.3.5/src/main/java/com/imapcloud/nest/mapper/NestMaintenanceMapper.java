package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.imapcloud.nest.model.NestMaintenanceEntity;
import com.imapcloud.nest.pojo.vo.NestMaintenanceExportVO;
import com.imapcloud.nest.pojo.vo.NestMaintenancePageVO;
import com.imapcloud.nest.pojo.vo.NestMaintenanceVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 机巢维保记录表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2021-05-14
 */
public interface NestMaintenanceMapper extends BaseMapper<NestMaintenanceEntity> {

    /**
     * 维护项目记录分页查询
     * @param params 查询参数
     * @param page 分页参数
     * @return Map
     */
    IPage<NestMaintenancePageVO> queryPage(@Param("page") IPage<NestMaintenanceEntity> page,@Param("params") Map<String, Object> params);

    /**
     * 获取维保记录
     * @param nestId 机巢id
     */
    List<NestMaintenancePageVO> getExportNestMaintenance(@Param("nestId") Integer nestId);

    /**
     *  分页查询-支持时间跟单基站
     * @param page
     * @param startTimeDate
     * @param endTimeDate
     * @return
     */
    IPage<NestMaintenanceVO> getNestMaintenanceRecordPage(@Param("page") IPage<NestMaintenanceVO> page
            , @Param("nestId") String nestId
            , @Param("startTimeDate") Date startTimeDate
            , @Param("endTimeDate") Date endTimeDate);

    /**
     * 获取需要导出的数据
     * @param nestIds
     * @param startTimeDate
     * @param endTimeDate
     * @return
     */
    List<NestMaintenanceExportVO> getNestMaintenanceRecordExport(@Param("nestIds") List<String> nestIds
            , @Param("startTimeDate") Date startTimeDate
            , @Param("endTimeDate") Date endTimeDate);

}
