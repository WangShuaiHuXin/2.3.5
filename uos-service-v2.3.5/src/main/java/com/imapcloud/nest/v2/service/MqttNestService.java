package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.nest.NestAlarmDTO;
import com.imapcloud.nest.v2.service.dto.nest.NestBaseDTO;
import com.imapcloud.nest.v2.service.dto.nest.NestUavDTO;

import java.util.List;

/**
 * mqtt服务
 *
 * @author boluo
 * @date 2023-02-15
 */
public interface MqttNestService {

    /**
     * 基站基本信息
     *
     * @param nestBaseDTO 巢基地dto
     * @param nestUavDTO nestUavDTO
     * @param nestAlarmDTO nestAlarmDTO
     * @param accountId accountId 为空则发送所有用户
     * @param nestUuid nestUuid
     */
    void nestAll(NestBaseDTO nestBaseDTO, NestUavDTO nestUavDTO, NestAlarmDTO nestAlarmDTO, String accountId, String nestUuid);

    /**
     * 批量
     *
     * @param nestBaseDTOList 巢基地dtolist
     * @param nestUavDTOList  巢无人机dtolist
     * @param alarmDTOList    报警dtolist
     * @param accountId       帐户id
     */
    void batchNestAll(List<NestBaseDTO> nestBaseDTOList, List<NestUavDTO> nestUavDTOList, List<NestAlarmDTO> alarmDTOList, String accountId);
}
