package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.repository.Customer4;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CompositeItemWriterJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job compositeWriterJob() throws Exception {
        return this.jobBuilderFactory.get("compositeWriterJob")
                .start(compositeWriterStep())
                .build();
    }

    @Bean
	public Step compositeWriterStep() throws Exception {
		return this.stepBuilderFactory.get("compositeWriterStep")
				.<Customer4, Customer4>chunk(10)
				.reader(compositewriterItemReader(null))
				.writer(compositeItemWriter())
				.build();
	}

    @Bean
    @StepScope
    public FlatFileItemReader<Customer4> compositewriterItemReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile
    ){

        return new FlatFileItemReaderBuilder<Customer4>()
                .name("compositewriterItemReader")
                .resource(inputFile)
                .delimited()
                .names(new String[]{"firstName","middleInitial","lastName","address","city","state","zip","email"})
                .targetType(Customer4.class)
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<Customer4> xmlDelegateItemWrtier(
            @Value("#{jobParameters['outputFile']}") Resource outputFile
    ){
        Map<String, Class> aliases = new HashMap<>();
        aliases.put("customer", Customer4.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(aliases);

        marshaller.afterPropertiesSet();

        return new StaxEventItemWriterBuilder<Customer4>()
                .name("customerItemWriter")
                .resource(outputFile)
                .marshaller(marshaller)
                .rootTagName("customers")
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Customer4> jdbcDelgateItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Customer4>()
                .namedParametersJdbcTemplate(new NamedParameterJdbcTemplate(dataSource))
                .sql("INSERT INTO CUSTOMER4 (first_name, " +
                        "middle_initial, " +
                        "last_name, " +
                        "address, " +
                        "city, " +
                        "state, " +
                        "zip, " +
                        "email) " +
                        "VALUES (:firstName, " +
                        ":middleInitial, " +
                        ":lastName, " +
                        ":address, " +
                        ":city, " +
                        ":state, " +
                        ":zip, " +
                        ":email)")
                .beanMapped()
                .build();

    }

    @Bean
    public CompositeItemWriter<Customer4> compositeItemWriter(){

        return new CompositeItemWriterBuilder<Customer4>()
                .delegates(Arrays.asList(xmlDelegateItemWrtier(null), jdbcDelgateItemWriter(null)))
                .build();
    }



}
