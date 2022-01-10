package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.RandomDecider;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ConditionalJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Tasklet passTasklet(){
        return (stepContribution, chunkContext) -> {
//            return RepeatStatus.FINISHED;
            throw new RuntimeException("Causing a failure");
        };

    }

    @Bean
    public Tasklet successTasklet(){
        return (stepContribution, chunkContext) -> {
            System.out.println("Success !");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet failTasklet(){
        return (stepContribution, chunkContext) -> {
            System.out.println("Failure !");
            return RepeatStatus.FINISHED;
        };
    }

//    @Bean
//    public Job conditionalTestJob(){
//        return this.jobBuilderFactory.get("conditionalJob")
//                .start(firstStep())
//                .on("FAILED").to(failureStep())// 스텝의 ExitStatus를 평가해 어떤 일을 수행할 지 결정
//                .from(firstStep()).on("*").to(successStep()) // * 0개 이상의 문자를 일치 ? 1개의 문자 일치
//                .end()
//                .build();
//    }

    @Bean
    public Job conditionalTestJob(){
        return this.jobBuilderFactory.get("conditionalJob")
                .start(firstStep())
                .on("FAILED")
                .fail()
//                .end()
                .from(firstStep()).on("*").to(successStep())
                .end()
                .build();
//                .next(decider())
//                .from(decider())
//                .on("FAILED").to(failureStep())
//                .from(decider())
//                .on("*").to(successStep())
//                .end()
//                .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new RandomDecider();
    }


    @Bean
    public Step firstStep() {
        return this.stepBuilderFactory.get("firstStep")
                .tasklet(passTasklet())
                .build()
                ;
    }
    @Bean
    public Step successStep() {
        return this.stepBuilderFactory.get("successStep")
                .tasklet(successTasklet())
                .build()
                ;
    }
    @Bean
    public Step failureStep() {
        return this.stepBuilderFactory.get("failureStep")
                .tasklet(failTasklet())
                .build()
                ;
    }


}
