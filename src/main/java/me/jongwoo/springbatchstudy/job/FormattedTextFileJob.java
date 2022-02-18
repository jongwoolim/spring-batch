package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.domain.Customer3;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
        return this.jobBuilderFactory.get("formatJob")
                .start(formatStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step formatStep(){
        return this.stepBuilderFactory.get("formatStep")
                .<Customer3, Customer3>chunk(10)
                .reader(customer3FileReader(null))
                .writer(delimitedCustomerItemWriter(null))
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
    public FlatFileItemWriter<Customer3> delimitedCustomerItemWriter(
            @Value("#{jobParameters['outputFile']}") Resource outputFile
    ){
        return new FlatFileItemWriterBuilder<Customer3>()
                .name("customerItemWriter")
                .resource(outputFile)
                .delimited()
                .delimiter(";")
                .names(new String[]{"zip","state","city","address","lastName","firstName"})
//                .shouldDeleteIfExists(false)
//                .shouldDeleteIfEmpty(true) // 비어 있는 파일을 입력으로 전달하면 출력 파일 남지 않음 하지만 출력 파일이 생성되고 open, close가 수행된다
                .append(true) // 여러 번 실행하더라도 동일한 출력 파일 사용
                .build();
    }
//    @Bean
//    @StepScope
//    public FlatFileItemWriter<Customer3> customer3ItemWriter(
//            @Value("#{jobParameters['outputFile']}") Resource outputFile
//    ){
//        return new FlatFileItemWriterBuilder<Customer3>()
//                .name("customer3ItemWriter")
//                .resource(outputFile)
//                // 구분자로 구분된 파일 , -> ;  DelimitedLineAggregator 사용
//                .delimited()
//                .delimiter(";")
//                .names(new String[]{"zip","state","city","address","lastName","firstName"})
////                .formatted() FormatterLineAggregator 사용
////                .format("%s %s lives at %s %s in %s, %s.")
////                .names(new String[]{"firstName","lastName","address","city","state","zip"})
//                .build();
//
//    }
}
