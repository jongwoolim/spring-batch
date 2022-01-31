//package me.jongwoo.springbatchstudy.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class QuartzJobConfiguration {
//
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Job quartzJob(){
//        return this.jobBuilderFactory.get("quartz test job")
//                // 여러 번 잡을 실행하는 기능은 기본적인 요구 사항
//                .incrementer(new RunIdIncrementer())
//                .start(quartzStep())
//                .build();
//    }
//
//    @Bean
//    public Step quartzStep() {
//        return this.stepBuilderFactory.get("quartz test step")
//                .tasklet((contribution, chunkContext) -> {
//                    System.out.println("step quartz run !!!!");
//                    return RepeatStatus.FINISHED;
//                }).build();
//
//    }
//}
