package me.jongwoo.springbatchstudy.domain;

import lombok.*;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class Transaction {

    private String accountNumber;
    private Date transactionDate;
    private Double amount;

    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

}
