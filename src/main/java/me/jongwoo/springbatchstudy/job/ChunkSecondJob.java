//package me.jongwoo.springbatchstudy.job;
//
//import lombok.RequiredArgsConstructor;
//import me.jongwoo.springbatchstudy.listener.LoggingStepStartStopListener;
//import me.jongwoo.springbatchstudy.policy.RandomChunkSizePolicy;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
//import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
//import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
//import org.springframework.batch.item.support.ListItemReader;
//import org.springframework.batch.repeat.CompletionPolicy;
//import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
//import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
//import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.Resource;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Configuration
//@RequiredArgsConstructor
//public class ChunkSecondJob {
//
//    private final JobBuilderFactory jobBuilderFactory;
//
//    private final StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Job chunkSecondTestJob(){
//        return this.jobBuilderFactory.get("chunkBasedJob2")
//                .start(chunkSecondStep())
//                .build();
//    }
//
//    @Bean
//    public Step chunkSecondStep() {
//        return this.stepBuilderFactory.get("chunkStep2")
////                .<String, String>chunk(completionPolicy()) // 커밋 간격 하드 코딩은 모든 상황에 적절하지 않음..
//                .<String, String>chunk(randomCompletionPolicy())
//                .reader(itemSecondReader())
//                .writer(itemSecondWriter())
//                .listener(new LoggingStepStartStopListener())
//                .build()
//                ;
//    }
//
//    @Bean
//    public CompletionPolicy randomCompletionPolicy(){
//        return new RandomChunkSizePolicy();
//    }
//
//    @Bean
//    public CompletionPolicy completionPolicy() {
//        CompositeCompletionPolicy policy =
//                new CompositeCompletionPolicy();
//
//        policy.setPolicies(
//                new CompletionPolicy[]{
//                        new TimeoutTerminationPolicy(3),
//                        new SimpleCompletionPolicy(1000)
//                }
//        );
//
//        return policy;
//    }
//
//    @Bean
//    public ListItemReader<String> itemSecondReader() {
//
//        List<String> items = new ArrayList<>(40);
//
//        for(int i = 0; i< 40; i++){
//            items.add(UUID.randomUUID().toString());
//        }
//
//        return new ListItemReader<>(items);
//
//    }
//
//    @Bean
//    public ItemWriter<String> itemSecondWriter() {
//
//        return items -> {
//            for(String item: items){
//                System.out.println(">> current item = " + item);
//            }
//        };
//    }
//
//}
