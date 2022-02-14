package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.domain.Customer2;
import me.jongwoo.springbatchstudy.processor.UniqueLastNameValidator;
import me.jongwoo.springbatchstudy.service.UpperCaseNameService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.ScriptItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
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
                .processor(itemProcessor(null))
                .writer(itemValidateWriter())
                .stream(validator())
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
    @StepScope
    public ScriptItemProcessor<Customer2, Customer2> itemProcessor(
            @Value("#{jobParameters['script']}") Resource script
    ){
        ScriptItemProcessor<Customer2, Customer2> itemProcessor = new ScriptItemProcessor<>();
        itemProcessor.setScript(script);

        return itemProcessor;

    }

//    @Bean
//    public ItemProcessorAdapter<Customer2,Customer2> itemProcessor(UpperCaseNameService service){
//
//        ItemProcessorAdapter<Customer2,Customer2> adapter = new ItemProcessorAdapter<>();
//        adapter.setTargetObject(service);
//        adapter.setTargetMethod("upperCase");
//
//        return adapter;
//    }

    @Bean
    public UniqueLastNameValidator validator(){
        UniqueLastNameValidator uniqueLastNameValidator = new UniqueLastNameValidator();
        uniqueLastNameValidator.setName("validator");
        return uniqueLastNameValidator;
    }

    @Bean
    public ValidatingItemProcessor<Customer2> customer2ValidatingItemProcessor(){
        return new ValidatingItemProcessor<>(validator());
    }

//    @Bean
//	public BeanValidatingItemProcessor<Customer2> customerValidatingItemProcessor() {
//		return new BeanValidatingItemProcessor<>();
//	}

    @Bean
    public ItemWriter<Customer2> itemValidateWriter(){
        return list -> list.forEach(System.out::println);
    }

}
