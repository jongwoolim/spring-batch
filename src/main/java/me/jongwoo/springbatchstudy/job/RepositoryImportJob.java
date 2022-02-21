package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.repository.Customer4;
import me.jongwoo.springbatchstudy.repository.Customer4Repository;
import me.jongwoo.springbatchstudy.service.CustomService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.adapter.PropertyExtractingDelegatingItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(basePackageClasses = Customer4.class)
public class RepositoryImportJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job repositoryFormatJob(){
        return this.jobBuilderFactory.get("repositoryFormatJob")
                .start(repositoryFormatStep())
                .build();
    }

    @Bean
    public Step repositoryFormatStep() {
        return this.stepBuilderFactory.get("repositoryFormatStep")
                .<Customer4, Customer4>chunk(10)
                .reader(customer4FlatFileItemReader3(null))
                .writer(itemWriterAdapter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer4> customer4FlatFileItemReader3(
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

//    @Bean
//    public RepositoryItemWriter<Customer4> repositoryItemWriter(Customer4Repository repository){
//
//        return new RepositoryItemWriterBuilder<Customer4>()
//                .repository(repository)
//                .methodName("save")
//                .build();
//    }

    @Bean
    public PropertyExtractingDelegatingItemWriter<Customer4> itemWriterAdapter(CustomService customeService){
        PropertyExtractingDelegatingItemWriter<Customer4> itemWriter =
                new PropertyExtractingDelegatingItemWriter<>();

        itemWriter.setTargetObject(customeService);
        itemWriter.setTargetMethod("logCustomerAddress");
        itemWriter.setFieldsUsedAsTargetMethodArguments(new String[]{"address","city","state","zip"});

        return itemWriter;

    }

//    @Bean
//    public ItemWriterAdapter<Customer4> itemWriterAdapter(CustomService customeService){
//        ItemWriterAdapter<Customer4> customer4ItemWriterAdapter = new ItemWriterAdapter<>();
//
//        customer4ItemWriterAdapter.setTargetObject(customeService);
//        customer4ItemWriterAdapter.setTargetMethod("logCustomer");
//
//        return customer4ItemWriterAdapter;
//
//    }
}
