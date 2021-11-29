package me.jongwoo.springbatchstudy.repository;

import me.jongwoo.springbatchstudy.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
