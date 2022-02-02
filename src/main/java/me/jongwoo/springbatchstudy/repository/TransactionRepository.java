package me.jongwoo.springbatchstudy.repository;

import me.jongwoo.springbatchstudy.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//public interface TransactionRepository extends JpaRepository<Transaction, Long> {
//
//    @Query("select t from Transaction t inner join AccountSummary a where a.accountNumber = :accountNumber")
//    List<Transaction> getTransactionByAccountNumber(String accountNumber);
//}
