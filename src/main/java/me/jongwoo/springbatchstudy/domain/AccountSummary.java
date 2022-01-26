package me.jongwoo.springbatchstudy.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummary {

    @Id @GeneratedValue
    private Long id;

    private String accountNumber;
    private Double currentBalance;
}
