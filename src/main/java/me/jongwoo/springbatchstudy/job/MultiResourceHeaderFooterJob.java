package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.batch.CustomerOutputFileSuffixCreator;
import me.jongwoo.springbatchstudy.batch.CustomerRecordCountFooterCallback;
import me.jongwoo.springbatchstudy.repository.Customer4;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class MultiResourceHeaderFooterJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multiFlatFileGeneratorJob() throws Exception {
        return this.jobBuilderFactory.get("multiFlatFileGeneratorJob")
                .start(multiFlatFileGeneratorStep())
                .build();
    }

    @Bean
    public Step multiFlatFileGeneratorStep() throws Exception {
        return this.stepBuilderFactory.get("multiFlatFileGeneratorStep")
                .<Customer4, Customer4>chunk(10)
                .reader(multiResourceJdbcReader(null))
                .writer(multiFlatFileItemWriter(null))
                .build();
    }


	@Bean
	public JdbcCursorItemReader<Customer4> multiResourceJdbcReader(DataSource dataSource) {

		return new JdbcCursorItemReaderBuilder<Customer4>()
				.name("customerItemReader")
				.dataSource(dataSource)
				.sql("select * from customer4")
				.rowMapper(new BeanPropertyRowMapper<>(Customer4.class))
				.build();
	}

    @Bean
	public MultiResourceItemWriter<Customer4> multiFlatFileItemWriter(CustomerOutputFileSuffixCreator suffixCreator) throws Exception {

		return new MultiResourceItemWriterBuilder<Customer4>()
				.name("multiFlatFileItemWriter")
				.delegate(delegateCustomerItemWriter(null))
				.itemCountLimitPerResource(25)
				.resource(new FileSystemResource("/Users/limjongwoo/Desktop/output/customer"))
				.resourceSuffixCreator(suffixCreator)
				.build();
	}

    @Bean
	@StepScope
	public FlatFileItemWriter<Customer4> delegateCustomerItemWriter(CustomerRecordCountFooterCallback footerCallback) throws Exception {
		BeanWrapperFieldExtractor<Customer4> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] {"firstName", "lastName", "address", "city", "state", "zip"});
		fieldExtractor.afterPropertiesSet();

		FormatterLineAggregator<Customer4> lineAggregator = new FormatterLineAggregator<>();

		lineAggregator.setFormat("%s %s lives at %s %s in %s, %s.");
		lineAggregator.setFieldExtractor(fieldExtractor);

		FlatFileItemWriter<Customer4> itemWriter = new FlatFileItemWriter<>();

		itemWriter.setName("delegateCustomerItemWriter");
		itemWriter.setLineAggregator(lineAggregator);
		itemWriter.setAppendAllowed(true);
		itemWriter.setFooterCallback(footerCallback);

		return itemWriter;
	}

}
