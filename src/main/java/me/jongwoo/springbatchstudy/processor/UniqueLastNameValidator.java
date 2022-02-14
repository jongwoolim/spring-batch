package me.jongwoo.springbatchstudy.processor;

import me.jongwoo.springbatchstudy.domain.Customer2;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UniqueLastNameValidator extends ItemStreamSupport implements Validator<Customer2> {

    private Set<String> lastNames = new HashSet<>();

    @Override
    public void validate(Customer2 customer2) throws ValidationException {

        System.out.println("=======================");
//        lastNames.forEach(System.out::println);
        System.out.println(lastNames.contains(customer2.getLastName()));
        System.out.println("***********************");
        System.out.println("=======================");
        if(lastNames.contains(customer2.getLastName())){
            throw new ValidationException("Duplicate last name was found: " + customer2.getLastName());
        }
        this.lastNames.add(customer2.getLastName());
    }

    @Override
    public void open(ExecutionContext executionContext) {
        String lastNames = getExecutionContextKey("lastNames");

        if(executionContext.containsKey(lastNames)){
            this.lastNames = (Set<String>) executionContext.get(lastNames);
        }

    }

    @Override
    public void update(ExecutionContext executionContext) {
        Iterator<String> iterator = lastNames.iterator();
        Set<String> copiedLastNames = new HashSet<>();

        while(iterator.hasNext()){
            copiedLastNames.add(iterator.next());
        }

        executionContext.put(getExecutionContextKey("lastNames"), copiedLastNames);
    }
}
