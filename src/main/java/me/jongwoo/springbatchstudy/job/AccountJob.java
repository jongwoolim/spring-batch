package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.batch.TransactionFieldSetMapper;
import me.jongwoo.springbatchstudy.domain.Account;
import me.jongwoo.springbatchstudy.listener.AccountItemListener;
import me.jongwoo.springbatchstudy.listener.EmptyInputStepFailer;
import me.jongwoo.springbatchstudy.policy.FileVerificationSkipper;
import me.jongwoo.springbatchstudy.reader.AccountItemReader;
import me.jongwoo.springbatchstudy.repository.AccountRepository;
import me.jongwoo.springbatchstudy.service.AccountService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AccountJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final AccountRepository accounutRepository;

    @Bean
    public Job copyFileJob() {
        return this.jobBuilderFactory.get("copyFileJob")
                .start(copyFileStep())
                .build();

    }


    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                .<Account,Account>chunk(10)
                .reader(accountItemReader())
                .writer(accountItemWriter())
                .faultTolerant()
                .skip(Exception.class) // Exception을 상속한 모든 예외를 10번까지 건너뛸 수 있음
                .skipLimit(100)
                .listener(emptyFileFailer())
                .build();
    }

    @Bean
    public AccountItemReader accountItemReader(){
        AccountItemReader accountItemReader = new AccountItemReader();
        accountItemReader.setName("accountItemReader");

        return accountItemReader;
    }

    @Bean
    public EmptyInputStepFailer emptyFileFailer(){
        return new EmptyInputStepFailer();
    }

    @Bean
    public AccountItemListener accountItemListener(){
        return new AccountItemListener();
    }

//    @Bean
//    public ItemReaderAdapter<Account> accountItemReader(AccountService accountService) {
//        ItemReaderAdapter<Account> adapter = new ItemReaderAdapter<>();
//        adapter.setTargetObject(accountService);
//        adapter.setTargetMethod("getAccount");
//
//        return adapter;
//    }

