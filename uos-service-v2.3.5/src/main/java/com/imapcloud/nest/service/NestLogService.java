package com.imapcloud.nest.service;

import com.imapcloud.nest.enums.NestLogModuleEnum;
import com.imapcloud.nest.model.NestLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.pojo.dto.NestLogsDto;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.dto.in.CpsSyncNestLogInDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 基站日志表 服务类
 * </p>
 *
 * @author wmin
 * @since 2021-06-22
 */
public interface NestLogService extends IService<NestLogEntity> {


    /**
     * 更新基站日志从基站获取
     * @deprecated 2.2.3，使用接口{@link NestLogService#notifyCpsUploadNestLog(java.lang.String, java.lang.Integer, com.imapcloud.nest.enums.NestLogModuleEnum)}替代，将在后续版本删除
     */
    @Deprecated
    RestRes sendUploadFileOrder2Nest(String nestId, NestLogModuleEnum module, String uploadUrl, String filename , Integer uavWhich);

    /**
     * 通知CPS上传基站日志
     * @param nestId    基站ID
     * @param uavWhich  无人机位置编号
     * @param module    日志模块
     * @return  是否通知成功
     */
    boolean notifyCpsUploadNestLog(String nestId, Integer uavWhich, NestLogModuleEnum module);

    /**
     * 上传基站日志包到服务器并且解析基站日志包相关数据保存到数据库
     * @deprecated 2.2.3，
     * 使用新接口{@link NestLogService#saveNestLogStorageInfo(com.imapcloud.nest.v2.service.dto.in.CpsSyncNestLogInDTO)}替代，将在后续版本删除
     */
    @Deprecated
    RestRes uploadAndParseNestLogZip(String nestUuid,Integer uavWhich , MultipartFile logFile);

    /**
     * 保存基站日志存储信息
     * @param data  基站日志存储数据
     * @return  保存记录ID
     */
    String saveNestLogStorageInfo(CpsSyncNestLogInDTO data);

    /**
     * 清理基站日志文件
     *
     * @param nestId
     * @return
     */
    RestRes clearNestLog(String nestId);

    /**
     * 查询基站日志包列表
     *
     * @param nestLogsDto
     * @return
     */
    RestRes listNestLogs(NestLogsDto nestLogsDto);

    /**
     * 批量删除基站日志
     *
     * @param logIdList
     * @return
     */
    RestRes batchDelLogs(List<Integer> logIdList);
}
