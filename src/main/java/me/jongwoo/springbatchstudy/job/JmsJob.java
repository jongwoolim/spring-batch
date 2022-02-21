package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import me.jongwoo.springbatchstudy.repository.Customer4;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.jms.JmsItemReader;
import org.springframework.batch.item.jms.JmsItemWriter;
import org.springframework.batch.item.jms.builder.JmsItemReaderBuilder;
import org.springframework.batch.item.jms.builder.JmsItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JmsJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

	// 스프링 인티그레이션은 자바 직렬화를 통해 자체적으로 Message 객체를 직렬화 함
	// 그러나 이 기능은 유용하지 않아 액티브MQ의 지점 간 전송에 메시지가 JSON 전달되도록 변환 구성
	@Bean
	public MessageConverter jacksonJmsMessageConverter(){
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	// 스프링 부트가 JmsTemplate을 제공하지만
	// 스프링 부트가 제공하는 ConnectionFactory는 JmsTemplate과 잘 동작하지 않음
	// CachingConnectionFactory를 통해 JmsTemplate 구성
	@Bean
	public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
		cachingConnectionFactory.afterPropertiesSet();

		JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
		jmsTemplate.setDefaultDestinationName("customers");
		jmsTemplate.setReceiveTimeout(5000L);

		return jmsTemplate;
	}

    @Bean
	public Job jmsFormatJob() throws Exception {
		return this.jobBuilderFactory.get("jmsFormatJob")
				.start(formatInputStep())
				.next(formatOutputStep())
				.build();
	}

	// step 1
	@Bean
	public Step formatInputStep() throws Exception {
		return this.stepBuilderFactory.get("formatInputStep")
				.<Customer4, Customer4>chunk(10)
				.reader(jmsCustomerFileReader(null))
				.writer(jmsItemWriter(null))
				.build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Customer4> jmsCustomerFileReader(
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
						"zip"})
				.targetType(Customer4.class)
				.build();
	}

	@Bean
	public JmsItemWriter<Customer4> jmsItemWriter(JmsTemplate jmsTemplate) {

		return new JmsItemWriterBuilder<Customer4>()
				.jmsTemplate(jmsTemplate)
				.build();
	}


	// step2
	@Bean
	public Step formatOutputStep() throws Exception {
		return this.stepBuilderFactory.get("formatOutputStep")
				.<Customer4, Customer4>chunk(10)
				.reader(jmsItemReader(null))
				.writer(xmlOutputWriter(null))
				.build();
	}


	@Bean
	public JmsItemReader<Customer4> jmsItemReader(JmsTemplate jmsTemplate) {

		return new JmsItemReaderBuilder<Customer4>()
				.jmsTemplate(jmsTemplate)
				.itemType(Customer4.class)
				.build();
	}

	@Bean
	@StepScope
	public StaxEventItemWriter<Customer4> xmlOutputWriter(
			@Value("#{jobParameters['outputFile']}") Resource outputFile) {

		Map<String, Class> aliases = new HashMap<>();
		aliases.put("customer", Customer4.class);

		XStreamMarshaller marshaller = new XStreamMarshaller();
		marshaller.setAliases(aliases);
		marshaller.afterPropertiesSet();

		return new StaxEventItemWriterBuilder<Customer4>()
				.name("xmlOutputWriter")
				.resource(outputFile)
				.marshaller(marshaller)
				.rootTagName("customers")
				.build();
	}

}
