package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.batch.CustomerOutputFileSuffixCreator;
import me.jongwoo.springbatchstudy.batch.CustomerXmlHeaderCallback;
import me.jongwoo.springbatchstudy.repository.Customer4;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class MultiResourceJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
	public JdbcCursorItemReader<Customer4> customer4JdbcCursorItemReader2(DataSource dataSource) {

		return new JdbcCursorItemReaderBuilder<Customer4>()
				.name("customerItemReader")
				.dataSource(dataSource)
				.sql("select * from customer4")
				.rowMapper(new BeanPropertyRowMapper<>(Customer4.class))
				.build();
	}

	@Bean
	public MultiResourceItemWriter<Customer4> multiCustomerFileWriter(
			CustomerOutputFileSuffixCreator suffixCreator) throws Exception {

		return new MultiResourceItemWriterBuilder<Customer4>()
				.name("multiCustomerFileWriter")
				.delegate(delegateItemWriter(null)) // 각 아이템을 쓰는 데 사용하는 위임 ItemWriter
				.itemCountLimitPerResource(25) // 각 리소스에 쓰기 작업을 수행할 아이템 수
				.resource(new FileSystemResource("/Users/limjongwoo/Desktop/output/customer")) // 디렉터리와 파일 이름
				.resourceSuffixCreator(suffixCreator) // 생성하는 파일 이름 끝에 접미사
				.build();
	}

	@Bean
	@StepScope
	public StaxEventItemWriter<Customer4> delegateItemWriter(
			CustomerXmlHeaderCallback headerCallback
	) throws Exception {

		Map<String, Class> aliases = new HashMap<>();
		aliases.put("customer", Customer4.class);

		XStreamMarshaller marshaller = new XStreamMarshaller();

		marshaller.setAliases(aliases);

		marshaller.afterPropertiesSet();

		return new StaxEventItemWriterBuilder<Customer4>()
				.name("customerItemWriter")
				.marshaller(marshaller)
				.rootTagName("customers")
				.headerCallback(headerCallback)
				.build();
	}

	@Bean
	public Step multiXmlGeneratorStep() throws Exception {
		return this.stepBuilderFactory.get("multiXmlGeneratorStep")
				.<Customer4, Customer4>chunk(10)
				.reader(customer4JdbcCursorItemReader2(null))
				.writer(multiCustomerFileWriter(null))
				.build();
	}

	@Bean
	public Job xmlGeneratorJob() throws Exception {
		return this.jobBuilderFactory.get("xmlGeneratorJob")
				.start(multiXmlGeneratorStep())
				.build();
	}
}
