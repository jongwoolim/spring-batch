package me.jongwoo.springbatchstudy.repository;

import me.jongwoo.springbatchstudy.domain.Transaction;

import java.util.List;

public interface TransactionDao {

    List<Transaction> getTransactionByAccountNumber(String accountNumber);
}
