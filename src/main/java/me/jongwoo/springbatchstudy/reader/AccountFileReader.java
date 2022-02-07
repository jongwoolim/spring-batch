package me.jongwoo.springbatchstudy.reader;

import me.jongwoo.springbatchstudy.domain.Account;
import me.jongwoo.springbatchstudy.domain.Transaction;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.util.ArrayList;

public class AccountFileReader implements ResourceAwareItemReaderItemStream<Account> {

    private Object curItem = null;
    private ResourceAwareItemReaderItemStream<Object> delegate;

    public AccountFileReader(ResourceAwareItemReaderItemStream<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Account read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // 제어 중지 로직
        // Account 아이템을 하나 읽어들이고 다음 고객 아이템을 만나기 전까지 현재  처리 중인 고객 레코드으ㅘ 관련된
        // 거래 내역 레코드를 한 줄씩 읽어들이고 다음 고객 레코드를 발견하면 현재 처리 중인 고객 레코드 처리가 끝난 것으로 간주하여 커스텀 ItemReader로 반환한다
        if(curItem == null){
            curItem = delegate.read();
        }

        Account item = (Account)curItem;
        curItem = null;

        if(item != null){
//            item.setTransactions(new ArrayList<>());
//            while(peek() instanceof Transaction){
//                item.getTransactions().add((Transaction) curItem);
//                curItem = null;
//            }
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

    @Override
    public void setResource(Resource resource) {
        this.delegate.setResource(resource);
    }
}
