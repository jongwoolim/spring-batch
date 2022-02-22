package me.jongwoo.springbatchstudy.writer;

import me.jongwoo.springbatchstudy.repository.Customer4;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.classify.Classifier;


public class CustomerClassifier implements Classifier<Customer4, ItemWriter<? super Customer4>> {

    private ItemWriter<Customer4> fileItemWriter;
    private ItemWriter<Customer4> jdbcItemWriter;

    public CustomerClassifier(
            StaxEventItemWriter<Customer4> fileItemWriter,
            JdbcBatchItemWriter<Customer4> jdbcItemWriter) {
        this.fileItemWriter = fileItemWriter;
        this.jdbcItemWriter = jdbcItemWriter;
    }

    @Override
    public ItemWriter<? super Customer4> classify(Customer4 customer4) {
        if(customer4.getState().matches("^[A-M].*")){
            return fileItemWriter;
        }

        return jdbcItemWriter;
    }
}
