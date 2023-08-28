package com.imapcloud.nest.pojo.quartz;

import com.imapcloud.nest.common.constant.MessageConstant;
import com.imapcloud.nest.model.PubMessageEntity;
import com.imapcloud.nest.model.PubUserMessageEntity;
import com.imapcloud.nest.service.PubMessageService;
import com.imapcloud.nest.service.PubUserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName MessagePushJob.java
 * @Description MessagePushJob 消息推送后台任务
 * @createTime 2022年04月18日 15:35:00
 */
@Slf4j
public class MessagePushJob extends QuartzJobBean {

    @Resource
    private PubMessageService pubMessageService;

    @Resource
    private PubUserMessageService pubUserMessageService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        if (jobDataMap != null && !jobDataMap.isEmpty()) {
            //组装任务
            String jobName  = (String) jobDataMap.get(MessageConstant.Job.JOB_NAME_STR);
            String jobGroupName = (String) jobDataMap.get(MessageConstant.Job.JOB_GROUP_NAME_STR);
            log.info("执行后台任务:{}-{}",jobName,jobGroupName);
            List<PubUserMessageEntity> pubUserMessageEntityList = (List<PubUserMessageEntity>) jobDataMap.get(MessageConstant.Job.VALUES);
            int id = pubUserMessageEntityList.get(0).getMessageId();
            int count = pubMessageService.lambdaQuery()
                    .eq(PubMessageEntity::getId,id)
                    .eq(PubMessageEntity::getDeleted,0).count();
            if(count>=1){
                pubUserMessageService.saveBatch(pubUserMessageEntityList);
                pubMessageService.lambdaUpdate().setSql(" message_state = 2 ")
                        .eq(PubMessageEntity::getId,id)
                        .update();
            }
        }
    }
}
