package com.imapcloud.nest.v2.manager.job;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.imapcloud.nest.v2.common.utils.AsyncBusinessUtils;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.SysOrgPilotSpaceService;
import com.imapcloud.nest.v2.service.dto.in.SysOrgPilotSpaceInDTO;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PilotOrgBindSpaceProcessor.java
 * @Description PilotOrgBindSpaceProcessor
 * @createTime 2022年07月26日 09:11:00
 */
@Component
public class PilotOrgBindSpaceProcessor implements BasicProcessor {

    @Resource
    private UosOrgManager uosOrgManager;

    @Resource
    private SysOrgPilotSpaceService sysOrgPilotSpaceService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        // 在线日志功能，可以直接在控制台查看任务日志，非常便捷
        OmsLogger omsLogger = context.getOmsLogger();
        omsLogger.info("PilotOrgBindSpaceProcessor start to process, current JobParams is {}.", context.getJobParams());

        List<OrgSimpleOutDO> orgSimpleOutDOList =  this.uosOrgManager.listAllOrgSimpleInfoWithoutAccount();
        List<String> sysOrgCodeList = this.sysOrgPilotSpaceService.querySysOrgPilotList();
        List<String> orgCodeInfoList = orgSimpleOutDOList.stream().map(OrgSimpleOutDO::getOrgCode).collect(Collectors.toList());
        List<String> targetOrgCodeList = (List<String>) CollectionUtils.subtract(orgCodeInfoList , sysOrgCodeList);
        if(CollectionUtil.isNotEmpty(targetOrgCodeList)){
            List<SysOrgPilotSpaceInDTO> sysOrgPilotSpaceInDTOS = new ArrayList<>();
            sysOrgPilotSpaceInDTOS = targetOrgCodeList.stream().map(x->{
                SysOrgPilotSpaceInDTO inDTO = new SysOrgPilotSpaceInDTO();
                inDTO.setOrgCode(x);
                return inDTO;
            }).collect(Collectors.toList());
            List<SysOrgPilotSpaceInDTO> finalSysOrgPilotSpaceInDTOS = sysOrgPilotSpaceInDTOS;
            AsyncBusinessUtils.executeBusiness(()->{
                this.sysOrgPilotSpaceService.insertSysOrgPilot(finalSysOrgPilotSpaceInDTOS);
            });
        }
        return new ProcessResult(true, "创建pilotOrgSpace关联完结");
    }
}