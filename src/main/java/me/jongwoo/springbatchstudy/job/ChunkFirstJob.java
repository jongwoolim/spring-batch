//package me.jongwoo.springbatchstudy.job;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.FlatFileItemWriter;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
//import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
//import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
//
//@Configuration
//@RequiredArgsConstructor
//public class ChunkFirstJob {
//
//    private final JobBuilderFactory jobBuilderFactory;
//
//    private final StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Job chunkFirstTestJob(){
//        return this.jobBuilderFactory.get("chunkBasedJob1")
//                .start(chunkFirstStep())
//                .build();
//    }
//
//    @Bean
//    public Step chunkFirstStep() {
//        return this.stepBuilderFactory.get("chunkStep1")
//                .<String, String>chunk(10) // 커밋 간격 10 -> 10개 단위로 레코드 처리한 후 작업 커밋
//                .reader(itemReader(null))
//                .writer(itemWriter(null))
//                .build()
//                ;
//    }
//
//    @Bean
//    @StepScope
//    public FlatFileItemReader<String> itemReader(
//            @Value("#{jobParameters['inputFile']}") Resource inputFile) {
//
//        return new FlatFileItemReaderBuilder<String>()
//                .name("itemReader")
//                .resource(inputFile)
//                .lineMapper(new PassThroughLineMapper())
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public FlatFileItemWriter<String> itemWriter(
//            @Value("#{jobParameters['outputFile']}") Resource outputFile) {
//
//        return new FlatFileItemWriterBuilder<String>()
//                .name("itemWriter")
//                .resource(outputFile)
//                .lineAggregator(new PassThroughLineAggregator<>())
//                .build();
//    }
//
//}
