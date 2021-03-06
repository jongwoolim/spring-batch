package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.domain.Customer3;
import me.jongwoo.springbatchstudy.writer.CustomerItemPreparedStatementSetter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

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
                .writer(jdbcBatchItemWriter(null))
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
    public JdbcBatchItemWriter<Customer3> jdbcBatchItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Customer3>()
                .dataSource(dataSource)
                .sql("INSERT INTO CUSTOMER3 (first_name, " +
                        "middle_initial, " +
                        "last_name, " +
                        "address, " +
                        "city, " +
                        "state, " +
                        "zip) VALUES (:firstName, :middleInitial, :lastName, :address, :city, :state, :zip)")
//                .itemPreparedStatementSetter(new CustomerItemPreparedStatementSetter())
                .beanMapped()
                .build();
    }

//    @Bean
//    @StepScope
//    public StaxEventItemWriter<Customer3> xmlCustomerWriter(
//            @Value("#{jobParameters['outputFile']}") Resource outputFile){
//
//        Map<String, Class> aliases = new HashMap<>();
//        aliases.put("customer", Customer3.class);
//
//        XStreamMarshaller marshaller = new XStreamMarshaller();
//
//        marshaller.setAliases(aliases);
//
//        marshaller.afterPropertiesSet();
//
//        return new StaxEventItemWriterBuilder<Customer3>()
//                .name("customerItemWriter")
//                .resource(outputFile)
//                .marshaller(marshaller)
//                .rootTagName("customers")
//                .build();
//
//    }

//    @Bean
//    @StepScope
//    public FlatFileItemWriter<Customer3> delimitedCustomerItemWriter(
//            @Value("#{jobParameters['outputFile']}") Resource outputFile
//    ){
//        return new FlatFileItemWriterBuilder<Customer3>()
//                .name("customerItemWriter")
//                .resource(outputFile)
//                .delimited()
//                .delimiter(";")
//                .names(new String[]{"zip","state","city","address","lastName","firstName"})
////                .shouldDeleteIfExists(false)
////                .shouldDeleteIfEmpty(true) // ?????? ?????? ????????? ???????????? ???????????? ?????? ?????? ?????? ?????? ????????? ?????? ????????? ???????????? open, close??? ????????????
//                .append(true) // ?????? ??? ?????????????????? ????????? ?????? ?????? ??????
//                .build();
//    }
//    @Bean
//    @StepScope
//    public FlatFileItemWriter<Customer3> customer3ItemWriter(
//            @Value("#{jobParameters['outputFile']}") Resource outputFile
//    ){
//        return new FlatFileItemWriterBuilder<Customer3>()
//                .name("customer3ItemWriter")
//                .resource(outputFile)
//                // ???????????? ????????? ?????? , -> ;  DelimitedLineAggregator ??????
//                .delimited()
//                .delimiter(";")
//                .names(new String[]{"zip","state","city","address","lastName","firstName"})
////                .formatted() FormatterLineAggregator ??????
////                .format("%s %s lives at %s %s in %s, %s.")
////                .names(new String[]{"firstName","lastName","address","city","state","zip"})
//                .build();
//
//    }
}
