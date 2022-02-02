package me.jongwoo.springbatchstudy.job;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.domain.AccountSummary;
import me.jongwoo.springbatchstudy.domain.Transaction;
import me.jongwoo.springbatchstudy.processor.TransactionApplierProcessor;
import me.jongwoo.springbatchstudy.reader.TransactionReader;
import me.jongwoo.springbatchstudy.repository.TransactionDao;
import me.jongwoo.springbatchstudy.repository.TransactionDaoSupport;
import me.jongwoo.springbatchstudy.repository.TransactionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class TransactionJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;
//    private final EntityManagerFactory entityManagerFactory;
//    private final TransactionRepository transactionRepository;



    @Bean
    @StepScope
    public TransactionReader transactionReader(){
        return new TransactionReader(fileItemReader());
    }

    @Bean
    public Job transactionFileJob(){

        return this.jobBuilderFactory.get("transactionFileJob")
                .start(importTransactionFileStep())

                    .on("STOPPED")
                    .stopAndRestart(importTransactionFileStep()) // 잡이 프로그래밍 방식으로 중지된 경우 잡을 다시 시작
                .from(importTransactionFileStep())
                    .on("*")
                    .to(applyTransactionStep())
                .from(applyTransactionStep())
                    .next(generateAccountSummaryStep())
                .end()
                .build();
    }

    // 스텝 1 파일에서 데이터를 읽어 db에 저장
    @Bean
    public Step importTransactionFileStep() {
        return this.stepBuilderFactory.get("importTransactionFileStep")
                .<Transaction, Transaction>chunk(100)
                .reader(transactionReader())
                .writer(transactionWriter(null))
                .allowStartIfComplete(true)
                .listener(transactionReader())
                .build();
    }


    @Bean
    @StepScope
    public FlatFileItemReader<FieldSet> fileItemReader(
//            @Value("#{jobParameters['transactionFile']}") Resource inputFile
    ){
        return new FlatFileItemReaderBuilder<FieldSet>()
                .name("fileItemReader")
                .resource(new ClassPathResource("input/transaction.csv"))
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PassThroughFieldSetMapper())
                .build();
    }


    @Bean
    public JdbcBatchItemWriter<Transaction> transactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(
                        new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO TRANSACTION " +
                        "(ACCOUNT_SUMMARY_ID, TIMESTAMP, AMOUNT) " +
                        "VALUES ((SELECT ID FROM ACCOUNT_SUMMARY " +
                        "	WHERE ACCOUNT_NUMBER = :accountNumber), " +
                        ":timestamp, :amount)")
                .dataSource(dataSource)
                .build();
    }
//    @Bean
//    public JpaItemWriter<Transaction> transactionWriter(){
//        JpaItemWriter<Transaction> jpaItemWriter = new JpaItemWriter<>();
//        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
//        return jpaItemWriter;
//    }

    // 스텝 2 파일에서 찾은 거래 정보를 계좌에 적용
    @Bean
    @StepScope
    public JdbcCursorItemReader<AccountSummary> accountSummaryReader(DataSource dataSource){
        return new JdbcCursorItemReaderBuilder<AccountSummary>()
                .name("accountSummaryReader")
                .dataSource(dataSource)
                .sql("SELECT ACCOUNT_NUMBER, CURRENT_BALANCE " +
                        "FROM ACCOUNT_SUMMARY A " +
                        "WHERE A.ID IN (" +
                        "   SELECT DISTINCT T.ACCOUNT_SUMMARY_ID " +
                        "   FROM TRANSACTION T " +
                        "ORDER BY A.ACCOUNT_NUMBER")
                .rowMapper((rs, rowNum) -> {
                    AccountSummary summary = new AccountSummary();
                    summary.setAccountNumber(rs.getString("account_number"));
                    summary.setCurrentBalance(rs.getDouble("current_balance"));
                    return summary;
                }).build();
    }

    @Bean
    public TransactionDao transactionDao(DataSource dataSource) {
        return new TransactionDaoSupport(dataSource);
    }

    @Bean
    public TransactionApplierProcessor transactionApplierProcessor(){
        return new TransactionApplierProcessor(transactionDao(null));
    }

    @Bean
    public JdbcBatchItemWriter<AccountSummary> accountSummaryWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<AccountSummary>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(
                        new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE ACCOUNT_SUMMARY " +
                        "SET CURRENT_BALANCE = :currentBalance " +
                        "WHERE ACCOUNT_NUMBER = :accountNumber")
                .build();
    }
//    @Bean
//    public JpaItemWriter<AccountSummary> accountSummaryWriter(){
//        JpaItemWriter<AccountSummary> jpaItemWriter = new JpaItemWriter<>();
//        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
//        return jpaItemWriter;
//    }

    @Bean
    public Step applyTransactionStep(){
        return this.stepBuilderFactory.get("applyTransactionStep")
                .<AccountSummary, AccountSummary>chunk(100)
                .reader(accountSummaryReader(null))
                .processor(transactionApplierProcessor())
                .writer(accountSummaryWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<AccountSummary> accountSummaryFileWriter(
            @Value("#{jobParameters['summaryFile']}") Resource summaryFile
    ){

        DelimitedLineAggregator<AccountSummary> lineAggregator =
                new DelimitedLineAggregator<>();

        BeanWrapperFieldExtractor<AccountSummary> fieldExtractor =
                new BeanWrapperFieldExtractor<>();

        fieldExtractor.setNames(new String[]{"accountNumber", "currentBalance"});
        fieldExtractor.afterPropertiesSet();
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<AccountSummary>()
                .name("accountSummaryFileWriter")
                .resource(summaryFile)
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    public Step generateAccountSummaryStep(){
        return this.stepBuilderFactory.get("generateAccountSummaryStep")
                .<AccountSummary, AccountSummary>chunk(100)
                .reader(accountSummaryReader(null))
                .writer(accountSummaryFileWriter(null))
                .build();
    }
}
