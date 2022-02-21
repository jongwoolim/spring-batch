package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.repository.Customer4;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.mail.SimpleMailMessageItemWriter;
import org.springframework.batch.item.mail.builder.SimpleMailMessageItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class EmailJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job emailFormatJob() throws Exception {
        return this.jobBuilderFactory.get("emailFormatJob")
                .start(importStep())
                .next(emailStep())
                .build();
    }

    // step 1
    @Bean
    public Step importStep(){
        return this.stepBuilderFactory.get("importStep")
                .<Customer4,Customer4>chunk(10)
                .reader(CustomerEmailFileReader(null))
                .writer(customer4JdbcBatchItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer4> CustomerEmailFileReader(
            @Value("#{jobParameters['customerFile']}") Resource inputFile) {

        return new FlatFileItemReaderBuilder<Customer4>()
                .name("customerFileReader")
                .resource(inputFile)
                .delimited()
                .names(new String[] {"firstName",
                        "middleInitial",
                        "lastName",
                        "address",
                        "city",
                        "state",
                        "zip","email"})
                .targetType(Customer4.class)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Customer4> customer4JdbcBatchItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Customer4>()
                .namedParametersJdbcTemplate(new NamedParameterJdbcTemplate(dataSource))
                .sql("INSERT INTO CUSTOMER4 (first_name, middle_initial, last_name, " +
                        "address, city, state, zip, email)" +
                        "VALUES(:firstName, :middleInitial, :lastName, :address, :city, :state, :zip, :email)")
                .beanMapped()
                .build();
    }

    // step 2
    @Bean
    public Step emailStep(){
        return this.stepBuilderFactory.get("emailStep")
                .<Customer4, SimpleMailMessage>chunk(10)
                .reader(customer4JdbcCursorItemReader(null))
                .processor((ItemProcessor<Customer4, SimpleMailMessage>) customer4 -> {

                    SimpleMailMessage mail = new SimpleMailMessage();

                    mail.setFrom("whddn528@gmail.com");
                    mail.setTo(customer4.getEmail());
                    mail.setSubject("WelCome!");
                    mail.setText(String.format("Welcome %s %s, \nYou were " +
                            "imported into the system using Spring Batch!", customer4.getFirstName(), customer4.getLastName()));

                    return mail;
                })
                .writer(emailItemWriter(null))
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Customer4> customer4JdbcCursorItemReader(DataSource dataSource){
        return new JdbcCursorItemReaderBuilder<Customer4>()
                .name("customerItemReader")
                .dataSource(dataSource)
                .sql("select * from customer4")
                .rowMapper(new BeanPropertyRowMapper<>(Customer4.class))
                .build();
    }

    @Bean
    public SimpleMailMessageItemWriter emailItemWriter(MailSender mailSender){
        return new SimpleMailMessageItemWriterBuilder()
                .mailSender(mailSender)
                .build();
    }



}
