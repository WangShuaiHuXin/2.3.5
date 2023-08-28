package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.NestMaintenanceEntity;
import com.imapcloud.nest.pojo.dto.NestMaintenanceDTO;
import com.imapcloud.nest.utils.RestRes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 * 机巢维保记录表 服务类
 * </p>
 *
 * @author hc
 * @since 2021-05-14
 */
public interface NestMaintenanceService extends IService<NestMaintenanceEntity> {

    /**
     * 维护项目记录分页查询
     * @param params 查询参数
     * @return Map
     */
    RestRes queryPage(Map<String, Object> params);

//    /**
//     * @deprecated 2.2.3，前端确认未使用，将在后续版本删除
//     */
//    @Deprecated
//    RestRes downloadAttachment(HttpServletResponse response, String filePath);

    /**
     * 创建维保记录
     *
     * @param nestMaintenanceDTO
     * @return
     */
    RestRes createNestMaintenanceRecord(NestMaintenanceDTO nestMaintenanceDTO);

    /**
     * 查询维保记录
     * @param nestId
     * @return
     */
    RestRes listNestMaintenanceRecord(Integer nestId);

    /**
     * 查询维保记录-分页方式
     * @param nestId
     * @return
     */
    RestRes listNestMaintenanceRecordByPage(String nestId,Integer page,Integer limit,String startTime,String endTime);

    void exportNestMaintenanceRecord(String nestIds, String startTime , String endTime ,  HttpServletResponse response);
}
