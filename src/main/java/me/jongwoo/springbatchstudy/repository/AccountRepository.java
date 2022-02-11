package me.jongwoo.springbatchstudy.repository;

import me.jongwoo.springbatchstudy.domain.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findByCity(String city, Pageable pageable);
}
