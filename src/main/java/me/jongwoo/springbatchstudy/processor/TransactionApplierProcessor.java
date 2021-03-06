package me.jongwoo.springbatchstudy.processor;

import me.jongwoo.springbatchstudy.domain.AccountSummary;
import me.jongwoo.springbatchstudy.domain.Transaction;
import me.jongwoo.springbatchstudy.repository.TransactionDao;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

public class TransactionApplierProcessor implements ItemProcessor<AccountSummary, AccountSummary> {

//    private TransactionRepository transactionRepository;
    private TransactionDao transactionDao;

//    public TransactionApplierProcessor(TransactionRepository transactionRepository) {
//        this.transactionRepository = transactionRepository;
//    }

    public TransactionApplierProcessor(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public AccountSummary process(AccountSummary accountSummary) throws Exception {
        final List<Transaction> transactions =
                transactionDao.getTransactionByAccountNumber(accountSummary.getAccountNumber());

        for(Transaction transaction : transactions){
            accountSummary.setCurrentBalance(accountSummary.getCurrentBalance() + transaction.getAmount());
        }
        return null;
    }
}
