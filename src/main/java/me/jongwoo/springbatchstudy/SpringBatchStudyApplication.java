package me.jongwoo.springbatchstudy;

import me.jongwoo.springbatchstudy.parameters.DailyJobTimestamper;
import me.jongwoo.springbatchstudy.validator.ParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

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
                .validator(validator())
                .incrementer(new RunIdIncrementer()) // 잡 파라미터 증가시키기
//                .incrementer(new DailyJobTimestamper()) // 타임스탬프 파라미터
                .listener(new JobLoggerListener())
                .build();
    }

    @Bean
    public Step step(){
        return this.stepBuilderFactory.get("step1")
                .tasklet(helloWorldTasklet(null, null)).build();
    }

    @StepScope // 늦은 바인딩을 이용한 잡 파라미터 주입
    @Bean
    public Tasklet helloWorldTasklet(
            @Value("#{jobParameters['name']}") String name,
            @Value("#{jobParameters['fileName']}") String fileName) {
        return (stepContribution, chunkContext) -> {
//            String name = (String) chunkContext.getStepContext()
//                    .getJobParameters()
//                    .get("name");

            System.out.println(String.format("hello, %s!,", name));
            System.out.println("fileName: " + fileName);


            return RepeatStatus.FINISHED;
        };


    }

    @Bean
    public CompositeJobParametersValidator validator(){
        CompositeJobParametersValidator validator =
                new CompositeJobParametersValidator();


        DefaultJobParametersValidator defaultJobParametersValidator =
                new DefaultJobParametersValidator(
                        new String[]{"fileName"},
                        new String[]{"name", "run.id"});


        defaultJobParametersValidator.afterPropertiesSet();

        validator.setValidators(
                Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));

        return validator;
    }

//    @Bean
//    public JobParametersValidator validator(){
//        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
//
//        validator.setRequiredKeys(new String[]{"fileName"});
//        validator.setOptionalKeys(new String[]{"name"});
//
//        return validator;
//    }



}
