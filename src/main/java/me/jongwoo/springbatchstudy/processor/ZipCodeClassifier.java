package me.jongwoo.springbatchstudy.processor;

import me.jongwoo.springbatchstudy.domain.Customer2;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

public class ZipCodeClassifier implements Classifier<Customer2, ItemProcessor<Customer2, Customer2>> {

    private ItemProcessor<Customer2, Customer2> oddItemProcessor;
    private ItemProcessor<Customer2, Customer2> evenItemProcessor;

    public ZipCodeClassifier(ItemProcessor<Customer2, Customer2> oddItemProcessor, ItemProcessor<Customer2, Customer2> evenItemProcessor) {
        this.oddItemProcessor = oddItemProcessor;
        this.evenItemProcessor = evenItemProcessor;
    }

    @Override
    public ItemProcessor<Customer2, Customer2> classify(Customer2 customer2) {

        if(Integer.parseInt(customer2.getZip()) % 2 == 0){
            return evenItemProcessor;
        }

        return oddItemProcessor;
    }
}
