//package me.jongwoo.springbatchstudy.job;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.Callable;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class CallableJob {
//
//    private final JobBuilderFactory jobBuilderFactory;
//
//    private final StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Job callableTestJob(){
//        return this.jobBuilderFactory.get("callableJob")
//                .start(callableTestStep())
//                .build();
//    }
//
//    @Bean
//    public Step callableTestStep() {
//        return this.stepBuilderFactory.get("callableStep")
//                .tasklet(callableTasklet())
//                .build();
//    }
//
//    @Bean
//    public Callable<RepeatStatus> callableObject() {
//        return () -> {
//            System.out.println("This was executed in another thread");
//            return RepeatStatus.FINISHED;
//        };
//    }
//
//    @Bean
//    public CallableTaskletAdapter callableTasklet() {
//        CallableTaskletAdapter callableTaskletAdapter =
//                new CallableTaskletAdapter();
//
//        // CallableTaskletAdapter 은 callable 객체에 대한 단일 의존성만 필요
//        callableTaskletAdapter.setCallable(callableObject());
//
//        return callableTaskletAdapter;
//    }
//}
