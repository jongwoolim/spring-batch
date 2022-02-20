package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.repository.Customer4;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.HibernateItemWriter;
import org.springframework.batch.item.database.builder.HibernateItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class HibernateImportJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job hibernateFormatJob(){
        return this.jobBuilderFactory.get("hibernateFormatJob")
                .start(hibernateFormatStep())
                .build();
    }

    @Bean
    public Step hibernateFormatStep() {
        return this.stepBuilderFactory.get("hibernateFormatStep")
                .<Customer4, Customer4>chunk(15)
                .reader(customer4FlatFileItemReader(null))
                .writer(hibernateItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer4> customer4FlatFileItemReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile
    ){
                return new FlatFileItemReaderBuilder<Customer4>()
                        .name("customerFileReader")
                        .resource(inputFile)
                        .delimited()
                        .names(new String[]{"firstName","middleInitial","lastName","address","city","state","zip"})
                        .targetType(Customer4.class)
                        .build();
    }

    @Bean
    public HibernateItemWriter<Customer4> hibernateItemWriter(EntityManagerFactory entityManagerFactory){

        return new HibernateItemWriterBuilder<Customer4>()
                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
                .build();

    }
}
