package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.domain.Customer3;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
public class FormattedTextFileJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job formatJob(){
        return this.jobBuilderFactory.get("customer3Job")
                .start(formatStep())
                .build();
    }

    @Bean
    public Step formatStep(){
        return this.stepBuilderFactory.get("customer3Step")
                .<Customer3, Customer3>chunk(10)
                .reader(customer3FileReader(null))
                .writer(customer3ItemWriter(null))
                .build();

    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer3> customer3FileReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile
    ){
        return new FlatFileItemReaderBuilder<Customer3>()
                .name("customer3FileReader")
                .resource(inputFile)
                .delimited()
                .names(new String[]{"firstName","middleInitial","lastName","address","city","state","zip"})
                .targetType(Customer3.class)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Customer3> customer3ItemWriter(
            @Value("#{jobParameters['outputFile']}") Resource outputFile
    ){
        return new FlatFileItemWriterBuilder<Customer3>()
                .name("customer3ItemWriter")
                .resource(outputFile)
                .formatted()
                .format("%s %s lives at %s %s in %s, %s.")
                .names(new String[]{"firstName","lastName","address","city","state","zip"})
                .build();

    }
}