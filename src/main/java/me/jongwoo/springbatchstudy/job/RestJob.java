package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RestJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job restTestJob(){
        return this.jobBuilderFactory.get("rest job")
                .incrementer(new RunIdIncrementer())
                .start(restTestStep())
                .build();
    }

    @Bean
    public Step restTestStep() {
        return this.stepBuilderFactory.get("rest step")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("step 1 ran today !!!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
