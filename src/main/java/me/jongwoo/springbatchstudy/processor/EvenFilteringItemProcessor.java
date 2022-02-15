package me.jongwoo.springbatchstudy.processor;

import me.jongwoo.springbatchstudy.domain.Customer2;
import org.springframework.batch.item.ItemProcessor;

public class EvenFilteringItemProcessor implements ItemProcessor<Customer2, Customer2> {

    @Override
    public Customer2 process(Customer2 customer2) throws Exception {
        // null 반환하면 해당 아이템이 필터링되도록 함으로써 필터링 과정을 단순화한다
        // 해당 아이템은 이후에 수행되는 여러 ItemProcessor 나 ItemWriter에게 전달되지 않고
        // 이때 스프링 배치는 필터링된 레코드 수를 보관하고 JobRepository에 저장한다
        return Integer.parseInt(customer2.getZip()) % 2 == 0 ? null : customer2;
    }
}
