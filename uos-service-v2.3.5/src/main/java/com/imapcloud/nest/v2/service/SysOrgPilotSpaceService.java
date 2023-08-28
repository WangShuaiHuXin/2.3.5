package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.SysOrgPilotSpaceInDTO;
import com.imapcloud.nest.v2.service.dto.out.SysOrgPilotSpaceOutDTO;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName SysOrgPilotSpaceService.java
 * @Description SysOrgPilotSpaceService
 * @createTime 2022年07月13日 15:21:00
 */
public interface SysOrgPilotSpaceService {

    SysOrgPilotSpaceOutDTO getSysOrgPilotSpace(String orgCode , String workSpaceId);

    List<String> querySysOrgPilotList();

    boolean insertSysOrgPilot(List<SysOrgPilotSpaceInDTO> sysOrgPilotSpaceInDTOS);

}