//    @Bean
//    @StepScope
//    // Spring Data
//    // RepositoryItemReader
//    // JPA
//    // cursor: x
//    // paging: JpaPagingItemReader
//    // Hibernate
//    // cursor: HibernateCursorItemReader
//    // paging: HibernatePagingItemReader
//    public RepositoryItemReader<Account> accountItemReader(
//            @Value("#{jobParameters['city']}") String city
//    ){
////        AccountByCityQueryProvider queryProvider = new AccountByCityQueryProvider();
////        queryProvider.setCityName(city);
//        return new RepositoryItemReaderBuilder<Account>()
//                .name("accountItemReader")
//                .arguments(Collections.singletonList(city))
////                .pageSize(5) 기본값 10
//                .methodName("findByCity")
//                .repository(accounutRepository)
//                .sorts(Collections.singletonMap("lastName", Sort.Direction.ASC))
//
//////                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
////                .entityManagerFactory(entityManagerFactory)
//////                .queryString("from Account where city = :city")
//////                .queryString("select a from Account a where a.city = :city")
////                .queryProvider(queryProvider)
////                .parameterValues(Collections.singletonMap("city", city))
//                .build();
//    }

    /**
     *
     JdbcPagingItemReader
     //    @Bean
     //    @StepScope
     //    public JdbcPagingItemReader<Account> accountItemReader(
     //            DataSource dataSource,
     //            PagingQueryProvider queryProvider,
     //            @Value("#{jobParameters['city']}") String city
     //
     //    ){
     //        Map<String, Object> parameterValues = new HashMap<>(1);
     //        parameterValues.put("city", city);
     //
     //        return new JdbcPagingItemReaderBuilder<Account>()
     //                .name("accountItemReader")
     //                .dataSource(dataSource)
     //                .queryProvider(queryProvider)
     //                .parameterValues(parameterValues)
     //                .pageSize(10) // 페이지 크기
     //                .rowMapper(new AccountRowMapper())
     //                .build();
     //    }
     //
     //    @Bean
     //    public SqlPagingQueryProviderFactoryBean pagingQueryProviderFactoryBean(DataSource dataSource){
     //        SqlPagingQueryProviderFactoryBean factoryBean =
     //                new SqlPagingQueryProviderFactoryBean();
     //
     //        factoryBean.setSelectClause("select *");
     //        factoryBean.setFromClause("from account");
     //        factoryBean.setWhereClause("where city = :city");
     //        factoryBean.setSortKey("lastName"); // 페이징 기법 사용할 때 결과 정렬하는 것 중요
     //        factoryBean.setDataSource(dataSource);
     //
     //        return factoryBean;
     //    }
     */

    /**
     *
     JdbcCursorItemReader
     //    @Bean
     //    @StepScope
     //    public JdbcCursorItemReader<Account> accountItemReader(DataSource dataSource){
     //        return new JdbcCursorItemReaderBuilder<Account>()
     //                .name("accountItemReader")
     //                .dataSource(dataSource)
     //                .sql("select * from account where city = ?")
     //                .rowMapper(new AccountRowMapper())
     //                .preparedStatementSetter(citySetter(null))
     //                .build();
     //    }
     //
     //    @Bean
     //    @StepScope
     //    public ArgumentPreparedStatementSetter citySetter(
     //            @Value("#{jobParameters['city']}") String city
     //    ){
     //        return new ArgumentPreparedStatementSetter(new Object[]{city});
     //    }
     */

    /**
     json Reader

     //    @Bean
     //    @StepScope
     //    public JsonItemReader<Account> accountFileReader(
     //            @Value("#{jobParameters['customerFile']}") Resource inputFile
     //    ){
     //        ObjectMapper objectMapper = new ObjectMapper();
     //        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
     //
     //        JacksonJsonObjectReader<Account> jacksonJsonObjectReader =
     //                new JacksonJsonObjectReader<>(Account.class);
     //
     //        jacksonJsonObjectReader.setMapper(objectMapper);
     //
     //        return new JsonItemReaderBuilder<Account>()
     //                .name("accountFileReader")
     //                .jsonObjectReader(jacksonJsonObjectReader)
     //                .resource(inputFile)
     //                .build();
     //    }

     */


    /**
     xml reader

     //    @Bean
     //    @StepScope
     //    public StaxEventItemReader<Account> accountFileReader(
     //            @Value("#{jobParameters['customerFile']}") String inputFile
     //    ){
     //
     //        return new StaxEventItemReaderBuilder<Account>()
     //                .name("accountFileReader")
     //                .resource(new ClassPathResource(inputFile))
     //                .addFragmentRootElements("account")
     //                .unmarshaller(accountMarshaller())
     //                .build();
     //    }
     //
     //    // Jaxb2Marshaller 에게 대상 클래스를 알려주도록 작성
     //     @Bean
     //     public Jaxb2Marshaller accountMarshaller(){
     //        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
     //        jaxb2Marshaller.setClassesToBeBound(Account.class, Transaction.class);
     //        return jaxb2Marshaller;
     //     }
     */

    /**
     * 플랫 파일 reader
     * <p>
     * //    @Bean
     * //    @StepScope
     * //    public FlatFileItemReader accountItemReader(
     * ////            @Value("#{jobParameters['customerFile']}") String inputFile
     * //    ){
     * //        return new FlatFileItemReaderBuilder<Account>()
     * //                .name("accountItemReader")
     * ////                .resource(inputFIle)
     * //                .lineMapper(lineTokenizer())
     * ////                .lineTokenizer(new AccountFileLineTokenizer()) // 특이한 파일 포맷 파싱, 엑셀 워크시트 같은 서드파티 파일 포맷, 특수한 타입 변환 요구 조건 처리 시 사용
     * ////                .delimited()
     * //                //고정 너비 파일일 경우
     * ////                .fixedLength()
     * ////                .columns(new Range[]{new Range(1,11), new Range(12,12),new Range(13,22),
     * ////                                new Range(23,26),new Range(27,46),new Range(47,62),new Range(63,64),new Range(65,69)})
     * ////                .names("firstName", "middleInitial", "lastName","addressNumber","street","city","state","zipCode")
     * ////                .fieldSetMapper(new AccountFileSetMapper())
     * ////                .targetType(Account.class)
     * //                .build();
     * //
     * //    }
     * //
     * //    @Bean
     * //    @StepScope
     * //    public MultiResourceItemReader multiAccountReader(
     * //            @Value("#{jobParameters['customerFile']}") Resource[] inputFiles
     * //    ){
     * //        return new MultiResourceItemReaderBuilder<>()
     * //                .name("multiAccountReader")
     * //                .resources(inputFiles)
     * //                .delegate(accountFileReader())
     * //                .build();
     * //    }
     * //
     * //    @Bean
     * //    public AccountFileReader accountFileReader(){
     * //        return new AccountFileReader(accountItemReader());
     * //    }
     */

    @Bean
    public PatternMatchingCompositeLineMapper lineTokenizer() {
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
    public DelimitedLineTokenizer transactionLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("prefix", "accountNumber", "transactionDate", "amount");
        return lineTokenizer;
    }

    @Bean
    public DelimitedLineTokenizer accountLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setNames("firstName", "middleInitial", "lastName", "address", "city", "state", "zipCode");

        lineTokenizer.setIncludedFields(1, 2, 3, 4, 5, 6, 7);
        return lineTokenizer;
    }

    @Bean
    public ItemWriter accountItemWriter() {
        return list -> list.forEach(System.out::println);
    }


}
