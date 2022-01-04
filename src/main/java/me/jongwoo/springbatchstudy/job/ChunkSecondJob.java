package me.jongwoo.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ChunkSecondJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkSecondTestJob(){
        return this.jobBuilderFactory.get("chunkBasedJob")
                .start(chunkSecondStep())
                .build();
    }

    public Step chunkSecondStep() {
        return this.stepBuilderFactory.get("chunkStep")
                .<String, String>chunk(1000) // 커밋 간격 하드 코딩은 모든 상황에 적절하지 않음..
                .reader(itemSecondReader())
                .writer(itemSecondWriter())
                .build()
                ;
    }

    @Bean
    public ListItemReader<String> itemSecondReader() {

        List<String> items = new ArrayList<>(100000);

        for(int i = 0; i< 100000; i++){
            items.add(UUID.randomUUID().toString());
        }

        return new ListItemReader<>(items);

    }

    @Bean
    public ItemWriter<String> itemSecondWriter() {

        return items -> {
            for(String item: items){
                System.out.println(">> current item = " + item);
            }
        };
    }

}
