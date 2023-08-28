package com.imapcloud.nest.service.quarzt;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.utils.DateUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.calendar.HolidayCalendar;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class QuartzService {

    private static final String QUARTZ_SKIPPED_DATE_PREFIX = "cale_skip_";

    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;

    /**
     * 新增job
     * @param jobClass 业务处理逻辑类
     * @param jobName   任务名称
     * @param jobGroupName  任务组
     * @param planId    计划ID
     * @param cronExpr cron规则
     */
    public void addJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, Integer planId, String cronExpr) {
        addJob(jobClass, jobName, jobGroupName, planId, cronExpr, null);
    }

    /**
     * 新增规则执行任务
     * @param jobIdentity   任务标识
     * @param jobDataMap    任务执行参数
     * @param cronExpr  规则表达式
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public void addCronJob(JobIdentity jobIdentity, Map<String, ?> jobDataMap, String cronExpr, LocalDateTime startTime, LocalDateTime endTime) {
        if(log.isDebugEnabled()){
            log.debug("新增规则执行任务，任务名称：{}，任务组：{}，执行规则：[{}~{}]@[{}]", jobIdentity.getJobName(), jobIdentity.getJobGroupName(), startTime, endTime, cronExpr);
        }
        JobDetail jobDetail = JobBuilder.newJob(jobIdentity.getJobClass())
                .withIdentity(jobIdentity.getJobName(), jobIdentity.getJobGroupName())
                .build();
        if(!CollectionUtils.isEmpty(jobDataMap)){
            jobDetail.getJobDataMap().putAll(jobDataMap);
        }
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobDetail.getKey().getName(), jobDetail.getKey().getGroup())
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpr).withMisfireHandlingInstructionDoNothing())
                .startAt(DateUtils.toDate(Objects.nonNull(startTime) ? startTime : LocalDateTime.now()))
                .endAt(Objects.nonNull(endTime) ? DateUtils.toDate(endTime) : null)
                .build();
        addJob(jobDetail, trigger);
    }

    /**
     * 新增一次性任务
     * @param jobIdentity   任务表示
     * @param jobDataMap    任务参数列表
     * @param execTime  执行时间
     */
    public void addOneOffJob(JobIdentity jobIdentity, Map<String, ?> jobDataMap, LocalDateTime execTime) {
        if(log.isDebugEnabled()){
            log.debug("新增一次性执行任务，任务名称：{}，任务组：{}，执行日期：{}", jobIdentity.getJobName(), jobIdentity.getJobGroupName(), execTime);
        }
        JobDetail jobDetail = JobBuilder.newJob(jobIdentity.getJobClass())
                .withIdentity(jobIdentity.getJobName(), jobIdentity.getJobGroupName())
                .build();
        if(!CollectionUtils.isEmpty(jobDataMap)){
            jobDetail.getJobDataMap().putAll(jobDataMap);
        }
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobDetail.getKey().getName(), jobDetail.getKey().getGroup())
                .forJob(jobDetail)
                .startAt(DateUtils.toDate(execTime))
                // 只执行一次的话，指定结束时间为执行时间之后1min
                .endAt(DateUtils.toDate(execTime.plusMinutes(1L)))
                .build();
        addJob(jobDetail, trigger);
    }

    public void updateCronJob(String jobName, String jobGroupName, String cronExpr, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
            CronTrigger trigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(triggerKey);
            trigger = trigger.getTriggerBuilder()
                    .withIdentity(jobName, jobGroupName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpr).withMisfireHandlingInstructionDoNothing())
                    .startAt(DateUtils.toDate(Objects.nonNull(startTime) ? startTime : LocalDateTime.now()))
                    .endAt(Objects.nonNull(endTime) ? DateUtils.toDate(endTime) : null)
                    .build();
            schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, trigger);
            if(log.isDebugEnabled()){
                log.debug("修改规则执行任务，任务名称：{}，任务组：{}，执行规则：[{}~{}]@[{}]", jobName, jobGroupName, startTime, endTime, cronExpr);
            }
        } catch (SchedulerException e) {
            log.error("修改计划任务失败", e);
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MODIFY_PLAN_FAILED.getContent()));
        }
    }

    private void addJob(JobDetail jobDetail, Trigger trigger) {
        try {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("新增计划任务失败", e);
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ADDING_PLAN_FAILED.getContent()));
        }
    }

    /**
     * 新增job
     * @param job 业务处理逻辑类
     * @param jobName   任务名称
     * @param jobGroupName  任务组
     * @param planId    计划ID
     * @param corn cron规则
     * @param endDate 结束日期
     */
    public void addJob(Class<? extends QuartzJobBean> job, String jobName, String jobGroupName, Integer planId, String corn, Date endDate) {
        //调度器
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //构建JobDetail
        JobDetail jobDetail = JobBuilder
                .newJob(job)
                .withIdentity(jobName, jobGroupName)
                .build();

        //将业务的planId存入参数
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("planId", planId);

        //构建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(corn).withMisfireHandlingInstructionDoNothing())
                .startNow()
                .endAt(endDate)
                .build();

        //将Job、Trigger注入Scheduler
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            if(log.isDebugEnabled()){
                log.debug("新增巡检计划【{}】定时任务，任务名称：{}，任务组：{}，执行规则：【{}】", planId, jobName, jobGroupName, corn);
            }
        } catch (SchedulerException e) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ADDING_PLAN_FAILED.getContent()));
        }
    }

    /**
     * 新增计划任务
     * @param job  任务处理逻辑类
     * @param jobName   任务名称
     * @param jobGroupName  任务组名
     * @param map   参数
     * @param triggerSimple 触发器
     * @param objs 不知道是啥。。。
     */
    public void addJob(Class<? extends QuartzJobBean> job, String jobName, String jobGroupName, Map<String, ?> map,
                       ScheduleBuilder<? extends Trigger> triggerSimple , Object... objs) {
        //调度器
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        //构建JobDetail
        JobDetail jobDetail = JobBuilder
                .newJob(job)
                .withIdentity(jobName, jobGroupName)
                .storeDurably()
                .build();

        //将业务的planId存入参数
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.putAll(map);

        //构建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroupName)
                .withSchedule(triggerSimple)
                .startNow()
                .build();

        //将Job、Trigger注入Scheduler
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ADDING_PLAN_FAILED.getContent()));
        }
    }

    /**
     * 更新job
     * @param jobName   任务名称
     * @param jobGroupName  任务组
     * @param corn  规则表达式
     */
    public void updateJob(String jobName, String jobGroupName, String corn) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            trigger = trigger
                    .getTriggerBuilder()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(corn))
                    .startNow()
                    .build();

            scheduler.rescheduleJob(triggerKey, trigger);
            if(log.isDebugEnabled()){
                log.debug("修改定时任务，任务名称：{}，任务组：{}，执行规则：【{}】", jobName, jobGroupName, corn);
            }
        } catch (SchedulerException e) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MODIFY_PLAN_FAILED.getContent()));
        }
    }

    /**
     * 删除Job
     * @param jobName   任务名称
     * @param jobGroupName  任务组
     */
    public void deleteJob(String jobName, String jobGroupName) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            scheduler.deleteJob(new JobKey(jobName, jobGroupName));
        } catch (SchedulerException e) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DELETE_PLAN_FAILED.getContent()));
        }
    }

    /**
     * 暂停一个job
     * @param jobName      任务名称
     * @param jobGroupName 任务组名
     */

    public void pauseJob(String jobName, String jobGroupName) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_PAUSE_FAILURE.getContent()));
        }
    }

    /**
     * 恢复一个job
     * @param jobName      任务名称
     * @param jobGroupName 任务组名
     */
    public void resumeJob(String jobName, String jobGroupName) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALARM_TIMING_TASK_RESUME_FAILED.getContent()));
        }
    }

    /**
     * 立即执行一个job
     * @param jobName      任务名称
     * @param jobGroupName 任务组名
     */
    public void runAJobNow(String jobName, String jobGroupName) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IMMEDIATE_EXECUTION_FAILURE.getContent()));
        }
    }

    /**
     * 指定计划任务跳过某个日期
     * @param jobName   任务名称
     * @param jobGroupName  任务组名
     * @param skippedDate   跳过日期
     */
    public void addSkippedCalendar(String jobName, String jobGroupName, LocalDate skippedDate) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        try {
            // 【cale_skip_2022-04-19】按组进行划分，同一天的可能会复用
            Trigger trigger = scheduler.getTrigger(triggerKey)
                    .getTriggerBuilder()
                    .modifiedByCalendar(createCalendarIfAbsent(skippedDate, scheduler))
                    .build();
            scheduler.rescheduleJob(triggerKey, trigger);
            if(log.isDebugEnabled()){
                log.debug("quartz智能跳过定时任务：任务组：{}，任务名称：{}，计划执行日期：{}", jobGroupName, jobName, skippedDate);
            }
        } catch (SchedulerException e) {
            log.error("Quartz跳过日期失败", e);
            throw new NestException(String.format("添加跳过日期[%s]失败", skippedDate));
        }
    }

    public interface JobIdentity {

        String getJobName();

        String getJobGroupName();

        Class<? extends QuartzJobBean> getJobClass();

    }

    private String createCalendarIfAbsent(LocalDate skippedDate, Scheduler scheduler) throws SchedulerException {
        String caleKey = QUARTZ_SKIPPED_DATE_PREFIX + skippedDate.format(DateUtils.DATE_FORMATTER_OF_CN);
        Calendar skippedCalendar = scheduler.getCalendar(caleKey);
        if(Objects.isNull(skippedCalendar)){
            skippedCalendar = getExcludedCalendar(skippedDate);
            scheduler.addCalendar(caleKey, skippedCalendar, false, false);
        }
        return caleKey;
    }

    private Calendar getExcludedCalendar(LocalDate date) {
        HolidayCalendar holidayCalendar = new HolidayCalendar();
        holidayCalendar.addExcludedDate(DateUtils.to(date));
//        AnnualCalendar annualCalendar = new AnnualCalendar();
        return holidayCalendar;
    }

}
