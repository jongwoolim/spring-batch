package me.jongwoo.springbatchstudy.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

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
    private Date timestamp;
    private Double amount;

}
