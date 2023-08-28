package com.imapcloud.nest.controller;

import com.imapcloud.nest.pojo.quartz.InspectionPlanTaskJob;
import com.imapcloud.nest.service.quarzt.QuartzService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

@RestController
@RequestMapping("/quartz")
public class QuartzTestController extends BaseController{

    @Resource
    private QuartzService quartzService;

    @PostMapping("/add/{jobGroupName}/{jobName}")
    public void test1(@PathVariable String jobName, @PathVariable String jobGroupName, @RequestBody String cron) {
        // 每10s执行一次
//        String cron = "0/10 * * * * ?";
        quartzService.addJob(InspectionPlanTaskJob.class,jobName, jobGroupName,555, cron);
    }

    @PostMapping("skip/{jobGroupName}/{jobName}")
    public void test2(@PathVariable String jobName, @PathVariable String jobGroupName, LocalDate date){
        quartzService.addSkippedCalendar(jobName, jobGroupName, date);
    }

    @PostMapping("update/{jobGroupName}/{jobName}")
    public void test3(@PathVariable String jobName, @PathVariable String jobGroupName, @RequestBody String cron){
        quartzService.updateJob(jobName, jobGroupName, cron);
    }

    @PostMapping("delete/{jobGroupName}/{jobName}")
    public void test4(@PathVariable String jobName, @PathVariable String jobGroupName){
        quartzService.deleteJob(jobName, jobGroupName);
    }

    @PostMapping("pause/{jobGroupName}/{jobName}")
    public void test5(@PathVariable String jobName, @PathVariable String jobGroupName){
        quartzService.pauseJob(jobName, jobGroupName);
    }

    @PostMapping("resume/{jobGroupName}/{jobName}")
    public void test6(@PathVariable String jobName, @PathVariable String jobGroupName){
        quartzService.resumeJob(jobName, jobGroupName);
    }

    @PostMapping("run/{jobGroupName}/{jobName}")
    public void test7(@PathVariable String jobName, @PathVariable String jobGroupName){
        quartzService.runAJobNow(jobName, jobGroupName);
    }

}
