package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.TransactionFieldSetMapper;
import me.jongwoo.springbatchstudy.domain.Account;
import me.jongwoo.springbatchstudy.reader.AccountFileReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AccountJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job copyFileJob(){
        return this.jobBuilderFactory.get("copyFileJob")
                .start(copyFileStep())
                .build();

    }


    @Bean
    public Step copyFileStep(){
        return this.stepBuilderFactory.get("copyFileStep")
                .chunk(10)
                .reader(multiAccountReader(null))
                .writer(accountItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader accountItemReader(
//            @Value("#{jobParameters['customerFile']}") String inputFile
    ){
        return new FlatFileItemReaderBuilder<Account>()
                .name("accountItemReader")
//                .resource(inputFIle)
                .lineMapper(lineTokenizer())
//                .lineTokenizer(new AccountFileLineTokenizer()) // 특이한 파일 포맷 파싱, 엑셀 워크시트 같은 서드파티 파일 포맷, 특수한 타입 변환 요구 조건 처리 시 사용
//                .delimited()
                //고정 너비 파일일 경우
//                .fixedLength()
//                .columns(new Range[]{new Range(1,11), new Range(12,12),new Range(13,22),
//                                new Range(23,26),new Range(27,46),new Range(47,62),new Range(63,64),new Range(65,69)})
//                .names("firstName", "middleInitial", "lastName","addressNumber","street","city","state","zipCode")
//                .fieldSetMapper(new AccountFileSetMapper())
//                .targetType(Account.class)
                .build();

    }

    @Bean
    @StepScope
    public MultiResourceItemReader multiAccountReader(
            @Value("#{jobParameters['customerFile']}") Resource[] inputFiles
    ){
        return new MultiResourceItemReaderBuilder<>()
                .name("multiAccountReader")
                .resources(inputFiles)
                .delegate(accountFileReader())
                .build();
    }

    @Bean
    public AccountFileReader accountFileReader(){
        return new AccountFileReader(accountItemReader());
    }

    @Bean
    public PatternMatchingCompositeLineMapper lineTokenizer(){
        Map<String, LineTokenizer> lineTokenizers = new HashMap<>(2);
        lineTokenizers.put("CUST*", accountLineTokenizer());
        lineTokenizers.put("TRANS*", transactionLineTokenizer());

        Map<String, FieldSetMapper> fieldSetMappers = new HashMap<>(2);
        BeanWrapperFieldSetMapper<Account> accountFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        accountFieldSetMapper.setTargetType(Account.class);

        fieldSetMappers.put("CUST*", accountFieldSetMapper);
        fieldSetMappers.put("TRANS*", new TransactionFieldSetMapper());

        PatternMatchingCompositeLineMapper lineMappers =
                new PatternMatchingCompositeLineMapper();

        lineMappers.setTokenizers(lineTokenizers);
        lineMappers.setFieldSetMappers(fieldSetMappers);

        return lineMappers;
    }

    @Bean
    public DelimitedLineTokenizer transactionLineTokenizer(){
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("prefix","accountNumber","transactionDate","amount");
        return lineTokenizer;
    }

    @Bean
    public DelimitedLineTokenizer accountLineTokenizer(){
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setNames("firstName","middleInitial","lastName","address","city","state","zipCode");

        lineTokenizer.setIncludedFields(1,2,3,4,5,6,7);
        return lineTokenizer;
    }
    @Bean
    public ItemWriter accountItemWriter(){
        return list -> list.forEach(System.out::println);
    }


}
