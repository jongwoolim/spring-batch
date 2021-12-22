package me.jongwoo.springbatchstudy;

import me.jongwoo.springbatchstudy.service.CustomService;
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
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.concurrent.Callable;


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
    public Job methodInvokingJob(){
        return this.jobBuilderFactory.get("methodInvokingJob")
                .start(methodInvokingStep())
                .build();
    }

    @Bean
    public Step methodInvokingStep() {
        return this.stepBuilderFactory.get("methodInvokingStep")
                .tasklet(methodInvokingTasklet(null))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet methodInvokingTasklet(
            @Value("#{jobParameters['message']}") String message) {
        MethodInvokingTaskletAdapter methodInvokingTaskletAdapter =
                new MethodInvokingTaskletAdapter();

        methodInvokingTaskletAdapter.setTargetObject(service());
        methodInvokingTaskletAdapter.setTargetMethod("serviceMethod");
        methodInvokingTaskletAdapter.setArguments(new String[]{message});

        return methodInvokingTaskletAdapter;
    }

    @Bean
    public CustomService service(){

        return new CustomService();
    }

    @Bean
    public Job callableJob(){
        return this.jobBuilderFactory.get("callableJob")
                .start(callableStep())
                .build();
    }

    @Bean
    public Step callableStep() {
        return this.stepBuilderFactory.get("callableStep")
                .tasklet(callableTasklet())
                .build();
    }

    @Bean
    public Callable<RepeatStatus> callableObject() {
        return () -> {
            System.out.println("This was executed in another thread");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public CallableTaskletAdapter callableTasklet() {
        CallableTaskletAdapter callableTaskletAdapter =
                new CallableTaskletAdapter();

        // CallableTaskletAdapter 은 callable 객체에 대한 단일 의존성만 필요
        callableTaskletAdapter.setCallable(callableObject());

        return callableTaskletAdapter;
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
                        new String[]{"name", "run.id", "message"});


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
