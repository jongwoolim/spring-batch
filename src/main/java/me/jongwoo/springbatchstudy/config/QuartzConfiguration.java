package me.jongwoo.springbatchstudy.config;

import me.jongwoo.springbatchstudy.quartz.BatchScheduledJob;

import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {

    @Bean
    public JobDetail quartzJobDetail(){
        return JobBuilder.newJob(BatchScheduledJob.class)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger jobTrigger(){
        return TriggerBuilder
                .newTrigger()
                .forJob(quartzJobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                .build();
//        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
//                .withIntervalInSeconds(5).withRepeatCount(4);
//
//        return TriggerBuilder.newTrigger()
//                .forJob(quartzJobDetail())
//                .withSchedule(scheduleBuilder)
//                .build();
    }
}
