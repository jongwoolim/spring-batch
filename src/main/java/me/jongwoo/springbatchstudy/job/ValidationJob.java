package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.domain.Customer2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
public class ValidationJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job copyFileValidateJob(){
        return jobBuilderFactory.get("copyFileValidateJob")
                .start(copyFileValidateStep())
                .build();
    }

    @Bean
    public Step copyFileValidateStep() {
        return stepBuilderFactory.get("copyFileValidateStep")
                .<Customer2, Customer2>chunk(5)
                .reader(customerItemReader(null))
                .processor(customerValidatingItemProcessor())
                .writer(itemValidateWriter())
                .build()
                ;
    }



    @Bean
    @StepScope
    public FlatFileItemReader<Customer2> customerItemReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile
    ){
        return new FlatFileItemReaderBuilder<Customer2>()
                .name("customerItemReader")
                .delimited()
                .names(new String[]{"firstName","middleInitial","lastName","address","city","state","zip"})
                .targetType(Customer2.class)
                .resource(inputFile)
                .build();


    }

    @Bean
	public BeanValidatingItemProcessor<Customer2> customerValidatingItemProcessor() {
		return new BeanValidatingItemProcessor<>();
	}

    @Bean
    public ItemWriter<Customer2> itemValidateWriter(){
        return list -> list.forEach(System.out::println);
    }

}
