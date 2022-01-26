package me.jongwoo.springbatchstudy.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Transaction {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_summary_id")
    private AccountSummary accountSummary;
    private LocalDate timestamp;
    private Double amount;

}
