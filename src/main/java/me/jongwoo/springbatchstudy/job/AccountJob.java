package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.domain.Account;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
public class AccountJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job copyFileJob(){
        return this.jobBuilderFactory.get("copyFileJob")
                .start(copyFileStep())
                .build();

    }


    @Bean
    public Step copyFileStep(){
        return this.stepBuilderFactory.get("copyFileStep")
                .<Account, Account>chunk(10)
                .reader(accountItemReader(null))
                .writer(accountItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Account> accountItemReader(
            @Value("#{jobParameters['customerFile']}") String inputFile
    ){
        return new FlatFileItemReaderBuilder<Account>()
                .name("accountItemReader")
                .resource(new ClassPathResource(inputFile))
                .fixedLength()
                .columns(new Range[]{new Range(1,11), new Range(12,12),new Range(13,22),
                                new Range(23,26),new Range(27,46),new Range(47,62),new Range(63,64),new Range(65,69)})
                .names("firstName", "middleInitial", "lastName","addressNumber","street","city","state","zipCode")
                .targetType(Account.class)
                .build();

    }

    @Bean
    public ItemWriter<Account> accountItemWriter(){
        return list -> list.forEach(System.out::println);
    }


}
