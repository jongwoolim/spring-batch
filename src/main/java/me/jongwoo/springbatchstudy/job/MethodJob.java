//package me.jongwoo.springbatchstudy.job;
//
//import lombok.RequiredArgsConstructor;
//import me.jongwoo.springbatchstudy.service.CustomService;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@RequiredArgsConstructor
//@Configuration
//public class MethodJob {
//
//    private final JobBuilderFactory jobBuilderFactory;
//
//    private final StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Job methodInvokingJob(){
//        return this.jobBuilderFactory.get("methodInvokingJob")
//                .start(methodInvokingStep())
//                .build();
//    }
//
//    @Bean
//    public Step methodInvokingStep() {
//        return this.stepBuilderFactory.get("methodInvokingStep")
//                .tasklet(methodInvokingTasklet(null))
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public Tasklet methodInvokingTasklet(
//            @Value("#{jobParameters['message']}") String message) {
//        MethodInvokingTaskletAdapter methodInvokingTaskletAdapter =
//                new MethodInvokingTaskletAdapter();
//
//        methodInvokingTaskletAdapter.setTargetObject(service());
//        methodInvokingTaskletAdapter.setTargetMethod("serviceMethod");
//        methodInvokingTaskletAdapter.setArguments(new String[]{message});
//
//        return methodInvokingTaskletAdapter;
//    }
//
//    @Bean
//    public CustomService service(){
//
//        return new CustomService();
//    }
//}
