package me.jongwoo.springbatchstudy.reader;

import me.jongwoo.springbatchstudy.domain.Account;
import me.jongwoo.springbatchstudy.domain.Transaction;
import org.springframework.batch.item.*;

import java.util.ArrayList;

public class AccountFileReader implements ItemStreamReader<Account> {

    private Object curItem = null;
    private ItemStreamReader<Object> delegate;

    public AccountFileReader(ItemStreamReader<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Account read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(curItem == null){
            curItem = delegate.read();
        }

        Account item = (Account)curItem;
        curItem = null;

        if(item != null){
            item.setTransactions(new ArrayList<>());
            while(peek() instanceof Transaction){
                item.getTransactions().add((Transaction) curItem);
                curItem = null;
            }
        }

        return item;
    }

    private Object peek() throws Exception {
        if(curItem == null){
            curItem = delegate.read();
        }
        return curItem;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}
