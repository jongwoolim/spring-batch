package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.SimpleSystemProcessExitCodeMapper;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.core.step.tasklet.SystemProcessExitCodeMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;


@RequiredArgsConstructor
@Configuration
public class SystemJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

//    @Bean
//    public Job systemCommandJob(){
//        return this.jobBuilderFactory.get("systemCommandJob")
//                .start(systemCommandStep())
//                .build();
//    }
//
//    @Bean
//    public Step systemCommandStep() {
//        return this.stepBuilderFactory.get("systemCommandStep")
//                .tasklet(systemCommandTasklet())
//                .build();
//    }
//
//    @Bean
//    public SystemCommandTasklet systemCommandTasklet() {
//        SystemCommandTasklet systemCommandTasklet = new SystemCommandTasklet();
//
//        systemCommandTasklet.setCommand("touch tmp.txt");
//
//        systemCommandTasklet.setWorkingDirectory("/users/limjongwoo/batch");
//        systemCommandTasklet.setSystemProcessExitCodeMapper(touchCodeMapper());
//        systemCommandTasklet.setTimeout(5000);
//        systemCommandTasklet.setTaskExecutor(new SimpleAsyncTaskExecutor());
////        systemCommandTasklet.setEnvironmentParams(new String[]{
////                "JAVA_HOME=/java", "BATCH_HOME=/Users/batch"});
//        systemCommandTasklet.setInterruptOnCancel(true);
//
//        return systemCommandTasklet;
//    }
//
//    @Bean
//    public SystemProcessExitCodeMapper touchCodeMapper() {
//        return new SimpleSystemProcessExitCodeMapper();
//    }


}
