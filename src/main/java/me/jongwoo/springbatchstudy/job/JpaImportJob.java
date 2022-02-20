package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.domain.Customer4;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.HibernateItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.HibernateItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class JpaImportJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jpaFormatJob(){
        return this.jobBuilderFactory.get("jpaFormatJob")
                .start(jpaFormatStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jpaFormatStep() {
        return this.stepBuilderFactory.get("jpaFormatStep")
                .<Customer4, Customer4>chunk(10)
                .reader(customer4FlatFileItemReader2(null))
                .writer(jpaItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer4> customer4FlatFileItemReader2(
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
    public JpaItemWriter<Customer4> jpaItemWriter(EntityManagerFactory entityManagerFactory){

        return new JpaItemWriterBuilder<Customer4>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
