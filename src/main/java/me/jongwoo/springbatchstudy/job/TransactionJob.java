package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.domain.AccountSummary;
import me.jongwoo.springbatchstudy.domain.Transaction;
import me.jongwoo.springbatchstudy.processor.TransactionApplierProcessor;
import me.jongwoo.springbatchstudy.reader.TransactionReader;
import me.jongwoo.springbatchstudy.repository.TransactionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class TransactionJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final TransactionRepository transactionRepository;

    @Bean
    public Job transactionFileJob(){

        return this.jobBuilderFactory.get("transactionFileJob")
                .start(importTransactionFileStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    // 스텝 1 파일에서 데이터를 읽어 db에 저장
    @Bean
    public Step importTransactionFileStep() {
        return this.stepBuilderFactory.get("importTransactionFileStep")
                .<Transaction, Transaction>chunk(100)
                .reader(transactionReader())
                .writer(transactionWriter())
                .allowStartIfComplete(true)
                .listener(transactionReader())
                .build();
    }

    @Bean
    @StepScope
    public TransactionReader transactionReader(){
        return new TransactionReader(fileItemReader(null));
    }

    @Bean
    @StepScope
    public FlatFileItemReader<FieldSet> fileItemReader(
            @Value("#{jobParameters['transactionFile']}") Resource inputFile){
        return new FlatFileItemReaderBuilder<FieldSet>()
                .name("fileItemReader")
                .resource(inputFile)
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PassThroughFieldSetMapper())
                .build();
    }

    @Bean
    public JpaItemWriter<Transaction> transactionWriter(){
        JpaItemWriter<Transaction> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    // 스텝 2 파일에서 찾은 거래 정보를 계좌에 적용
    @Bean
    @StepScope
    public JpaCursorItemReader<AccountSummary> accountSummaryReader(){
        return new JpaCursorItemReaderBuilder<AccountSummary>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select a from AccountSummary a where a.id IN (" +
                        "   select distinct t.id" +
                        "   from Transaction t) " +
                        "order by a.account_number")
                .build();
    }

    @Bean
    public TransactionApplierProcessor transactionApplierProcessor(){
        return new TransactionApplierProcessor(transactionRepository);
    }

    @Bean
    public JpaItemWriter<AccountSummary> accountSummaryWriter(){
        JpaItemWriter<AccountSummary> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public Step applyTransactionStep(){
        return this.stepBuilderFactory.get("applyTransactionStep")
                .<AccountSummary, AccountSummary>chunk(100)
                .reader(accountSummaryReader())
                .processor(transactionApplierProcessor())
                .writer(accountSummaryWriter())
                .build();
    }
}
