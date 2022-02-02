package me.jongwoo.springbatchstudy.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Transaction {

    private String accountNumber;
    private Date timestamp;
    private Double amount;

}
