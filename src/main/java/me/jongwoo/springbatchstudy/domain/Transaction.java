package me.jongwoo.springbatchstudy.domain;

import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString @XmlType(name = "transaction")
public class Transaction {

    private String accountNumber;
    private Date transactionDate;
    private Double amount;

    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

}
