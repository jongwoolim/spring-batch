package me.jongwoo.springbatchstudy;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.config.Task;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchStudyApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchStudyApplication.class, args);

    }

    @Bean
    public Job job(){
        return this.jobBuilderFactory.get("job")
                .start(step())
                .build();
    }

    @Bean
    public Step step(){
        return this.stepBuilderFactory.get("step1")
                .tasklet(helloWorldTasklet(null)).build();
    }

    @StepScope // 늦은 바인딩을 이용한 잡 파라미터 주입
    @Bean
    public Tasklet helloWorldTasklet(
            @Value("#{jobParameters['name']}") String name) {
        return (stepContribution, chunkContext) -> {
//            String name = (String) chunkContext.getStepContext()
//                    .getJobParameters()
//                    .get("name");

            System.out.println(String.format("hello, %s!,", name));

            return RepeatStatus.FINISHED;
        };


    }



}
